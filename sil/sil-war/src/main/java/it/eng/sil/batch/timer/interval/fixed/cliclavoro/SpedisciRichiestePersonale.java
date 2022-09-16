/**
 * 
 */
package it.eng.sil.batch.timer.interval.fixed.cliclavoro;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.ejb.Schedule;
import javax.ejb.Singleton;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

import org.apache.log4j.Logger;

import com.engiweb.framework.dbaccess.DataConnectionManager;

import it.eng.sil.Values;
import it.eng.sil.batch.timer.interval.fixed.FixedTimerBatch;
import it.eng.sil.util.batch.BatchInvioRichiestePersonale;

/**
 * @author
 *
 */
@Singleton
public class SpedisciRichiestePersonale extends FixedTimerBatch {

	private Logger logger = Logger.getLogger(SpedisciRichiestePersonale.class.getName());

	private static final String QUERY_ESEGUIBILITA = " select nvl( (select to_char(ts_config_loc.num) num from ts_config_loc "
			+ " where strcodrif=(select ts_generale.codprovinciasil from ts_generale where ts_generale.prggenerale = 1) and codtipoconfig = 'FLVACAN') , '0') as num, "
			+ " (select ts_generale.codprovinciasil from ts_generale where ts_generale.prggenerale = 1) as codprovinciasil "
			+ " from dual";

	@Schedule(dayOfMonth = "*", dayOfWeek = "*", hour = "22", minute = "30", persistent = false)
	@TransactionAttribute(TransactionAttributeType.NEVER)
	public void perform() {
		if (this.isEnabled()) {
			if (logger.isDebugEnabled()) {
				logger.debug("perform() - start");
			}

			Connection connection = null;
			boolean oldAutoCommit = false;
			ResultSet resultSet = null;
			Statement statement = null;
			String numConfig = null;

			try {
				connection = DataConnectionManager.getInstance().getConnection(Values.DB_SIL_DATI)
						.getInternalConnection();
				oldAutoCommit = connection.getAutoCommit();
				connection.setAutoCommit(false);

				if (connection != null) {
					logger.info("SpedisciRichiestePersonale. VERIFICA ESEGUIBILITA' BATCH");
					statement = connection.createStatement();
					resultSet = statement.executeQuery(QUERY_ESEGUIBILITA);
					if (resultSet != null) {
						while (resultSet.next()) {
							numConfig = resultSet.getString("num");
						}
					}
				}
			} catch (Exception e) {
				logger.error("Errore Batch SpedisciRichiestePersonale: ", e);
			} finally {
				releaseResources(connection, resultSet, statement, oldAutoCommit);
			}

			if (numConfig != null && numConfig.equalsIgnoreCase("0")) {
				String[] str = new String[0];
				BatchInvioRichiestePersonale.main(str);
			} else {
				logger.info(
						"Batch SpedisciRichiestePersonale non eseguito: LA CONFIGURAZIONE NON PREVEDE LA SUA ESECUZIONE.");
			}
			if (logger.isDebugEnabled()) {
				logger.debug("perform() - end");
			}
		} else {
			// Timer non abilitato su questo nodo
			logger.warn(
					"[SpedisciRichiestePersonale] ---> WARN: non abilitato su questo nodo, probabilmente è abilitato su un altro nodo");
		}
	}

	private void releaseResources(Connection connection, ResultSet resultSet, Statement statement,
			boolean oldAutoCommit) {
		if (resultSet != null) {
			try {
				resultSet.close();
			} catch (SQLException e) {
				logger.error(e);
			}
		}

		if (statement != null) {
			try {
				statement.close();
			} catch (SQLException e) {
				logger.error(e);
			}
		}

		if (connection != null) {
			try {
				connection.setAutoCommit(oldAutoCommit);
				connection.close();
			} catch (SQLException e) {
				logger.error(e);
			}
		}
	}
}

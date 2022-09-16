package it.eng.sil.batch.timer.interval.fixed.tda;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.util.Date;

import javax.ejb.Schedule;
import javax.ejb.Singleton;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

import org.apache.log4j.Logger;

import com.engiweb.framework.dbaccess.DataConnectionManager;
import com.engiweb.framework.dbaccess.Utils;
import com.engiweb.framework.dbaccess.sql.DataConnection;
import com.engiweb.framework.error.EMFInternalError;
import com.engiweb.framework.util.QueryExecutorObject;

import it.eng.sil.Values;
import it.eng.sil.batch.timer.interval.fixed.FixedTimerBatch;
import it.eng.sil.module.sifer.PartecipanteTirociniUtils;
import it.eng.sil.module.voucher.Voucher;

@Singleton
public class BatchScadenzaAutomaticaTda extends FixedTimerBatch {

	private static final BigDecimal USER_BATCH = new BigDecimal("100");

	private static final String COD_MOTIVO_ANNULL = new String("SCADATT");

	private static final String QUERY_GIORNI_SCADENZA = " select num " + " from ts_config_loc "
			+ " where codtipoconfig = 'TDA_SCAD' "
			+ " and strcodrif = (select ts_generale.codprovinciasil from ts_generale where prggenerale = 1) ";

	private static final String QUERY_ESEGUIBILITA = " select strvalore " + " from ts_config_loc "
			+ " where codtipoconfig = 'TDA_SCAD' "
			+ " and strcodrif = (select ts_generale.codprovinciasil from ts_generale where prggenerale = 1) ";

	private static final String QUERY_SELECT_TDA_SCADENZA = " SELECT PRGVOUCHER, PRGCOLLOQUIO, PRGPERCORSO "
			+ " FROM OR_VCH_VOUCHER " + " WHERE  CODSTATOVOUCHER = 'ASS' " + " AND (SYSDATE - DATMAXATTIVAZIONE) > ?";

	static Logger logger = Logger.getLogger(BatchScadenzaAutomaticaTda.class.getName());

	@Schedule(dayOfMonth = "*", dayOfWeek = "*", hour = "11", minute = "0", persistent = false)
	@TransactionAttribute(TransactionAttributeType.NEVER)
	public void perform() {
		if (this.isEnabled()) {
			DateFormat df = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.MEDIUM);
			final Date effectiveStartDate = new Date();

			logger.info("Batch BatchScadenzaAutomaticaTda START effectiveStartDate:" + df.format(effectiveStartDate));

			Connection connection = null;
			Statement statementAttivo = null;
			Statement statementGiorni = null;

			boolean oldAutoCommit = false;

			try {

				connection = DataConnectionManager.getInstance().getConnection(Values.DB_SIL_DATI)
						.getInternalConnection();
				oldAutoCommit = connection.getAutoCommit();
				connection.setAutoCommit(false);

				ResultSet rsBatchAttivo = null;
				String valoreAttivo = null;

				ResultSet rsGiorni = null;
				BigDecimal giorniScadenza = null;
				if (connection != null) {

					statementAttivo = connection.createStatement();
					rsBatchAttivo = statementAttivo.executeQuery(QUERY_ESEGUIBILITA);
					if (rsBatchAttivo != null) {
						while (rsBatchAttivo.next()) {
							valoreAttivo = rsBatchAttivo.getString("strvalore");
						}
					}
					statementAttivo.close();
					rsBatchAttivo.close();
					if (!valoreAttivo.isEmpty() && valoreAttivo.equalsIgnoreCase("S")) {
						statementGiorni = connection.createStatement();
						rsGiorni = statementGiorni.executeQuery(QUERY_GIORNI_SCADENZA);
						if (rsGiorni != null) {
							while (rsGiorni.next()) {
								giorniScadenza = rsGiorni.getBigDecimal("num");
							}
						}
						statementGiorni.close();
						rsGiorni.close();
						if (giorniScadenza != null && giorniScadenza.intValue() > 0) {
							ResultSet resultSetInScadenza = null;
							PreparedStatement psTdaInScadenza = connection.prepareStatement(QUERY_SELECT_TDA_SCADENZA);
							psTdaInScadenza.setBigDecimal(1, giorniScadenza);
							resultSetInScadenza = psTdaInScadenza.executeQuery();
							if (resultSetInScadenza != null) {
								while (resultSetInScadenza.next()) {
									QueryExecutorObject qExec = null;
									DataConnection dataConnection = null;
									qExec = PartecipanteTirociniUtils.getQueryExecutorObject();
									dataConnection = qExec.getDataConnection();
									dataConnection.initTransaction();
									BigDecimal prgPercorso = resultSetInScadenza.getBigDecimal("PRGPERCORSO");
									BigDecimal prgColloquio = resultSetInScadenza.getBigDecimal("PRGCOLLOQUIO");
									BigDecimal prgVoucher = resultSetInScadenza.getBigDecimal("PRGVOUCHER");
									logger.debug("Batch BatchScadenzaAutomaticaTda - TDA con PRGVOUCHER: " + prgVoucher
											+ " ; PRGPERCORSO: " + prgPercorso + " ;  PRGCOLLOQUIO: " + prgColloquio
											+ " IN CHIUSURA CON MOTIVO ANNULLAMENTO " + COD_MOTIVO_ANNULL);
									Voucher vch = new Voucher(prgPercorso, prgColloquio);
									try {
										Integer esito = vch.annulla(dataConnection, USER_BATCH, COD_MOTIVO_ANNULL);
										logger.debug(
												"Batch BatchScadenzaAutomaticaTda -Codice Esito annullamento voucher: "
														+ esito + " (PRGVOUCHER: " + prgVoucher + " ; PRGPERCORSO: "
														+ prgPercorso + " ;  PRGCOLLOQUIO: " + prgColloquio + ")");
										if (esito.intValue() == 0) {
											dataConnection.commitTransaction();
										} else {
											logger.info(
													"Batch BatchScadenzaAutomaticaTda - Esito annullamento diverso da zero , rollback");
											dataConnection.rollBackTransaction();
										}
									} catch (Exception e) {
										try {
											dataConnection.commitTransaction();
										} catch (EMFInternalError e1) {
											logger.error("Errore: BatchScadenzaAutomaticaTda: ", e1);
										}
										logger.error("Errore: batch BatchScadenzaAutomaticaTda: ", e);
										continue;
									} finally {
										Utils.releaseResources(dataConnection, null, null);
										qExec = null;
									}
								}
							} else {
								logger.info("Batch BatchScadenzaAutomaticaTda nessun TDA da annullare in automatico");
							}
							psTdaInScadenza.close();
							resultSetInScadenza.close();
						}
					} else {
						logger.info(
								"Batch BatchScadenzaAutomaticaTda ANNULLA VOUCHER AUTOMATICO non eseguito: LA CONFIGURAZIONE NON PREVEDE LA SUA ESECUZIONE.");
					}

				}

			} catch (Exception e) {
				logger.error("Errore Batch BatchScadenzaAutomaticaTda: ", e);
			} finally {
				releaseResources(connection, statementGiorni, oldAutoCommit);
			}

			final Date stopDate = new Date();
			logger.info("Batch BatchScadenzaAutomaticaTda STOP at:" + df.format(stopDate));
		} else {
			// Timer non abilitato su questo nodo
			logger.warn(
					"[BatchScadenzaAutomaticaTda] ---> WARN: non abilitato su questo nodo, probabilmente è abilitato su un altro nodo");
		}
	}

	private void releaseResources(Connection connection, Statement statement, boolean oldAutoCommit) {
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

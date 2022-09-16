package it.eng.afExt.dbaccess.sql;

import java.math.BigDecimal;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.dbaccess.DataConnectionManager;
import com.engiweb.framework.dbaccess.Utils;
import com.engiweb.framework.dbaccess.sql.DataConnection;
import com.engiweb.framework.dbaccess.sql.SQLCommand;
import com.engiweb.framework.dbaccess.sql.result.DataResult;
import com.engiweb.framework.dbaccess.sql.result.ScrollableDataResult;

/**
 * @author fantinati_a e Franco Vuoto
 */
public class DBKeyGenerator {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(DBKeyGenerator.class.getName());

	/**
	 * Ritorna una stringa con una chiave ottenuto staccando il next value dal squence indicato
	 * 
	 * @param dbName
	 * @param sequenceName
	 * @return Successivo sequence, null se si verifica un errore.
	 * 
	 */
	public static synchronized BigDecimal getNextSequence(String dbName, String sequenceName) {
		BigDecimal retVal = null;
		DataConnection conn = null;
		DataConnectionManager dcm = null;
		SQLCommand stmt = null;
		DataResult result = null;

		try {
			dcm = DataConnectionManager.getInstance();

			conn = dcm.getConnection(dbName);
			stmt = conn.createSelectCommand("SELECT " + sequenceName + ".NEXTVAL KEY FROM DUAL");
			result = stmt.execute();

			ScrollableDataResult scrollableDataResult = (ScrollableDataResult) result.getDataObject();
			SourceBean retSBean = scrollableDataResult.getSourceBean();
			retVal = (BigDecimal) retSBean.getAttribute("ROW.KEY");
		} catch (Exception ex) {
			it.eng.sil.util.TraceWrapper.debug(_logger, className + "::getNextSequence()", ex);

		} finally {
			Utils.releaseResources(conn, stmt, result);
		}

		return retVal;
	}

	/**
	 * 
	 * data una connessione, la utilizza per restituire il sucessivo valore della sequence indicata
	 * 
	 * @param conn
	 * @param sequenceName
	 * @return Successivo valore della sequence, null se si verifica un errore.
	 */
	public static synchronized BigDecimal getNextSequence(DataConnection conn, String sequenceName) {
		BigDecimal retVal = null;
		SQLCommand stmt = null;
		DataResult result = null;
		try {
			stmt = conn.createSelectCommand("SELECT " + sequenceName + ".NEXTVAL KEY FROM DUAL");
			result = stmt.execute();

			ScrollableDataResult scrollableDataResult = (ScrollableDataResult) result.getDataObject();
			SourceBean retSBean = scrollableDataResult.getSourceBean();
			retVal = (BigDecimal) retSBean.getAttribute("ROW.KEY");
		} catch (Exception ex) {
			it.eng.sil.util.TraceWrapper.debug(_logger, className + "::getNextSequence()", ex);

		} finally {
			// Non devo chiudere la connessione, ci penserà il chiamante nel caso.
			Utils.releaseResources(null, stmt, result);
		}

		return retVal;
	}

	private static final String className = "DBKeyGenerator";
	public static final String S_TS_UTENTE = "S_TS_UTENTE";
}
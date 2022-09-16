package it.eng.sil.module.preferenze;

import java.sql.Connection;
import java.sql.SQLException;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.dbaccess.DataConnectionManager;
import com.engiweb.framework.dbaccess.sql.DataConnection;
import com.engiweb.framework.dbaccess.sql.SQLCommand;
import com.engiweb.framework.dbaccess.sql.result.DataResult;
import com.engiweb.framework.error.EMFInternalError;
import com.engiweb.framework.util.QueryExecutorObject;

import it.eng.afExt.utils.MessageCodes;
import it.eng.afExt.utils.ReportOperationResult;
import it.eng.sil.Values;
import it.eng.sil.module.AbstractSimpleModule;
import it.eng.sil.util.ga.db.Tabella;

public class CancellaTabDecod extends AbstractSimpleModule {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(CancellaTabDecod.class.getName());
	private String className = this.getClass().getName();

	public void service(SourceBean request, SourceBean response) {
		boolean skipComments = false;
		boolean showKeys = false;
		String tableName = ((String) request.getAttribute("TABLE_NAME")).toUpperCase();

		String skipCommentsStr = (String) request.getAttribute("SKIP_COMMENTS");
		if (skipCommentsStr != null) {
			skipComments = skipCommentsStr.equalsIgnoreCase("true");
		} else {
			skipCommentsStr = "false";
		}

		String showKeysStr = (String) request.getAttribute("SHOW_KEYS");
		if (showKeysStr != null) {
			showKeys = showKeysStr.equalsIgnoreCase("true");
		} else {
			showKeysStr = "false";
		}

		DataConnection dataConnection = null;
		Connection conn = null;
		Tabella tab = null;
		ReportOperationResult reportOperation = new ReportOperationResult(this, response);

		try {

			String pool = (String) getConfig().getAttribute("POOL");
			SQLCommand cmdSelect = null;
			DataResult dr = null;
			DataConnectionManager dataConnectionManager = DataConnectionManager.getInstance();
			dataConnection = dataConnectionManager.getConnection(Values.DB_SIL_DATI);

			conn = dataConnection.getInternalConnection();

			// oracle.jdbc.OracleConnection oracleConnection =
			// (oracle.jdbc.OracleConnection) conn;
			String userNameSQL = conn.getMetaData().getUserName();

			// oracleConnection.setRemarksReporting(true);

			// if (!skipComments){
			// oracleConnection.setRemarksReporting(true);
			// }

			tab = new Tabella(conn, userNameSQL, tableName);
			GenQuery gq = new GenQuery(tab);
			StringBuffer delQuery = gq.generaDelete(request, response);

			// Eseguire la query
			String statement = delQuery.toString();

			QueryExecutorObject queryExecObj = new QueryExecutorObject();

			queryExecObj.setRequestContainer(null);
			queryExecObj.setResponseContainer(null);
			queryExecObj.setDataConnection(dataConnection);
			queryExecObj.setStatement(statement);
			queryExecObj.setType(QueryExecutorObject.DELETE);

			queryExecObj.setTransactional(false);
			queryExecObj.setDontForgetException(true);

			Object result = queryExecObj.exec();

			if (result instanceof EMFInternalError) {// La cancellazione è
														// fallita
				try {
					if (((EMFInternalError) result).getNativeException() instanceof java.sql.SQLException) {
						SQLException ex = (SQLException) ((EMFInternalError) result).getNativeException();
						if (ex.getErrorCode() == 2292) {
							response.setAttribute("ERROR",
									"Vincolo di integrit&agrave violato: <br/>chiave gi&agrave utilizzata in altre tabelle");
							reportOperation.reportFailure(MessageCodes.General.DELETE_FAILED_FK, ex, "services()",
									"Vincolo di integrit&agrave violato: <br/>chiave gi&agrave utilizzata in altre tabelle");
						} else {
							response.setAttribute("ERROR", "Errore SQL numero: " + ex.getErrorCode());
							reportOperation.reportFailure(MessageCodes.General.DELETE_FAIL, ex, "services()",
									"Errore SQL numero: " + ex.getErrorCode());
						}
					} else {
						response.setAttribute("ERROR", "Errore interno nel tantativo di cancellazione del record");
						reportOperation.reportFailure(MessageCodes.General.DELETE_FAIL, ((EMFInternalError) result),
								"services()", "Errore durante la cancellazione");
					}
				} catch (Exception e) {

					it.eng.sil.util.TraceWrapper.debug(_logger, className + "::service()", e);
				}

			} else {// La cancellazione ha avuto successo
				response.setAttribute("Tabella", tab);
				response.setAttribute("SHOW_KEYS", showKeysStr);
				response.setAttribute("SKIP_COMMENTS", skipCommentsStr);

				reportOperation.reportSuccess(MessageCodes.General.DELETE_SUCCESS);
			}

		} catch (Exception ex) {
			try {
				response.setAttribute("ERROR", ex.getMessage());
			} catch (Exception e) {

				it.eng.sil.util.TraceWrapper.debug(_logger, className + "::service()", e);
			}

			it.eng.sil.util.TraceWrapper.debug(_logger, className + "::service()", ex);

		} finally {
			try {
				dataConnection.close();
				// conn.close();
			} catch (Exception ex) {
				it.eng.sil.util.TraceWrapper.debug(_logger, className + "::service()", ex);

			}

		} // finally
	}

}// class CancellaTabDecod

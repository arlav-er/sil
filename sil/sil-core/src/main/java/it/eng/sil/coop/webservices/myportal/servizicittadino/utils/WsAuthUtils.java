package it.eng.sil.coop.webservices.myportal.servizicittadino.utils;

import java.util.ArrayList;
import java.util.List;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.dbaccess.sql.DataField;
import com.engiweb.framework.error.EMFInternalError;
import com.engiweb.framework.util.QueryExecutorObject;

public class WsAuthUtils {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(WsAuthUtils.class.getName());

	private static final String WS_QUERY_AUTH = " SELECT prgws, struserid, strpassword AS cln_pwd FROM ts_ws WHERE  codservizio = ";

	public Credentials getCredentials(String wsCodServizio) {

		QueryUtils queryUtils = new QueryUtils();
		String username = "", password = "";
		// DataConnection dc;
		QueryExecutorObject qExec = null;

		try {

			qExec = queryUtils.getQueryExecutorObject();
			List<DataField> inPars = new ArrayList<DataField>();
			// dc = qExec.getDataConnection();
			qExec.setInputParameters(inPars);
			qExec.setType(QueryExecutorObject.SELECT);
			qExec.setStatement(WS_QUERY_AUTH + "'" + wsCodServizio + "'");
			Object result = qExec.exec();
			if (result instanceof SourceBean) {
				SourceBean logon = (SourceBean) result;
				password = (String) logon.getAttribute("ROW.cln_pwd");
				username = (String) logon.getAttribute("ROW.struserid");
			}
			if (password == null || username == null) {
				return null;
			}
			return new Credentials(username, password);

		} catch (Exception e) {
			_logger.error("Errore lettura credenziali", e);
			return null;
		} finally {
			if (qExec != null && qExec.getDataConnection() != null) {
				try {
					qExec.getDataConnection().close();
				} catch (EMFInternalError e) {
					_logger.error("Errore nella chiusura della connessione per il recupero delle credenziali", e);
				}
			}
		}

	}

}

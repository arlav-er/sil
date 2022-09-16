/*
 * Created on 9-ott-07
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package it.eng.sil.util;

import java.util.ArrayList;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.dbaccess.DataConnectionManager;
import com.engiweb.framework.dbaccess.SQLStatements;
import com.engiweb.framework.dbaccess.Utils;
import com.engiweb.framework.dbaccess.sql.DataConnection;
import com.engiweb.framework.dbaccess.sql.SQLCommand;
import com.engiweb.framework.dbaccess.sql.result.DataResult;
import com.engiweb.framework.dbaccess.sql.result.DataResultInterface;
import com.engiweb.framework.error.EMFInternalError;

import it.eng.afExt.utils.TransactionQueryExecutor;

/**
 * @author mancinid
 *
 *         To change the template for this generated type comment go to Window&gt;Preferences&gt;Java&gt;Code
 *         Generation&gt;Code and Comments
 */
public class StatementUtils {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(StatementUtils.class);

	public static SourceBean getSourceBeanByStatement(String statementName) throws EMFInternalError {
		return getSourceBeanByStatement(statementName, (ArrayList) null, null, null);
	}

	public static SourceBean getSourceBeanByStatement(String statementName, ArrayList params) throws EMFInternalError {
		return getSourceBeanByStatement(statementName, params, null, null);
	}

	public static SourceBean getSourceBeanByStatement(String statementName, String param) throws EMFInternalError {
		ArrayList params = new ArrayList();
		params.add(param);
		return getSourceBeanByStatement(statementName, params, null, null);
	}

	public static SourceBean getSourceBeanByStatement(String statementName, ArrayList params, String statement)
			throws EMFInternalError {
		return getSourceBeanByStatement(statementName, params, statement, null);
	}

	public static SourceBean getSourceBeanByStatement(String statementName, String param, String statement)
			throws EMFInternalError {
		ArrayList params = new ArrayList();
		params.add(param);
		return getSourceBeanByStatement(statementName, params, statement, null);
	}

	public static SourceBean getSourceBeanByStatement(String statementName, String param, String statement,
			String statementType) throws EMFInternalError {
		ArrayList params = new ArrayList();
		params.add(param);
		return getSourceBeanByStatement(statementName, params, statement, statementType);
	}

	public static SourceBean getSourceBeanByStatement(String statementName, ArrayList params, String statement,
			String statementType) throws EMFInternalError {
		DataConnection conn = null;
		SQLCommand cm = null;
		DataResult dr = null;

		try {
			DataConnectionManager dcm = DataConnectionManager.getInstance();
			conn = dcm.getConnection();

			if ((statementType == null) || (statementType.equalsIgnoreCase("SELECT"))) {
				if (statement == null) {
					String statementByXml = SQLStatements.getStatement(statementName);
					cm = conn.createSelectCommand(statementByXml);
				} else {
					cm = conn.createSelectCommand(statement);
				}
			} else if (statementType.equalsIgnoreCase("DELETE")) {
				if (statement == null) {
					String statementByXml = SQLStatements.getStatement(statementName);
					cm = conn.createDeleteCommand(statementByXml);
				} else {
					cm = conn.createDeleteCommand(statement);
				}
			}

			if (params == null) {
				dr = cm.execute();
			} else {
				ArrayList ar = new ArrayList();
				for (int i = 0; i < params.size(); i++) {
					ar.add(conn.createDataField("yyy", java.sql.Types.CHAR, params.get(i)));
				}
				dr = cm.execute(ar);
			}

			DataResultInterface dob = dr.getDataObject();

			return dob.getSourceBean();

		} catch (Exception e) {
			String errorMsg = "it.eng.sil.util.StatementUtils::getSourceBeanByStatement(): " + e.getMessage();
			it.eng.sil.util.TraceWrapper.error(_logger, errorMsg, e);
			throw Utils.generateInternalError(e, errorMsg);
		} finally {
			Utils.releaseResources(conn, cm, dr);
		}
	}

	public static String getStringByStatement(String statementName) throws EMFInternalError {
		return getStringByStatement(statementName, null, null);
	}

	public static String getStringByStatement(String statementName, ArrayList params) throws EMFInternalError {
		return getStringByStatement(statementName, params, null);
	}

	public static String getStringByStatement(String statementName, String param) throws EMFInternalError {
		ArrayList params = new ArrayList();
		params.add(param);
		return getStringByStatement(statementName, params, null);
	}

	public static String getStringByStatement(String statementName, ArrayList params, String statement)
			throws EMFInternalError {
		SourceBean sb = getSourceBeanByStatement(statementName, params, statement, null);
		Object result = sb.getAttribute("ROW.RESULT");
		return (result == null) ? "" : result.toString();
	}

	/**
	 * La query viene eseguita in un contesto transazionale
	 * 
	 * @param tex
	 *            e' il TransactionQueryExecutor
	 * @param statementName
	 * @param params
	 * @return
	 * @throws EMFInternalError
	 */
	public static String getStringByStatement(TransactionQueryExecutor tex, String statementName, Object[] params)
			throws EMFInternalError {
		SourceBean sb = (SourceBean) tex.executeQuery(statementName, params, "SELECT");
		Object result = sb.getAttribute("ROW.RESULT");
		return (result == null) ? "" : result.toString();
	}
}

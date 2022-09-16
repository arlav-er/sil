package it.eng.sil.module.profil;

import java.math.BigDecimal;

import com.engiweb.framework.base.SessionContainer;
import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.base.SourceBeanException;
import com.engiweb.framework.dbaccess.DataConnectionManager;
import com.engiweb.framework.dbaccess.Utils;
import com.engiweb.framework.dbaccess.sql.DataConnection;
import com.engiweb.framework.dispatching.module.AbstractModule;
import com.engiweb.framework.error.EMFInternalError;
import com.engiweb.framework.util.QueryExecutor;
import com.engiweb.framework.util.QueryExecutorObjectExt;

import it.eng.afExt.dbaccess.sql.DBKeyGenerator;
import it.eng.afExt.utils.MessageCodes;
import it.eng.afExt.utils.ReportOperationResult;
import it.eng.sil.Values;
import it.eng.sil.bean.SsoBean;
import it.eng.sil.security.Password;
import it.eng.sil.security.User;
import it.eng.sil.util.Sottosistema;

/**
 * 
 * @author Franco Vuoto
 * @version 1.0
 */
public class ProfSalvaUtente extends AbstractModule {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(ProfSalvaUtente.class.getName());
	private String className = this.getClass().getName();

	private static final String sql_seq = "SELECT S_TS_UTENTE.NEXTVAL CDUT FROM DUAL";

	public ProfSalvaUtente() {
	}

	public void service(SourceBean request, SourceBean response) {

		if (Sottosistema.DWH.isOff())
			execServiceNoDwh(request, response);
		else {
			execServiceDwh(request, response);
		}
	}

	private void execServiceDwh(SourceBean request, SourceBean response) {

		String cdutStr = (String) request.getAttribute("cdut");
		if ((cdutStr == null) || cdutStr.equals(""))
			insertUtenteDWH(request, response);
		else
			updateUtenteDWH(request, response);
	}

	private void insertUtenteDWH(SourceBean request, SourceBean response) {

		ReportOperationResult result = new ReportOperationResult(this, response);

		String cdutStr = (String) request.getAttribute("cdut");
		String password = (String) request.getAttribute("password");

		DataConnection connSIL = null;
		// DataConnection connDWH = null;

		try {

			connSIL = DataConnectionManager.getInstance().getConnection(Values.DB_SIL_DATI);
			connSIL.initTransaction();

			// 1. recupero sequence

			QueryExecutorObjectExt qSIL = new QueryExecutorObjectExt();
			qSIL.setRequestContainer(getRequestContainer());
			qSIL.setResponseContainer(getResponseContainer());

			qSIL.setDataConnection(connSIL);
			qSIL.setTransactional(true);

			qSIL.setStatement(sql_seq);
			qSIL.setType(QueryExecutorObjectExt.SELECT);
			SourceBean esitoSB = (SourceBean) qSIL.execExt();

			BigDecimal cdut = (BigDecimal) esitoSB.getAttribute("ROW.CDUT");

			request.updAttribute("cdut", cdut);
			response.setAttribute("cdut", cdut.toString());

			String encPwd = (new Password(password)).getEncValue();
			request.updAttribute("password", encPwd);

			// 2. INSERT in TS_UTENTE
			qSIL = new QueryExecutorObjectExt();
			qSIL.setDataConnection(connSIL);
			qSIL.setTransactional(true);

			qSIL.setRequestContainer(getRequestContainer());
			qSIL.setResponseContainer(getResponseContainer());

			SourceBean statement = (SourceBean) getConfig().getAttribute("QUERIES.INSERT_QUERY");
			qSIL.setQuery(statement);
			qSIL.setType(QueryExecutorObjectExt.INSERT);

			qSIL.execExt();

			connSIL.commitTransaction();
			result.reportSuccess(MessageCodes.General.OPERATION_SUCCESS);

		} catch (Exception esitoEx) {

			/*******************************************************************
			 * ERRORE
			 ******************************************************************/

			String _description = esitoEx.getMessage();
			if (_description.indexOf("IX_AK_TS_UTENTE_UPPER_STRLOGIN") > 0) {

				// decodifica la violazione di AK
				result.reportFailure(MessageCodes.Login.ERR_LOGIN_IN_USE);

				try {
					response.setAttribute("ERR_INSERT", "");
				} catch (SourceBeanException e1) {
					it.eng.sil.util.TraceWrapper.fatal(_logger, className, (Exception) e1);

				}

			} else {
				// altri errori
				result.reportFailure(MessageCodes.General.OPERATION_FAIL);

			}

			if (connSIL != null)
				try {
					connSIL.rollBackTransaction();
				} catch (EMFInternalError e) {
					it.eng.sil.util.TraceWrapper.debug(_logger, className, (Exception) e);

				}

		} finally {
			Utils.releaseResources(connSIL, null, null);

		}

	}

	private void updateUtenteDWH(SourceBean request, SourceBean response) {

		ReportOperationResult result = new ReportOperationResult(this, response);

		String cdutStr = (String) request.getAttribute("cdut");
		String password = (String) request.getAttribute("password");

		DataConnection connSIL = null;
		DataConnection connDWH = null;

		try {

			connSIL = DataConnectionManager.getInstance().getConnection(Values.DB_SIL_DATI);
			connSIL.initTransaction();

			connDWH = DataConnectionManager.getInstance().getConnection(Values.DB_SIL_DWH);
			connDWH.initTransaction();

			// 1. recupero sequence

			QueryExecutorObjectExt qSIL = new QueryExecutorObjectExt();
			qSIL.setDataConnection(connSIL);
			qSIL.setTransactional(true);
			qSIL.setRequestContainer(getRequestContainer());
			qSIL.setResponseContainer(getResponseContainer());

			// QueryExecutorObjectExt qDWH = new QueryExecutorObjectExt();
			// qDWH.setDataConnection(connDWH);
			// qDWH.setTransactional(true);
			// qDWH.setRequestContainer(getRequestContainer());
			// qDWH.setResponseContainer(getResponseContainer());

			if ((password != null) && (!password.equals(""))) {

				// Si richiede di agiornare la password...
				String encPwd = (new Password(password)).getEncValue();
				request.updAttribute("password", encPwd);

				SourceBean statement = (SourceBean) getConfig().getAttribute("QUERIES.UPDATE_QUERY");
				qSIL.setQuery(statement);
				qSIL.setType(QueryExecutorObjectExt.UPDATE);

				qSIL.execExt();

				SsoBean sso = new SsoBean(connSIL, connDWH, new Integer(cdutStr).intValue());
				sso.setMustChangePwdNextLogin(true);
				sso.changePswd(encPwd);

			} else {
				SourceBean statement = (SourceBean) getConfig().getAttribute("QUERIES.UPDATE_QUERY2");
				qSIL.setQuery(statement);
				qSIL.setType(QueryExecutorObjectExt.UPDATE);

				qSIL.execExt();

			}

			connDWH.commitTransaction();
			connSIL.commitTransaction();

			result.reportSuccess(MessageCodes.General.OPERATION_SUCCESS);

		} catch (Exception esitoEx) {

			/*******************************************************************
			 * ERRORE
			 ******************************************************************/

			String _description = esitoEx.getMessage();
			if (_description.indexOf("IX_AK_TS_UTENTE_UPPER_STRLOGIN") > 0) {

				// decodifica la violazione di AK
				result.reportFailure(MessageCodes.Login.ERR_LOGIN_IN_USE);

				try {
					response.setAttribute("ERR_UPDATE", "");
				} catch (SourceBeanException e1) {
					it.eng.sil.util.TraceWrapper.fatal(_logger, className, (Exception) e1);

				}

			} else {
				// altri errori
				result.reportFailure(MessageCodes.General.OPERATION_FAIL);

			}

			if (connSIL != null)
				try {
					connSIL.rollBackTransaction();
				} catch (EMFInternalError e) {
					it.eng.sil.util.TraceWrapper.debug(_logger, className, (Exception) e);

				}

			if (connDWH != null)
				try {
					connDWH.rollBackTransaction();
				} catch (EMFInternalError e) {
					it.eng.sil.util.TraceWrapper.debug(_logger, className, (Exception) e);

				}

		} finally {
			Utils.releaseResources(connSIL, null, null);
			Utils.releaseResources(connDWH, null, null);

		}

	}

	private void execServiceNoDwh(SourceBean request, SourceBean response) {

		String pool = (String) getConfig().getAttribute("POOL");
		SessionContainer sessionContainer = getRequestContainer().getSessionContainer();
		User user = (User) sessionContainer.getAttribute(User.USERID);
		SourceBean statement = null;
		ReportOperationResult result = new ReportOperationResult(this, response);

		String cdutStr = (String) request.getAttribute("cdut");
		String password = (String) request.getAttribute("password");
		Object esito = null;
		boolean isNew = false;

		try {
			if ((cdutStr == null) || cdutStr.equals("")) {

				isNew = true;

				BigDecimal cdut = DBKeyGenerator.getNextSequence(Values.DB_SIL_DATI, DBKeyGenerator.S_TS_UTENTE);

				cdut = cdut.add(new BigDecimal(1));

				request.updAttribute("cdut", cdut);
				response.setAttribute("cdut", cdut.toString());

				String encPwd = (new Password(password)).getEncValue();
				request.updAttribute("password", encPwd);

				statement = (SourceBean) getConfig().getAttribute("QUERIES.INSERT_QUERY");

				esito = QueryExecutor.executeQuery(getRequestContainer(), getResponseContainer(), pool, statement,
						"INSERT", true);

			} else {
				// **** UPDATE*******

				if ((password != null) && (!password.equals(""))) {

					// Si richiede di agiornare la password...
					String encPwd = (new Password(password)).getEncValue();
					request.updAttribute("password", encPwd);

					statement = (SourceBean) getConfig().getAttribute("QUERIES.UPDATE_QUERY");

					esito = QueryExecutor.executeQuery(getRequestContainer(), getResponseContainer(), pool, statement,
							"UPDATE", true);
				} else {
					// La password non va aggiornata

					statement = (SourceBean) getConfig().getAttribute("QUERIES.UPDATE_QUERY2");

					esito = QueryExecutor.executeQuery(getRequestContainer(), getResponseContainer(), pool, statement,
							"UPDATE", true);

				}

			}
		} catch (SourceBeanException e) {
			esito = e;
		}

		if ((esito != null) && (esito instanceof Exception)) {
			/*******************************************************************
			 * ERRORE
			 ******************************************************************/

			Exception esitoEx = (Exception) esito;

			String _description = esitoEx.getMessage();
			if (_description.indexOf("IX_AK_TS_UTENTE_UPPER_STRLOGIN") > 0) {

				// decodifica la violazione di AK
				result.reportFailure(MessageCodes.Login.ERR_LOGIN_IN_USE);

				if (isNew) {
					try {
						response.setAttribute("ERR_INSERT", "");
					} catch (SourceBeanException e1) {
						it.eng.sil.util.TraceWrapper.fatal(_logger, className, (Exception) e1);

					}

				}
			} else {
				// altri errori
				result.reportFailure(MessageCodes.General.OPERATION_FAIL);

			}

		} else if ((esito != null) && (esito instanceof Boolean)) {

			if (((Boolean) esito).booleanValue() == true)
				result.reportSuccess(MessageCodes.General.OPERATION_SUCCESS);
			else
				result.reportFailure(MessageCodes.General.OPERATION_FAIL);
		}

	}
}
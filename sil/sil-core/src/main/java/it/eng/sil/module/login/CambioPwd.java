package it.eng.sil.module.login;

import java.math.BigDecimal;
import java.util.Vector;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.SessionContainer;
import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.base.SourceBeanException;
import com.engiweb.framework.dbaccess.DataConnectionManager;
import com.engiweb.framework.dbaccess.Utils;
import com.engiweb.framework.dbaccess.sql.DataConnection;
import com.engiweb.framework.error.EMFInternalError;
import com.engiweb.framework.util.QueryExecutor;

import it.eng.afExt.utils.MessageCodes;
import it.eng.afExt.utils.ReportOperationResult;
import it.eng.sil.Values;
import it.eng.sil.bean.SsoBean;
import it.eng.sil.module.AbstractSimpleModule;
import it.eng.sil.security.Password;
import it.eng.sil.security.User;
import it.eng.sil.util.Sottosistema;

public class CambioPwd extends AbstractSimpleModule {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(CambioPwd.class.getName());
	private String className = this.getClass().getName();

	public void service(SourceBean request, SourceBean response) {

		RequestContainer requestContainer = getRequestContainer();
		SessionContainer sessionContainer = (SessionContainer) requestContainer.getSessionContainer();

		ReportOperationResult result = new ReportOperationResult(this, response);

		// Password Vecchia, letta dai dati di input della JSP (ne calcolo la
		// versione cifrata)
		String strPwdOld = (String) request.getAttribute("oldPwd");
		Password pwdOld = new Password(strPwdOld);
		String strPwdOldEnc = pwdOld.getEncValue();

		// Password Corrente (vecchia), letta dal DB dato user+pwdOld (NB: la
		// pwd è cifrata)
		String strPwdCurEnc = (String) request.getAttribute("PASSWORD");

		String strPwd = (String) request.getAttribute("newPwd");
		Password pwd = new Password(strPwd);
		String newPwdCrypt = pwd.getEncValue();
		boolean mustReturn = false;

		SourceBean sbListaStoricoPassword = (SourceBean) QueryExecutor.executeQuery("QUERY_STORICO_PASSWORD",
				new Object[] { sessionContainer.getAttribute("_CDUT_") }, "SELECT", Values.DB_SIL_DATI);
		Vector elencoStoricoPwd = sbListaStoricoPassword.getAttributeAsVector("ROW");
		// Confronto la vecchia password con la nuova (entrambe nelle versioni
		// cifrate)
		// Se non è uguale, non posso procedere al cambio password
		if ((strPwdCurEnc != null) && !strPwdCurEnc.equals(strPwdOldEnc)) {
			result.reportFailure(MessageCodes.Login.ERR_WRONG_OLD_PWD);
			return; // RITORNO SUBITO
		}

		// La nuova password non dev'essere uguale alla vecchia.
		if ((strPwdOld == null) || strPwd.equals(strPwdOld)) {
			result.reportFailure(MessageCodes.Login.ERR_PWD_NOT_DIFFERENT);
			return; // RITORNO SUBITO
		}

		// controllo che non deve essere uguale alle ultime 3 salvate in TS_UTENTE_PASSWORD
		// si controlla la pwd cifrata
		for (int i = 0; i < elencoStoricoPwd.size(); i++) {
			SourceBean sbStoricoPwd = (SourceBean) elencoStoricoPwd.get(i);
			String strStoricoPwd = (String) sbStoricoPwd.getAttribute("STRPASSWORD");
			if ((strStoricoPwd != null) && newPwdCrypt.equals(strStoricoPwd)) {
				result.reportFailure(MessageCodes.Login.ERR_PWD_NOT_DIFFERENT_STORICO);
				return; // RITORNO SUBITO
			}

			// si controllano al max 2 pwd storicizzate
			if (i == 1) {
				break;
			}
		}

		if (!pwd.isEnoughLong()) {
			result.reportFailure(MessageCodes.Login.ERR_PWD_TOO_SHORT);
			mustReturn = true;
		}

		if (!pwd.hasAltenateCase()) {
			result.reportFailure(MessageCodes.Login.ERR_PWD_CASE_ALT);
			mustReturn = true;

		}

		if (!pwd.hasDigits()) {
			result.reportFailure(MessageCodes.Login.ERR_PWD_WITHOUT_DIGITS);
			mustReturn = true;
		}

		if (mustReturn)
			return;
		// }
		// La password soddisfa i requisiti per la
		// sua modificabilità

		try {
			request.updAttribute("newPwd", pwd.getEncValue());

		} catch (SourceBeanException e) {

			it.eng.sil.util.TraceWrapper.debug(_logger, className, e);

			result.reportFailure(MessageCodes.General.OPERATION_FAIL);
			return;
		}

		// Inizio cambiamento PWD

		DataConnection connSIL = null;
		DataConnection connDWH = null;

		BigDecimal cdutBD = (BigDecimal) sessionContainer.getAttribute("_CDUT_");
		int cdut = cdutBD.intValue();
		String username = (String) request.getAttribute("username");
		String nome = (String) request.getAttribute("nome");
		String cognome = (String) request.getAttribute("cognome");

		User user;

		try {
			connSIL = DataConnectionManager.getInstance().getConnection(Values.DB_SIL_DATI);

			if (Sottosistema.DWH.isOn()) {
				connDWH = DataConnectionManager.getInstance().getConnection(Values.DB_SIL_DWH);

			}
			SsoBean sso = new SsoBean(connSIL, connDWH, cdut);
			sso.changePswd(pwd.getEncValue());

			user = new User(cdut, username, nome, cognome);
			sessionContainer.setAttribute(User.USERID, user);
			response.setAttribute("UPDATE_OK", "true");
			result.reportSuccess(MessageCodes.General.OPERATION_SUCCESS);

		} catch (Exception e2) {

			it.eng.sil.util.TraceWrapper.fatal(_logger, "CambioPwd: errore durante il cambio di password", e2);

			if (connSIL != null)
				try {
					connSIL.rollBackTransaction();
				} catch (EMFInternalError e1) {

					it.eng.sil.util.TraceWrapper.fatal(_logger, "CambioPwd: errore durante il rollback dal DB del SIL",
							(Exception) e1);

				}

			if (connDWH != null)
				try {
					connDWH.rollBackTransaction();
				} catch (EMFInternalError e1) {

					it.eng.sil.util.TraceWrapper.fatal(_logger, "CambioPwd: errore durante il rollback dal DB del DWH",
							(Exception) e1);

				}

			// Errore nella JSP
			result.reportFailure(MessageCodes.General.OPERATION_FAIL);

		} finally {

			Utils.releaseResources(connSIL, null, null);
			Utils.releaseResources(connDWH, null, null);

		}

	}

}
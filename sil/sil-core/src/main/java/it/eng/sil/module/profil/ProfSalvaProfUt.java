package it.eng.sil.module.profil;

import java.math.BigDecimal;

import com.engiweb.framework.base.SessionContainer;
import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.base.SourceBeanException;
import com.engiweb.framework.dispatching.module.AbstractModule;
import com.engiweb.framework.util.QueryExecutor;

import it.eng.afExt.dbaccess.sql.DBKeyGenerator;
import it.eng.afExt.utils.MessageCodes;
import it.eng.afExt.utils.ReportOperationResult;
import it.eng.sil.Values;
import it.eng.sil.security.User;

/**
 * 
 * @author Franco Vuoto
 * @version 1.0
 */
public class ProfSalvaProfUt extends AbstractModule {
	private String className = this.getClass().getName();

	public ProfSalvaProfUt() {
	}

	public void service(SourceBean request, SourceBean response) {
		String pool = (String) getConfig().getAttribute("POOL");
		SessionContainer sessionContainer = getRequestContainer().getSessionContainer();
		User user = (User) sessionContainer.getAttribute(User.USERID);
		SourceBean statement = null;
		Boolean esito = null;
		ReportOperationResult result = new ReportOperationResult(this, response);

		try {

			BigDecimal prgProfUt = DBKeyGenerator.getNextSequence(Values.DB_SIL_DATI, "S_TS_PROFILATURA_UTENTE");

			prgProfUt = prgProfUt.add(new BigDecimal(1));

			request.updAttribute("prgProfUt", prgProfUt);

			statement = (SourceBean) getConfig().getAttribute("QUERIES.INSERT_QUERY");

			esito = (Boolean) QueryExecutor.executeQuery(getRequestContainer(), getResponseContainer(), pool, statement,
					"INSERT");
		} catch (SourceBeanException e) {
			esito = new Boolean(false);
		}

		if ((esito != null) && (esito.booleanValue() == true)) {
			result.reportSuccess(MessageCodes.General.OPERATION_SUCCESS);
		} else {
			result.reportFailure(MessageCodes.General.OPERATION_FAIL);
		}
	}
}

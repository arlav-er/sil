package it.eng.sil.module.profil;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.dispatching.module.AbstractModule;
import com.engiweb.framework.util.QueryExecutor;

import it.eng.afExt.utils.MessageCodes;
import it.eng.afExt.utils.ReportOperationResult;

/**
 * 
 * @author Franco Vuoto
 * @version 1.0
 */
public class ProfCancellaProfUt extends AbstractModule {
	private String className = this.getClass().getName();

	public ProfCancellaProfUt() {
	}

	public void service(SourceBean request, SourceBean response) {
		String pool = (String) getConfig().getAttribute("POOL");
		// SessionContainer sessionContainer =
		// getRequestContainer().getSessionContainer();
		// User user = (User) sessionContainer.getAttribute(User.USERID);
		SourceBean statement = null;
		Boolean esito = null;
		ReportOperationResult result = new ReportOperationResult(this, response);

		statement = (SourceBean) getConfig().getAttribute("QUERIES.DELETE_QUERY");

		esito = (Boolean) QueryExecutor.executeQuery(getRequestContainer(), getResponseContainer(), pool, statement,
				"DELETE");

		if ((esito != null) && (esito.booleanValue() == true)) {
			result.reportSuccess(MessageCodes.General.OPERATION_SUCCESS);
		} else {
			result.reportFailure(MessageCodes.General.OPERATION_FAIL);
		}
	}
}

package it.eng.sil.module.presel;

import com.engiweb.framework.base.SessionContainer;
import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.dispatching.module.AbstractModule;
import com.engiweb.framework.util.QueryExecutor;

import it.eng.afExt.utils.MessageCodes;
import it.eng.afExt.utils.ReportOperationResult;
import it.eng.sil.security.User;

// @author: Stefania Orioli

public class MSalvaEvidenza extends AbstractModule {
	private String className = this.getClass().getName();

	public MSalvaEvidenza() {
	}

	public void service(SourceBean request, SourceBean response) {
		String pool = (String) getConfig().getAttribute("POOL");
		SessionContainer sessionContainer = getRequestContainer().getSessionContainer();
		User user = (User) sessionContainer.getAttribute(User.USERID);
		SourceBean statement = null;
		Boolean esito = null;
		ReportOperationResult result = new ReportOperationResult(this, response);

		String prgEvidenza = (String) request.getAttribute("prgEvidenza");
		String tipoOp = "";

		try {
			if ((prgEvidenza == null) || (prgEvidenza.equals(""))) {
				// INSERT
				statement = (SourceBean) getConfig().getAttribute("QUERIES.INSERT_QUERY");
				tipoOp = "INSERT";
			} else {
				// UPDATE
				statement = (SourceBean) getConfig().getAttribute("QUERIES.UPDATE_QUERY");
				tipoOp = "UPDATE";
			}
			esito = (Boolean) QueryExecutor.executeQuery(getRequestContainer(), getResponseContainer(), pool, statement,
					tipoOp);

		} catch (Exception e) {
			esito = new Boolean(false);
		}

		if ((esito != null) && (esito.booleanValue() == true)) {
			if (tipoOp.equals("UPDATE")) {
				result.reportSuccess(MessageCodes.General.UPDATE_SUCCESS);
			} else {
				result.reportSuccess(MessageCodes.General.INSERT_SUCCESS);
			}
		} else {
			if (tipoOp.equals("UPDATE")) {
				result.reportFailure(MessageCodes.General.UPDATE_FAIL);
			} else {
				result.reportFailure(MessageCodes.General.INSERT_FAIL);
			}
		}
	}
}

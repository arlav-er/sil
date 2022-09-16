package it.eng.sil.module.profil;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.dispatching.module.AbstractModule;
import com.engiweb.framework.util.QueryExecutor;

import it.eng.afExt.utils.MessageCodes;
import it.eng.afExt.utils.ReportOperationResult;

public class ProfSbloccaUtente extends AbstractModule {

	public ProfSbloccaUtente() {
	}

	public void service(SourceBean request, SourceBean response) {
		String pool = (String) getConfig().getAttribute("POOL");
		SourceBean statement = null;
		Boolean esito = null;
		ReportOperationResult result = new ReportOperationResult(this, response);

		statement = (SourceBean) getConfig().getAttribute("QUERIES.UPDATE_QUERY");

		esito = (Boolean) QueryExecutor.executeQuery(getRequestContainer(), getResponseContainer(), pool, statement,
				"UPDATE");

		if ((esito != null) && (esito.booleanValue() == true)) {
			result.reportSuccess(MessageCodes.General.OPERATION_SUCCESS);
		} else {
			result.reportFailure(MessageCodes.General.OPERATION_FAIL);
		}
	}
}

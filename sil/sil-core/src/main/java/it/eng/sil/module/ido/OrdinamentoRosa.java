package it.eng.sil.module.ido;

import com.engiweb.framework.base.SessionContainer;
import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.dispatching.module.AbstractModule;
import com.engiweb.framework.util.QueryExecutor;

import it.eng.afExt.utils.MessageCodes;
import it.eng.afExt.utils.ReportOperationResult;

// @author: Stefania Orioli

public class OrdinamentoRosa extends AbstractModule {
	public OrdinamentoRosa() {
	}

	private String className = this.getClass().getName();

	public void service(SourceBean request, SourceBean response) {
		String pool = (String) getConfig().getAttribute("POOL");
		SessionContainer sessionContainer = getRequestContainer().getSessionContainer();
		SourceBean statement = null;
		Boolean esito = null;
		ReportOperationResult result = new ReportOperationResult(this, response);

		try {
			statement = (SourceBean) getConfig().getAttribute("QUERIES.QUERY");
			esito = (Boolean) QueryExecutor.executeQuery(getRequestContainer(), getResponseContainer(), pool, statement,
					"UPDATE");
			if ((esito != null) && (esito.booleanValue() == true)) {
				result.reportSuccess(MessageCodes.General.UPDATE_SUCCESS);
			} else {
				result.reportFailure(MessageCodes.General.UPDATE_FAIL);
			}
		} catch (Exception e) {
			result.reportFailure(MessageCodes.General.UPDATE_FAIL);
		}
	}
}

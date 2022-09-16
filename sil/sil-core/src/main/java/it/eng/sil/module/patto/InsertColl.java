package it.eng.sil.module.patto;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.dispatching.module.AbstractModule;
import com.engiweb.framework.util.QueryExecutor;

import it.eng.afExt.utils.MessageCodes;
import it.eng.afExt.utils.ReportOperationResult;

public class InsertColl extends AbstractModule {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(InsertColl.class.getName());

	private String className = this.getClass().getName();

	public void service(SourceBean Request, SourceBean Response) throws Exception {
		String pool = (String) getConfig().getAttribute("POOL");
		SourceBean statement = (SourceBean) getConfig().getAttribute("QUERY");
		ReportOperationResult result = new ReportOperationResult(this, Response);

		Boolean esito = (Boolean) QueryExecutor.executeQuery(getRequestContainer(), getResponseContainer(), pool,
				statement, "INSERT");

		it.eng.sil.util.TraceWrapper.debug(_logger, className + "::INSERT: request", Request);

		if ((esito != null) && (esito.booleanValue() == true)) {
			result.reportSuccess(MessageCodes.General.INSERT_SUCCESS);
		} else {
			result.reportFailure(MessageCodes.General.INSERT_FAIL);
		}

	}// end service

}// InsertColl

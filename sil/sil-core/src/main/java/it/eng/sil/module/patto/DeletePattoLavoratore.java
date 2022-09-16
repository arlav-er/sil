package it.eng.sil.module.patto;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.dispatching.module.AbstractModule;
import com.engiweb.framework.error.EMFErrorHandler;
import com.engiweb.framework.error.EMFInternalError;
import com.engiweb.framework.error.EMFUserError;

import it.eng.afExt.utils.TransactionQueryExecutor;

/**
 * Cancella una riga dal db, o meglio esegue una istruzione di delete standard.
 */
public class DeletePattoLavoratore extends AbstractModule {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(DeletePattoLavoratore.class.getName());
	/*
	 * Nome della classe
	 */
	private String className = this.getClass().getName();

	public void service(SourceBean request, SourceBean response) {
		int i = 0;

		EMFErrorHandler engErrorHandler = getErrorHandler();
		TransactionQueryExecutor queryExecutor = null;

		try {
			String pool = (String) getConfig().getAttribute("POOL");
			SourceBean statement = (SourceBean) getConfig().getAttribute("QUERYES.DELETE_PATTO_LAV");
			SourceBean statement1 = (SourceBean) getConfig().getAttribute("QUERYES.DELETE_LAV_PATTO_SCELTA");
			queryExecutor = new TransactionQueryExecutor(pool);
			queryExecutor.initTransaction();

			Boolean esito = (Boolean) queryExecutor.executeQuery(getRequestContainer(), getResponseContainer(),
					statement, "DELETE");

			if (esito.booleanValue()) {
				esito = (Boolean) queryExecutor.executeQuery(getRequestContainer(), getResponseContainer(), statement1,
						"DELETE");
			} else {
				throw new Exception("fallita delete: " + statement);
			}
			if (esito.booleanValue()) {
				queryExecutor.commitTransaction();
			} else {
				throw new Exception("fallita delete: " + statement1);
			}
			//
			queryExecutor.commitTransaction();
			//
			it.eng.sil.util.TraceWrapper.debug(_logger, className + "::delete: request", request);

		} catch (Exception e) {
			if (queryExecutor != null) {
				try {
					queryExecutor.rollBackTransaction();
				} catch (EMFInternalError ie) {
					it.eng.sil.util.TraceWrapper.debug(_logger, className + "::rollback:", (Exception) ie);

				}
			}
			it.eng.sil.util.TraceWrapper.debug(_logger, className + "::delete:", e);

			engErrorHandler.addError((EMFUserError) e);
		}
	}
}
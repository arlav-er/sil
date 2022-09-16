package it.eng.sil.module.agenda;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.dispatching.module.AbstractModule;
import com.engiweb.framework.error.EMFErrorHandler;
import com.engiweb.framework.error.EMFErrorSeverity;
import com.engiweb.framework.error.EMFUserError;
import com.engiweb.framework.util.QueryExecutor;

import it.eng.afExt.utils.MessageCodes;

public class InsertAgendaModule extends AbstractModule {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(InsertAgendaModule.class.getName());

	public InsertAgendaModule() {
	}

	private String className = this.getClass().getName();

	public void service(SourceBean request, SourceBean response) {
		boolean checkOK = true;

		EMFErrorHandler engErrorHandler = getErrorHandler();

		if (checkOK) {
			try {
				String pool = (String) getConfig().getAttribute("POOL");
				SourceBean statement = (SourceBean) getConfig().getAttribute("QUERIES.INSERT_QUERY");

				Boolean esito = (Boolean) QueryExecutor.executeQuery(getRequestContainer(), getResponseContainer(),
						pool, statement, "INSERT");

				it.eng.sil.util.TraceWrapper.debug(_logger, className + "::insert: request", request);

				Object rowObject = null;

				if (!esito.booleanValue()) {
					_logger.debug(className + "::insert: nessuna riga inserita");

					// engErrorHandler.addError(new
					// EMFUserError(EMFErrorSeverity.WARNING, 10006));
					engErrorHandler
							.addError(new EMFUserError(EMFErrorSeverity.ERROR, MessageCodes.General.OPERATION_FAIL));
					// MessageAppender.appendMessage(response,ErrorCodes.ERRORE_QUERY);
					return;
				} // if (rowObject == null)

			} catch (Exception e) {
				it.eng.sil.util.TraceWrapper.error(_logger, className + "::insert:", e);

				engErrorHandler.addError((EMFUserError) e);
				// MessageAppender.appendMessage(response,ErrorCodes.ERRORE_MODULO);
			}
		} // fine di if (checkOK)
		/*
		 * else{ engErrorHandler.addError(new EMFUserError(EMFErrorSeverity.ERROR, ErrorCodes.ERRORE_QUERY));
		 * /*restoreFieldsFromRequest(request, response); try{ response.setAttribute("ESITO","ERRORE CONTROLLO CAMPI");
		 * engErrorHandler.addError(new EMFUserError(EMFErrorSeverity.ERROR, 10006)); } catch(SourceBeanException Sbe){
		 * it.eng.sil.util.TraceWrapper.error( _logger,className + "::insert:", Sbe); } } //fine di else a if (checkOK)
		 */
	}
}
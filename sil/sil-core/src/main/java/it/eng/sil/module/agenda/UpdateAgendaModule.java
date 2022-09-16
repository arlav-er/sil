package it.eng.sil.module.agenda;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.dispatching.module.AbstractModule;
import com.engiweb.framework.error.EMFErrorHandler;
import com.engiweb.framework.error.EMFErrorSeverity;
import com.engiweb.framework.error.EMFUserError;
import com.engiweb.framework.util.QueryExecutor;

import it.eng.afExt.utils.MessageCodes;

public class UpdateAgendaModule extends AbstractModule {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(UpdateAgendaModule.class.getName());

	public UpdateAgendaModule() {
	}

	private String className = this.getClass().getName();

	public void service(SourceBean request, SourceBean response) {
		EMFErrorHandler engErrorHandler = getErrorHandler();

		try {
			String pool = (String) getConfig().getAttribute("POOL");
			SourceBean statement = (SourceBean) getConfig().getAttribute("QUERIES.UPDATE_QUERY");

			Boolean esito = (Boolean) QueryExecutor.executeQuery(getRequestContainer(), getResponseContainer(), pool,
					statement, "UPDATE");

			it.eng.sil.util.TraceWrapper.debug(_logger, className + "::update: request", request);

			Object rowObject = null;

			if (!esito.booleanValue()) {
				_logger.debug(className + "::update: nessuna riga inserita");

				engErrorHandler.addError(new EMFUserError(EMFErrorSeverity.ERROR, MessageCodes.General.OPERATION_FAIL));
				return;
			} // if (rowObject == null)
		} catch (Exception e) {
			it.eng.sil.util.TraceWrapper.error(_logger, className + "::update:", e);

			engErrorHandler.addError((EMFUserError) e);
			// MessageAppender.appendMessage(response,ErrorCodes.ERRORE_MODULO);
		}
	}
}
package it.eng.sil.module.agenda;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.dbaccess.sql.DataRow;
import com.engiweb.framework.dispatching.module.AbstractModule;
import com.engiweb.framework.error.EMFErrorHandler;
import com.engiweb.framework.error.EMFErrorSeverity;
import com.engiweb.framework.error.EMFUserError;
import com.engiweb.framework.util.QueryExecutor;

import it.eng.afExt.utils.MessageCodes;
import it.eng.afExt.utils.ReportOperationResult;

public class InsertAgendaLavoratoreModule extends AbstractModule {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger
			.getLogger(InsertAgendaLavoratoreModule.class.getName());

	public InsertAgendaLavoratoreModule() {
	}

	private String className = this.getClass().getName();

	public void service(SourceBean request, SourceBean response) {
		EMFErrorHandler engErrorHandler = getErrorHandler();
		ReportOperationResult result = new ReportOperationResult(this, response);

		try {
			String pool = (String) getConfig().getAttribute("POOL");
			SourceBean statement = (SourceBean) getConfig().getAttribute("QUERIES.SELECT_QUERY");
			SourceBean rowsSourceBean = (SourceBean) QueryExecutor.executeQuery(getRequestContainer(),
					getResponseContainer(), pool, statement, "SELECT");
			SourceBean row = null;
			String nro = "0";
			if (rowsSourceBean != null) {
				row = (SourceBean) rowsSourceBean.getAttribute(DataRow.ROW_TAG);
				nro = row.getAttribute("NRO").toString();
			}
			statement = null;
			if (nro != null && nro.equals("0")) {

				statement = (SourceBean) getConfig().getAttribute("QUERIES.INSERT_QUERY");
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
					return;
				} // if (rowObject == null)

			} else {
				_logger.debug(className + "::insert: nessuna riga inserita");

				engErrorHandler.addError(new EMFUserError(EMFErrorSeverity.ERROR, MessageCodes.General.OPERATION_FAIL));
				result.reportFailure(MessageCodes.General.ELEMENT_DUPLICATED);
			}

		} catch (Exception e) {
			it.eng.sil.util.TraceWrapper.error(_logger, className + "::insert:", e);

			engErrorHandler.addError((EMFUserError) e);
			// MessageAppender.appendMessage(response,ErrorCodes.ERRORE_MODULO);
			result.reportFailure(MessageCodes.General.OPERATION_SUCCESS);
		}
	}
}
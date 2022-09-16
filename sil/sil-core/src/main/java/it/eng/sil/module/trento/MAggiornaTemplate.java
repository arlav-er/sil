package it.eng.sil.module.trento;

import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.utils.MessageCodes;
import it.eng.afExt.utils.ReportOperationResult;
import it.eng.afExt.utils.TransactionQueryExecutor;
import it.eng.sil.module.AbstractSimpleModule;

public class MAggiornaTemplate extends AbstractSimpleModule {
	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(MAggiornaTemplate.class.getName());

	public MAggiornaTemplate() {
	}

	public void service(SourceBean request, SourceBean response) throws Exception {
		ReportOperationResult reportOperation = new ReportOperationResult(this, response);

		// Segnalazione soli errori/problemi
		int idSuccess = this.disableMessageIdSuccess();
		int idFail = this.disableMessageIdFail();
		int errorCode = MessageCodes.General.UPDATE_FAIL;

		TransactionQueryExecutor trans = null;

		try {
			trans = new TransactionQueryExecutor(getPool());
			this.enableTransactions(trans);
			trans.initTransaction();

			// Dopo ogni operazione raccoglie il risultato
			boolean result = true;

			setSectionQueryUpdate("QUERY_UPDATE_CONFIG_TEMPLATE");
			result = doUpdate(request, response);
			if (!result) {
				trans.rollBackTransaction();
				response.setAttribute("aggiornato", "false");
				reportOperation.reportFailure(errorCode);
				return;
			}

			setSectionQueryUpdate("QUERY_UPDATE_TEMPLATE");
			result = doUpdate(request, response);
			if (!result) {
				trans.rollBackTransaction();
				response.setAttribute("aggiornato", "false");
				reportOperation.reportFailure(errorCode);
				return;
			}

			trans.commitTransaction();
			response.setAttribute("aggiornato", "true");
			reportOperation.reportSuccess(MessageCodes.General.OPERATION_SUCCESS);
		} catch (Throwable ex) {
			if (trans != null) {
				trans.rollBackTransaction();
			}
			it.eng.sil.util.TraceWrapper.debug(_logger, "MAggiornaTemplate.service()", ex);
			response.setAttribute("aggiornato", "false");
			reportOperation.reportFailure(errorCode);
		}
	}

}

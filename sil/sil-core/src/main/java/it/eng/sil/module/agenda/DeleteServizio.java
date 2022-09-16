package it.eng.sil.module.agenda;

import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.utils.MessageCodes;
import it.eng.afExt.utils.ReportOperationResult;
import it.eng.afExt.utils.TransactionQueryExecutor;
import it.eng.sil.module.AbstractSimpleModule;

public class DeleteServizio extends AbstractSimpleModule {
	public DeleteServizio() {
	}

	private String className = this.getClass().getName();

	public void service(SourceBean request, SourceBean response) throws Exception {
		ReportOperationResult reportOperation = new ReportOperationResult(this, response);

		// Segnalazione soli errori/problemi
		int idSuccess = this.disableMessageIdSuccess();
		int idFail = this.disableMessageIdFail();

		// Transazione per DE_SERVIZI, MA_SERVIZIO_PRESTAZIONE, MA_SERVIZIO_TIPOATTIVITA
		TransactionQueryExecutor trans = new TransactionQueryExecutor(getPool());
		this.enableTransactions(trans);
		trans.initTransaction();

		// Dopo ogni operazione raccoglie il risultato
		boolean result = true;

		// Esecuzione della delete su MA_SERVIZIO_PRESTAZIONE

		setSectionQueryDelete("QUERY_DELETE_MA_SERVIZIO_PRESTAZIONE");
		result = doDelete(request, response);

		if (!result) {
			setMessageIdFail(MessageCodes.General.DELETE_FAILED_FK);
			trans.rollBackTransaction();
			return;
		}

		// Esecuzione della delete su MA_SERVIZIO_TIPOATTIVITA

		setSectionQueryDelete("QUERY_DELETE_MA_SERVIZIO_TIPOATTIVITA");
		result = doDelete(request, response);

		if (!result) {
			setMessageIdFail(MessageCodes.General.DELETE_FAILED_FK);
			trans.rollBackTransaction();
			return;
		}

		// Esecuzione della delete su DE_SERVIZIO
		setSectionQueryDelete("QUERY_DELETE_SERVIZIO");
		result = doDelete(request, response);

		if (!result) {
			trans.rollBackTransaction();
			response.setAttribute("cancellato", "false");
			reportOperation.reportFailure(MessageCodes.General.DELETE_FAILED_FK);
		} else {
			trans.commitTransaction();
			response.setAttribute("cancellato", "true");
			reportOperation.reportFailure(MessageCodes.General.OPERATION_SUCCESS);
		}

	}
}
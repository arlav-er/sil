package it.eng.sil.module.orientamento;

import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.utils.MessageCodes;
import it.eng.afExt.utils.ReportOperationResult;
import it.eng.afExt.utils.TransactionQueryExecutor;
import it.eng.sil.module.AbstractSimpleModule;

public class DeleteProgramma extends AbstractSimpleModule {
	public void service(SourceBean request, SourceBean response) throws Exception {
		boolean ret = false;
		int idSuccess = this.disableMessageIdSuccess();
		int idFail = this.disableMessageIdFail();
		TransactionQueryExecutor transExec = null;
		ReportOperationResult reportOperation = new ReportOperationResult(this, response);

		try {
			transExec = new TransactionQueryExecutor(getPool());
			this.enableTransactions(transExec);

			transExec.initTransaction();

			this.setSectionQueryDelete("QUERYDELETE");
			ret = doDelete(request, response);

			if (!ret) {
				throw new Exception("Impossibile Eliminare il programma. Esiste un corso associato.");
			}

			transExec.commitTransaction();
			reportOperation.reportSuccess(MessageCodes.General.DELETE_SUCCESS);

		} catch (Exception e) {
			transExec.rollBackTransaction();
			reportOperation.reportFailure(MessageCodes.Programmi.ESISTECORSO, e, "services()", "delete in transazione");
		} finally {
		}
	}
}

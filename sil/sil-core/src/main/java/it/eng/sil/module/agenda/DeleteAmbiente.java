package it.eng.sil.module.agenda;

import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.utils.MessageCodes;
import it.eng.afExt.utils.ReportOperationResult;
import it.eng.sil.module.AbstractSimpleModule;

public class DeleteAmbiente extends AbstractSimpleModule {
	public DeleteAmbiente() {
	}

	private String className = this.getClass().getName();

	public void service(SourceBean request, SourceBean response) {
		ReportOperationResult reportOperation = new ReportOperationResult(this, response);
		int idSuccess = this.disableMessageIdSuccess();
		int idFail = this.disableMessageIdFail();

		boolean bRis = doDelete(request, response);
		if (!bRis) {
			reportOperation.reportFailure(MessageCodes.General.DELETE_FAILED_FK);
		} else {
			reportOperation.reportFailure(MessageCodes.General.OPERATION_SUCCESS);
		}
	}
}
package it.eng.sil.module.profil;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.error.EMFInternalError;

import it.eng.afExt.utils.MessageCodes;
import it.eng.afExt.utils.ReportOperationResult;
import it.eng.afExt.utils.TransactionQueryExecutor;
import it.eng.sil.module.AbstractSimpleModule;

public class ProfInsertNewDelega extends AbstractSimpleModule {

	@Override
	public void service(SourceBean serviceRequest, SourceBean serviceResponse) throws EMFInternalError {
		System.out.println("----> " + this.getClass().getSimpleName() + " VIENE INVOCATO");
		RequestContainer requestContainer = getRequestContainer();
		ReportOperationResult reportOperation = new ReportOperationResult(this, serviceResponse);
		TransactionQueryExecutor transExec = null;
		SourceBean ret = null;
		boolean ret2 = false;
		int idSuccess = this.disableMessageIdSuccess();
		int idFail = this.disableMessageIdFail();
		try {
			transExec = new TransactionQueryExecutor(getPool());
			transExec.initTransaction();
			ret = super.doSelect(serviceRequest, serviceResponse);
			if (ret != null) {

				// prelevo il NEXTVAL dal sourcebean
				// e lo inserisco in request
				if (ret.containsAttribute("ROW.DO_NEXTVAL")) {
					Object prgStatoOcc = ret.getAttribute("ROW.DO_NEXTVAL");
					serviceRequest.setAttribute("CDNDELEGA", prgStatoOcc);
					requestContainer.setAttribute("CDNDELEGA", prgStatoOcc);

				} else {
					throw new Exception("Modulo di nextval ha fallito.");
				}

				ret2 = super.doInsert(serviceRequest, serviceResponse);
			}
			if (ret != null && ret2) {
				transExec.commitTransaction();
				this.setMessageIdSuccess(idSuccess);
				this.setMessageIdFail(idFail);
				reportOperation.reportSuccess(idSuccess);
			} else {
				throw new Exception();
			}

		} catch (Exception e) {
			// this.setMessageIdFail(MessageCodes.General.DELETE_FAIL);
			reportOperation.reportFailure(MessageCodes.General.INSERT_FAIL);
			if (transExec != null)
				transExec.rollBackTransaction();
		}
	}

}

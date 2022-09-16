package it.eng.sil.module.delega;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.error.EMFInternalError;

import it.eng.afExt.utils.MessageCodes;
import it.eng.afExt.utils.ReportOperationResult;
import it.eng.afExt.utils.TransactionQueryExecutor;
import it.eng.sil.module.AbstractSimpleModule;

public class ModuleDeleteDelega extends AbstractSimpleModule {

	@Override
	public void service(SourceBean serviceRequest, SourceBean serviceResponse) throws EMFInternalError {

		ReportOperationResult reportOperation = new ReportOperationResult(this, serviceResponse);
		TransactionQueryExecutor transExec = null;
		boolean ret = false;
		boolean ret2 = false;
		int idSuccess = this.disableMessageIdSuccess();
		int idFail = this.disableMessageIdFail();
		try {
			transExec = new TransactionQueryExecutor(getPool());
			transExec.initTransaction();
			ret = super.doDelete(serviceRequest, serviceResponse);
			if (ret)
				ret2 = super.doUpdate(serviceRequest, serviceResponse);
			if (ret && ret2) {
				transExec.commitTransaction();
				this.setMessageIdSuccess(idSuccess);
				this.setMessageIdFail(idFail);
				reportOperation.reportSuccess(idSuccess);
			} else {
				throw new Exception();
			}

		} catch (Exception e) {
			reportOperation.reportFailure(MessageCodes.General.DELETE_FAIL);
			if (transExec != null)
				transExec.rollBackTransaction();
		}
	}

}

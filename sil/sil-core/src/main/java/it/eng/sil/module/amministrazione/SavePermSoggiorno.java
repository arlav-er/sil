package it.eng.sil.module.amministrazione;

import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.utils.MessageCodes;
import it.eng.afExt.utils.ReportOperationResult;
import it.eng.afExt.utils.TransactionQueryExecutor;
import it.eng.sil.module.AbstractSimpleModule;
import it.eng.sil.module.patto.PattoManager;

public class SavePermSoggiorno extends AbstractSimpleModule {

	public void service(SourceBean request, SourceBean response) throws Exception {
		boolean ret = false;

		// this.setSectionQueryUpdate();

		if (PattoManager.withPatto(request)) {
			ReportOperationResult reportOperation = new ReportOperationResult(this, response);
			TransactionQueryExecutor transExec = null;
			int idSuccess = this.disableMessageIdSuccess();
			int idFail = this.disableMessageIdFail();
			try {
				transExec = new TransactionQueryExecutor(getPool());
				PattoManager patto = new PattoManager(this, transExec);
				transExec.initTransaction();
				// this.setSectionQueryUpdate("UPDATE_IND_T");
				ret = this.doUpdate(request, response);

				if (ret) {
					ret = patto.execute(request, response);
				} else {
					throw new Exception("");
				}
				//
				if (ret) {
					transExec.commitTransaction();
					this.setMessageIdSuccess(idSuccess);
					this.setMessageIdFail(idFail);
					reportOperation.reportSuccess(idSuccess);
				} else {
					throw new Exception();
				}
			} catch (Exception e) {
				// this.setMessageIdFail(MessageCodes.General.UPDATE_FAIL);
				reportOperation.reportFailure(MessageCodes.General.UPDATE_FAIL);
				if (transExec != null)
					transExec.rollBackTransaction();
			}
		} else {
			// setSectionQueryUpdate("UPDATE_IND_T");
			doUpdate(request, response);
		}
	}
	// end service
}

// end class SaveIndispTemp

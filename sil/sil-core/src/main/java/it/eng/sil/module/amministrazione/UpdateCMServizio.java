package it.eng.sil.module.amministrazione;

import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.utils.MessageCodes;
import it.eng.afExt.utils.ReportOperationResult;
import it.eng.afExt.utils.TransactionQueryExecutor;
import it.eng.sil.module.AbstractSimpleModule;

public class UpdateCMServizio extends AbstractSimpleModule {

	private static final long serialVersionUID = 6713425996290265118L;

	public void service(SourceBean request, SourceBean response) throws Exception {

		boolean ret = false;

		TransactionQueryExecutor transExec = null;
		ReportOperationResult reportOperation = new ReportOperationResult(this, response);

		setMessageIdSuccess(MessageCodes.General.UPDATE_SUCCESS);

		try {

			transExec = new TransactionQueryExecutor(getPool());
			this.enableTransactions(transExec);
			transExec.initTransaction();

			this.setSectionQueryUpdate("QUERY_UPDATE_SERVIZI");
			ret = doUpdate(request, response);

			if (!ret) {
				throw new Exception("impossibile aggiornare in CM_ASS_LAV_SERVIZIO in transazione");
			}

			transExec.commitTransaction();

			// viene già fatto da doUpdate
			// reportOperation.reportSuccess(MessageCodes.General.UPDATE_SUCCESS);

		} catch (Exception e) {
			transExec.rollBackTransaction();
			// this.setMessageIdFail(MessageCodes.General.INSERT_FAIL);
			reportOperation.reportFailure(MessageCodes.General.INSERT_FAIL, e, "services()", "insert in transazione");
		} finally {

		}
	}
}

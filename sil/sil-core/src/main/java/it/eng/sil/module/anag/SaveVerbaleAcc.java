package it.eng.sil.module.anag;

import java.math.BigDecimal;

import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.utils.MessageCodes;
import it.eng.afExt.utils.ReportOperationResult;
import it.eng.afExt.utils.TransactionQueryExecutor;
import it.eng.sil.module.AbstractSimpleModule;

public class SaveVerbaleAcc extends AbstractSimpleModule {
	public void service(SourceBean request, SourceBean response) throws Exception {
		boolean ret = false;
		int idSuccess = this.disableMessageIdSuccess();
		int idFail = this.disableMessageIdFail();
		TransactionQueryExecutor transExec = null;
		ReportOperationResult reportOperation = new ReportOperationResult(this, response);

		try {
			transExec = new TransactionQueryExecutor(getPool());
			this.enableTransactions(transExec);

			request.delAttribute("NUMKLOVERBALEACC");
			this.setSectionQuerySelect("QUERY_SELECT");
			SourceBean row = doSelect(request, response);
			String NUMKLOVERBALEACC = String
					.valueOf(((BigDecimal) row.getAttribute("ROW.NUMKLOVERBALEACC")).intValue());
			request.setAttribute("NUMKLOVERBALEACC", NUMKLOVERBALEACC);

			transExec.initTransaction();
			this.setSectionQueryUpdate("QUERY_UPDATE_VER_ACC");
			ret = doUpdate(request, response);

			if (!ret) {
				throw new Exception("impossibile aggiornare CM_VERBALE_ACCERT in transazione");
			}

			transExec.commitTransaction();
			this.setMessageIdSuccess(MessageCodes.General.UPDATE_SUCCESS);
			reportOperation.reportSuccess(idSuccess);
		} catch (Exception e) {
			transExec.rollBackTransaction();
			this.setMessageIdFail(MessageCodes.General.UPDATE_FAIL);
			reportOperation.reportFailure(e, "service()", "aggiornamento in transazione");
		} finally {
		}
	}
}

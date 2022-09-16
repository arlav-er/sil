package it.eng.sil.module.anag;

import com.engiweb.framework.base.SourceBean;

import it.eng.sil.module.AbstractSimpleModule;
import it.eng.sil.util.Utils;

public class SaveDispL68 extends AbstractSimpleModule {
	public void service(SourceBean request, SourceBean response) throws Exception {
		boolean ret = false;
		// int idSuccess = this.disableMessageIdSuccess();
		// int idFail = this.disableMessageIdFail();
		// TransactionQueryExecutor transExec = null;
		// ReportOperationResult reportOperation = new ReportOperationResult(this, response);

		// transExec = new TransactionQueryExecutor(getPool());
		// this.enableTransactions(transExec);

		//
		// transExec.initTransaction();

		String codMonoDispoL8 = Utils.notNull(request.getAttribute("codMonoDispoL8"));

		if (codMonoDispoL8.equals("")) {
			setSectionQueryDelete("QUERY_DELETE_DISP_L68");
			ret = doDelete(request, response);
		} else {
			setSectionQueryUpdate("QUERY_UPDATE_DISP_L68");
			ret = doUpdate(request, response);
		}

		/*
		 * if (!ret) { this.setMessageIdFail(MessageCodes.General.UPDATE_FAIL); reportOperation.reportFailure(e,
		 * "service()", "aggiornamento in transazione"); //new
		 * Exception("impossibile aggiornare pr_dispo_l68 in transazione"); }
		 * 
		 * //transExec.commitTransaction(); this.setMessageIdSuccess(MessageCodes.General.UPDATE_SUCCESS);
		 * reportOperation.reportSuccess(idSuccess);
		 */

	}
}

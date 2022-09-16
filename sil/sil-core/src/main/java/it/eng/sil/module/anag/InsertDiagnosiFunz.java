package it.eng.sil.module.anag;

import java.math.BigDecimal;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.utils.MessageCodes;
import it.eng.afExt.utils.ReportOperationResult;
import it.eng.afExt.utils.TransactionQueryExecutor;
import it.eng.sil.module.AbstractSimpleModule;

public class InsertDiagnosiFunz extends AbstractSimpleModule {
	public void service(SourceBean request, SourceBean response) throws Exception {
		// System.out.println("InsertColloquio chiamato");
		// if (1==1) return ;
		boolean ret = false;
		int idSuccess = this.disableMessageIdSuccess();
		int idFail = this.disableMessageIdFail();
		TransactionQueryExecutor transExec = null;
		ReportOperationResult reportOperation = new ReportOperationResult(this, response);
		RequestContainer r = this.getRequestContainer();
		try {
			transExec = new TransactionQueryExecutor(getPool());
			this.enableTransactions(transExec);

			transExec.initTransaction();

			BigDecimal prgDiagnosiFunzionale = doNextVal(request, response);

			if (prgDiagnosiFunzionale == null) {
				throw new Exception("Impossibile leggere S_CM_DIAGNOSI_FUNZIONALE.NEXTVAL");
			}

			request.delAttribute("prgDiagnosiFunzionale");
			request.setAttribute("prgDiagnosiFunzionale", prgDiagnosiFunzionale.toString());

			this.setSectionQueryInsert("QUERY_INSERT_DIAGNOSI_FUNZ");
			ret = doInsert(request, response);

			if (!ret) {
				throw new Exception("impossibile inserire in cm_diagnosi_funzionale in transazione");
			}
			r.setServiceRequest(request);

			transExec.commitTransaction();

			response.delAttribute("prgDiagnosiFunzionale");
			response.setAttribute("prgDiagnosiFunzionale", prgDiagnosiFunzionale);

			reportOperation.reportSuccess(idSuccess);
		} catch (Exception e) {
			transExec.rollBackTransaction();
			// this.setMessageIdFail(MessageCodes.General.INSERT_FAIL);
			reportOperation.reportFailure(MessageCodes.General.INSERT_FAIL, e, "services()", "insert in transazione");
		} finally {
		}
	}
}

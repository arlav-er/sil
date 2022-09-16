package it.eng.sil.module.anag;

import java.math.BigDecimal;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.utils.MessageCodes;
import it.eng.afExt.utils.ReportOperationResult;
import it.eng.afExt.utils.TransactionQueryExecutor;
import it.eng.sil.module.AbstractSimpleModule;

public class VerbaleAccertamento extends AbstractSimpleModule {
	public void service(SourceBean request, SourceBean response) throws Exception {
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

			BigDecimal prgverbaleAcc = doNextVal(request, response);

			if (prgverbaleAcc == null) {
				throw new Exception("Impossibile leggere S_CM_VERBALE_ACCERT.NEXTVAL");
			}

			request.delAttribute("prgverbaleAcc");
			request.setAttribute("prgverbaleAcc", prgverbaleAcc);

			this.setSectionQueryInsert("QUERY_INS_VER_ACC");
			ret = doInsert(request, response);

			if (!ret) {
				throw new Exception("impossibile inserire in CM_VERBALE_ACCERT in transazione");
			}
			r.setServiceRequest(request);

			transExec.commitTransaction();

			response.delAttribute("prgverbaleAcc");
			response.setAttribute("prgverbaleAcc", prgverbaleAcc);

			reportOperation.reportSuccess(idSuccess);
		} catch (Exception e) {
			transExec.rollBackTransaction();
			// this.setMessageIdFail(MessageCodes.General.INSERT_FAIL);
			reportOperation.reportFailure(MessageCodes.General.INSERT_FAIL, e, "services()", "insert in transazione");
		} finally {
		}
	}
}

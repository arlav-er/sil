package it.eng.sil.module.orientamento;

import java.math.BigDecimal;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.utils.MessageCodes;
import it.eng.afExt.utils.ReportOperationResult;
import it.eng.afExt.utils.TransactionQueryExecutor;
import it.eng.sil.module.AbstractSimpleModule;

public class InsertCorsi extends AbstractSimpleModule {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public void service(SourceBean request, SourceBean response) throws Exception {

		boolean ret = false;
		int idSuccess = this.disableMessageIdSuccess();
		// int idFail = this.disableMessageIdFail();
		TransactionQueryExecutor transExec = null;
		ReportOperationResult reportOperation = new ReportOperationResult(this, response);
		RequestContainer r = this.getRequestContainer();
		try {
			transExec = new TransactionQueryExecutor(getPool());
			this.enableTransactions(transExec);

			transExec.initTransaction();

			BigDecimal prgcorso = doNextVal(request, response);

			if (prgcorso == null) {
				throw new Exception("Impossibile leggere GET_or_Corso_NEXTVAL.NEXTVAL");
			}

			request.delAttribute("prgcorso");
			request.setAttribute("prgcorso", prgcorso.toString());

			this.setSectionQueryInsert("QUERY_INSERT_CORSO");
			ret = doInsert(request, response);

			if (!ret) {
				throw new Exception("impossibile inserire in QUERY_INSERT_CORSO in transazione");
			}
			r.setServiceRequest(request);

			transExec.commitTransaction();

			// response.delAttribute("PRGLAVREDDITO");
			// response.setAttribute("PRGLAVREDDITO", PRGLAVREDDITO);

			reportOperation.reportSuccess(idSuccess);
		} catch (Exception e) {
			transExec.rollBackTransaction();
			// this.setMessageIdFail(MessageCodes.General.INSERT_FAIL);
			reportOperation.reportFailure(MessageCodes.General.INSERT_FAIL, e, "services()", "insert in transazione");
		} finally {
		}
	}

	private void setKeyinRequest(BigDecimal prgcorso, SourceBean request) throws Exception {
		if (request.getAttribute("prgcorso") != null) {
			request.delAttribute("prgcorso");
		}
		request.updAttribute("prgcorso", prgcorso);
	}

}

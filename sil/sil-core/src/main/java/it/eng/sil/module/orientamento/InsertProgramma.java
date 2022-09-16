package it.eng.sil.module.orientamento;

import java.math.BigDecimal;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.utils.MessageCodes;
import it.eng.afExt.utils.ReportOperationResult;
import it.eng.afExt.utils.TransactionQueryExecutor;
import it.eng.sil.module.AbstractSimpleModule;

public class InsertProgramma extends AbstractSimpleModule {
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

			BigDecimal prgprogrammaq = doNextVal(request, response);

			if (prgprogrammaq == null) {
				throw new Exception("Impossibile leggere GET_or_programma_q_NEXTVAL.NEXTVAL");
			}

			request.delAttribute("prgprogrammaq");
			request.setAttribute("prgprogrammaq", prgprogrammaq.toString());

			this.setSectionQueryInsert("QUERY_INSERT_PROGRAMMA");
			ret = doInsert(request, response);

			if (!ret) {
				throw new Exception("impossibile inserire in or_PROGRAMMA in transazione");
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

	private void setKeyinRequest(BigDecimal prgprogrammaq, SourceBean request) throws Exception {
		if (request.getAttribute("prgprogrammaq") != null) {
			request.delAttribute("prgprogrammaq");
		}

		request.updAttribute("prgprogrammaq", prgprogrammaq);
		// request.updAttribute("strChiaveTabella", PRGRICHCOMPUTO.toString());
	}

}

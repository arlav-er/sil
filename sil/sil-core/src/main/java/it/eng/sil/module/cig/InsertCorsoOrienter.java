package it.eng.sil.module.cig;

import java.math.BigDecimal;

import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.utils.ReportOperationResult;
import it.eng.afExt.utils.TransactionQueryExecutor;
import it.eng.sil.module.AbstractSimpleModule;

public class InsertCorsoOrienter extends AbstractSimpleModule {
	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(InsertCorsoOrienter.class.getName());

	private TransactionQueryExecutor transExec;
	BigDecimal userid;
	private ReportOperationResult reportOperation;

	public void service(SourceBean request, SourceBean response) throws Exception {
		boolean success = false;

		int idSuccess = this.disableMessageIdSuccess();
		reportOperation = new ReportOperationResult(this, response);

		BigDecimal prgCorsoCi = getprgCorsoCi(request, response);

		try {
			transExec = new TransactionQueryExecutor(getPool());
			this.enableTransactions(transExec);

			transExec.initTransaction();

			if (prgCorsoCi != null) {
				setKeyinRequest(prgCorsoCi, request);
				this.setSectionQueryInsert("QUERY_INSERT_CI_CORSO");
				success = this.doInsert(request, response);
				this.setSectionQueryInsert("QUERY_INSERT_CORSO_ORIENTER");
				success = success && this.doInsert(request, response);
			} else
				throw new Exception();

			if (success) {
				transExec.commitTransaction();
				reportOperation.reportSuccess(idSuccess);
			} else {
				transExec.rollBackTransaction();
			}

		} catch (Exception e) {
			reportOperation.reportFailure(200006, e, "services()", "Errore generico");
			transExec.rollBackTransaction();
		}
	}

	private BigDecimal getprgCorsoCi(SourceBean request, SourceBean response) throws Exception {
		this.setSectionQuerySelect("QUERY_NEXTVAL");
		SourceBean beanprgCorsoCi = (SourceBean) doSelect(request, response);
		return (BigDecimal) beanprgCorsoCi.getAttribute("ROW.PRGCORSOCI");
	}

	private void setKeyinRequest(BigDecimal prgCorsoCi, SourceBean request) throws Exception {
		if (request.getAttribute("PRGCORSOCI") != null) {
			request.delAttribute("PRGCORSOCI");
		}
		request.setAttribute("PRGCORSOCI", prgCorsoCi);
	}

}

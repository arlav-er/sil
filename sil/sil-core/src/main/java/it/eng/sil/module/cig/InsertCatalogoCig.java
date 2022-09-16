package it.eng.sil.module.cig;

import java.math.BigDecimal;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.SessionContainer;
import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.utils.MessageCodes;
import it.eng.afExt.utils.ReportOperationResult;
import it.eng.afExt.utils.StringUtils;
import it.eng.afExt.utils.TransactionQueryExecutor;
import it.eng.sil.module.AbstractSimpleModule;
import it.eng.sil.security.User;

public class InsertCatalogoCig extends AbstractSimpleModule {
	private final String className = StringUtils.getClassName(this);
	private TransactionQueryExecutor transExec;
	BigDecimal userid;

	public void service(SourceBean request, SourceBean response) throws Exception {

		RequestContainer requestContainer = getRequestContainer();
		SessionContainer sessionContainer = (SessionContainer) requestContainer.getSessionContainer();
		User user = (User) sessionContainer.getAttribute(User.USERID);
		userid = new BigDecimal(user.getCodut());

		int idSuccess = this.disableMessageIdSuccess();
		int idFail = this.disableMessageIdFail();
		int msgCode = MessageCodes.General.INSERT_FAIL;
		ReportOperationResult reportOperation = new ReportOperationResult(this, response);

		String numidproposta = StringUtils.getAttributeStrNotNull(request, "numidproposta");
		String numrecid = StringUtils.getAttributeStrNotNull(request, "numrecid");
		String flgItalianoStranieri = StringUtils.getAttributeStrNotNull(request, "flgItalianoStranieri");

		try {
			transExec = new TransactionQueryExecutor(getPool());
			this.enableTransactions(transExec);
			transExec.initTransaction();
			boolean success = false;

			BigDecimal prgCorsoCi = getPrgCorsoCi(request, response);
			if (prgCorsoCi != null) {
				setKeyinRequest(prgCorsoCi, request);

				// deve arrivare al modulo di inserimento contatto
				sessionContainer.setAttribute("PRGCORSOCI", prgCorsoCi);

				request.updAttribute("numidproposta", numidproposta);
				request.updAttribute("numrecid", numrecid);
				request.updAttribute("flgItalianoStranieri", flgItalianoStranieri);
				this.setSectionQueryInsert("QUERY_INSERT_CORSO");
				success = this.doInsert(request, response);
			}

			if (success) {
				transExec.commitTransaction();
				reportOperation.reportSuccess(idSuccess);
			} else {
				transExec.rollBackTransaction();
			}

		} catch (Exception e) {
			transExec.rollBackTransaction();
			reportOperation.reportFailure(msgCode, e, "services()", "insert in transazione");
		}
	}

	private BigDecimal getPrgCorsoCi(SourceBean request, SourceBean response) throws Exception {
		this.setSectionQuerySelect("QUERY_NEXTVAL");
		SourceBean beanprgCorsoCi = (SourceBean) doSelect(request, response);
		return (BigDecimal) beanprgCorsoCi.getAttribute("ROW.PRGCORSOCI");
	}

	private void setKeyinRequest(BigDecimal PRGCORSOCI, SourceBean request) throws Exception {
		if (request.getAttribute("PRGCORSOCI") != null) {
			request.delAttribute("PRGCORSOCI");
		}
		request.updAttribute("PRGCORSOCI", PRGCORSOCI);
	}
}

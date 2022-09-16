package it.eng.sil.module.presel;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.utils.MessageCodes;
import it.eng.afExt.utils.ReportOperationResult;
import it.eng.afExt.utils.TransactionQueryExecutor;
import it.eng.sil.module.AbstractSimpleModule;

public class UpdateTirocinio extends AbstractSimpleModule {

	public void service(SourceBean request, SourceBean response) throws Exception {
		boolean ret = false;
		String DELIMETER = "-";
		String codice = "";
		String[] tempListprg = null;
		String strprgColloquio = "";
		String strprgPercorso = "";
		String attributeValueColloquioPercorso = "";
		int idSuccess = this.disableMessageIdSuccess();
		int idFail = this.disableMessageIdFail();
		TransactionQueryExecutor transExec = null;
		ReportOperationResult reportOperation = new ReportOperationResult(this, response);

		try {
			transExec = new TransactionQueryExecutor(getPool());
			this.enableTransactions(transExec);
			transExec.initTransaction();
			RequestContainer r = this.getRequestContainer();
			attributeValueColloquioPercorso = (String) request.getAttribute("codice");

			if (attributeValueColloquioPercorso != null && !"".equals(attributeValueColloquioPercorso)) {
				tempListprg = attributeValueColloquioPercorso.split(DELIMETER);
				strprgColloquio = tempListprg[0];
				strprgPercorso = tempListprg[1];
				request.delAttribute("prgcolloquio");
				request.setAttribute("prgcolloquio", strprgColloquio);
				request.delAttribute("prgpercorso");
				request.setAttribute("prgpercorso", strprgPercorso);
			}

			this.setSectionQueryInsert("QUERY_UPDATE_TIROCINIO");
			ret = doInsert(request, response);
			if (!ret) {
				throw new Exception("impossibile inserire in QUERY_INSERT_Tirocinio in transazione");
			}
			r.setServiceRequest(request);
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

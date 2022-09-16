package it.eng.sil.module.presel;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.utils.MessageCodes;
import it.eng.afExt.utils.ReportOperationResult;
import it.eng.afExt.utils.TransactionQueryExecutor;
import it.eng.sil.module.AbstractSimpleModule;
import it.eng.sil.module.patto.InsertPercorso;

public class InsertTirocinio extends AbstractSimpleModule {

	private static final long serialVersionUID = 1L;

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(InsertPercorso.class.getName());

	public void service(SourceBean request, SourceBean response) throws Exception {

		Boolean ris = new Boolean(false);
		boolean ret = false;
		boolean resultIns = false;
		int idSuccess = this.disableMessageIdSuccess();

		String codice = "";
		String[] tempListprg = null;
		String strprgColloquio = "";
		String strprgPercorso = "";
		String attributeValueColloquioPercorso = "";

		TransactionQueryExecutor transExec = null;
		ReportOperationResult reportOperation = new ReportOperationResult(this, response);
		RequestContainer r = this.getRequestContainer();
		try {
			transExec = new TransactionQueryExecutor(getPool());
			this.enableTransactions(transExec);
			String DELIMETER = "-";
			transExec.initTransaction();

			transExec = new TransactionQueryExecutor(getPool(), this);
			enableTransactions(transExec);
			transExec.initTransaction();
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

			this.setSectionQueryInsert("QUERY_INSERT_Tirocinio");
			ret = doInsert(request, response);
			if (!ret) {
				throw new Exception("impossibile inserire in QUERY_INSERT_Tirocinio in transazione");
			}
			r.setServiceRequest(request);
			transExec.commitTransaction();
			resultIns = true;

			if (resultIns) {
				reportOperation.reportSuccess(MessageCodes.General.INSERT_SUCCESS);
			}

		} catch (Exception ex) {
			reportOperation.reportFailure(MessageCodes.General.INSERT_FAIL, ex, "execute()",
					"Fallito inserimento del tirocinio.");
			it.eng.sil.util.TraceWrapper.debug(_logger, "service(): Fallito inserimento del tirocinio.", ex);

		}

	}

}

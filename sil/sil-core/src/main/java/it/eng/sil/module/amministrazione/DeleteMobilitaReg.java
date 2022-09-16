package it.eng.sil.module.amministrazione;

import java.util.Vector;

import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.utils.MessageCodes;
import it.eng.afExt.utils.ReportOperationResult;
import it.eng.sil.module.AbstractSimpleModule;
import it.eng.sil.module.movimenti.MultipleTransactionQueryExecutor;

public class DeleteMobilitaReg extends AbstractSimpleModule {
	private String className;

	public DeleteMobilitaReg() {
		className = this.getClass().getName();
	}

	public void service(SourceBean request, SourceBean response) throws Exception {
		SourceBean sbMobFiltrato = null;
		Object prg = null;
		ReportOperationResult reportOperation = new ReportOperationResult(this, response);
		int idSuccess = this.disableMessageIdSuccess();
		int idFail = this.disableMessageIdFail();
		MultipleTransactionQueryExecutor queryExecutor = null;
		String pool = (String) getConfig().getAttribute("POOL");
		try {
			queryExecutor = new MultipleTransactionQueryExecutor(pool);
			Vector mobilitaFiltrate = getServiceResponse().getAttributeAsVector("GetMobilitaRegFiltrate.ROWS.ROW");
			for (int i = 0; i < mobilitaFiltrate.size(); i++) {
				sbMobFiltrato = (SourceBean) mobilitaFiltrate.get(i);
				prg = sbMobFiltrato.getAttribute("prgMobIscrDaEnteBk");
				enableTransactions(queryExecutor);
				request.delAttribute("PRGMOBISCRDAENTEBK");
				request.setAttribute("PRGMOBISCRDAENTEBK", prg);
				try {
					queryExecutor.initTransaction();
					doDelete(request, response);
					queryExecutor.commitTransaction();
				} catch (Exception e) {
					queryExecutor.rollBackTransaction();
				}
			}
			queryExecutor.closeConnection();
			reportOperation.reportSuccess(MessageCodes.General.DELETE_SUCCESS);
			response.setAttribute("OPERAZIONE_COMPLETATA", "OK");
		} catch (Exception e) {
			if (queryExecutor != null) {
				queryExecutor.rollBackTransaction();
			}
			reportOperation.reportFailure(MessageCodes.General.DELETE_FAIL);
		}
	}

}
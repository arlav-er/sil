package it.eng.sil.module.ido;

import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.utils.MessageCodes;
import it.eng.afExt.utils.ReportOperationResult;
import it.eng.afExt.utils.StringUtils;
import it.eng.afExt.utils.TransactionQueryExecutor;
import it.eng.sil.module.AbstractSimpleModule;

public class AggiornaDispRosa extends AbstractSimpleModule {
	private String className = this.getClass().getName();
	/*
	 * public void service(SourceBean request, SourceBean response) { doUpdate(request,response); }
	 */

	public void service(SourceBean request, SourceBean response) throws Exception {

		ReportOperationResult reportOperation = new ReportOperationResult(this, response);
		TransactionQueryExecutor transExec = null;
		boolean ret = false;
		boolean ret2 = false;
		int idSuccess = this.disableMessageIdSuccess();
		int idFail = this.disableMessageIdFail();
		try {
			transExec = new TransactionQueryExecutor(getPool());

			transExec.initTransaction();

			String checkUpdateDispoL68 = StringUtils.getAttributeStrNotNull(request, "CHECKMODDISPOL68");

			this.setSectionQueryInsert("QUERY_UPDATE");
			ret2 = this.doUpdate(request, response);

			if (ret2) {
				if (("1").equalsIgnoreCase(checkUpdateDispoL68)) {
					this.setSectionQueryUpdate("QUERY_UPDATE_DISPOL68");
					ret = this.doUpdate(request, response);
				} else {
					ret = true;
				}
			}

			//
			if (ret && ret2) {
				transExec.commitTransaction();
				this.setMessageIdSuccess(idSuccess);
				this.setMessageIdFail(idFail);
				reportOperation.reportSuccess(idSuccess);
			} else {
				throw new Exception();
			}
		} catch (Exception e) {
			reportOperation.reportFailure(MessageCodes.General.INSERT_FAIL);
			if (transExec != null)
				transExec.rollBackTransaction();
		}
	}
}
/*
 * Creato il 18-ago-06
 *
 * Per modificare il modello associato a questo file generato, aprire
 * Finestra&gt;Preferenze&gt;Java&gt;Generazione codice&gt;Codice e commenti
 */
package it.eng.sil.module.ido;

import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.utils.MessageCodes;
import it.eng.afExt.utils.ReportOperationResult;
import it.eng.afExt.utils.TransactionQueryExecutor;
import it.eng.sil.module.AbstractSimpleModule;

public class ASSaveRiapertura extends AbstractSimpleModule {

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
			this.setSectionQueryUpdate("QUERY_UPDATE");
			ret = this.doUpdate(request, response);

			this.setSectionQueryInsert("QUERY_INSERT");
			ret2 = this.doInsert(request, response);

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
			// this.setMessageIdFail(MessageCodes.General.DELETE_FAIL);
			reportOperation.reportFailure(MessageCodes.General.DELETE_FAIL);
			if (transExec != null)
				transExec.rollBackTransaction();
		}
	}
}
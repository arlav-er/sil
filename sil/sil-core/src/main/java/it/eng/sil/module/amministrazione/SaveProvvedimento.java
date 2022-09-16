/*
 * Creato il 17-gen-07
 *
 * Per modificare il modello associato a questo file generato, aprire
 * Finestra&gt;Preferenze&gt;Java&gt;Generazione codice&gt;Codice e commenti
 */
package it.eng.sil.module.amministrazione;

import java.math.BigDecimal;

import com.engiweb.framework.base.SourceBean;

/**
 * @author melandri
 * 
 * Per modificare il modello associato al commento di questo tipo generato,
 * aprire Finestra&gt;Preferenze&gt;Java&gt;Generazione codice&gt;Codice e
 * commenti
 */

import it.eng.afExt.utils.MessageCodes;
import it.eng.afExt.utils.ReportOperationResult;
import it.eng.afExt.utils.TransactionQueryExecutor;
import it.eng.sil.module.AbstractSimpleModule;

public class SaveProvvedimento extends AbstractSimpleModule {
	BigDecimal userid;
	private TransactionQueryExecutor transExec;

	public void service(SourceBean request, SourceBean response) throws Exception {
		boolean ret = false;
		int idSuccess = this.disableMessageIdSuccess();
		int idFail = this.disableMessageIdFail();
		TransactionQueryExecutor transExec = null;
		ReportOperationResult reportOperation = new ReportOperationResult(this, response);

		try {
			transExec = new TransactionQueryExecutor(getPool());
			this.enableTransactions(transExec);

			request.delAttribute("numkloprovvcomputo");
			this.setSectionQuerySelect("QUERY_SELECT");
			SourceBean row = doSelect(request, response);
			// NUMKLOPROVVCOMPUTO
			String numkloprovvcomputo = String
					.valueOf(((BigDecimal) row.getAttribute("ROW.NUMKLOPROVVCOMPUTO")).intValue());
			request.setAttribute("NUMKLOPROVVCOMPUTO", numkloprovvcomputo);

			transExec.initTransaction();
			this.setSectionQueryUpdate("QUERY_UPDATE_PROVVEDIMENTO");
			ret = doUpdate(request, response);

			if (!ret) {
				throw new Exception("impossibile aggiornare il Provvedimento in transazione");
			}

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
/*
 * Creato il 24-gen-07
 *
 * Per modificare il modello associato a questo file generato, aprire
 * Finestra&gt;Preferenze&gt;Java&gt;Generazione codice&gt;Codice e commenti
 */
package it.eng.sil.module.collocamentoMirato;

import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.utils.MessageCodes;
import it.eng.afExt.utils.ReportOperationResult;
import it.eng.afExt.utils.TransactionQueryExecutor;
import it.eng.sil.module.AbstractSimpleModule;

/**
 * @author riccardi
 * 
 *         Per modificare il modello associato al commento di questo tipo generato, aprire
 *         Finestra&gt;Preferenze&gt;Java&gt;Generazione codice&gt;Codice e commenti
 */
public class InsertAgevolazione extends AbstractSimpleModule {
	public void service(SourceBean request, SourceBean response) throws Exception {
		boolean ret = false;
		int idSuccess = this.disableMessageIdSuccess();
		int idFail = this.disableMessageIdFail();
		TransactionQueryExecutor transExec = null;
		ReportOperationResult reportOperation = new ReportOperationResult(this, response);

		try {

			transExec = new TransactionQueryExecutor(getPool());
			this.enableTransactions(transExec);

			transExec.initTransaction();

			int numRows = Integer.valueOf("" + request.getAttribute("numRows")).intValue();

			for (int i = 1; i <= numRows; i++) {

				String j = Integer.toString(i);
				request.delAttribute("codAgevolazione");
				String codAgevolazione = "" + request.getAttribute("codAgevolazione" + j);

				if (!codAgevolazione.equals("") && !codAgevolazione.equals("null")) {

					request.setAttribute("codAgevolazione", codAgevolazione);

					this.setSectionQuerySelect("QUERY_SELECT_AGEVOLAZIONE");
					this.setSectionQueryInsert("QUERY_INSERT_AGEVOLAZIONI");
					ret = doInsertNoDuplicate(request, response);

				}
			}
			transExec.commitTransaction();

			reportOperation.reportSuccess(idSuccess);

		} catch (Exception e) {
			transExec.rollBackTransaction();
			reportOperation.reportFailure(MessageCodes.General.INSERT_FAIL, e, "AGEVOLAZIONI()",
					"insert in transazione");
		} finally {
		}
	}
}

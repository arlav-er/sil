/*
 * Creato il 23-feb-07
 *
 * Per modificare il modello associato a questo file generato, aprire
 * Finestra&gt;Preferenze&gt;Java&gt;Generazione codice&gt;Codice e commenti
 */
package it.eng.sil.module.amministrazione;

import java.math.BigDecimal;

import com.engiweb.framework.base.RequestContainer;
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
import it.eng.sil.util.Utils;

public class InsertReddito extends AbstractSimpleModule {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public void service(SourceBean request, SourceBean response) throws Exception {
		// System.out.println("InsertColloquio chiamato");
		// if (1==1) return ;

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

			// controlliamo il valore del reddito
			String numReddito = (String) request.getAttribute("NUMREDDITO");
			if (!Utils.containsOnlyNumbers(numReddito)) {

				reportOperation.reportFailure(MessageCodes.General.INVALID_FORMAT);
				return;
			}

			BigDecimal PRGLAVREDDITO = doNextVal(request, response);

			if (PRGLAVREDDITO == null) {
				throw new Exception("Impossibile leggere S_CM_LAV_REDDITO.NEXTVAL");
			}

			request.delAttribute("PRGLAVREDDITO");
			request.setAttribute("PRGLAVREDDITO", PRGLAVREDDITO.toString());

			this.setSectionQueryInsert("QUERY_INSERT_REDDITO");
			ret = doInsert(request, response);

			if (!ret) {
				throw new Exception("impossibile inserire in CM_LAV_REDDITO in transazione");
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
}

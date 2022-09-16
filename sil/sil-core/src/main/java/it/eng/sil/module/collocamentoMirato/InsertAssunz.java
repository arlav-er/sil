/*
 * Creato il 22-gen-07
 *
 * Per modificare il modello associato a questo file generato, aprire
 * Finestra&gt;Preferenze&gt;Java&gt;Generazione codice&gt;Codice e commenti
 */
package it.eng.sil.module.collocamentoMirato;

import java.math.BigDecimal;

import com.engiweb.framework.base.RequestContainer;
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
public class InsertAssunz extends AbstractSimpleModule {
	public void service(SourceBean request, SourceBean response) throws Exception {
		boolean ret = false;
		int idSuccess = this.disableMessageIdSuccess();
		int idFail = this.disableMessageIdFail();
		TransactionQueryExecutor transExec = null;
		ReportOperationResult reportOperation = new ReportOperationResult(this, response);
		RequestContainer r = this.getRequestContainer();
		try {
			transExec = new TransactionQueryExecutor(getPool());
			this.enableTransactions(transExec);

			transExec.initTransaction();

			BigDecimal PRGCONVDETTAGLIO = doNextVal(request, response);

			if (PRGCONVDETTAGLIO == null) {
				throw new Exception("Impossibile leggere S_CM_CONV_DETTAGLIO.NEXTVAL");
			}

			request.delAttribute("PRGCONVDETTAGLIO");
			request.setAttribute("PRGCONVDETTAGLIO", PRGCONVDETTAGLIO.toString());

			this.setSectionQueryInsert("QUERY_INSERT_ASS");
			ret = doInsert(request, response);

			if (!ret) {
				throw new Exception("impossibile inserire in CM_CONV_DETTAGLIO in transazione");
			}
			r.setServiceRequest(request);

			transExec.commitTransaction();

			response.delAttribute("PRGCONVDETTAGLIO");
			response.setAttribute("PRGCONVDETTAGLIO", PRGCONVDETTAGLIO);

			reportOperation.reportSuccess(idSuccess);
		} catch (Exception e) {
			transExec.rollBackTransaction();
			reportOperation.reportFailure(MessageCodes.General.INSERT_FAIL, e, "services()", "insert in transazione");
		} finally {
		}
	}
}

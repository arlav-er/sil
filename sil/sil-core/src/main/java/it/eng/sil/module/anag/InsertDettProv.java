/*
 * Creato il 15-dic-06
 *
 * Per modificare il modello associato a questo file generato, aprire
 * Finestra&gt;Preferenze&gt;Java&gt;Generazione codice&gt;Codice e commenti
 */
package it.eng.sil.module.anag;

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

public class InsertDettProv extends AbstractSimpleModule {
	public void service(SourceBean request, SourceBean response) throws Exception {
		// System.out.println("InsertColloquio chiamato");
		// if (1==1) return ;
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

			BigDecimal PRGCOMPTERRDETT = doNextVal(request, response);

			if (PRGCOMPTERRDETT == null) {
				throw new Exception("Impossibile leggere S_CM_COMPTERR_DETTAGLIO.NEXTVAL");
			}

			request.delAttribute("PRGCOMPTERRDETT");
			request.setAttribute("PRGCOMPTERRDETT", PRGCOMPTERRDETT.toString());

			this.setSectionQueryInsert("QUERY_INSERT_DETT_PROV");
			ret = doInsert(request, response);

			if (!ret) {
				throw new Exception("impossibile inserire in CM_COMPTERR_DETTAGLIO in transazione");
			}
			r.setServiceRequest(request);

			transExec.commitTransaction();

			response.delAttribute("PRGCOMPTERRDETT");
			response.setAttribute("PRGCOMPTERRDETT", PRGCOMPTERRDETT);

			reportOperation.reportSuccess(idSuccess);
		} catch (Exception e) {
			transExec.rollBackTransaction();
			// this.setMessageIdFail(MessageCodes.General.INSERT_FAIL);
			reportOperation.reportFailure(MessageCodes.General.INSERT_FAIL, e, "services()", "insert in transazione");
		} finally {
		}
	}
}

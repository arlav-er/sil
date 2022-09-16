package it.eng.sil.module.collocamentoMirato;

import java.math.BigDecimal;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.ResponseContainer;
import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.utils.MessageCodes;
import it.eng.afExt.utils.ReportOperationResult;
import it.eng.afExt.utils.TransactionQueryExecutor;
import it.eng.sil.module.AbstractSimpleModule;

/**
 * 
 * Per modificare il modello associato al commento di questo tipo generato, aprire
 * Finestra&gt;Preferenze&gt;Java&gt;Generazione codice&gt;Codice e commenti
 */
public class ReiteraAssunz extends AbstractSimpleModule {

	public void service(SourceBean request, SourceBean response) throws Exception {
		boolean ret = false;
		int idSuccess = this.disableMessageIdSuccess();
		int idFail = this.disableMessageIdFail();
		TransactionQueryExecutor transExec = null;
		ReportOperationResult reportOperation = new ReportOperationResult(this, response);
		RequestContainer reqCont = getRequestContainer();
		ResponseContainer resCont = getResponseContainer();
		try {
			transExec = new TransactionQueryExecutor(getPool());
			this.enableTransactions(transExec);

			transExec.initTransaction();

			BigDecimal numRigheAss = new BigDecimal("0");
			String numRighe = (String) request.getAttribute("NUMRIGHEREITERA");
			if (numRighe != null || !("").equals(numRighe)) {
				numRigheAss = new BigDecimal(numRighe);
			}

			SourceBean statement = (SourceBean) getConfig().getAttribute("QUERY_REITERA_ASS");
			for (int i = 0; i < numRigheAss.intValue(); i++) {
				Boolean esito = (Boolean) transExec.executeQuery(reqCont, resCont, statement, "INSERT");

				ret = esito.booleanValue();

				if (!ret) {
					break;
				}
			}

			if (!ret) {
				throw new Exception("impossibile inserire in CM_CONV_DETTAGLIO in transazione");
			}

			transExec.commitTransaction();

			reportOperation.reportSuccess(idSuccess);
		} catch (Exception e) {
			transExec.rollBackTransaction();
			reportOperation.reportFailure(MessageCodes.General.INSERT_FAIL, e, "services()", "insert in transazione");
		} finally {
		}
	}

}

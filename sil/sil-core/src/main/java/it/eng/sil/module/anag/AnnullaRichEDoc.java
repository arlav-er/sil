/*
 * Creato il 6-giu-07
 *
 * Per modificare il modello associato a questo file generato, aprire
 * Finestra&gt;Preferenze&gt;Java&gt;Generazione codice&gt;Codice e commenti
 */
package it.eng.sil.module.anag;

import java.math.BigDecimal;

import com.engiweb.framework.base.SourceBean;

/**
 * @author riccardi
 * 
 * Per modificare il modello associato al commento di questo tipo generato,
 * aprire Finestra&gt;Preferenze&gt;Java&gt;Generazione codice&gt;Codice e
 * commenti
 */

import it.eng.afExt.utils.ReportOperationResult;
import it.eng.afExt.utils.TransactionQueryExecutor;
import it.eng.sil.bean.protocollo.ProtocolloDocumentoUtil;
import it.eng.sil.module.AbstractSimpleModule;

public class AnnullaRichEDoc extends AbstractSimpleModule {
	public void service(SourceBean request, SourceBean response) throws Exception {
		boolean ret = false;
		int idSuccess = this.disableMessageIdSuccess();
		int idFail = this.disableMessageIdFail();
		TransactionQueryExecutor transExec = null;
		ReportOperationResult reportOperation = new ReportOperationResult(this, response);
		BigDecimal prgDocumento = null;

		try {
			transExec = new TransactionQueryExecutor(getPool());
			this.enableTransactions(transExec);

			transExec.initTransaction();

			this.setSectionQuerySelect("QUERY_SELECT_DOC");
			SourceBean rowDoc = this.doSelect(request, response);

			if (rowDoc.containsAttribute("ROW.PRGDOCUMENTO")) {
				prgDocumento = (BigDecimal) rowDoc.getAttribute("ROW.PRGDOCUMENTO");
			}

			if (prgDocumento != null) {
				String numkloDocumento = String
						.valueOf(((BigDecimal) rowDoc.getAttribute("ROW.NUMKLODOCUMENTO")).intValue() + 1);
				request.setAttribute("prgDocumento", prgDocumento);
				request.setAttribute("numkloDocumento", numkloDocumento);

				this.setSectionQueryUpdate("QUERY_ANNULLA_DOC");
				ret = doUpdate(request, response);
			} else
				throw new Exception();

			if (ret) {
				this.setSectionQueryUpdate("QUERY_ANNULLA_RICH");
				ret = doUpdate(request, response);
			}

			if (ret) {
				transExec.commitTransaction();
				ProtocolloDocumentoUtil.cancellaFileDocarea();
				reportOperation.reportSuccess(idSuccess);
			} else {
				transExec.rollBackTransaction();
			}
		} catch (Exception e) {
			transExec.rollBackTransaction();
			reportOperation.reportFailure(e, "service()", "aggiornamento in transazione");
		}
	}
}

/*
 * Creato il 12-mar-07
 *
 * Per modificare il modello associato a questo file generato, aprire
 * Finestra&gt;Preferenze&gt;Java&gt;Generazione codice&gt;Codice e commenti
 */
package it.eng.sil.module.amministrazione;

import java.util.Vector;

import com.engiweb.framework.base.SourceBean;

/**
 * @author melandri
 * 
 * Per modificare il modello associato al commento di questo tipo generato,
 * aprire Finestra&gt;Preferenze&gt;Java&gt;Generazione codice&gt;Codice e
 * commenti
 */

import it.eng.afExt.utils.MessageAppender;
import it.eng.afExt.utils.MessageCodes;
import it.eng.afExt.utils.ReportOperationResult;
import it.eng.afExt.utils.TransactionQueryExecutor;
import it.eng.sil.module.AbstractSimpleModule;

public class InsertCMServizio extends AbstractSimpleModule {
	public void service(SourceBean request, SourceBean response) throws Exception {
		boolean ret = false;
		int idSuccess = this.disableMessageIdSuccess();
		int idFail = this.disableMessageIdFail();
		TransactionQueryExecutor transExec = null;
		ReportOperationResult reportOperation = new ReportOperationResult(this, response);
		String nonInseriti = "Non è stato possibile inserire i seguenti servizi in quanto risultano già presenti: ";
		int countNonInseriti = 0;
		int countInseriti = 0;

		try {

			transExec = new TransactionQueryExecutor(getPool());
			this.enableTransactions(transExec);
			transExec.initTransaction();

			int numRows = Integer.valueOf("" + request.getAttribute("numRows")).intValue();

			// ---------------------------------------
			String cdnLavoratore = "" + request.getAttribute("cdnLavoratore");

			request.delAttribute("cdnLavoratore");
			request.setAttribute("cdnLavoratore", cdnLavoratore);

			this.setSectionQuerySelect("QUERY_LOAD_SERVIZI");
			SourceBean result = (SourceBean) doSelect(request, response);
			Vector vec = null;
			if (result != null) {
				vec = result.getAttributeAsVector("ROW");
			}
			SourceBean row = null;
			String codServ = "";
			String strDescrizione = "";
			// ---------------------------------------

			for (int i = 1; i <= numRows; i++) {

				String j = Integer.toString(i);
				request.delAttribute("codServizio");
				String codServizio = "" + request.getAttribute("codServizio" + j);

				boolean isOk = true;
				for (int k = 0; k < vec.size(); k++) {
					row = (SourceBean) vec.elementAt(k);
					codServ = "" + row.getAttribute("codServizio");
					strDescrizione = "" + row.getAttribute("strdescrizione");
					if (codServizio.equals(codServ)) {
						isOk = false;
						if (countNonInseriti > 0) {
							nonInseriti += ", ";
						}
						nonInseriti += strDescrizione;
						countNonInseriti++;
					}
				}

				if (!codServizio.equals("") && !codServizio.equals("null") && isOk) {

					request.setAttribute("codServizio", codServizio);

					this.setSectionQueryInsert("QUERY_INSERT_SERVIZI");
					ret = doInsert(request, response);
					countInseriti++;

					if (!ret) {
						throw new Exception("impossibile inserire in CM_ASS_LAV_SERVIZIO in transazione");
					}
				}
			}
			transExec.commitTransaction();

			if (countNonInseriti > 0) {
				MessageAppender.appendMessage(response, nonInseriti, null);
			}

			if (countInseriti > 0) {
				reportOperation.reportSuccess(idSuccess);
			}

		} catch (Exception e) {
			transExec.rollBackTransaction();
			// this.setMessageIdFail(MessageCodes.General.INSERT_FAIL);
			reportOperation.reportFailure(MessageCodes.General.INSERT_FAIL, e, "services()", "insert in transazione");
		} finally {
		}
	}
}

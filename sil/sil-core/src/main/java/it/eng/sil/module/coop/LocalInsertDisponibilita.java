/*
 * Creato il 13-giu-06
 *
 * Per modificare il modello associato a questo file generato, aprire
 * Finestra&gt;Preferenze&gt;Java&gt;Generazione codice&gt;Codice e commenti
 */
package it.eng.sil.module.coop;

import java.math.BigDecimal;

import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.utils.ReportOperationResult;

/**
 * @author riccardi
 * 
 */
public class LocalInsertDisponibilita extends CoopDoInsertModule {
	public boolean doInsertModule(SourceBean request, SourceBean response) {
		ReportOperationResult reportOperation = new ReportOperationResult(this, response);
		int idSuccess = this.disableMessageIdSuccess();
		int idFail = this.disableMessageIdFail();
		boolean ris = true;
		try {
			SourceBean mansione = doSelect(request, response);
			BigDecimal prgMansione = (BigDecimal) mansione.getAttribute("ROW.PRGMANSIONE");
			request.updAttribute("PRGMANSIONE", prgMansione);
			ris = doInsert(request, response);
		} catch (Exception e) {
			reportOperation.reportFailure(idFail);
			ris = false;
		}
		if (ris) {
			reportOperation.reportSuccess(idSuccess);
		}
		reportSuccess(reportOperation);
		return ris;
	}
}

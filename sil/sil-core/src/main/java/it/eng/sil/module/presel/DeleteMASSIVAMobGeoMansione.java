package it.eng.sil.module.presel;

import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.utils.ReportOperationResult;
import it.eng.sil.module.AbstractSimpleModule;

/**
 * 
 * @author rolfini
 * 
 */

public class DeleteMASSIVAMobGeoMansione extends AbstractSimpleModule {
	public void service(SourceBean request, SourceBean response) {
		ReportOperationResult reportOperation = new ReportOperationResult(this, response);
		int idSuccess = this.disableMessageIdSuccess();
		int idFail = this.disableMessageIdFail();

		String prgStr = (String) request.getAttribute("PRGMANSIONECANCMASSIVA");
		String prg = null;
		int virgola;
		boolean ret = true;

		while (prgStr.length() > 0) {
			virgola = prgStr.indexOf(',');
			if (virgola != -1) {
				prg = prgStr.substring(0, virgola);
				prgStr = prgStr.substring(virgola + 1, prgStr.length()); // cancello
																			// il
																			// progressivo
																			// trovato
			} else { // prgStr è >0, ma non abbiamo virgole. Ciò significa
						// necessariamente
				prg = prgStr; // che la stringa è composta da un solo codice.
				prgStr = ""; // ora che abbiamo consumato l'ultimo codice
								// impostiamo prgStr a null
			}
			if (prg != null) {
				try {
					setKeyinRequest(prg, request);
					doDelete(request, response);
				} catch (Exception ex) {
					reportOperation.reportFailure(idFail);
					ret = false;
				}
			}
		}
		if (ret) {
			reportOperation.reportSuccess(idSuccess);
		}
		reportSuccess(reportOperation);
	}

	private void setKeyinRequest(Object prg, SourceBean request) throws Exception {
		if (request.getAttribute("PRGMANSIONE") != null) {
			request.delAttribute("PRGMANSIONE");
		}
		request.setAttribute("PRGMANSIONE", prg);
	}

}
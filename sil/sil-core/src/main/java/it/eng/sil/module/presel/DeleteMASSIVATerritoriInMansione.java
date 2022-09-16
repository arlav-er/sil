package it.eng.sil.module.presel;

import java.util.ArrayList;

import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.utils.MessageCodes;
import it.eng.afExt.utils.ReportOperationResult;
import it.eng.sil.module.AbstractSimpleModule;

/**
 * 
 * @author olivieri
 * 
 */

public class DeleteMASSIVATerritoriInMansione extends AbstractSimpleModule {
	public void service(SourceBean request, SourceBean response) {

		ArrayList list1 = new ArrayList();
		ArrayList list2 = new ArrayList();
		ArrayList list3 = new ArrayList();

		String strPrgComune = (String) request.getAttribute("PRGCOMUNECANCMASSIVA");
		String strPrgProvincia = (String) request.getAttribute("PRGPROVINCIACANCMASSIVA");
		String strPrgRegione = (String) request.getAttribute("PRGREGIONECANCMASSIVA");
		String strPrgStato = (String) request.getAttribute("PRGSTATOCANCMASSIVA");

		list1.add(0, strPrgComune);
		list2.add(0, "PRGDISCOMUNE");
		list3.add(0, "QUERY_DEL_COMUNE");
		list1.add(1, strPrgProvincia);
		list2.add(1, "PRGDISPROVINCIA");
		list3.add(1, "QUERY_DEL_PROVINCIA");
		list1.add(2, strPrgRegione);
		list2.add(2, "PRGDISREGIONE");
		list3.add(2, "QUERY_DEL_REGIONE");
		list1.add(3, strPrgStato);
		list2.add(3, "PRGDISSTATO");
		list3.add(3, "QUERY_DEL_STATO");

		ReportOperationResult reportOperation = new ReportOperationResult(this, response);
		int idSuccess = this.disableMessageIdSuccess();
		int idFail = this.disableMessageIdFail();

		String prg = "";
		String prgStr = "";
		int virgola;
		boolean ret = true;

		int size = list1.size();
		int i = 0;
		prgStr = (String) list1.get(0);
		boolean finito = false;

		while (!finito) {
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
			if ((prg != null) && (!"".equals(prg))) {
				try {
					setKeyinRequest(prg, (String) list2.get(i), request);
					setSectionQueryDelete((String) list3.get(i));
					doDelete(request, response);
				} catch (Exception ex) {
					reportOperation.reportFailure(idFail);
					ret = false;
				}
			}
			if (prgStr.length() == 0) {
				i = i + 1;
				if (i < size) {
					prgStr = (String) list1.get(i);
				} else {
					finito = true;
				}
			}
		}
		if (ret) {
			this.setMessageIdSuccess(idSuccess);
			this.setMessageIdFail(idFail);
			reportOperation.reportSuccess(idSuccess);
		} else {
			reportOperation.reportFailure(MessageCodes.General.DELETE_FAIL);
		}

	}

	private void setKeyinRequest(Object prg, String strPrg, SourceBean request) throws Exception {
		if (request.getAttribute(strPrg) != null) {
			request.delAttribute(strPrg);
		}
		request.setAttribute(strPrg, prg);
	}

}
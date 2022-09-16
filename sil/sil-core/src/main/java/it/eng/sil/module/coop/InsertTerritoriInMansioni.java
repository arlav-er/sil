/*
 * Creato il 14-giu-06
 *
 * Per modificare il modello associato a questo file generato, aprire
 * Finestra&gt;Preferenze&gt;Java&gt;Generazione codice&gt;Codice e commenti
 */
package it.eng.sil.module.coop;

import java.math.BigDecimal;
import java.util.Vector;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.base.SourceBeanAttribute;
import com.engiweb.framework.base.SourceBeanException;

/**
 * @author riccardi
 * 
 *         Per modificare il modello associato al commento di questo tipo generato, aprire
 *         Finestra&gt;Preferenze&gt;Java&gt;Generazione codice&gt;Codice e commenti
 */
public class InsertTerritoriInMansioni extends CoopDoInsertModule {

	public boolean doInsertModule(SourceBean serviceRequest, SourceBean serviceResponse) {

		try {
			SourceBean mansione = doSelect(serviceRequest, serviceResponse);
			BigDecimal prgMansione = (BigDecimal) mansione.getAttribute("ROW.PRGMANSIONE");

			Vector territori = serviceRequest.getContainedSourceBeanAttributes();
			for (int i = 0; i < territori.size(); i++) {
				SourceBeanAttribute territorio = (SourceBeanAttribute) territori.get(i);
				String stmt = territorio.getKey();
				SourceBean territorioSB = (SourceBean) territorio.getValue();
				if (territorioSB.containsAttribute("ROWS.ROW")) {
					setSectionQueryInsert(stmt);
					Vector territorioColumn = territorioSB.getAttributeAsVector("ROWS.ROW");
					for (int j = 0; j < territorioColumn.size(); j++) {
						SourceBean row = (SourceBean) territorioColumn.get(j);
						row.updAttribute("prgmansione", prgMansione);
						row.updAttribute("cdnlavoratore", serviceRequest.getAttribute("cdnlavoratore"));
						getRequestContainer().setServiceRequest(row);
						boolean ris = doInsert(row, serviceResponse);
						if (!ris) {
							return false;
						}
					}
				}
			}
			return true;
		} catch (SourceBeanException e) {
			return false;
		}
	}
}

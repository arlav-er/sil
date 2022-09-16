/*
 * Creato il 20-giu-06
 *
 * Per modificare il modello associato a questo file generato, aprire
 * Finestra&gt;Preferenze&gt;Java&gt;Generazione codice&gt;Codice e commenti
 */
package it.eng.sil.module.coop;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Vector;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.base.SourceBeanAttribute;

/**
 * @author riccardi
 * 
 *         Per modificare il modello associato al commento di questo tipo generato, aprire
 *         Finestra&gt;Preferenze&gt;Java&gt;Generazione codice&gt;Codice e commenti
 */
public class DeleteTerritoriInMansioni extends CoopDoDeleteModule {

	public boolean doDeleteModule(SourceBean serviceRequest, SourceBean serviceResponse) {
		Vector mansioniRow = serviceRequest.getAttributeAsVector("mansioni.rows.row");
		HashSet hashTerritori = new HashSet();
		for (int i_man = 0; i_man < mansioniRow.size(); i_man++) {
			SourceBean mansioneRow = (SourceBean) mansioniRow.get(i_man);
			// Vector mansioni =
			// serviceRequest.getContainedSourceBeanAttributes();
			Vector territori = mansioneRow.getContainedSourceBeanAttributes();
			for (int j = 0; j < territori.size(); j++) {
				SourceBeanAttribute territorio = (SourceBeanAttribute) territori.get(j);
				String stmt = territorio.getKey();
				SourceBean territorioSB = (SourceBean) territorio.getValue();
				if (territorioSB.containsAttribute("ROWS.ROW")) {
					hashTerritori.add(stmt);
				}
			}
		}
		if (!hashTerritori.isEmpty()) {
			for (Iterator iterTerr = hashTerritori.iterator(); iterTerr.hasNext();) {
				String stm = (String) iterTerr.next();
				setSectionQueryDelete(stm);
				boolean ris = doDelete(serviceRequest, serviceResponse);
				if (!ris)
					return false;
			}
		}
		hashTerritori.clear();

		return true;
	}
}
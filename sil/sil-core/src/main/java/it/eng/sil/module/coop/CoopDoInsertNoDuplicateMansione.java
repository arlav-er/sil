/*
 * Creato il 23-giu-06
 *
 * Per modificare il modello associato a questo file generato, aprire
 * Finestra&gt;Preferenze&gt;Java&gt;Generazione codice&gt;Codice e commenti
 */
package it.eng.sil.module.coop;

import com.engiweb.framework.base.SourceBean;

/**
 * @author riccardi
 * 
 *         Per modificare il modello associato al commento di questo tipo generato, aprire
 *         Finestra&gt;Preferenze&gt;Java&gt;Generazione codice&gt;Codice e commenti
 */
public class CoopDoInsertNoDuplicateMansione extends CoopDoInsertModule {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger
			.getLogger(CoopDoInsertNoDuplicateMansione.class.getName());

	public boolean doInsertModule(SourceBean serviceRequest, SourceBean serviceResponse) {
		boolean res = false;
		SourceBean mansione = doSelect(serviceRequest, serviceResponse, false);
		try {
			Object prgMansione = mansione.getAttribute("row.prgMansione");
			if (prgMansione == null) {
				res = doInsert(serviceRequest, serviceResponse);
			} else
				res = true;
		} catch (Exception e) {
			it.eng.sil.util.TraceWrapper.debug(_logger,
					"Impossibile recuperare il prgMansione. Inserimento non possibile.", e);

		}
		return res;
	}
}

/*
 * Creato il 1-set-06
 *
 * Per modificare il modello associato a questo file generato, aprire
 * Finestra&gt;Preferenze&gt;Java&gt;Generazione codice&gt;Codice e commenti
 */
package it.eng.sil.module.anag;

import com.engiweb.framework.base.SourceBean;

import it.eng.sil.module.AbstractSimpleModule;

/**
 * @author riccardi
 * 
 *         Per modificare il modello associato al commento di questo tipo generato, aprire
 *         Finestra&gt;Preferenze&gt;Java&gt;Generazione codice&gt;Codice e commenti
 */
public class CheckEsistenzaDocTrasferimento extends AbstractSimpleModule {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger
			.getLogger(CheckEsistenzaDocTrasferimento.class.getName());

	public void service(SourceBean request, SourceBean response) {
		try {
			SourceBean prgdocumentocollSB = doSelect(request, response);
			if (prgdocumentocollSB.containsAttribute("ROW.prgdocumentocoll")) {
				response.setAttribute("stampaRichDocTrasf", "true");
			} else {
				response.setAttribute("stampaRichDocTrasf", "false");
			}
		} catch (Exception e) {
			e.printStackTrace();
			it.eng.sil.util.TraceWrapper.debug(_logger, "Progressivo documentocoll non recuperabile.", e);

		}
	}
}
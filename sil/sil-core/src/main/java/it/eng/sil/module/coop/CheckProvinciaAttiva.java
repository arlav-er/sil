/*
 * Creato il 14-lug-06
 *
 * Per modificare il modello associato a questo file generato, aprire
 * Finestra&gt;Preferenze&gt;Java&gt;Generazione codice&gt;Codice e commenti
 */
package it.eng.sil.module.coop;

import com.engiweb.framework.base.SourceBean;

import it.eng.sil.module.AbstractSimpleModule;

/**
 * @author riccardi
 * 
 *         Per modificare il modello associato al commento di questo tipo generato, aprire
 *         Finestra&gt;Preferenze&gt;Java&gt;Generazione codice&gt;Codice e commenti
 */
public class CheckProvinciaAttiva extends AbstractSimpleModule {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(CheckProvinciaAttiva.class.getName());

	public void service(SourceBean request, SourceBean response) {
		SourceBean flagSB = doSelect(request, response);
		try {
			String flag = (String) flagSB.getAttribute("row.flgpoloattivo");
			if (flag != null && flag.equals("S")) {
				response.setAttribute("poloInCoop", "true");
			} else {
				response.setAttribute("poloInCoop", "false");
			}
		} catch (Exception e) {
			e.printStackTrace();
			it.eng.sil.util.TraceWrapper.debug(_logger, "Impossibile sapere se il polo è in cooperazione.", e);

		}
	}
}
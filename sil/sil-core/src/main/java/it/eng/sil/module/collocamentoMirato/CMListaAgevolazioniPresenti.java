/*
 * Creato il 30-ago-07
 *
 * Per modificare il modello associato a questo file generato, aprire
 * Finestra&gt;Preferenze&gt;Java&gt;Generazione codice&gt;Codice e commenti
 */
package it.eng.sil.module.collocamentoMirato;

import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.dispatching.module.impl.ListModule;

/**
 * @author riccardi
 * 
 *         Per modificare il modello associato al commento di questo tipo generato, aprire
 *         Finestra&gt;Preferenze&gt;Java&gt;Generazione codice&gt;Codice e commenti
 */

public class CMListaAgevolazioniPresenti extends ListModule {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger
			.getLogger(CMListaAgevolazioniPresenti.class.getName());

	public void service(SourceBean request, SourceBean response) {
		String className = this.getClass().getName();
		try {
			request.delAttribute("LIST_PAGE");
			request.setAttribute("LIST_PAGE", "1");
			super.service(request, response);
		} catch (Exception e) {
			_logger.error(className + "::service: " + e.getMessage());

		}

	}
}
/*
 * Creato il 2-set-04
 *
 * Per modificare il modello associato a questo file generato, aprire
 * Finestra&gt;Preferenze&gt;Java&gt;Generazione codice&gt;Codice e commenti
 */
package it.eng.sil.module.patto;

import com.engiweb.framework.base.SourceBean;

import it.eng.sil.module.AbstractSimpleModule;

/**
 * @author savino
 * 
 *         Per modificare il modello associato al commento di questo tipo generato, aprire
 *         Finestra&gt;Preferenze&gt;Java&gt;Generazione codice&gt;Codice e commenti
 */
public class ModifichePattoMansioni extends AbstractSimpleModule {
	public void service(SourceBean request, SourceBean response) throws Exception {

		doSelect(request, response);

	}
}

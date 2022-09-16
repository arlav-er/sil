/*
 * Creato il 4-lug-06
 *
 * Per modificare il modello associato a questo file generato, aprire
 * Finestra&gt;Preferenze&gt;Java&gt;Generazione codice&gt;Codice e commenti
 */
package it.eng.sil.module.amministrazione;

import com.engiweb.framework.base.SourceBean;

/**
 * @author gritti
 * 
 * Per modificare il modello associato al commento di questo tipo generato,
 * aprire Finestra&gt;Preferenze&gt;Java&gt;Generazione codice&gt;Codice e
 * commenti
 */

import it.eng.sil.module.AbstractSimpleModule;

public class ASGetValISEE extends AbstractSimpleModule {
	public void service(SourceBean request, SourceBean response) throws Exception {

		doSelect(request, response);
	}
}

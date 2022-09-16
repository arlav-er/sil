/*
 * Creato il 26-lug-07
 *
 * Per modificare il modello associato a questo file generato, aprire
 * Finestra&gt;Preferenze&gt;Java&gt;Generazione codice&gt;Codice e commenti
 */
package it.eng.sil.module.movimenti;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.base.SourceBeanException;

import it.eng.sil.module.AbstractSimpleModule;

/**
 * @author riccardi
 * 
 *         Per modificare il modello associato al commento di questo tipo generato, aprire
 *         Finestra&gt;Preferenze&gt;Java&gt;Generazione codice&gt;Codice e commenti
 */
public class M_MovGetDettaglioApp extends AbstractSimpleModule {
	public M_MovGetDettaglioApp() {
	}

	public void service(SourceBean request, SourceBean response) throws SourceBeanException {
		doDynamicSelect(request, response);
	}
}

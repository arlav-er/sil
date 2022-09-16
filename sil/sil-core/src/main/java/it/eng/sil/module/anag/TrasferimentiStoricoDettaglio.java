/*
 * Creato il 2-nov-04
 *
 */
package it.eng.sil.module.anag;

import com.engiweb.framework.base.SourceBean;

import it.eng.sil.module.AbstractSimpleModule;

/**
 * @author giuliani
 * 
 *         Per modificare il modello associato al commento di questo tipo generato, aprire
 *         Finestra&gt;Preferenze&gt;Java&gt;Generazione codice&gt;Codice e commenti
 */
public class TrasferimentiStoricoDettaglio extends AbstractSimpleModule {

	public void service(SourceBean request, SourceBean response) throws Exception {
		doSelect(request, response);
	}

}

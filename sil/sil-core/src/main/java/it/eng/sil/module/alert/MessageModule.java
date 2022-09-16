/*
 * Creato il 21-gen-05
 *
 * Per modificare il modello associato a questo file generato, aprire
 * Finestra&gt;Preferenze&gt;Java&gt;Generazione codice&gt;Codice e commenti
 */
package it.eng.sil.module.alert;

import com.engiweb.framework.base.SourceBean;

import it.eng.sil.module.AbstractSimpleModule;

/**
 * @author Togna Cosimo
 * @author D'Auria Giovanni
 * 
 *         Per modificare il modello associato al commento di questo tipo generato, aprire
 *         Finestra&gt;Preferenze&gt;Java&gt;Generazione codice&gt;Codice e commenti
 */
public class MessageModule extends AbstractSimpleModule {

	public MessageModule() {
		super();
	}

	public void service(SourceBean serviceRequest, SourceBean serviceResponse) throws Exception {
		doSelect(serviceRequest, serviceResponse);
	}

}

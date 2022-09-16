/*
 * Creato il 5-gen-05
 *
 * Per modificare il modello associato a questo file generato, aprire
 * Finestra&gt;Preferenze&gt;Java&gt;Generazione codice&gt;Codice e commenti
 */
package it.eng.sil.module.ido;

import it.eng.sil.module.AbstractSimpleModule;

import com.engiweb.framework.base.SourceBean;

/**
 * @author Togna
 * @author D'Auria
 * 
 * Recupera l'indirizzo email dell'azienda
 */
public class GetEmailAzienda extends AbstractSimpleModule {

	/*
	 * (non Javadoc)
	 * 
	 * @see com.engiweb.framework.dispatching.service.ServiceIFace#service(com.engiweb.framework.base.SourceBean,
	 *      com.engiweb.framework.base.SourceBean)
	 */
	public void service(SourceBean request, SourceBean response) throws Exception {
		doSelect(request, response);

	}

}

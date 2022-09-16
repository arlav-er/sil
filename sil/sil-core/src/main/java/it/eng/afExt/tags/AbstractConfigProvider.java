/*
 * Creato il 10-mar-04
 *
 * Per modificare il modello associato a questo file generato, aprire
 * Finestra&gt;Preferenze&gt;Java&gt;Generazione codice&gt;Codice e commenti
 */
package it.eng.afExt.tags;

import com.engiweb.framework.base.SessionContainer;
import com.engiweb.framework.base.SourceBean;

/**
 * @author vuoto
 * 
 *         Per modificare il modello associato al commento di questo tipo generato, aprire
 *         Finestra&gt;Preferenze&gt;Java&gt;Generazione codice&gt;Codice e commenti
 */
abstract public class AbstractConfigProvider implements IDynamicConfigProvider {
	private SessionContainer sessionContainer = null;

	abstract public SourceBean getConfigSourceBean(SourceBean serviceRequest, SourceBean serviceResponse);

	/**
	 * @return
	 */
	public SessionContainer getSessionContainer() {
		return sessionContainer;
	}

	/**
	 * @param container
	 */
	public void setSessionContainer(SessionContainer container) {
		sessionContainer = container;
	}

}

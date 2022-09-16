/*
 * Creato il 28-gen-04
 *
 * Per modificare il modello associato a questo file generato, aprire
 * Finestra&gt;Preferenze&gt;Java&gt;Generazione codice&gt;Codice e commenti
 */
package it.eng.afExt.tags;

import com.engiweb.framework.base.SourceBean;

/**
 * @author vuoto
 * 
 *         Per modificare il modello associato al commento di questo tipo generato, aprire
 *         Finestra&gt;Preferenze&gt;Java&gt;Generazione codice&gt;Codice e commenti
 */
public interface IDynamicConfigProvider {
	public SourceBean getConfigSourceBean(SourceBean serviceRequest, SourceBean serviceResponse);
}

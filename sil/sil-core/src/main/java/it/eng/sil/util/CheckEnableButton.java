/*
 * Creato il 7-giu-06
 *
 * Per modificare il modello associato a questo file generato, aprire
 * Finestra&gt;Preferenze&gt;Java&gt;Generazione codice&gt;Codice e commenti
 */
package it.eng.sil.util;

import com.engiweb.framework.base.SourceBean;

import it.eng.sil.module.AbstractSimpleModule;

/**
 * @author landi
 * 
 *         Per modificare il modello associato al commento di questo tipo generato, aprire
 *         Finestra&gt;Preferenze&gt;Java&gt;Generazione codice&gt;Codice e commenti
 */
public class CheckEnableButton extends AbstractSimpleModule {

	public void service(SourceBean request, SourceBean response) throws Exception {
		SourceBean rowButton = null;
		rowButton = new SourceBean("ROW");
		rowButton.setAttribute("BUTTON_MOB", "TRUE");
		response.setAttribute((SourceBean) rowButton);
	}
}

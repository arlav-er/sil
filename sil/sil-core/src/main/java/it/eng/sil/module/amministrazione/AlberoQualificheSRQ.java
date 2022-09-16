/*
 * Creato il 15-giu-06
 *
 * Per modificare il modello associato a questo file generato, aprire
 * Finestra&gt;Preferenze&gt;Java&gt;Generazione codice&gt;Codice e commenti
 */
package it.eng.sil.module.amministrazione;

import com.engiweb.framework.base.SourceBean;

import it.eng.sil.module.AbstractSimpleModule;

/**
 * @author landi
 * 
 *         Per modificare il modello associato al commento di questo tipo generato, aprire
 *         Finestra&gt;Preferenze&gt;Java&gt;Generazione codice&gt;Codice e commenti
 */
public class AlberoQualificheSRQ extends AbstractSimpleModule {

	public void init(SourceBean config) {
		// Insert here your initiliazation code
	}

	public void service(SourceBean request, SourceBean response) throws Exception {
		String codPadre = (String) request.getAttribute("padre");
		response.setAttribute(SourceBean.fromXMLString(QualificheSRQ.getMansioniFiglie(codPadre)));
	}
}

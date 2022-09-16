/*
 * Creato il 27-ago-04
 *
 * Per modificare il modello associato a questo file generato, aprire
 * Finestra&gt;Preferenze&gt;Java&gt;Generazione codice&gt;Codice e commenti
 */
package it.eng.sil.module.movimenti.trasfRamoAzienda;

import com.engiweb.framework.base.SourceBean;

import it.eng.sil.module.AbstractSimpleModule;

/**
 * Classe per estrazione dati per combo motivo cessazione
 * 
 * @author roccetti
 */
public class GetMotivoCess extends AbstractSimpleModule {

	/**
	 * 
	 */
	public void service(SourceBean request, SourceBean response) throws Exception {
		doSelect(request, response);
	}

}

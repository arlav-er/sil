/*
 * Creato il 29-lug-04
 */
package it.eng.sil.module.movimenti;

import com.engiweb.framework.base.SourceBean;

import it.eng.sil.module.AbstractSimpleModule;

/**
 * @author roccetti
 * 
 *         Esegue l'aggiornamento dell'azienda con i dati selezionati
 */
public class AggiornaAzienda extends AbstractSimpleModule {

	public void service(SourceBean request, SourceBean response) throws Exception {
		doDynamicUpdate(request, response);
	}

}

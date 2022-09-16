/*
 * Creato il 29-giu-04
 *
 * Per modificare il modello associato a questo file generato, aprire
 * Finestra&gt;Preferenze&gt;Java&gt;Generazione codice&gt;Codice e commenti
 */
package it.eng.sil.module.anag;

import com.engiweb.framework.base.SourceBean;

import it.eng.sil.module.AbstractSimpleModule;

/**
 * @author roccetti
 * 
 *         !!!! QUESTA CLASSE PER ORA NON è UTILIZZATA !!! Classe per l'aggiornamento del CPI lavoratore sul DB locale
 *         dopo un'interrogazione sull'indice regionale.
 */
public class UpdateCpiLav extends AbstractSimpleModule {

	/**
	 * @see com.engiweb.framework.dispatching.module.AbstractModule#service(com.engiweb.framework.base.SourceBean,
	 *      com.engiweb.framework.base.SourceBean)
	 */
	public void service(SourceBean request, SourceBean response) throws Exception {
		doUpdate(request, response);
	}
}

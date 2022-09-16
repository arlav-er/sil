/*
 * Creato il 24-giu-04
 *
 * Per modificare il modello associato a questo file generato, aprire
 * Finestra&gt;Preferenze&gt;Java&gt;Generazione codice&gt;Codice e commenti
 */
package it.eng.sil.module.movimenti;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.base.SourceBeanException;

import it.eng.sil.module.AbstractSimpleModule;

/**
 * @author roccetti
 * 
 *         Classe per il modulo che esegue l'a selezione dei tipi di movimento inseribili in base al codmonotempo del
 *         movimento precedente (scarta la proroga se TI).
 */
public class ComboTipoMovColl extends AbstractSimpleModule {

	public ComboTipoMovColl() {
	}

	public void service(SourceBean request, SourceBean response) throws SourceBeanException {
		// Eseguo la query di ricerca dinamica per riempire la combo
		doDynamicSelect(request, response);
	}
}

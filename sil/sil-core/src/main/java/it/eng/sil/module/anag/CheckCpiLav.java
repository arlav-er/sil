/*
 * Creato il 29-giu-04
 */
package it.eng.sil.module.anag;

import com.engiweb.framework.base.SourceBean;

import it.eng.sil.module.AbstractSimpleModule;

/**
 * @author roccetti
 * 
 *         !!!! QUESTA CLASSE PER ORA NON è UTILIZZATA !!! Classe per il controllo sull'indice regionale del Cpi del
 *         lavoratore dato il suo cdnLavoratore su DB.
 */
public class CheckCpiLav extends AbstractSimpleModule {

	/**
	 * @see com.engiweb.framework.dispatching.module.AbstractModule#service(com.engiweb.framework.base.SourceBean,
	 *      com.engiweb.framework.base.SourceBean)
	 */
	public void service(SourceBean request, SourceBean response) throws Exception {
		response.setAttribute("trasferisci", request.getAttribute("trasferisci"));
	}

}

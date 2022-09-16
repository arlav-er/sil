/*
 * Creato il 29-giu-04
 */
package it.eng.sil.module.anag;

import com.engiweb.framework.base.SourceBean;

import it.eng.sil.module.AbstractSimpleModule;

/**
 * @author roccetti
 * 
 *         Recupero dati del trasferimento del Lavoratore.
 */
public class GetDettTrasfLav extends AbstractSimpleModule {

	/**
	 * @see com.engiweb.framework.dispatching.module.AbstractModule#service(com.engiweb.framework.base.SourceBean,
	 *      com.engiweb.framework.base.SourceBean)
	 */
	public void service(SourceBean request, SourceBean response) throws Exception {
		doSelect(request, response);
	}
}

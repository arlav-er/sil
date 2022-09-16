/*
 * Creato il 2-lug-04
 */
package it.eng.sil.module.anag;

import com.engiweb.framework.base.SourceBean;

import it.eng.sil.module.AbstractSimpleModule;

/**
 * @author roccetti
 * 
 *         Recupera la descrizione del CPI passato alla query
 */
public class GetDescrCpi extends AbstractSimpleModule {
	/**
	 * @see com.engiweb.framework.dispatching.module.AbstractModule#service(com.engiweb.framework.base.SourceBean,
	 *      com.engiweb.framework.base.SourceBean)
	 */
	public void service(SourceBean request, SourceBean response) throws Exception {
		doSelect(request, response);
	}
}

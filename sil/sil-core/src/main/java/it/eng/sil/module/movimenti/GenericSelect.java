/*
 * Creato il 26-ott-04
 */
package it.eng.sil.module.movimenti;

import com.engiweb.framework.base.SourceBean;

import it.eng.sil.module.AbstractSimpleModule;

/**
 * Esegue una select standard statica sul DB.
 * 
 * @author roccetti
 */
public class GenericSelect extends AbstractSimpleModule {

	public void service(SourceBean serviceRequest, SourceBean serviceResponse) throws Exception {
		doSelect(serviceRequest, serviceResponse);
	}
}

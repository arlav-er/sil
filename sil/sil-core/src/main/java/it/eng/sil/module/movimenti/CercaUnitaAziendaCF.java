/*
 * Creato il 21-ott-04
 */
package it.eng.sil.module.movimenti;

import com.engiweb.framework.base.SourceBean;

import it.eng.sil.module.AbstractSimpleModule;

/**
 * @author roccetti
 */
public class CercaUnitaAziendaCF extends AbstractSimpleModule {

	public void service(SourceBean request, SourceBean response) throws Exception {
		doDynamicSelect(request, response);
	}
}

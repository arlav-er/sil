/*
 * Creato il 8-giu-05
 *
 */
package it.eng.sil.module.movimenti;

import com.engiweb.framework.base.SourceBean;

import it.eng.sil.module.AbstractSimpleModule;

/**
 * @author D'Auria
 * 
 */
public class GetMovimentiFiltrati extends AbstractSimpleModule {

	public void service(SourceBean request, SourceBean response) throws Exception {
		doDynamicSelect(request, response);
	}
}

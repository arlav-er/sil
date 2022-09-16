/*
 * Creato il Dec 30, 2004
 * 
 */
package it.eng.sil.module.patto;

/**
 * @author savino
 * 
 */
import com.engiweb.framework.base.SourceBean;

import it.eng.sil.module.AbstractSimpleModule;

public class GetPattoInfoCollegate extends AbstractSimpleModule {
	public void service(SourceBean request, SourceBean response) {
		doDynamicSelect(request, response);
	}
}

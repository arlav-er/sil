/*
 * Creato il 13-set-04
 *
 */
package it.eng.sil.module.movimenti;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.base.SourceBeanException;

import it.eng.sil.module.AbstractSimpleModule;

/**
 * @author roccetti
 */
public class CercaDIDPerMovNonCollegati extends AbstractSimpleModule {

	public void service(SourceBean request, SourceBean response) throws SourceBeanException {
		doSelect(request, response);
	}
}

package it.eng.sil.module;

import com.engiweb.framework.base.SourceBean;

/**
 * Invoca la doDelete() di AbstractSimpleModule. Il comportamento varia a seconda della busta di CONFIG XML del modulo.
 */
public class doDeleteModule extends AbstractSimpleModule {

	public void service(SourceBean request, SourceBean response) {

		doDelete(request, response);
	}

}

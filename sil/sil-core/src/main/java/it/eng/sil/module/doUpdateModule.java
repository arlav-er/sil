package it.eng.sil.module;

import com.engiweb.framework.base.SourceBean;

/**
 * Invoca la doUpdate() di AbstractSimpleModule. Il comportamento varia a seconda della busta di CONFIG XML del modulo.
 */
public class doUpdateModule extends AbstractSimpleModule {

	public void service(SourceBean request, SourceBean response) {

		doUpdate(request, response);
	}

}

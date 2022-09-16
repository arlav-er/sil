package it.eng.sil.module;

import com.engiweb.framework.base.SourceBean;

/**
 * Invoca la doSelect() di AbstractSimpleModule. Il comportamento varia a seconda della busta di CONFIG XML del modulo.
 */
public class doSelectModule extends AbstractSimpleModule {

	public void service(SourceBean request, SourceBean response) {

		doSelect(request, response);
	}

}

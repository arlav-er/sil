package it.eng.sil.module.agenda;

import com.engiweb.framework.base.SourceBean;

import it.eng.sil.module.AbstractSimpleModule;

public class MAggiornaOperatore extends AbstractSimpleModule {
	public MAggiornaOperatore() {
	}

	public void service(SourceBean request, SourceBean response) {
		doUpdate(request, response);
	}
}

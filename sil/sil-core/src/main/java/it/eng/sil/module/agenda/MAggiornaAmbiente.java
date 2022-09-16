package it.eng.sil.module.agenda;

import com.engiweb.framework.base.SourceBean;

import it.eng.sil.module.AbstractSimpleModule;

public class MAggiornaAmbiente extends AbstractSimpleModule {
	public MAggiornaAmbiente() {
	}

	public void service(SourceBean request, SourceBean response) {
		doUpdate(request, response);
	}
}

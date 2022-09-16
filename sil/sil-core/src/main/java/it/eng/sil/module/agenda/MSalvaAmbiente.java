package it.eng.sil.module.agenda;

import com.engiweb.framework.base.SourceBean;

import it.eng.sil.module.AbstractSimpleModule;

public class MSalvaAmbiente extends AbstractSimpleModule {
	public MSalvaAmbiente() {
	}

	public void service(SourceBean request, SourceBean response) {
		doInsert(request, response);
	}
}

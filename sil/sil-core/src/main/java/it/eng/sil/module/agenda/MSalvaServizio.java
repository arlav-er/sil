package it.eng.sil.module.agenda;

import com.engiweb.framework.base.SourceBean;

import it.eng.sil.module.AbstractSimpleModule;

public class MSalvaServizio extends AbstractSimpleModule {
	public MSalvaServizio() {
	}

	public void service(SourceBean request, SourceBean response) {
		doInsert(request, response);
	}
}

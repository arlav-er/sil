package it.eng.sil.module.agenda;

import com.engiweb.framework.base.SourceBean;

import it.eng.sil.module.AbstractSimpleModule;

public class MSalvaOperatore extends AbstractSimpleModule {
	public MSalvaOperatore() {
	}

	public void service(SourceBean request, SourceBean response) {
		doInsert(request, response, "prgSpi");
	}
}

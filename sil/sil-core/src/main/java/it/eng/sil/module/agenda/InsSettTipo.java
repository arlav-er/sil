package it.eng.sil.module.agenda;

import com.engiweb.framework.base.SourceBean;

import it.eng.sil.module.AbstractSimpleModule;

public class InsSettTipo extends AbstractSimpleModule {
	public InsSettTipo() {
	}

	public void service(SourceBean request, SourceBean response) {
		doInsert(request, response);
	}

}

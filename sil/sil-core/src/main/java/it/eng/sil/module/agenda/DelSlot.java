package it.eng.sil.module.agenda;

import com.engiweb.framework.base.SourceBean;

import it.eng.sil.module.AbstractSimpleModule;

public class DelSlot extends AbstractSimpleModule {
	public DelSlot() {
	}

	public void service(SourceBean request, SourceBean response) {
		doDelete(request, response);
	}
}
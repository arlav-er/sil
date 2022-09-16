package it.eng.sil.module.agenda;

import com.engiweb.framework.base.SourceBean;

import it.eng.sil.module.AbstractSimpleModule;

public class DelSlotTipo extends AbstractSimpleModule {
	public DelSlotTipo() {
	}

	public void service(SourceBean request, SourceBean response) {
		doDelete(request, response);
	}
}
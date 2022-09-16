package it.eng.sil.module.agenda;

import com.engiweb.framework.base.SourceBean;

import it.eng.sil.module.AbstractSimpleModule;

public class UpdSlotTipo extends AbstractSimpleModule {
	public UpdSlotTipo() {
	}

	public void service(SourceBean request, SourceBean response) {
		doUpdateNoDuplicate(request, response, "PRGGIORNO");
	}

}
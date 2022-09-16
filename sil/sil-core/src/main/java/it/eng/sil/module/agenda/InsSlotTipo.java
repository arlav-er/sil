package it.eng.sil.module.agenda;

import com.engiweb.framework.base.SourceBean;

import it.eng.sil.module.AbstractSimpleModule;

public class InsSlotTipo extends AbstractSimpleModule {
	public InsSlotTipo() {
	}

	public void service(SourceBean request, SourceBean response) {
		doInsertNoDuplicate(request, response);
	}
}
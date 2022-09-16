package it.eng.sil.module.agenda;

import com.engiweb.framework.base.SourceBean;

import it.eng.sil.module.AbstractSimpleModule;

public class MAggiornaAssegnazione extends AbstractSimpleModule {
	public MAggiornaAssegnazione() {
	}

	public void service(SourceBean request, SourceBean response) {
		doUpdateNoDuplicate(request, response, "PRGASSEGNAZIONE");
	}
}

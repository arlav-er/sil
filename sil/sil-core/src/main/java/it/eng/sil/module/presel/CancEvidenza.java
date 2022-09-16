package it.eng.sil.module.presel;

import com.engiweb.framework.base.SourceBean;

import it.eng.sil.module.AbstractSimpleModule;

public class CancEvidenza extends AbstractSimpleModule {
	public CancEvidenza() {
	}

	public void service(SourceBean request, SourceBean response) {
		doDelete(request, response);
	}
}
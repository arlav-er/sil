package it.eng.sil.module.movimenti;

import com.engiweb.framework.base.SourceBean;

import it.eng.sil.module.AbstractSimpleModule;

public class M_MovGetDettaglioAppoggio extends AbstractSimpleModule {
	public M_MovGetDettaglioAppoggio() {
	}

	public void service(SourceBean request, SourceBean response) {
		doSelect(request, response);
	}
}
package it.eng.sil.module.movimenti;

import com.engiweb.framework.base.SourceBean;

import it.eng.sil.module.AbstractSimpleModule;

public class GetDettaglioMovimento extends AbstractSimpleModule {
	private String className;

	public GetDettaglioMovimento() {
		className = this.getClass().getName();
	}

	public void service(SourceBean request, SourceBean response) {
		doSelect(request, response);
	}
}
package it.eng.sil.module.movimenti;

import com.engiweb.framework.base.SourceBean;

import it.eng.sil.module.AbstractSimpleModule;

public class SalvaMovimento extends AbstractSimpleModule {
	private String className;

	public SalvaMovimento() {
		className = this.getClass().getName();
	}

	public void service(SourceBean request, SourceBean response) {
		doUpdate(request, response);
	}
}
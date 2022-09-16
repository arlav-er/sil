package it.eng.sil.module.scadenziario;

import com.engiweb.framework.base.SourceBean;

import it.eng.sil.module.AbstractSimpleModule;

public class ScadGetServizio extends AbstractSimpleModule {
	private String className = this.getClass().getName();

	public void service(SourceBean request, SourceBean response) {

		doSelect(request, response);

	}
}// class ScadGetServizio

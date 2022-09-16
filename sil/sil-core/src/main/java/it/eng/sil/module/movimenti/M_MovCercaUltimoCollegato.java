package it.eng.sil.module.movimenti;

import com.engiweb.framework.base.SourceBean;

import it.eng.sil.module.AbstractSimpleModule;

public class M_MovCercaUltimoCollegato extends AbstractSimpleModule {
	private String className;

	public M_MovCercaUltimoCollegato() {
		className = this.getClass().getName();
	}

	public void service(SourceBean request, SourceBean response) {
		doSelect(request, response);
	}
}
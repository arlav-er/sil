package it.eng.sil.module.ido;

import com.engiweb.framework.base.SourceBean;

import it.eng.sil.module.AbstractSimpleModule;

public class InserisciModello extends AbstractSimpleModule {
	private String className = this.getClass().getName();

	public void service(SourceBean request, SourceBean response) {
		doInsert(request, response);
	}
}// class InserisciModello

package it.eng.sil.module.trento;

import com.engiweb.framework.base.SourceBean;

import it.eng.sil.module.AbstractSimpleModule;

public class MDettaglioClassificazione extends AbstractSimpleModule {

	public MDettaglioClassificazione() {
	}

	private String className = this.getClass().getName();

	public void service(SourceBean request, SourceBean response) throws Exception {
		doSelect(request, response);
	}
}
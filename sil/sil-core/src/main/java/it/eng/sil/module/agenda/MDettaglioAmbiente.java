package it.eng.sil.module.agenda;

import com.engiweb.framework.base.SourceBean;

import it.eng.sil.module.AbstractSimpleModule;

public class MDettaglioAmbiente extends AbstractSimpleModule {
	public MDettaglioAmbiente() {
	}

	private String className = this.getClass().getName();

	public void service(SourceBean request, SourceBean response) {
		doSelect(request, response);
	}
}
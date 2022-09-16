package it.eng.sil.module.preferenze;

import com.engiweb.framework.base.SourceBean;

import it.eng.sil.module.AbstractSimpleModule;

public class DettaglioTabDecod extends AbstractSimpleModule {
	private String className = this.getClass().getName();

	public DettaglioTabDecod() {
	}

	public void service(SourceBean request, SourceBean response) {

		SourceBean row = doDynamicSelect(request, response);

	}

}

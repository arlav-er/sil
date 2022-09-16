package it.eng.sil.module.anag;

import com.engiweb.framework.base.SourceBean;

import it.eng.sil.module.AbstractSimpleModule;

public class SaveLavoratoreRecapitiREC_uguale_DOM extends AbstractSimpleModule {
	public void service(SourceBean request, SourceBean response) {

		doUpdate(request, response);

	}

}
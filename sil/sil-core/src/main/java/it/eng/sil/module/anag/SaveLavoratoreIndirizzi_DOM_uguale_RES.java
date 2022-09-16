package it.eng.sil.module.anag;

import com.engiweb.framework.base.SourceBean;

import it.eng.sil.module.AbstractSimpleModule;

public class SaveLavoratoreIndirizzi_DOM_uguale_RES extends AbstractSimpleModule {
	public void service(SourceBean request, SourceBean response) {

		doUpdate(request, response);

	}

}
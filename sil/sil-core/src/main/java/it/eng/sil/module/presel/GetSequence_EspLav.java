package it.eng.sil.module.presel;

import com.engiweb.framework.base.SourceBean;

import it.eng.sil.module.AbstractSimpleModule;

public class GetSequence_EspLav extends AbstractSimpleModule {
	public void service(SourceBean request, SourceBean response) {

		doSelect(request, response);
	}

}
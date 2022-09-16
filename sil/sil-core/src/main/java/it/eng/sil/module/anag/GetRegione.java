package it.eng.sil.module.anag;

import com.engiweb.framework.base.SourceBean;

import it.eng.sil.module.AbstractSimpleModule;

public class GetRegione extends AbstractSimpleModule {

	public void service(SourceBean request, SourceBean response) {
		doSelect(request, response);
	}
}
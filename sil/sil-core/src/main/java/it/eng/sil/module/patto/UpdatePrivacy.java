package it.eng.sil.module.patto;

import com.engiweb.framework.base.SourceBean;

import it.eng.sil.module.AbstractSimpleModule;

public class UpdatePrivacy extends AbstractSimpleModule {
	public void service(SourceBean request, SourceBean response) throws Exception {
		doUpdate(request, response);
	}
}

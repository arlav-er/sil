package it.eng.sil.module.patto;

import com.engiweb.framework.base.SourceBean;

import it.eng.sil.module.AbstractSimpleModule;

public class DeletePercorsi extends AbstractSimpleModule {
	public void service(SourceBean request, SourceBean response) throws Exception {
		doDelete(request, response);
	}
}

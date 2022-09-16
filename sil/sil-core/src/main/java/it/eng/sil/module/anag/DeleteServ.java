package it.eng.sil.module.anag;

import com.engiweb.framework.base.SourceBean;

import it.eng.sil.module.AbstractSimpleModule;

public class DeleteServ extends AbstractSimpleModule {
	public void service(SourceBean request, SourceBean response) {
		doDelete(request, response);
	}
}

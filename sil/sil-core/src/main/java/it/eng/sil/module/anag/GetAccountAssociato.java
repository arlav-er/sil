package it.eng.sil.module.anag;

import com.engiweb.framework.base.SourceBean;

public class GetAccountAssociato extends it.eng.sil.module.AbstractSimpleModule {

	public void service(SourceBean request, SourceBean response) {
		doSelect(request, response);
	}
}
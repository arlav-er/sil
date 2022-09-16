package it.eng.sil.module;

import com.engiweb.framework.base.SourceBean;

public class doDynamicSelectModule extends AbstractSimpleModule {

	public void service(SourceBean request, SourceBean response) {
		doDynamicSelect(request, response);
	}
}
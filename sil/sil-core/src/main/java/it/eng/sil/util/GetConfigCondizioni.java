package it.eng.sil.util;

import com.engiweb.framework.base.SourceBean;

import it.eng.sil.module.AbstractSimpleModule;

public class GetConfigCondizioni extends AbstractSimpleModule {

	public void service(SourceBean serviceRequest, SourceBean serviceResponse) throws Exception {
		serviceRequest.setAttribute("CODTIPOCONFIG", "CONFCOND");
		doSelect(serviceRequest, serviceResponse);
	}
}

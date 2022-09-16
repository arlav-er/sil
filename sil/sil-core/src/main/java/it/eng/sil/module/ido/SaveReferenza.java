package it.eng.sil.module.ido;

import com.engiweb.framework.base.SourceBean;

import it.eng.sil.module.AbstractSimpleModule;

public class SaveReferenza extends AbstractSimpleModule {
	public void service(SourceBean request, SourceBean response) {

		this.doUpdate(request, response);
	}
}
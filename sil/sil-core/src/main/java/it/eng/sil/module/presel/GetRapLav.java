package it.eng.sil.module.presel;

import com.engiweb.framework.base.SourceBean;

import it.eng.sil.module.AbstractSimpleModule;

public class GetRapLav extends AbstractSimpleModule {
	private static final long serialVersionUID = -314785063209697054L;

	public void service(SourceBean request, SourceBean response) {
		doSelect(request, response);
	}

}
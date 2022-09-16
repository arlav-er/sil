package it.eng.sil.module.presel;

import com.engiweb.framework.base.SourceBean;

import it.eng.sil.module.AbstractSimpleModule;

public class SaveAbilitazione extends AbstractSimpleModule {
	public void service(SourceBean request, SourceBean response) {

		doUpdateNoDuplicate(request, response, "prgAbilitazione");

	}

}
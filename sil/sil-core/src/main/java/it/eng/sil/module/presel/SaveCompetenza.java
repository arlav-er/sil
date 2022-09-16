package it.eng.sil.module.presel;

import com.engiweb.framework.base.SourceBean;

import it.eng.sil.module.AbstractSimpleModule;

public class SaveCompetenza extends AbstractSimpleModule {
	public void service(SourceBean request, SourceBean response) {

		doUpdateNoDuplicate(request, response, "prgCompetenza");

	}

}
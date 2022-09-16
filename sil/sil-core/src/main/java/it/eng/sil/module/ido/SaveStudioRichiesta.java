package it.eng.sil.module.ido;

import com.engiweb.framework.base.SourceBean;

import it.eng.sil.module.AbstractAlternativaSimpleModule;

public class SaveStudioRichiesta extends AbstractAlternativaSimpleModule {
	public void service(SourceBean request, SourceBean response) {
		getPrgAlternativa(request);
		doUpdate(request, response);

	}

}
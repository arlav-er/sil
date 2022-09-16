package it.eng.sil.module.presel;

import com.engiweb.framework.base.SourceBean;

import it.eng.sil.module.AbstractAlternativaSimpleModule;

public class UpdateConoscenzaInfo extends AbstractAlternativaSimpleModule {
	public void service(SourceBean request, SourceBean response) {

		// doUpdate(request, response);
		getPrgAlternativa(request);
		doUpdateNoDuplicate(request, response, "PRGINFO");
	}
}
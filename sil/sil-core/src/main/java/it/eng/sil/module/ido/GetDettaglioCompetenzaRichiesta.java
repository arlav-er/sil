package it.eng.sil.module.ido;

import com.engiweb.framework.base.SourceBean;

import it.eng.sil.module.AbstractAlternativaSimpleModule;

public class GetDettaglioCompetenzaRichiesta extends AbstractAlternativaSimpleModule {
	private String className = this.getClass().getName();

	public void service(SourceBean request, SourceBean response) {
		getPrgAlternativa(request);
		doSelect(request, response);
	}
}
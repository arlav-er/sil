package it.eng.sil.module.profil;

import com.engiweb.framework.base.SourceBean;

import it.eng.sil.module.AbstractSimpleModule;

public class ProfProfilaturaGruppo extends AbstractSimpleModule {
	public void service(SourceBean request, SourceBean response) throws Exception {
		doInsert(request, response);
	}
}// class ProfProfilaturaGruppo

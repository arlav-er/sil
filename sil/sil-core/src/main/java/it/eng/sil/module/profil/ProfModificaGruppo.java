package it.eng.sil.module.profil;

import com.engiweb.framework.base.SourceBean;

import it.eng.sil.module.AbstractSimpleModule;

public class ProfModificaGruppo extends AbstractSimpleModule {
	public void service(SourceBean request, SourceBean response) throws Exception {

		doUpdate(request, response);
		response.setAttribute("cdnGruppo", request.getAttribute("cdnGruppo"));
	}
}
package it.eng.sil.module.profil;

import com.engiweb.framework.base.SourceBean;

import it.eng.sil.module.AbstractSimpleModule;

public class ProfNuovoGruppo extends AbstractSimpleModule {
	public void service(SourceBean request, SourceBean response) throws Exception {

		doInsert(request, response, "CDNGRUPPO");
		response.setAttribute("cdnGruppo", request.getAttribute("cdnGruppo"));
		/*
		 * try { request.delAttribute("MODE"); } catch (Exception Ex) { String a=""; }
		 */
	}
}
package it.eng.sil.module.trento;

import com.engiweb.framework.base.SourceBean;

import it.eng.sil.module.AbstractSimpleModule;

public class MDettaglioTemplate extends AbstractSimpleModule {
	public MDettaglioTemplate() {
	}

	private String className = this.getClass().getName();

	public void service(SourceBean request, SourceBean response) throws Exception {
		SourceBean template = doSelect(request, response);
		Object prgConfigProt = template.containsAttribute("ROW.PRGCONFIGPROT")
				? template.getAttribute("ROW.PRGCONFIGPROT")
				: "";
		response.setAttribute("PRGCONFIGPROT", prgConfigProt);
	}
}
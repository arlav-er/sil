package it.eng.sil.module.patto;

import com.engiweb.framework.base.SourceBean;

import it.eng.sil.module.AbstractSimpleModule;

public class GetAzioni extends AbstractSimpleModule {
	public void service(SourceBean request, SourceBean response) throws Exception {

		String prgAzioniRagg = (String) request.getAttribute("prgAzioneRagg");
		if (prgAzioniRagg != null && !prgAzioniRagg.equals("")) {
			doSelect(request, response);
		}
	}
}

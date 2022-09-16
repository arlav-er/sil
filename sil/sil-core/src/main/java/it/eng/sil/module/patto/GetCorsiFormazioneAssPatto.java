package it.eng.sil.module.patto;

import com.engiweb.framework.base.SourceBean;

import it.eng.sil.module.AbstractSimpleModule;

public class GetCorsiFormazioneAssPatto extends AbstractSimpleModule {

	public void service(SourceBean request, SourceBean response) throws Exception {
		doSelectWithStatement(request, response, getStatement(request));
	}

	private String getStatement(SourceBean request) throws Exception {
		Object prgPattoLav = request.getAttribute("prgPattoLavoratore");
		if ((prgPattoLav == null) || (prgPattoLav.toString().equals(""))) {
			// ricerca in base al cdnLavoratore
			return "QUERIES.SELECT_FROM_CDNLAVORATORE";
		} else {
			// ricerca in base al prgPattoLavoratore
			return "QUERIES.SELECT_FROM_PRGPATTOLAVORATORE";
		}
	}
}

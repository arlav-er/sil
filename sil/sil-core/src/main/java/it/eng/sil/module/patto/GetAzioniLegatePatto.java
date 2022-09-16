package it.eng.sil.module.patto;

import com.engiweb.framework.base.SourceBean;

import it.eng.sil.module.AbstractSimpleModule;

public class GetAzioniLegatePatto extends AbstractSimpleModule {
	public void service(SourceBean request, SourceBean response) throws Exception {
		doSelectWithStatement(request, response, getStatement(request));
	}

	private String getStatement(SourceBean request) throws Exception {
		if (request.containsAttribute("prgPattoLavoratore")) {
			// ricerca in base al prgpattolavoratore
			return "QUERIES.SELECT_FROM_PRGPATTOLAVORATORE";

		} else {
			// ricerca in base al cdnlavoratore
			return "QUERIES.SELECT_FROM_CDNLAVORATORE";
		}
	}
}

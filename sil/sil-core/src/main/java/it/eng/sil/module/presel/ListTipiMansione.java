package it.eng.sil.module.presel;

import com.engiweb.framework.base.SourceBean;

import it.eng.sil.module.AbstractSimpleModule;

public class ListTipiMansione extends AbstractSimpleModule {
	public void service(SourceBean request, SourceBean response) {
		if (request.containsAttribute("FLGIDO") && request.getAttribute("FLGIDO").toString().equalsIgnoreCase("S")) {
			setSectionQuerySelect("QUERY_SELECT_TIPI_MANS_IDO");
		} else {
			setSectionQuerySelect("QUERY_SELECT_TIPI_MANS");
		}
		doSelect(request, response);
	}
}
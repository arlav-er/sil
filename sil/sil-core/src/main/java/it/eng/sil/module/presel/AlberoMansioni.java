package it.eng.sil.module.presel;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.dispatching.module.AbstractModule;

import it.eng.afExt.utils.StringUtils;

public class AlberoMansioni extends AbstractModule {
	public void init(SourceBean config) {
		// Insert here your initiliazation code
	}

	public void service(SourceBean request, SourceBean response) throws Exception {
		String codPadre = (String) request.getAttribute("padre");
		boolean flgFrequente = (boolean) request.containsAttribute("flgFrequente");
		String flgIdo = StringUtils.getAttributeStrNotNull(request, "FLGIDO");
		// Response.setAttribute(SourceBean.fromXMLString(Mansioni_iter.getMansioni(codPadre)));
		response.setAttribute(SourceBean.fromXMLString(Mansioni.getMansioni(codPadre, flgFrequente, flgIdo)));
	}
}
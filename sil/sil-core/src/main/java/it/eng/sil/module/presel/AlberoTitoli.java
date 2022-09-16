package it.eng.sil.module.presel;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.dispatching.module.AbstractModule;

public class AlberoTitoli extends AbstractModule {
	public void init(SourceBean config) {
		// Insert here your initiliazation code
	}

	public void service(SourceBean Request, SourceBean Response) throws Exception {
		String codPadre = (String) Request.getAttribute("padre");
		Response.setAttribute(SourceBean.fromXMLString(Titoli.getTitoli(codPadre)));
	}
}
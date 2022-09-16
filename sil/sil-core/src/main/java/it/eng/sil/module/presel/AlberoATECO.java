package it.eng.sil.module.presel;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.dispatching.module.AbstractModule;

public class AlberoATECO extends AbstractModule {
	public void init(SourceBean config) {
		// Insert here your initiliazation code
	}

	public void service(SourceBean Request, SourceBean Response) throws Exception {
		String codPadre = (String) Request.getAttribute("padre");
		String res = CodiciATECO.getCodici(codPadre);
		Response.setAttribute(SourceBean.fromXMLString(res));
	}
}
package it.eng.sil.module.ido;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.base.SourceBeanException;

import it.eng.sil.module.AbstractSimpleModule;

public class DecodificaGiornale extends AbstractSimpleModule {
	public void service(SourceBean request, SourceBean response) throws SourceBeanException {
		/*
		 * if ("".equals(request.getAttribute("CODGIORNALE"))) { if
		 * (!"".equals(request.getAttribute("CODGIORNALE_INS"))) { request.updAttribute("CODGIORNALE",
		 * request.getAttribute("CODGIORNALE_INS")); } }
		 */
		doSelect(request, response);
	}
}
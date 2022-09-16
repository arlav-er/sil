package it.eng.sil.module.modelli.tda;

import com.engiweb.framework.base.SourceBean;

import it.eng.sil.module.AbstractSimpleModule;

public class AggiornaTipoServizio extends AbstractSimpleModule {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5801219618633547958L;

	@Override
	public void service(SourceBean serviceRequest, SourceBean serviceResponse) throws Exception {
		doUpdate(serviceRequest, serviceResponse);
		serviceResponse.setAttribute("avvisaCampioTipologia", "avvisaCampioTipologia");
	}

}

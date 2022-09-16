package it.eng.sil.module.modelli.tda;

import com.engiweb.framework.base.SourceBean;

import it.eng.sil.module.AbstractSimpleModule;

public class GenericSelect extends AbstractSimpleModule {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3490183731915677936L;

	public void service(SourceBean request, SourceBean response) {
		doSelect(request, response);
	}
}
package it.eng.sil.module.documenti;

import com.engiweb.framework.base.SourceBean;

import it.eng.sil.module.AbstractSimpleModule;

public class DocHasInfoStoriche extends AbstractSimpleModule {
	/**
	 * 
	 */
	private static final long serialVersionUID = -8852983043614784629L;

	public void service(SourceBean request, SourceBean response) {
		doDynamicSelect(request, response);
	}
}

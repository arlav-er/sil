package it.eng.sil.module.amministrazione;

import com.engiweb.framework.base.SourceBean;

import it.eng.sil.module.AbstractSimpleModule;

public class GenericDelete extends AbstractSimpleModule {
	public void service(SourceBean request, SourceBean response) throws Exception {
		doDelete(request, response);

	}// end service

}// end class GenericDelete

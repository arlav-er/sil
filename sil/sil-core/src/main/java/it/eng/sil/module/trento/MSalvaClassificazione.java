package it.eng.sil.module.trento;

import com.engiweb.framework.base.SourceBean;

import it.eng.sil.module.AbstractSimpleModule;

public class MSalvaClassificazione extends AbstractSimpleModule {

	public MSalvaClassificazione() {
	}

	public void service(SourceBean request, SourceBean response) {
		doInsertNoDuplicate(request, response);
	}
}
package it.eng.sil.module.movimenti;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.base.SourceBeanException;

import it.eng.sil.module.AbstractSimpleModule;

public class M_MovGetDettaglioAgevolazioniApp extends AbstractSimpleModule {
	public M_MovGetDettaglioAgevolazioniApp() {
	}

	public void service(SourceBean request, SourceBean response) throws SourceBeanException {
		doDynamicSelect(request, response);
	}
}

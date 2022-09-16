package it.eng.sil.module.agenda;

import com.engiweb.framework.base.SourceBean;

import it.eng.sil.module.AbstractSimpleModule;

public class DelFestivo extends AbstractSimpleModule {
	public DelFestivo() {
	}

	public void service(SourceBean request, SourceBean response) {

		doDelete(request, response);
	}
}
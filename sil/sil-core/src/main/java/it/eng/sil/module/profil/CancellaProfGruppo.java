package it.eng.sil.module.profil;

import com.engiweb.framework.base.SourceBean;

import it.eng.sil.module.AbstractSimpleModule;

public class CancellaProfGruppo extends AbstractSimpleModule {

	public void service(SourceBean request, SourceBean response) {
		doDelete(request, response);
	}
}
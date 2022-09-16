package it.eng.sil.module.ido;

import com.engiweb.framework.base.SourceBean;

import it.eng.sil.module.AbstractSimpleModule;

public class MInserisciCittadinanzaRichiesta extends AbstractSimpleModule {
	public void service(SourceBean request, SourceBean response) {
		doInsertNoDuplicate(request, response);
	}
}
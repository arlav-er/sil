package it.eng.sil.module.presel;

import com.engiweb.framework.base.SourceBean;

import it.eng.sil.module.AbstractSimpleModule;

public class InsertVisibTerritorio extends AbstractSimpleModule {
	public void service(SourceBean request, SourceBean response) {
		doInsert(request, response, "PRGVISIBTERRITORIO");
	}
}
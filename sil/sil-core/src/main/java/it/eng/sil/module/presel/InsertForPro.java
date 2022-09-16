package it.eng.sil.module.presel;

import com.engiweb.framework.base.SourceBean;

import it.eng.sil.module.AbstractSimpleModule;
import it.eng.sil.module.patto.PattoFacade;

public class InsertForPro extends AbstractSimpleModule {
	public void service(SourceBean request, SourceBean response) {

		PattoFacade facade = new PattoFacade();
		if (facade.withPatto(request)) {

			facade.doInsert(this, getPool(), request, response, "PRGCORSO");

		} else {

			doInsert(request, response, "PRGCORSO");
		}
	}
}
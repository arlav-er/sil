package it.eng.sil.module.presel;

import com.engiweb.framework.base.SourceBean;

import it.eng.sil.module.AbstractSimpleModule;
import it.eng.sil.module.patto.PattoFacade;

public class InsertIndisp extends AbstractSimpleModule {
	public void service(SourceBean request, SourceBean response) throws Exception {

		PattoFacade facade = new PattoFacade();
		if (facade.withPatto(request)) {

			request.delAttribute("PRGINDISPONIBILITA");
			facade.doInsert(this, getPool(), request, response, "PRGINDISPONIBILITA");

		} else {

			doInsert(request, response, "PRGINDISPONIBILITA");
		}
	}
}
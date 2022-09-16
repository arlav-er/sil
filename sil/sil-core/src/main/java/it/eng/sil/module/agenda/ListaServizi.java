package it.eng.sil.module.agenda;

/*
 * Ottiene la lista degli servizi
 * 
 * @author: Giovanni Landi
 * 
 */

import com.engiweb.framework.base.SourceBean;

import it.eng.sil.module.AbstractSimpleModule;

public class ListaServizi extends AbstractSimpleModule {
	private String className = this.getClass().getName();

	public void service(SourceBean request, SourceBean response) {
		doSelect(request, response);
	}
}
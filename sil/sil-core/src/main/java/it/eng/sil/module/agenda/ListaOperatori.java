package it.eng.sil.module.agenda;

/*
 * Ottiene la lista degli operatori
 * 
 * @author: Giovanni Landi
 * 
 */

import com.engiweb.framework.base.SourceBean;

import it.eng.sil.module.AbstractSimpleModule;

public class ListaOperatori extends AbstractSimpleModule {
	private String className = this.getClass().getName();

	public void service(SourceBean request, SourceBean response) {
		doSelect(request, response);
	}
}
/*
 * Creato il 03-Aug-04
 * Author: rolfini
 * 
 */
package it.eng.sil.module.profil;

import com.engiweb.framework.base.SourceBean;

import it.eng.sil.module.AbstractSimpleModule;

public class CercaOperatori extends AbstractSimpleModule {
	private String className = this.getClass().getName();

	public void service(SourceBean request, SourceBean response) {

		doSelect(request, response);
	}
}
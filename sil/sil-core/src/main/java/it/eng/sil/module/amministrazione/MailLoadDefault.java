/*
 * Creato il 22-nov-04
 * Author: vuoto
 * 
 */
package it.eng.sil.module.amministrazione;

import com.engiweb.framework.base.SourceBean;

import it.eng.sil.module.AbstractSimpleModule;

/**
 * @author vuoto
 * 
 */
public class MailLoadDefault extends AbstractSimpleModule {

	public void service(SourceBean request, SourceBean response) {
		doSelect(request, response);
	}
}

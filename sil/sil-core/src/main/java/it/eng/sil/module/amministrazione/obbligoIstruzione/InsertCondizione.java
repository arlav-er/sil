/*
 */
package it.eng.sil.module.amministrazione.obbligoIstruzione;

import com.engiweb.framework.base.SourceBean;

/**
 * @author Girotti
 * 
 * NOF (NUOVO OBBLIGO FORMATIVO)
 */

import it.eng.sil.module.AbstractSimpleModule;

public class InsertCondizione extends AbstractSimpleModule {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public void service(SourceBean request, SourceBean response) throws Exception {
		doInsert(request, response, "prgobbligoistruzione");
	}
}
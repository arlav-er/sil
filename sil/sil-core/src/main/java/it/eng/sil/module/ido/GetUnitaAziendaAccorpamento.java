/*
 * Creato il 24-Aug-04
 * Author: rolfini
 * 
 */
package it.eng.sil.module.ido;

import com.engiweb.framework.base.SourceBean;

import it.eng.sil.module.AbstractSimpleModule;

public class GetUnitaAziendaAccorpamento extends AbstractSimpleModule {
	public void service(SourceBean request, SourceBean response) {

		/*
		 * String prgAzienda=(String) ((SourceBean) request.getAttribute("prgAzienda")).getCharacters(); String
		 * prgUnita1= (String) ((SourceBean) request.getAttribute("prgUnita1")).getCharacters(); String prgUnita2=
		 * (String) ((SourceBean) request.getAttribute("prgUnita2")).getCharacters();
		 * 
		 * request.delAttribute("prgUnita1"); request.delAttribute("prgUnita2");
		 * 
		 * 
		 * doSelectWithStatement(request, response, "GET_UNITA_AZIENDA" );
		 */

		doSelect(request, response);

	}

}
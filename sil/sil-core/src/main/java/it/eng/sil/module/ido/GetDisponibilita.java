package it.eng.sil.module.ido;

import com.engiweb.framework.base.SourceBean;

import it.eng.sil.module.AbstractSimpleModule;

public class GetDisponibilita extends AbstractSimpleModule {
	private String className = this.getClass().getName();

	public void service(SourceBean request, SourceBean response) {
		// RequestContainer requestContainer = getRequestContainer();
		// SessionContainer sessionContainer = (SessionContainer)
		// requestContainer.getSessionContainer();

		// Set di una variabile di sessione per la navigazione a contatto ed
		// appuntamento
		/*
		 * String prg = (String)request.getAttribute("PRGNOMINATIVO"); if ( prg != null ){
		 * sessionContainer.setAttribute("_PRGNOMINATIVO_",prg); }
		 */
		doSelect(request, response);
	}
}
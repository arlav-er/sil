/*
 * Creato il 13-ott-04
 *
 */
package it.eng.sil.module.movimenti;

import com.engiweb.framework.base.SourceBean;

import it.eng.sil.module.AbstractSimpleModule;

public class GetDescTitoloStudio extends AbstractSimpleModule {
	private String className;

	public GetDescTitoloStudio() {
		className = this.getClass().getName();
	}

	public void service(SourceBean request, SourceBean response) {
		doSelect(request, response);
	}

}// end class

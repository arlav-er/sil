/*
 * Creato il 26-ago-04
 */
package it.eng.sil.module.movimenti.trasfRamoAzienda;

import com.engiweb.framework.base.SourceBean;

import it.eng.sil.module.AbstractSimpleModule;

/**
 * Recupero dati aziende coinvolte nel trasferimento ramo aziendale
 * 
 * @author roccetti
 */
public class GetAzienda extends AbstractSimpleModule {

	/**
	 * 
	 */
	public void service(SourceBean request, SourceBean response) throws Exception {
		doSelect(request, response);
	}

}

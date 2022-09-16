/*
 * Creato il 25-lug-06
 *
 * Per modificare il modello associato a questo file generato, aprire
 * Finestra&gt;Preferenze&gt;Java&gt;Generazione codice&gt;Codice e commenti
 */
package it.eng.sil.module.anag;

import com.engiweb.framework.base.SourceBean;

import it.eng.sil.module.AbstractSimpleModule;

/**
 * @author riccardi
 * 
 *         Per modificare il modello associato al commento di questo tipo generato, aprire
 *         Finestra&gt;Preferenze&gt;Java&gt;Generazione codice&gt;Codice e commenti
 */
public class CheckStessaProvincia extends AbstractSimpleModule {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(CheckStessaProvincia.class.getName());

	public void service(SourceBean request, SourceBean response) {
		SourceBean codProvince = doSelect(request, response);
		try {
			String codProvOrig = (String) codProvince.getAttribute("row.codProvOrig");
			String codProvUser = (String) codProvince.getAttribute("row.codProvUser");
			if (codProvOrig.equals(codProvUser)) {
				response.setAttribute("IntraProvinciale", "true");
			} else {
				response.setAttribute("IntraProvinciale", "false");
				String coopAttiva = System.getProperty("cooperazione.enabled");
				if (coopAttiva != null && coopAttiva.equals("true")) {
					response.setAttribute("coopAttiva", "true");
				} else {
					response.setAttribute("coopAttiva", "false");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			it.eng.sil.util.TraceWrapper.debug(_logger,
					"Impossibile sapere se i CPI appartengono alla stessa provincia.", e);

		}
	}
}
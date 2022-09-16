/*
 * Creato il 20-apr-06
 *
 * Per modificare il modello associato a questo file generato, aprire
 * Finestra&gt;Preferenze&gt;Java&gt;Generazione codice&gt;Codice e commenti
 */
package it.eng.sil.module.anag;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.base.SourceBeanException;

import it.eng.afExt.utils.StringUtils;
import it.eng.sil.module.AbstractSimpleModule;

/**
 * @author riccardi
 * 
 *         Per modificare il modello associato al commento di questo tipo generato, aprire
 *         Finestra&gt;Preferenze&gt;Java&gt;Generazione codice&gt;Codice e commenti
 */
public class GetRegionedaCom extends AbstractSimpleModule {

	public void service(SourceBean request, SourceBean response) throws SourceBeanException {
		String codRegioneCpiUser = request.getAttribute("codRegioneCpiUser").toString();
		doSelect(request, response);
		String codRegionedaCom = StringUtils.getAttributeStrNotNull(response, "ROWS.ROW.CODREGIONE");
		if (!codRegionedaCom.equalsIgnoreCase(codRegioneCpiUser)) {
			response.setAttribute("Fuori_Regione", "True");
		} else {
			response.setAttribute("Fuori_Regione", "False");
		}
	}
}

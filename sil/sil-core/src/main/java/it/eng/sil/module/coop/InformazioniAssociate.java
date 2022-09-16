/*
 * Creato il 19-giu-06
 *
 * Per modificare il modello associato a questo file generato, aprire
 * Finestra&gt;Preferenze&gt;Java&gt;Generazione codice&gt;Codice e commenti
 */
package it.eng.sil.module.coop;

import java.math.BigDecimal;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.base.SourceBeanException;

import it.eng.afExt.utils.MessageAppender;
import it.eng.sil.module.AbstractSimpleModule;

/**
 * @author riccardi
 * 
 *         Per modificare il modello associato al commento di questo tipo generato, aprire
 *         Finestra&gt;Preferenze&gt;Java&gt;Generazione codice&gt;Codice e commenti
 */
public class InformazioniAssociate extends AbstractSimpleModule {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(InformazioniAssociate.class.getName());

	public void service(SourceBean serviceRequest, SourceBean serviceResponse) {

		try {
			setSectionQuerySelect("SELECT_PATTI_ASSOCIATI");
			SourceBean patti = doSelect(serviceRequest, serviceResponse);
			int num_patti = ((BigDecimal) patti.getAttribute("ROW.NUM_PATTI")).intValue();

			setSectionQuerySelect("SELECT_ASSOCIAZIONI_ESP_MOV");
			SourceBean movAssociatiEsp = doSelect(serviceRequest, serviceResponse);
			int num_mov = ((BigDecimal) movAssociatiEsp.getAttribute("ROW.NUM_ASSESPMOV")).intValue();

			if (num_patti == 0 && num_mov == 0) {
				serviceResponse.setAttribute("canUpdateLav", "true");
			} else {
				serviceResponse.setAttribute("canUpdateLav", "false");
				serviceResponse.delAttribute("USER_MESSAGE");
				if (num_patti != 0) {
					MessageAppender.appendMessage(serviceResponse,
							"Aggiornamento scheda lavoratore non eseguito: Essendo presenti informazioni associate al patto",
							null);
				}
				if (num_mov != 0) {
					if (num_patti == 0) {
						MessageAppender.appendMessage(serviceResponse,
								"Aggiornamento scheda lavoratore non eseguito: Essendo presenti esperienze di lavoro associate a movimenti",
								null);
					}
					if (num_patti != 0) {
						MessageAppender.appendMessage(serviceResponse, " ed esperienze di lavoro associate a movimenti",
								null);
					}
				}
			}
		} catch (SourceBeanException sbe) {
			_logger.fatal("Errore nel recupero delle informazioni associate");

		}
	}

}
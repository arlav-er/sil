/*
 * Creato il 31-lug-07
 *
 * Per modificare il modello associato a questo file generato, aprire
 * Finestra&gt;Preferenze&gt;Java&gt;Generazione codice&gt;Codice e commenti
 */
package it.eng.sil.module.amministrazione;

import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.utils.StringUtils;
import it.eng.sil.module.AbstractSimpleModule;

/**
 * @author riccardi
 * 
 *         Per modificare il modello associato al commento di questo tipo generato, aprire
 *         Finestra&gt;Preferenze&gt;Java&gt;Generazione codice&gt;Codice e commenti
 */
public class CheckCanConsultArchivioMovimenti extends AbstractSimpleModule {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger
			.getLogger(CheckCanConsultArchivioMovimenti.class.getName());

	public void service(SourceBean request, SourceBean response) {
		int idSuccess = this.disableMessageIdSuccess();
		int idFail = this.disableMessageIdFail();

		SourceBean checkSB = doSelect(request, response);
		try {
			String check = StringUtils.getAttributeStrNotNull(checkSB, "row.strvalore");
			if (check.equals("true")) {
				response.setAttribute("ArchivioConsultabile", "true");
			} else {
				response.setAttribute("ArchivioConsultabile", "false");
			}
		} catch (Exception e) {
			it.eng.sil.util.TraceWrapper.debug(_logger, "Impossibile consultare l'archivio dei movimenti da validare",
					e);

		}
	}
}
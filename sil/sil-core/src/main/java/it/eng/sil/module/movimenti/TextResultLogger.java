package it.eng.sil.module.movimenti;

import java.util.Iterator;
import java.util.List;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.message.MessageBundle;

import it.eng.afExt.utils.SourceBeanUtils;
import it.eng.afExt.utils.StringUtils;

/**
 * Oggetto per il log su FILE DI TESTO della validazione massiva dei movimenti. Eredita i metodi comuni dalla
 * FileResultLogger.
 * 
 * @author Luigi Antenucci
 */
public class TextResultLogger extends FileResultLogger {

	protected String getFileExt() {
		return "txt";
	}

	/**
	 * Dal SourceBean del risultato della validazione (resultSB) crea una stringa contenente le info di LOG e la rende.
	 * può essere fatto l'OVERLOAD nelle sottoclassi per ridefinire cosa stampare e come.
	 */
	protected String getResultMessage(SourceBean resultSB) {

		StringBuffer outStr = new StringBuffer();
		if (resultSB == null) {
			outStr.append("(nessun risultato per l'elaborazione)");
			return outStr.toString();
		}

		SourceBean recordSB = (SourceBean) resultSB.getAttribute("RECORD");

		// Estraggo l'identificativo del movimento dalla risposta e i dati
		// dall'identificativo
		SourceBean idMovSB = (SourceBean) recordSB.getAttribute("IDMOV");

		// Dati per identificazione del movimento se presenti
		if (idMovSB != null) {
			String codTipoMov = SourceBeanUtils.getAttrStrNotNull(idMovSB, "codTipoMov");
			String nomeLav = SourceBeanUtils.getAttrStrNotNull(idMovSB, "nomeLav");
			String cognomeLav = SourceBeanUtils.getAttrStrNotNull(idMovSB, "cognomeLav");
			String ragsocaz = SourceBeanUtils.getAttrStrNotNull(idMovSB, "ragSocAzienda");
			// Substring delle info troppo lunghe
			if (ragsocaz.length() > 30) {
				ragsocaz = ragsocaz.substring(0, 30) + "...";
			}

			outStr.append("Tipo Mov: ").append(codTipoMov);
			outStr.append(" - Azienda: ").append(ragsocaz);
			outStr.append(" - Lavoratore: ").append(cognomeLav).append(' ').append(nomeLav);
		} else {
			outStr.append("Dettagli dell'elaborazione");
		}
		outStr.append("\n\n");

		// Lista dei SB di "PROCESSOR"
		List processors = recordSB.getAttributeAsVector("PROCESSOR");
		Iterator iterProcessors = processors.iterator();
		while (iterProcessors.hasNext()) {
			// Prendo il tag processor
			SourceBean processor = (SourceBean) iterProcessors.next();

			// Recupero lista dei WARNING del "processore"
			List warns = processor.getAttributeAsVector("WARNING");
			if (warns != null && (warns.size() > 0)) {
				Iterator iterWarns = warns.iterator();
				while (iterWarns.hasNext()) {
					// Estraggo la warning e aggiungo la riga
					SourceBean warn = (SourceBean) iterWarns.next();
					if (!warn.getAttribute("code").equals("")) {

						String messWarn = SourceBeanUtils.getAttrStrNotNull(warn, "messagecode");
						String descWarn = SourceBeanUtils.getAttrStrNotNull(warn, "dettaglio");

						outStr.append("  - ATTENZIONE: ").append(messWarn).append("\n");

						if (StringUtils.isFilled(descWarn)) {
							// Eventuale sottomessaggio della warning
							outStr.append("     - Dettagli: ").append(descWarn).append("\n");
						}
						outStr.append("\n");
					}
				}
			}

			// Estraggo i dati generali del "processore"
			String resultProc = (String) processor.getAttribute("RESULT");

			// Se il risultato è un ERRORE ne estraggo il codice e il messaggio
			if (resultProc.equalsIgnoreCase("ERROR")) {
				String errorCode = SourceBeanUtils.getAttrStrNotNull(processor, "ERROR.code");
				String errorText = SourceBeanUtils.getAttrStrNotNull(processor, "ERROR.dettaglio");

				// Creo l'eventuale sottoriga di errore
				if (StringUtils.isFilled(errorCode)) {
					outStr.append("  - ERRORE: ");
					outStr.append(MessageBundle.getMessage(errorCode)).append("\n");

					// Sottoelemento per il testo del messaggio di errore
					if (StringUtils.isFilled(errorText)) {
						outStr.append("     - Dettagli: ").append(errorText).append("\n");
					}
					outStr.append("\n");
					outStr.append("  - L&acute;ERRORE HA PROVOCATO L&acute;ANNULLAMENTO "
							+ "DELLE OPERAZIONI PRECEDENTI E IL BLOCCO DEI CONTROLLI SUCCESSIVI\n");
				}
			}
		} // while

		outStr.append("\n");

		return outStr.toString();
	}

	/**
	 * Rende il messaggio per lo STOP d'utente.
	 */
	protected String getStopMessage() {
		return "Elaborazione interrotta dall'utente\n\n";
	}

}

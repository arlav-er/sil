/*
 * Creato il 12-lug-04
 *
 */
package it.eng.sil.module.movimenti.processors;

import java.util.Map;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.base.SourceBeanException;

import it.eng.afExt.utils.MessageCodes;
import it.eng.sil.module.movimenti.RecordProcessor;

/**
 * @author roccetti
 * 
 *         Processor che inserisce nella risposta utente l'identificativo del movimento correntemente processato. Per
 *         questo crea nella sua risposta un tag IDMOV che contiene: - il codice del tipo di movimento (attributo
 *         codTipoMov) - il nome e il cognome del lavoratore interessato (attributi nomeLav e cognomeLav) - la ragione
 *         sociale dell'azienda interessata (attributo ragSocAzienda)
 */
public class IdentificaMovimento implements RecordProcessor {

	private String name;
	private String classname = this.getClass().getName();

	/**
	 * 
	 */
	public IdentificaMovimento() {
		super();

	}

	public SourceBean processRecord(Map record) throws SourceBeanException {
		// Se il record è nullo non lo posso elaborare, ritorno l'errore
		if (record == null) {
			return ProcessorsUtils.createResponse(name, classname, new Integer(MessageCodes.ImportMov.ERR_NULL_RECORD),
					"Record da elaborare nullo.", null, null);
		}
		String codtipomov = "";
		String cognomeLav = "";
		String nomeLav = "";
		String ragSocAz = "";
		String indirAz = "";
		String context = "";
		String strCFlavoratore = "";
		String strNumAnnoProt = "";

		try {
			context = (String) record.get("CONTEXT");
			// Estrazione contesto di validazione o importazione
			boolean valida = "valida".equalsIgnoreCase(context) || "validazioneMassiva".equalsIgnoreCase(context);
			boolean importa = "importa".equalsIgnoreCase(context);

			// Identificativo del movimento in caso di validazione
			if (valida) {
				codtipomov = (String) record.get("CODTIPOMOV");
				cognomeLav = (String) record.get("STRCOGNOME");
				nomeLav = (String) record.get("STRNOME");
				ragSocAz = (String) record.get("STRAZRAGIONESOCIALE");
				indirAz = (String) record.get("STRUAINDIRIZZO");
				strCFlavoratore = (String) record.get("STRCODICEFISCALE");
				/*
				 * BigDecimal numProt = (BigDecimal) record.get("STRUAINDIRIZZO"); BigDecimal annoProt = (BigDecimal)
				 * record.get("STRUAINDIRIZZO"); strNumAnnoProt = numProt .toString() +"/"+annoProt.toString();
				 */
			}

			// Identificativo del movimento in caso di importazione
			if (importa) {
				codtipomov = (String) record.get("evento");
				cognomeLav = (String) record.get("CognomeLav");
				nomeLav = (String) record.get("NomeLav");
				ragSocAz = (String) record.get("RagSocAz");
				indirAz = (String) record.get("IndirAz");
			}

			return ProcessorsUtils.createIdMov(codtipomov, nomeLav, cognomeLav, ragSocAz, indirAz, strCFlavoratore,
					strNumAnnoProt);
		} catch (Exception e) {
			return ProcessorsUtils.createResponse(name, classname, new Integer(MessageCodes.General.OPERATION_FAIL),
					"Importazione movimento fallito. ", null, null);
		}
	}
}

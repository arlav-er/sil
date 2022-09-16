package it.eng.sil.module.movimenti.processors;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Map;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.base.SourceBeanException;

import it.eng.afExt.utils.MessageCodes;
import it.eng.sil.module.movimenti.RecordProcessor;

/**
 * Processore che setta alcuni dati del movimento effettuando controlli con il movimento precedente
 * <p>
 * 
 * @author Paolo Roccetti
 */
public class CrossController implements RecordProcessor {

	private String name;
	private String classname = this.getClass().getName();

	/**
	 * Costruttore
	 * 
	 * @param name
	 *            Nome del processore
	 */
	public CrossController(String name) {
		this.name = name;
	}

	/**
	 * Setta i seguenti campi della tabella AM_MOVIMENTO:
	 * <ul>
	 * <li>FLGMODTEMPO</li>
	 * <li>FLGMODREDDITO</li>
	 * </ul>
	 * <p>
	 * 
	 * @param record
	 *            Il record da processare
	 */
	public SourceBean processRecord(Map record) throws SourceBeanException {

		// vettore delle warnings da restituire
		ArrayList warnings = new ArrayList();
		// Vettore dei risultati annidati da restituire
		ArrayList nested = new ArrayList();

		// Se il record è nullo non lo posso elaborare, ritorno l'errore
		if (record == null) {
			return ProcessorsUtils.createResponse(name, classname, new Integer(MessageCodes.ImportMov.ERR_NULL_RECORD),
					null, warnings, nested);
		}

		// Tipo di movimento
		String codtipomov = (String) record.get("CODTIPOMOV");
		if (codtipomov == null) {
			warnings.add(new Warning(MessageCodes.ImportMov.WAR_TIPO_MOV_NOVALORIZ, null));
			return ProcessorsUtils.createResponse(name, classname, null, null, warnings, nested);
		}

		// Movimento non collegato
		String collegato = (String) record.get("COLLEGATO");
		if (collegato == null) {
			collegato = "";
		}

		boolean avv = codtipomov.equalsIgnoreCase("AVV");
		boolean tra = codtipomov.equalsIgnoreCase("TRA");
		boolean pro = codtipomov.equalsIgnoreCase("PRO");
		boolean ces = codtipomov.equalsIgnoreCase("CES");

		// A seconda del tipo di movimento devo fare alcune elaborazioni
		if (tra || pro) {
			// Imposto le modifiche al reddito e al tempo solo se ho un
			// movimento collegato
			if (!collegato.equalsIgnoreCase("nessuno")) {
				// Estraggo redditi e tempi del mvimento corrente e precedente
				BigDecimal redditoPrec = (BigDecimal) record.get("DECRETRIBUZIONEPREC");
				Object redditoMen = record.get("DECRETRIBUZIONEMEN");
				BigDecimal reddito = null;
				if (redditoMen != null) {
					String redditoStr = (redditoMen.toString()).replace(',', '.');
					reddito = BigDecimal.valueOf(Float.parseFloat(redditoStr));
				}
				String tempoPrec = (String) record.get("CODMONOTEMPOMOVPREC");
				String tempo = (String) record.get("CODMONOTEMPO");
				String datFineMovPrec = (String) record.get("DATFINEMOVPREC");
				String datFineMov = (String) record.get("DATFINEMOV");

				// settaggio flag
				if ((redditoPrec == null && reddito == null) || (redditoPrec != null && reddito != null
						&& reddito.doubleValue() == redditoPrec.doubleValue())) {
					record.put("FLGMODREDDITO", "N");
				} else {
					record.put("FLGMODREDDITO", "S");
				}

				if (tempoPrec != null && tempo != null) {
					if (tempoPrec.compareTo(tempo) != 0) {
						record.put("FLGMODTEMPO", "S");
					} else if (tempo.equalsIgnoreCase("I")) {
						record.put("FLGMODTEMPO", "N");
					} else {
						if (datFineMov != null && datFineMov.equalsIgnoreCase(datFineMovPrec)) {
							record.put("FLGMODTEMPO", "N");
						} else
							record.put("FLGMODTEMPO", "S");
					}
				}
			}
		}

		if (avv) {
			String datInizioMov = (String) record.get("DATINIZIOMOV");
			record.put("NUMPROROGHE", new BigDecimal(0));
			String tempo = (String) record.get("CODMONOTEMPO");
			if (datInizioMov != null) {
				record.put("CODMONOTEMPOMOVPREC", tempo);
			} else {
				// Segnalo con una warning che non ho il tempo dell'avviamento
				warnings.add(new Warning(MessageCodes.ImportMov.WAR_COD_TEMPO_AVV, null));
			}
		}

		// Se ho warning le riporto
		if (warnings.size() > 0) {
			return ProcessorsUtils.createResponse(name, classname, null, null, warnings, nested);
		} else {
			return null;
		}
	}
}
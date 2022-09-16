package it.eng.sil.module.movimenti.processors;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Map;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.base.SourceBeanException;
import com.engiweb.framework.util.QueryExecutor;

import it.eng.afExt.utils.MessageCodes;
import it.eng.sil.Values;
import it.eng.sil.module.movimenti.RecordProcessor;
import it.eng.sil.module.movimenti.constant.DeTipoContrattoConstant;
import it.eng.sil.module.movimenti.enumeration.CodiceVariazioneEnum;
import it.eng.sil.module.movimenti.enumeration.TipoTrasfEnum;

/**
 * Trasforma l'indicazione sul tempo determinato o indeterminato e la data di inizio del movimento in una compatibile
 * con quella della tabella AM_MOVIMENTO_APPOGGIO. Opera la trasformazione per tutte quattro le tipologie di movimento.
 * Se non trova la tipologia di movimento e/o il codice del tempo e/o la data richiesta non inserisce nulla e lo segnala
 * con delle warning
 * <p>
 * 
 * @author Paolo Roccetti
 */
public class TransformCodTempo implements RecordProcessor {
	private String name;
	private String classname = this.getClass().getName();

	/** Formattazione Date */
	SimpleDateFormat formatter[] = new SimpleDateFormat[2];
	{
		formatter[0] = new SimpleDateFormat("ddMMyyyy");
		formatter[1] = new SimpleDateFormat("dd/MM/yyyy");
	}

	/**
	 * Costruttore
	 * 
	 * @param name
	 *            Nome del processore
	 */
	public TransformCodTempo(String name) {
		this.name = name;
	}

	/**
	 * Elabora il record. Inserisce un campo CodTempo con il valore "I" o "D" (a seconda di quale trova) un campo
	 * DataInizioMov con il valore della data di inizio del movimento e un campo DataFineMov nel caso di tempo
	 * determinato. Se trova inesattezze le segnala.
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
		try {
			// Tipo di movimento
			String codTipoMov = (String) record.get("evento");
			String codtipomovNew = (String) record.get("eventoNew");
			if (codTipoMov == null)
				codTipoMov = codtipomovNew;

			if (codTipoMov == null) {
				warnings.add(new Warning(MessageCodes.ImportMov.WAR_TIPO_MOV_NOVALORIZ, null));
				return ProcessorsUtils.createResponse(name, classname, null, null, warnings, nested);
			}

			String codTipoAss = record.get("CodTipoAvv") != null ? (String) record.get("CodTipoAvv") : "";
			boolean contrattoApprendistatoTI = false;
			if (codTipoAss != null) {
				String stmName = "GET_INFO_CONTRATTO_MOVIMENTO";
				Object[] params = new Object[1];
				params[0] = codTipoAss;
				SourceBean result = (SourceBean) QueryExecutor.executeQuery(stmName, params, "SELECT",
						Values.DB_SIL_DATI);
				if (result != null) {
					String codmonotipo = result.getAttribute("row.codmonotipo") != null
							? result.getAttribute("row.codmonotipo").toString()
							: "";
					String flgTI = result.getAttribute("row.flgti") != null
							? result.getAttribute("row.flgti").toString()
							: "";
					if (codmonotipo.equalsIgnoreCase("A") && flgTI.equalsIgnoreCase("S")) {
						contrattoApprendistatoTI = true;
					}
				}
			}

			String flgLavoroStagionale = record.get("FLGLAVOROSTAGIONALE") != null
					? record.get("FLGLAVOROSTAGIONALE").toString()
					: "";

			// Distinguo i vari tipi di movimento e per ognuno estraggo i dati
			if (codTipoMov.equalsIgnoreCase("AVV")) {
				String datavvind = record.containsKey("DataAvvTempoInd") ? (String) record.get("DataAvvTempoInd") : "";
				String datavvdet = record.containsKey("DataAvvTempoDet") ? (String) record.get("DataAvvTempoDet") : "";
				String datfineavv = record.containsKey("DataCessazTempoDetAvv")
						? (String) record.get("DataCessazTempoDetAvv")
						: "";

				if (!datavvind.equals("")) {
					// Ho un tempo indeterminato
					record.put("CodTempo", "I");
					// Controllo data di inizio del tempo indeterminato
					if (isDate(datavvind)) {
						record.put("DataInizioMov", datavvind);
					} else {
						warnings.add(new Warning(MessageCodes.ImportMov.WAR_INIZIO_AVV, null));
					}

					// Controllo data fine del tempo indeterminato (può essere
					// valorizzata in caso di lavoro stagionale)
					if (isDate(datfineavv)) {
						record.put("DataFineMov", datfineavv);
					} else {
						warnings.add(new Warning(MessageCodes.ImportMov.WAR_FINE_AVV_DET, null));
					}
				} else if (!datavvdet.equals("")) {
					// Ho un tempo determinato
					record.put("CodTempo", "D");

					// Controllo data di inizio del tempo determinato
					if (isDate(datavvdet)) {
						record.put("DataInizioMov", datavvdet);
					} else {
						warnings.add(new Warning(MessageCodes.ImportMov.WAR_INIZIO_AVV, null));
					}

					// Controllo data fine del tempo determinato
					if (isDate(datfineavv)) {
						record.put("DataFineMov", datfineavv);
					} else {
						warnings.add(new Warning(MessageCodes.ImportMov.WAR_FINE_AVV_DET, null));
					}
				} else {
					// Non ho trovato nessuna delle indicazioni sul tempo
					warnings.add(new Warning(MessageCodes.ImportMov.WAR_COD_TEMPO_AVV, null));
				}

			} else if (codTipoMov.equalsIgnoreCase("TRA")) {
				String trasfind = record.containsKey("NuovoTempoIndTrasf") ? (String) record.get("NuovoTempoIndTrasf")
						: "";
				String trasfdet = record.containsKey("NuovoTempoDetTrasf") ? (String) record.get("NuovoTempoDetTrasf")
						: "";
				String dattrasf = record.containsKey("DataTrasf") ? (String) record.get("DataTrasf") : "";
				// Se la trasformazione è a tempo determinato la data di fine è
				// quella dell'avviamento
				String datfinetrasf = record.containsKey("DataCessazTempoDetAvv")
						? (String) record.get("DataCessazTempoDetAvv")
						: "";

				// Controllo data di trasformazione
				if (isDate(dattrasf)) {
					record.put("DataInizioMov", dattrasf);
				} else {
					warnings.add(new Warning(MessageCodes.ImportMov.WAR_INIZIO_TRASF, null));
				}

				if (!trasfind.equals("")) {
					// Trasformazione verso indeterminato
					record.put("CodTempo", "I");
				} else if (!trasfdet.equals("")) {
					// Trasformazione verso determinato
					record.put("CodTempo", "D");
					// Imposto la data di fine movimento
					if (isDate(datfinetrasf)) {
						record.put("DataFineMov", datfinetrasf);
					} else {
						warnings.add(new Warning(MessageCodes.ImportMov.WAR_FINE_TRA_DET, null));
					}
				} else {
					// Non ho trovato nessuna delle indicazioni sul tempo
					warnings.add(new Warning(MessageCodes.ImportMov.WAR_COD_TEMPO_TRASF, null));
				}

			} else if (codTipoMov.equalsIgnoreCase("PRO")) {

				String datiniziopro = record.containsKey("DataInizioPro") ? (String) record.get("DataInizioPro") : "";
				String datfinepro = record.containsKey("DataFinePro") ? (String) record.get("DataFinePro") : "";
				String datavvind = record.containsKey("DataAvvTempoInd") ? (String) record.get("DataAvvTempoInd") : "";

				// Controllo data di inizio proroga
				if (isDate(datiniziopro)) {
					record.put("DataInizioMov", datiniziopro);
				} else {
					warnings.add(new Warning(MessageCodes.ImportMov.WAR_INIZIO_PRO, null));
				}

				if (DeTipoContrattoConstant.APPRENDISTATO_PROFESSIONALIZZANTE_O_CONTRATTO_DI_MESTIERE
						.equalsIgnoreCase(codTipoAss)) {
					if (flgLavoroStagionale.equalsIgnoreCase("S")) {
						record.put("CodTempo", "D");
					} else {
						if (!datavvind.equals("")) {
							prorogaPeriodoFormativo(record);
						} else {
							record.put("CodTempo", "D");
						}
					}
				} else {
					if (contrattoApprendistatoTI && !flgLavoroStagionale.equalsIgnoreCase("S")) {
						prorogaPeriodoFormativo(record);
					} else {
						// La proroga può anche essere a tempo indeterminato
						// (quando il flag lavoro stagionale = SI)
						// La proroga siffatta sarà bloccata in validazione
						if (!datavvind.equals("")) {
							record.put("CodTempo", "I");
						} else {
							record.put("CodTempo", "D");
						}
					}
				}

				// Controllo data di fine proroga
				if (isDate(datfinepro)) {
					record.put("DataFineMov", datfinepro);
				} else {
					warnings.add(new Warning(MessageCodes.ImportMov.WAR_FINE_PRO, null));
				}

			} else if (codTipoMov.equalsIgnoreCase("CES")) {
				String datavvind = record.containsKey("DataAvvTempoInd") ? (String) record.get("DataAvvTempoInd") : "";
				String datavvdet = record.containsKey("DataAvvTempoDet") ? (String) record.get("DataAvvTempoDet") : "";

				String datces = record.containsKey("DataCess") ? (String) record.get("DataCess") : "";
				// Controllo data di fine cessazione
				if (isDate(datces)) {
					record.put("DataInizioMov", datces);
					record.put("DataFineMov", datces);
				} else {
					warnings.add(new Warning(MessageCodes.ImportMov.WAR_DATA_CES, null));
				}

				if (!datavvind.equals("")) {
					// Ho un tempo indeterminato
					record.put("CodTempo", "I");
				} else if (!datavvdet.equals("")) {
					// Ho un tempo determinato
					record.put("CodTempo", "D");
				} else {
					// Non ho trovato nessuna delle indicazioni sul tempo
					warnings.add(new Warning(MessageCodes.ImportMov.WAR_COD_TEMPO_CES, null));
				}
			}

			else {
				warnings.add(new Warning(MessageCodes.ImportMov.WAR_TIPO_MOV_NOCODIF, null));
			}

			if (warnings.size() > 0) {
				return ProcessorsUtils.createResponse(name, classname, null, null, warnings, nested);
			} else {
				return null;
			}
		} catch (Exception e) {
			return ProcessorsUtils.createResponse(name, classname, new Integer(MessageCodes.General.OPERATION_FAIL),
					"Importazione movimento fallito. ", warnings, nested);
		}
	}

	// Trasforma la proroga in una trasformazione
	// "proroga del periodo formativo"
	@SuppressWarnings("unchecked")
	private void prorogaPeriodoFormativo(Map record) throws Exception {
		record.put("FLGAPPRENDTI", "S");
		record.put("CodTempo", "I");
		record.put("evento", "TRA");
		record.put("eventoNew", "TRA");
		// Proroga periodo formativo
		record.put("CODTIPOTRASF", TipoTrasfEnum.PROSECUZIONE_PERIODO_FORMATIVO.getCodice());
		// mappatura codice variazione proroga -> trasformazione
		String codiceVariazione = record.get("CODVARIAZIONE") == null ? "" : record.get("CODVARIAZIONE").toString();
		String datfinepro = record.containsKey("DataFinePro") ? (String) record.get("DataFinePro") : "";
		// aggiornamento data fine pf
		record.remove("DATAFINEPERIODOFORM");
		record.put("DATAFINEPERIODOFORM", datfinepro);
		if (CodiceVariazioneEnum.PROROGA_ASSENZA_MISSIONE.getCodice().equals(codiceVariazione)) {
			codiceVariazione = CodiceVariazioneEnum.TRASFORMAZIONE_IN_ASSENZA_DI_MISSIONE.getCodice();
		} else if (CodiceVariazioneEnum.PROROGA_LAVORO_E_MISSIONE.getCodice().equals(codiceVariazione)
				|| CodiceVariazioneEnum.PROROGA_MISSIONE_RAPPORTO_TEMPO_INDETERMINATO.getCodice()
						.equals(codiceVariazione)) {
			codiceVariazione = CodiceVariazioneEnum.TRASFORMAZIONE_IN_COSTANZA_DI_MISSIONE.getCodice();
			// aggiornamento data fine missione
			record.remove("DATFINERAPLAV");
			record.put("DATFINERAPLAV", datfinepro);
		}
		record.remove("CODVARIAZIONE");
		record.put("CODVARIAZIONE", codiceVariazione);
	}

	// Controlla che la stringa passata corrisponda ad una data del formato
	// 'DDMMYYYY'
	private boolean isDate(String date) {
		try {
			formatter[0].parse(date);
			return true;
		} catch (Exception e) {
			try {
				formatter[1].parse(date);
				return true;
			} catch (Exception e1) {
				return false;
			}
		}
	}
}
/*
 * Creato il 18-nov-04
 *
 * Per modificare il modello associato a questo file generato, aprire
 * Finestra&gt;Preferenze&gt;Java&gt;Generazione codice&gt;Codice e commenti
 */
package it.eng.sil.module.movimenti.processors;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Map;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.base.SourceBeanException;
import com.engiweb.framework.util.QueryExecutor;

import it.eng.afExt.utils.DateUtils;
import it.eng.afExt.utils.MessageCodes;
import it.eng.sil.Values;
import it.eng.sil.module.movimenti.RecordProcessor;
import it.eng.sil.module.movimenti.constant.MotivoCesazioneConstant;
import it.eng.sil.module.movimenti.extractor.ManualValidationFieldExtractor;

/**
 * Processor che si occupa di inserire nella map i valori di default eventualmente non presenti in inserimento e/o
 * validazione manuale e massiva
 * 
 * @author roccetti
 */
public class PrevalorizzaCampi implements RecordProcessor {

	private String name;
	private String classname = this.getClass().getName();

	public PrevalorizzaCampi(String name) {
		this.name = name;
	}

	public SourceBean processRecord(Map record) throws SourceBeanException {
		// vettore delle warnings da restituire
		ArrayList warnings = new ArrayList();
		// Vettore dei risultati annidati da restituire
		ArrayList nested = new ArrayList();

		// Se il record è nullo non lo posso elaborare, ritorno l'errore
		if (record == null) {
			return ProcessorsUtils.createResponse(name, classname, new Integer(MessageCodes.ImportMov.ERR_INSERT_DATA),
					"Record da elaborare nullo.", warnings, nested);
		}

		// Se non ho il CODMONOMOVDICH lo setto ad "O"
		// (nella validazione massiva non è impostato da nessun'altra parte)
		if (!record.containsKey("CODMONOMOVDICH") || record.get("CODMONOMOVDICH").toString().equals("")) {
			record.put("CODMONOMOVDICH", "O");
		}
		/*
		 * Se sono in validazione massiva, o manuale devo impostare il CODSTATOATTO da 'DV' a 'PR'
		 */
		String context = record.get("CONTEXT") != null ? record.get("CONTEXT").toString() : "";
		if (context.equalsIgnoreCase("validazioneMassiva") && record.get("CODSTATOATTO") != null
				&& record.get("CODSTATOATTO").equals("DV")) {
			record.remove("CODSTATOATTO");
			record.put("CODSTATOATTO", "PR");
		}

		// Nella vecchia gestione dei movimenti venivano utilizzati più files per la gestione dei campi dei
		// movimenti, uno per l'avviamento, uno per la cessazione e uno per la proroga e trasformazione.
		// Nella nuova gestione è stato utilizzato un unico file contenente tutti i campi e per questo motivo
		// alcuni sono stati rinominati, in particolare le date.
		// la data inizio dell'avviamento è datInizioMov, quelle per la cessazione, trasformazione e proroga sono
		// state rinominate datInizioMovCes,datInizioMovTra,datFineMovPro rispettivamente.
		// Per avere una gestione simile alla precedente lato server sono stati eseguiti degli
		// uteriori controlli
		ManualValidationFieldExtractor extractor = new ManualValidationFieldExtractor();

		String codTipoMis = record.get("CODTIPOMIS") != null ? (String) record.get("CODTIPOMIS") : "";
		String codTipoAzienda = record.get("CODAZTIPOAZIENDA") != null ? record.get("CODAZTIPOAZIENDA").toString() : "";
		String flgAssPropria = record.get("FLGASSPROPRIA") != null ? (String) record.get("FLGASSPROPRIA") : "";
		boolean notAssPropria = "N".equalsIgnoreCase(flgAssPropria);
		String datfineMov = extractor.estraiDataFineMovimento(record);
		String codTipoMov = record.get("CODTIPOMOV") != null ? (String) record.get("CODTIPOMOV") : "";
		String flgLavoroAgr = record.get("FLGLAVOROAGR") != null ? (String) record.get("FLGLAVOROAGR") : "";
		String flgDistaAzEstera = record.get("FLGDISTAZESTERA") != null ? (String) record.get("FLGDISTAZESTERA") : "";
		String codMonoTempo = record.get("CODMONOTEMPO") != null ? (String) record.get("CODMONOTEMPO") : "";
		// Decreto 2016 - Assunzione e categoria obbligatorie
		String flgAssObbl = record.get("FLGASSOBBL") != null ? (String) record.get("FLGASSOBBL") : "";
		String codCatAssObbl = record.get("CODCATASSOBBL") != null ? (String) record.get("CODCATASSOBBL") : "";
		String flgLegge68 = "";
		if (flgAssObbl.equalsIgnoreCase(Values.FLAG_TRUE) && !codCatAssObbl.equals("")) {
			Object params[] = new Object[1];
			params[0] = codCatAssObbl;
			SourceBean row = null;
			row = (SourceBean) QueryExecutor.executeQuery("GET_INFO_CAT_LAV_ASS_OBBL", params, "SELECT",
					Values.DB_SIL_DATI);
			if (row == null) {
				throw new SourceBeanException(
						"impossibile leggere le info categoria lavoratore assunzione obbligatoria");
			}
			row = row.containsAttribute("ROW") ? (SourceBean) row.getAttribute("ROW") : row;
			flgLegge68 = row.getAttribute("flgLegge68") != null ? row.getAttribute("flgLegge68").toString() : "";
			String flgCatUniSomm = row.getAttribute("flgUniSomm") != null ? row.getAttribute("flgUniSomm").toString()
					: Values.FLAG_FALSE;
			if (flgLegge68.equalsIgnoreCase(Values.FLAG_TRUE)) {
				record.put("FLGLEGGE68", flgLegge68);
			} else {
				if (record.containsKey("FLGLEGGE68")) {
					record.remove("FLGLEGGE68");
				}
			}
			if (flgCatUniSomm != null) {
				record.put("FLGCATOBBLIGOUNISOMM", flgCatUniSomm);
			}
		} else {
			if (record.containsKey("FLGLEGGE68")) {
				record.remove("FLGLEGGE68");
			}
		}

		String datConvenzione = record.get("DATCONVENZIONE") != null ? (String) record.get("DATCONVENZIONE") : "";
		String numConvenzione = record.get("NUMCONVENZIONE") != null ? (String) record.get("NUMCONVENZIONE") : "";
		// Rettifica/validazione di vecchi movimenti che avevano le informazioni relative all'assunzione in obbligo
		if (!numConvenzione.equals("") && !datConvenzione.equals("")
				&& !flgLegge68.equalsIgnoreCase(Values.FLAG_TRUE)) {
			if (context.equalsIgnoreCase("validazioneMassiva")) {
				return ProcessorsUtils.createResponse(name, classname,
						new Integer(MessageCodes.ImportMov.ERR_CAT_LAV_ASS_OBBL_ASSENTE_VECCHIO_TRACCIATO), "",
						warnings, nested);
			} else {
				if (record.containsKey("FLGLEGGE68")) {
					record.remove("FLGLEGGE68");
				}
				if (record.containsKey("CODCATASSOBBL")) {
					record.remove("CODCATASSOBBL");
				}
				if (record.containsKey("FLGASSOBBL")) {
					record.remove("FLGASSOBBL");
				}
				record.remove("DATCONVENZIONE");
				record.remove("NUMCONVENZIONE");
			}
		}

		if (flgLavoroAgr.equals("")) {
			record.put("FLGLAVOROAGR", "N");
		}

		if (!context.equalsIgnoreCase("validazioneMassiva")) {
			String datInizioMov = record.get("DATINIZIOMOV") != null ? (String) record.get("DATINIZIOMOV") : "";
			String datInizioMovCes = record.get("DATINIZIOMOVCES") != null ? (String) record.get("DATINIZIOMOVCES")
					: "";
			String datInizioMovTra = record.get("DATINIZIOMOVTRA") != null ? (String) record.get("DATINIZIOMOVTRA")
					: "";
			String datfineMovPro = record.get("DATFINEMOVPRO") != null ? (String) record.get("DATFINEMOVPRO") : "";
			String codLavorazioneCes = record.get("CODLAVORAZIONECES") != null
					? (String) record.get("CODLAVORAZIONECES")
					: "";
			String numLivelloCes = record.get("NUMLIVELLOCES") != null ? (String) record.get("NUMLIVELLOCES") : "";
			String flgLavoroAgrCes = record.get("FLGLAVOROAGRCES") != null ? (String) record.get("FLGLAVOROAGRCES")
					: "";
			String codMvTrasformazione = record.get("CODTIPOTRASF") != null ? (String) record.get("CODTIPOTRASF") : "";
			BigDecimal prgAziendaTra = (BigDecimal) record.get("PRGAZIENDATRA");
			BigDecimal prgUnitaTra = (BigDecimal) record.get("PRGUNITATRA");
			String collegato = record.get("COLLEGATO") != null ? (String) record.get("COLLEGATO") : "";

			String strRagioneSocialeAzTra = record.get("STRRAGIONESOCIALEAZTRA") != null
					? (String) record.get("STRRAGIONESOCIALEAZTRA")
					: "";
			String strIndirizzoUAzTra = record.get("STRINDIRIZZOUAZTRA") != null
					? (String) record.get("STRINDIRIZZOUAZTRA")
					: "";
			String codComuneUAzTra = record.get("CODCOMUNEUAZTRA") != null ? (String) record.get("CODCOMUNEUAZTRA")
					: "";

			// Se il tipo di movimento è la CESSAZIONE:
			if (codTipoMov.equals("CES")) {
				// se si tratta di una cessazione veloce nel campo DATAINIZIOAVVCEV viene inserito il valore del
				// campo data inizio (se presente) della sezione Dati rapporto.
				if (collegato.equals("nessuno") && !datInizioMov.equals("")) {
					record.put("DATAINIZIOAVVCEV", datInizioMov);
				}
				// la data inizio cessazione, contenuta nella variabile datInizioMovCes, deve essere memorizzata in
				// DATINIZIOMOV (in questo modo il funzionamento lato server è identico al precedente)
				if (!datInizioMovCes.equals("")) {
					record.put("DATINIZIOMOV", datInizioMovCes);
				}
				// i campi numLivello e codLavorazione sono stati rinominati in numLivelloCes codLavorazioneCes solo
				// per una questione di layout, ma memorizzando i rispettivi valori in NUMLIVELLO e CODLAVORAZIONE
				// tutto continua a funzionare come prima

				record.put("NUMLIVELLO", numLivelloCes);
				record.put("CODLAVORAZIONE", codLavorazioneCes);
				if (flgLavoroAgrCes.equals("")) {
					flgLavoroAgrCes = "N";
				}
				record.put("FLGLAVOROAGR", flgLavoroAgrCes);

				// nella sezione dati rapporto è presente anche il campo data fine che, nel caso di una cessazione,
				// deve essere rimosso perché una cessazione non ha una data fine, tranne nel caso di sospensione dal
				// lavoro

				String codMvCessazione = extractor.estraiCodMotivoCessazione(record);
				if (!codMvCessazione.equalsIgnoreCase(MotivoCesazioneConstant.SOSPESO_DAL_LAVORO)) {
					if (!datfineMov.equals("")) {
						record.remove("DATFINEMOV");
					}
				} else {
					if (!datInizioMovCes.equals("")) {
						record.put("DATFINEMOV", datInizioMovCes);
					}
				}
			}
			// Se il tipo di movimento è la TRASFORMAZIONE:
			if (codTipoMov.equals("TRA")) {
				// la data inizio trasformazione, contenuta nella variabile datInizioMovTra, deve essere memorizzata in
				// DATINIZIOMOV (in questo modo il funzionamento lato server è identico al precedente)
				if (!datInizioMovTra.equals("")) {
					record.put("DATINIZIOMOV", datInizioMovTra);
				}
				// se il motivo trasformazione è trasferimento del lavoratore, si deve indicare la sede presso la quale
				// viene trasferito il lavoratore
				if (!codMvTrasformazione.equals("")) {
					if (codMvTrasformazione.equals("TL")) {
						if (prgAziendaTra != null) {
							record.put("PRGAZIENDA", prgAziendaTra);
						}
						if (prgUnitaTra != null) {
							record.put("PRGUNITAPRODUTTIVA", prgUnitaTra);
						}

						record.put("STRUAINDIRIZZO", strIndirizzoUAzTra);
						record.put("CODUACOM", codComuneUAzTra);
						record.put("STRAZRAGIONESOCIALE", strRagioneSocialeAzTra);
					}

					if (!codMvTrasformazione.equalsIgnoreCase("DL")) {
						if (record.get("DATFINEDISTACCO") != null) {
							record.remove("DATFINEDISTACCO");
						}
						if (record.get("FLGDISTPARZIALE") != null) {
							record.remove("FLGDISTPARZIALE");
						}
						if (record.get("FLGDISTAZESTERA") != null) {
							record.remove("FLGDISTAZESTERA");
						}
					}
				}

				if (codMonoTempo.equals("I") && !datfineMov.equals("")) {
					record.remove("DATFINEMOV");
					record.remove("DATFINEMOVEFFETTIVA");
				}

			}
			// Se il tipo di movimento è la PROROGA:
			if (codTipoMov.equals("PRO")) {
				try {
					if (datfineMov != null && !datfineMov.equals("")
							&& DateUtils.compare(datfineMov, datInizioMov) >= 0) {
						record.put("DATINIZIOMOV", DateUtils.giornoSuccessivo(datfineMov));
					} else {
						// sezione date contratto, inizio e fine, non compatibili in caso di validazione
						record.put("INIZIOPROROGAASSENTE", "1");
					}
				} catch (Exception e) {
					return ProcessorsUtils.createResponse(name, classname,
							new Integer(MessageCodes.ImportMov.ERR_REC_DATI_DB),
							"Impossibile calcolare data inizio proroga", warnings, nested);
				}
				// La data fine proroga è contenuta nella variabile datfineMovPro e deve essere inserita in DATFINEMOV
				if (!datfineMovPro.equals("")) {
					record.put("DATFINEMOV", datfineMovPro);
					record.put("DATFINEMOVEFFETTIVA", datfineMovPro);
				}
			}

			// Nel caso di distacco e sto in inserimento o rettifica devo riportare i campi dell'utilizzatrice
			// (PRGAZIENDAUTIL, PRGUNITAUTIL)
			// nei campi relativi all'azienda distaccataria (PRGAZIENDADIST, PRGUNITADIST)
			if (codTipoMov.equalsIgnoreCase("TRA") && codMvTrasformazione.equalsIgnoreCase("DL")
					&& !context.startsWith("valida") && !flgDistaAzEstera.equalsIgnoreCase("S")) {
				if (record.containsKey("PRGAZIENDAUTIL")) {
					record.put("PRGAZIENDADIST", record.get("PRGAZIENDAUTIL"));
				}
				if (record.containsKey("PRGUNITAUTIL")) {
					record.put("PRGUNITADIST", record.get("PRGUNITAUTIL"));
				}
			}
		} else {
			String codTipoTrasf = record.get("CODTIPOTRASF") != null ? (String) record.get("CODTIPOTRASF") : "";
			String datFineDistacco = record.get("DATAZINTFINECONTRATTO") != null
					? (String) record.get("DATAZINTFINECONTRATTO")
					: "";
			if (codTipoMov.equalsIgnoreCase("TRA") && codTipoTrasf.equalsIgnoreCase("DL")
					&& !datFineDistacco.equals("")) {
				record.put("DATFINEDISTACCO", datFineDistacco);
			}
		}

		if (codTipoAzienda.equalsIgnoreCase("INT") && notAssPropria && codTipoMis.equals("")) {
			record.put("CODTIPOMIS", codTipoMov);
		}

		return null;
	}

}

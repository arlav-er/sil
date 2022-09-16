package it.eng.sil.module.movimenti.processors;

import java.util.ArrayList;
import java.util.Map;
import java.util.Vector;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.base.SourceBeanException;

import it.eng.afExt.utils.MessageCodes;
import it.eng.afExt.utils.StringUtils;
import it.eng.afExt.utils.TransactionQueryExecutor;
import it.eng.sil.action.report.UtilsConfig;
import it.eng.sil.module.movimenti.RecordProcessor;
import it.eng.sil.util.UtilityNumGGTraDate;

/**
 * Processore che modifica i dati estratti dal file dei movimenti da importare e li prepara per l'inserimento nella
 * tabella di appoggio. (Ad esempio seleziona la data di inizio del movimento corretta, determina il valore dei flag dai
 * dati dekl record estratto, recupera il valore del codice del contratto a partire da quello del tipo di assunzione,
 * ecc...). Le operazioni eseguite dipendono dal tipo di movimento che si sta processando.
 * <p>
 * 
 * @author Paolo Roccetti
 */
public class SelectFieldsMovimentoAppoggio implements RecordProcessor {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger
			.getLogger(SelectFieldsMovimentoAppoggio.class.getName());

	private String name;
	private String classname = this.getClass().getName();

	private String provenienza;
	private TransactionQueryExecutor tex;

	/**
	 * Costruttore
	 * 
	 * @param name
	 *            Nome del processore
	 */
	public SelectFieldsMovimentoAppoggio(String name) {
		this.name = name;
	}

	/**
	 * Questo costruttore va usato solo se si proviene dalle migrazioni/comunicazioni obbligatorie (per ora).
	 * 
	 * @param name
	 * @param provenienza
	 * @param tex
	 * @throws IllegalArgumentException
	 *             se uno degli argomenti e' nullo.
	 */
	public SelectFieldsMovimentoAppoggio(String name, String provenienza, TransactionQueryExecutor tex) {
		this.name = name;
		if (tex == null || provenienza == null)
			throw new IllegalArgumentException(
					"provenienza e tex (TransactionQueryExecutor) debbono essere valorizzati entrambi.");
		this.provenienza = provenienza;
		this.tex = tex;
	}

	/**
	 * Seleziona alcuni campi da introdurre nella AM_MOVIMENTO_APPOGGIO
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

		try {

			// Se sono in importazione e la versione del tracciato è superiore
			// alla 2,0,3 allora
			// la matricola va reperita nel campo NewMatricolaAvv posizione 1561
			// del tracciato SARE
			String versioneTracciato = "";
			if (record.get("CONTEXT") != null && record.get("CONTEXT").toString().equalsIgnoreCase("importa")) { // Sono
																													// in
																													// fase
																													// di
																													// importazione
				if (record.containsKey("versioneTracciato")) {
					versioneTracciato = (record.get("versioneTracciato") != null)
							? (String) record.get("versioneTracciato")
							: "";
					if (!versioneTracciato.equals("")) {
						Vector vettVersione = null;
						if (versioneTracciato.indexOf(",") > 0)
							vettVersione = StringUtils.split(versioneTracciato, ",");
						else
							vettVersione = StringUtils.split(versioneTracciato, ".");
						versioneTracciato = "";
						for (int i = 0; i < vettVersione.size(); i++) {
							versioneTracciato = versioneTracciato + vettVersione.get(i);
						}
						int versTracciato = Integer.parseInt(versioneTracciato);
						int versRif = 203;
						if (versTracciato >= versRif) {
							String nuovaMatricola = (String) record.get("NewMatricolaAvv");
							// Se manca il campo metto una stringa vuota
							nuovaMatricola = (nuovaMatricola == null) ? "" : nuovaMatricola;
							record.put("MatricolaAvv", nuovaMatricola);
						}
					}
				}
			}

			boolean versione3 = false;
			String codtipomov = (String) record.get("evento");
			String codtipomovNew = (String) record.get("eventoNew");
			String referente = (String) record.get("Referente");
			String referenteNew = (String) record.get("ReferenteNew");
			String cfLav = (String) record.get("CodFiscLav");
			String cfAz = (String) record.get("CodFiscAz");
			String indirUaz = (String) record.get("IndirAz");
			if (!versioneTracciato.equals("")) {
				int versRif = 300;
				int versTracciato = Integer.parseInt(versioneTracciato);
				if (versTracciato >= versRif) {
					codtipomov = codtipomovNew;
					referente = referenteNew;
					record.put("Referente", referente);
					versione3 = true;
				}
			}

			// Tipo di movimento
			if (codtipomov == null) {
				return ProcessorsUtils.createResponse(name, classname,
						new Integer(MessageCodes.ImportMov.WAR_TIPO_MOV_NOVALORIZ), null, warnings, nested);
			}
			// se la versione del tracciato è >= 3.0.0, allora devo settare
			// due campi con il valore derivato dal nuovo tracciato
			if (versione3) {
				if (codtipomov.equalsIgnoreCase("av"))
					codtipomov = "AVV";
				else if (codtipomov.equalsIgnoreCase("pr"))
					codtipomov = "PRO";
				else if (codtipomov.equalsIgnoreCase("tr"))
					codtipomov = "TRA";
				else if (codtipomov.equalsIgnoreCase("ce"))
					codtipomov = "CES";
				record.put("evento", codtipomov);
			}

			// gestione campi motivo permesso di soggiorno
			if (record.get("CODMOTIVOPERMSOGGEX") != null) {
				String motPermSoggLav = record.get("CODMOTIVOPERMSOGGEX").toString();
				if (!motPermSoggLav.equals("")) {
					record.put("CODMOTIVOPERMSOGGEX", motPermSoggLav.toUpperCase());
				}
			}
			if (record.get("CODMOTIVOPSLEGRAPP") != null) {
				String motPermSoggLegaleRapp = record.get("CODMOTIVOPSLEGRAPP").toString();
				if (!motPermSoggLegaleRapp.equals("")) {
					record.put("CODMOTIVOPSLEGRAPP", motPermSoggLegaleRapp.toUpperCase());
				}
			}

			boolean avv = codtipomov.equalsIgnoreCase("AVV");
			boolean tra = codtipomov.equalsIgnoreCase("TRA");
			boolean pro = codtipomov.equalsIgnoreCase("PRO");
			boolean ces = codtipomov.equalsIgnoreCase("CES");

			if (!avv && !tra && !pro && !ces) {
				return ProcessorsUtils.createResponse(name, classname,
						new Integer(MessageCodes.ImportMov.WAR_TIPO_MOV_NOCODIF),
						"Il campo è obbligatorio per importare il movimento", warnings, nested);
			}

			// Controllo presenza CFLav, CFAz e IndirizzoAz per ricerche future
			if (cfLav == null || cfLav.equals("")) {
				return ProcessorsUtils.createResponse(name, classname, new Integer(MessageCodes.ImportMov.WAR_NO_CFLAV),
						"Il campo è obbligatorio per importare il movimento", warnings, nested);
			}
			if (cfAz == null || cfAz.equals("")) {
				return ProcessorsUtils.createResponse(name, classname, new Integer(MessageCodes.ImportMov.WAR_NO_CFAZ),
						"Il campo è obbligatorio per importare il movimento", warnings, nested);
			}
			if (indirUaz == null || indirUaz.equals("")) {
				warnings.add(new Warning(MessageCodes.ImportMov.WAR_NO_INDIRAZ,
						"Il campo è obbligatorio per validare il movimento"));
			}

			String codMansione = null;
			String codCCNL = null;
			String numLivello = null;
			String codAgevolaz = null;
			String numOreSett = null;
			String codGrado = null;
			String codOrario = null;

			// gestione campi generali
			if (record.get("DataRegCommittenti") != null && !record.get("DataRegCommittenti").equals("")) {
				record.put("flgLavDom", "S");
			}
			if (record.get("InBaseContrNaz") != null && ((String) record.get("InBaseContrNaz")).equalsIgnoreCase("S")) {
				record.put("InBaseContrNaz", "S");
			} else {
				record.put("InBaseContrNaz", "N");
			}

			/*
			 * Calcolo del numero di giorni che intercorrono tra la data di comunicazione e la data di inizio movimento.
			 */
			String datComunicaz = (String) record.get("dataEvento");
			String datInizioMov = (String) record.get("DataInizioMov");
			String codTipoAss = (String) record.get("CodTipoAvv");
			if ((datComunicaz != null) && (datInizioMov != null) && (codTipoAss != null) && !datComunicaz.equals("")
					&& !datInizioMov.equals("") && !codTipoAss.equals("")) {
				if (StringUtils.split(datComunicaz, "/").size() != 3 && datComunicaz.length() == 8) {
					datComunicaz = datComunicaz.substring(0, 2) + "/" + datComunicaz.substring(2, 4) + "/"
							+ datComunicaz.substring(4, 8);
				}
				if (StringUtils.split(datInizioMov, "/").size() != 3 && datInizioMov.length() == 8) {
					datInizioMov = datInizioMov.substring(0, 2) + "/" + datInizioMov.substring(2, 4) + "/"
							+ datInizioMov.substring(4, 8);
				}
				String numGiorni = String
						.valueOf(UtilityNumGGTraDate.getDateDiffNL(datComunicaz, datInizioMov, codTipoAss));
				record.put("NUMGGTRAMOVCOMUNICAZIONE", numGiorni);
			}

			// Tipo Azienda: se flgArtigina è a S è artigiana, se ho NumAlboInterinaliAz è interinale,
			// se ho flgpa è a S allora è pubblica amministrazione, altrimenti è privata
			String numAlboImprInt = (String) record.get("NumAlboInterinaliAz");
			boolean flgArtigiana = (record.get("flgAzArtigiana") != null
					&& ((String) record.get("flgAzArtigiana")).equalsIgnoreCase("S"));
			// nel caso di movimento di somministrazione, il flag FLGPA si riferisce all'azienda utilizzatrice
			boolean flgPubblicaAmm = (record.get("FLGPA") != null
					&& ((String) record.get("FLGPA")).equalsIgnoreCase("S"));

			if (numAlboImprInt != null && !numAlboImprInt.equals("")) {
				record.put("CodTipoAz", "INT");
			} else if (flgArtigiana) {
				record.put("CodTipoAz", "ART");
			} else if (flgPubblicaAmm) {
				record.put("CodTipoAz", "PA");
			} else {
				record.put("CodTipoAz", "AZI");
			}

			// Tipo Azienda Utilizzatrice: se flgArtigina è a S è artigiana, se flgpa è a S allora è pubblica
			// amministrazione
			// altrimenti è privata (ma solo se ho il CF dell'azienda utilizzatrice)
			if (record.get("CodFiscAzUtil") != null) {
				if (record.get("AzUtilArtigiana") != null
						&& ((String) record.get("AzUtilArtigiana")).equalsIgnoreCase("S")) {
					record.put("CodTipoAzUtil", "ART");
				} else {
					if (flgPubblicaAmm) {
						record.put("CodTipoAzUtil", "PA");
					} else {
						record.put("CodTipoAzUtil", "AZI");
					}
				}
			}

			// decreto 5 Novembre 2019
			// Gestione Orario

			String ft = (String) record.get("FullTimeAvv");
			String pt = (String) record.get("PartTimeAvv");
			// gestione campi avviamento
			if (avv || ces) {
				codMansione = (String) record.get("QualificaAvv");
				codCCNL = (String) record.get("CcnlAvv");
				numLivello = (String) record.get("LivelloAvv");
				codGrado = (String) record.get("GradoAvv");
				if (avv) {
					codAgevolaz = (String) record.get("BeneficiAvv");
				}
				// Gestione Orario
				if (ft != null && !ft.equals("")) {
					codOrario = "F";
				} else if (pt != null && !pt.equals("")) {
					// 26/07/2007 donato modifica codici orario parttime
					if (("P").equalsIgnoreCase(pt)) {
						codOrario = "P";
					} else if (("V").equalsIgnoreCase(pt)) {
						codOrario = "V";
					} else if (("M").equalsIgnoreCase(pt)) {
						codOrario = "M";
					} else {
						codOrario = "P";
					}
					numOreSett = (String) record.get("OrarioMedioSettAvv");
				} else
					codOrario = "N";
			}

			// gestione campi proroga / trasformazione
			if (tra || pro) {
				codMansione = (String) record.get("NuovaQualProTrasf");
				codCCNL = (String) record.get("NuovoCcnlProTrasf");
				numLivello = (String) record.get("NuovoLivProTrasf");
				codAgevolaz = (String) record.get("NuoviBeneficiProTrasf");
				codGrado = (String) record.get("NuovoGradoProTrasf");
				// Gestione Orario
				if (ft != null && !ft.equals("")) {
					codOrario = "F";
				} else if (pt != null && !pt.equals("")) {
					if (("P").equalsIgnoreCase(pt)) {
						codOrario = "P";
					} else if (("V").equalsIgnoreCase(pt)) {
						codOrario = "V";
					} else if (("M").equalsIgnoreCase(pt)) {
						codOrario = "M";
					} else {
						codOrario = "P";
					}
					numOreSett = (String) record.get("NuovoOrarioMedioSettTrasf");
				} else
					codOrario = "N";
			}
			// gestione campi trasformazione
			if (tra) {
				// gestione orario trasferimento
				if (numOreSett == null || numOreSett.equals("")) {
					codOrario = "F";
				}
			}

			// 11/09/2007: Per gestire la compatibilita' tra l'importazione da
			// comunicazione in xml e in formato
			// txt per il momento bisogna rimappare il campo codMansione.
			if ("importazione_file_txt".equals(provenienza)) {
				try {
					// Bisogna controllare se il movimento che si sta importando
					// da file txt proviene da sare o da una migrazione.
					// Se proviene da una migrazione il codice mansione e'
					// corretto, quindi non va mappato.
					boolean proviene_da_sare;
					// se proviena da una migrazione nel campo referente abbiamo
					// il codcpi che ha generato la migrazione
					if (referente != null && referente.length() == 9) {
						String codcpi_parteNumerica = referente.substring(0, 8);
						try {
							Integer.parseInt(codcpi_parteNumerica);
							proviene_da_sare = false;
						} catch (Exception e) {
							proviene_da_sare = true;
						}
					} else
						proviene_da_sare = true;

					if (proviene_da_sare) {

						if (codMansione != null) {
							SourceBean row = (SourceBean) tex.executeQueryByStringStatement(
									"select codmansione from de_mansione where codmansioneprec = ?",
									new Object[] { codMansione }, "SELECT");
							if (row == null)
								throw new SourceBeanException(
										"Impossibile rimappare la mansione: Importazione movimenti da migrazione in cooperazione.");
							String codMansioneMappato = (String) row.getAttribute("row.codmansione");
							if (codMansioneMappato != null)
								codMansione = codMansioneMappato;
							else
								_logger.debug("Codice di mappatura mansione non trovato:" + codMansione
										+ "  . Importazione movimenti da migrazione in cooperazione.");

						}
						String codManzioneTutoreAppr = (String) record.get("QualificaTutoreAppr");
						if (codManzioneTutoreAppr != null) {
							SourceBean row = (SourceBean) tex.executeQueryByStringStatement(
									"select codmansione from de_mansione where codmansioneprec = ?",
									new Object[] { codManzioneTutoreAppr }, "SELECT");
							if (row == null)
								throw new SourceBeanException(
										"Impossibile rimappare la mansione tutore apprendistato: Importazione movimenti da migrazione in cooperazione.");
							String codMansioneMappato = (String) row.getAttribute("row.codmansione");
							if (codMansioneMappato != null)
								record.put("QualificaTutoreAppr", codMansioneMappato);
							else
								_logger.debug("Codice di mappatura mansione tutore apprendistato non trovato:"
										+ codMansione + "  . Importazione movimenti da migrazione in cooperazione.");

						}
					}
				} catch (Exception e) {
					it.eng.sil.util.TraceWrapper.debug(_logger,
							"Errore nella mappatura del codice mansione in importazione movimento da migrazione in cooperazione",
							e);

					return ProcessorsUtils.createResponse(name, classname,
							new Integer(MessageCodes.ImportMov.ERR_QUAL_MOV),
							"Importazione movimento da Migrazioni in cooperazione. Impossibile mappare il campo codMansione",
							warnings, nested);
				}
			}

			// Inserimento dei campi nel record
			if (avv || tra || pro) {
				if (codMansione != null && !codMansione.equals("")) {
					record.put("codMansione", codMansione);
				} else if (avv) {
					warnings.add(new Warning(MessageCodes.ImportMov.WAR_COD_MANS,
							"Il campo è obbligatorio per validare il movimento"));
				}

				if (codCCNL != null && !codCCNL.equals("")) {
					record.put("codCCNL", codCCNL);
				} else if (avv) {
					warnings.add(new Warning(MessageCodes.ImportMov.WAR_COD_CCNL,
							"Il campo è obbligatorio per validare il movimento"));
				}

				if (numLivello != null && !numLivello.equals("")) {
					record.put("numLivello", numLivello);
				} else if (avv) {
					warnings.add(new Warning(MessageCodes.ImportMov.WAR_NUM_LIV,
							"Il campo è obbligatorio per validare il movimento"));
				}

				if (codAgevolaz != null && !codAgevolaz.equals("")) {
					record.put("codBenefici", codAgevolaz);
				}

				if (codGrado != null && !codGrado.equals("")) {
					record.put("codGrado", codGrado);
				} else if (avv) {
					warnings.add(new Warning(MessageCodes.ImportMov.WAR_COD_GRADO,
							"Il campo è obbligatorio per validare il movimento"));
				}

				if (codOrario != null && !codOrario.equals("")) {
					record.put("codOrario", codOrario);
				} else if (avv) {
					warnings.add(new Warning(MessageCodes.ImportMov.WAR_COD_ORARIO,
							"Il campo è obbligatorio per validare il movimento"));
				}

				if (numOreSett != null && !numOreSett.equals("")) {
					record.put("numOreSett", numOreSett);
				} else {
					if (codOrario != null && (codOrario.equalsIgnoreCase("P") || codOrario.equalsIgnoreCase("V")
							|| codOrario.equalsIgnoreCase("PTVS") || codOrario.equalsIgnoreCase("M"))) {
						warnings.add(new Warning(MessageCodes.ImportMov.WAR_NUM_ORESETT,
								"Il campo è obbligatorio per validare il movimento part-time"));
					}
				}
			}

			if (ces) {
				if (codGrado != null && !codGrado.equals("")) {
					record.put("codGrado", codGrado);
				} else {
					warnings.add(new Warning(MessageCodes.ImportMov.WAR_COD_GRADO,
							"Il campo è obbligatorio per validare il movimento"));
				}
				if (codMansione != null && !codMansione.equals("")) {
					record.put("codMansione", codMansione);
				} else {
					warnings.add(new Warning(MessageCodes.ImportMov.WAR_COD_MANS,
							"Il campo è obbligatorio per validare il movimento"));
				}
				if (numLivello != null && !numLivello.equals("")) {
					record.put("numLivello", numLivello);
				} else {
					warnings.add(new Warning(MessageCodes.ImportMov.WAR_NUM_LIV,
							"Il campo è obbligatorio per validare il movimento"));
				}
				if (codCCNL != null && !codCCNL.equals("")) {
					record.put("codCCNL", codCCNL);
				}
				if (codOrario != null && !codOrario.equals("")) {
					record.put("codOrario", codOrario);
				}
				if (numOreSett != null && !numOreSett.equals("")) {
					record.put("numOreSett", numOreSett);
				}
			}

			// Setto il flg per l'assunzione propria per interinali
			// (basato sulla presenza o meno dei dati principali dell'azienda
			// utilizzatrice)
			String codTipoAz = (String) record.get("CodTipoAz");
			if (codTipoAz != null && codTipoAz.equalsIgnoreCase("INT")) {
				String codFiscAzUtil = (String) record.get("CodFiscAzUtil");
				String codComAzUtil = (String) record.get("CodComAzUtil");
				if (codFiscAzUtil != null && codComAzUtil != null && !codFiscAzUtil.equals("")
						&& !codComAzUtil.equals("")) {
					record.put("FlgAssPropria", "N");
				} else {
					record.put("FlgAssPropria", "S");
				}
			}

			// A seconda della configurazione(Default "0" o Custom "1"), devo pulire il field DecRetribuzioneMen
			// Gestione configurazione (Default o Custom)
			UtilsConfig utility = new UtilsConfig("MOV_RED");
			String tipoConfig = utility.getConfigurazioneDefault_Custom();
			if (tipoConfig.equals("1")) {
				// configurazione custom
				if (record.containsKey("DECRETRIBUZIONEMEN")) {
					record.remove("DECRETRIBUZIONEMEN");
				}
			}

			if (warnings.size() > 0) {
				return ProcessorsUtils.createResponse(name, classname, null, null, warnings, nested);
			} else {
				return null;
			}
		} catch (Exception e1) {
			// 01/10/2007: aggiunto try/catch
			String msg = record != null ? record.toString() : "oggetto record nullo";
			msg = "Errore generico nel recupero delle informazioni del movimento da validare: record=" + msg;
			it.eng.sil.util.TraceWrapper.debug(_logger, msg, e1);

			return ProcessorsUtils.createResponse(name, classname, new Integer(MessageCodes.General.OPERATION_FAIL),
					"Importazione movimento fallito. ", warnings, nested);
		}
	}
}
package it.eng.sil.module.movimenti.processors;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.Map;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.SessionContainer;
import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.base.SourceBeanException;

import it.eng.afExt.utils.DateUtils;
import it.eng.afExt.utils.MessageCodes;
import it.eng.afExt.utils.TransactionQueryExecutor;
import it.eng.sil.Values;
import it.eng.sil.module.movimenti.RecordProcessor;
import it.eng.sil.util.UtilityNumGGTraDate;

public class ControllaGen implements RecordProcessor {
	// private Connection conn;
	private String className;
	private String prc;
	private TransactionQueryExecutor trans;
	private static final String DATA_EDILIZIA_LEGGE = "12/08/2006";
	private static final String DATA_FINE_EDILIZIA_LEGGE = "31/12/2006";
	private static final String DATA_LEGGE_FINANZIARIA_2006 = "01/01/2007";
	private static final String CODATECOEDILIZIA = "45";
	private boolean checkForzaValidazione = false;
	private boolean eseguiFiltro = false;
	private SourceBean sbInfoGenerale = null;
	private String dataLavoroAgr = "";
	private String codRegione = "";

	public ControllaGen(SourceBean sbInfoGenerali, TransactionQueryExecutor transexec) {
		className = this.getClass().getName();
		prc = "Controlla Generale";
		trans = transexec;
		this.sbInfoGenerale = sbInfoGenerali;
		if (sbInfoGenerali != null) {
			if (sbInfoGenerali.containsAttribute("CHECKFORZAVALIDAZIONE")) {
				checkForzaValidazione = (sbInfoGenerali.getAttribute("CHECKFORZAVALIDAZIONE").toString() == "true"
						? true
						: false);
			}
			dataLavoroAgr = sbInfoGenerali.getAttribute("DATAGRICOLTURA") != null
					? sbInfoGenerali.getAttribute("DATAGRICOLTURA").toString()
					: "";
			if (sbInfoGenerali.containsAttribute("CODREGIONE")) {
				codRegione = sbInfoGenerali.getAttribute("CODREGIONE").toString();
			}
		} else {
			checkForzaValidazione = ProcessorsUtils.checkForzaValidazione(trans);
		}
	}

	public SourceBean processRecord(Map record) throws SourceBeanException {
		boolean ok = true;
		String dataI = "";
		int giorno, mese, anno;
		GregorianCalendar dataInizio, currDate;
		Warning w = null;
		ArrayList warnings = new ArrayList();
		SourceBean result = null;

		// Se il record è nullo non lo posso elaborare, ritorno l'errore
		if (record == null) {
			return ProcessorsUtils.createResponse(prc, className, new Integer(MessageCodes.ImportMov.ERR_GLOBALE),
					"Nessun dato inserito.", warnings, null);
		}
		// checkForzaValidazione = false se non sono in validazione massiva
		if (record.get("CONTEXT") == null || !record.get("CONTEXT").toString().equalsIgnoreCase("validazioneMassiva")) {
			checkForzaValidazione = false;
		}

		// DATAGRICOLTURA
		String context = record.get("CONTEXT") != null ? record.get("CONTEXT").toString() : "";

		String codTipoMov = (record.get("CODTIPOMOV") != null) ? (String) record.get("CODTIPOMOV") : "";
		String codTipoTrasf = (record.get("CODTIPOTRASF") != null) ? (String) record.get("CODTIPOTRASF") : "";
		BigDecimal numGGagricoltura = null;
		if (record.containsKey("NUMGGPREVISTIAGR")) {
			if (record.get("NUMGGPREVISTIAGR") instanceof BigDecimal) {
				numGGagricoltura = (BigDecimal) record.get("NUMGGPREVISTIAGR");
			} else if (record.get("NUMGGPREVISTIAGR") instanceof String) {
				numGGagricoltura = new BigDecimal((String) record.get("NUMGGPREVISTIAGR"));
			}
		}

		/*
		 * Se il contesto è la validazione per le TRASFORMAZIONI se si fa riferimento al distacco si seguono le solite
		 * regole della validazione negli altri casi per capire se è un tempo determinato o indeterminato si deve
		 * verificare il tipo di contratto il controllo viene eseguito in ControllaTP
		 */
		// controllo che il tipo di assunzione sia compatibile con i dati del movimento.
		// Per fare questo recupero la stessa query di controllo che viene eseguita
		// quando si inserisce manualmenteil movimento
		if (context.equalsIgnoreCase("validazioneMassiva")
				&& (!codTipoMov.equals("TRA") || (codTipoMov.equals("TRA") && codTipoTrasf.equalsIgnoreCase("DL")))) {
			eseguiFiltro = true;
		}
		String strVal = "";
		SourceBean requestFittizia = new SourceBean("SERVICE_REQUEST");
		requestFittizia.setAttribute("PAGE", "SelezionaContrattiSelettivaPage");
		requestFittizia.setAttribute("CRITERIO", "codice");

		if (record.get("CODAZTIPOAZIENDA") == null) {
			requestFittizia.setAttribute("CODTIPOAZIENDA", "");
		} else if (((String) record.get("CODAZTIPOAZIENDA")).equalsIgnoreCase("INT")
				&& record.get("FLGASSPROPRIA") != null
				&& ((String) record.get("FLGASSPROPRIA")).equalsIgnoreCase("S")) {
			requestFittizia.setAttribute("CODTIPOAZIENDA", "AZI");
		} else {
			requestFittizia.setAttribute("CODTIPOAZIENDA", ((String) record.get("CODAZTIPOAZIENDA")));
		}

		// CONTROLLO POST FASE 3 : eliminare il controllo tipo azienda/tipo contratto nella sola validazione massiva
		if (context.equalsIgnoreCase("validazioneMassiva")) {
			requestFittizia.setAttribute("checkForzaValidazione", "S");
		} else {
			requestFittizia.setAttribute("checkForzaValidazione", "N");
		}

		if (eseguiFiltro || !context.equalsIgnoreCase("validazioneMassiva")) {
			strVal = (record.get("codNatGiuridicaAz") == null ? "" : (String) record.get("codNatGiuridicaAz"));
			requestFittizia.setAttribute("CODNATGIURIDICA", strVal);

			strVal = (record.get("CODMONOTEMPO") == null ? "" : (String) record.get("CODMONOTEMPO"));
			requestFittizia.setAttribute("CODMONOTEMPO", strVal);

			strVal = (record.get("CODTIPOASS") == null ? "" : (String) record.get("CODTIPOASS"));
			requestFittizia.setAttribute("CODTIPOASS", strVal);

			it.eng.sil.module.movimenti.DynSelectContratto dsa = new it.eng.sil.module.movimenti.DynSelectContratto();
			String checkTipoContratto = dsa.getStatement(requestFittizia, null);
			result = null;
			try {
				result = ProcessorsUtils.executeSelectQuery(checkTipoContratto, trans);
			} catch (Exception e) {
				return ProcessorsUtils.createResponse(prc, className,
						new Integer(MessageCodes.ImportMov.ERR_REC_DATI_DB), "", warnings, null);
			}
			if (!result.containsAttribute("ROW")) {
				return ProcessorsUtils.createResponse(prc, className,
						new Integer(MessageCodes.ImportMov.ERR_TIPO_CONTRATTO_INCOMPATIBILE),
						"Il codice inserito per il tipo di contratto è: " + ((String) record.get("CODTIPOASS")),
						warnings, null);
			}
		}
		SessionContainer sessione = RequestContainer.getRequestContainer().getSessionContainer();
		String encryptKey = (String) sessione.getAttribute("_ENCRYPTER_KEY_");

		Object decRetribuzioneMen = record.get("DECRETRIBUZIONEMEN");
		String codCCNL = (record.get("CODCCNL") != null) ? (String) record.get("CODCCNL") : "";
		String livello = (record.get("NUMLIVELLO") != null) ? (String) record.get("NUMLIVELLO") : "";
		String strNumAlboInterinali = (record.get("STRAZNUMALBOINTERINALI") != null)
				? (String) record.get("STRAZNUMALBOINTERINALI")
				: "";

		String codMonoTempo = record.containsKey("CODMONOTEMPO") ? record.get("CODMONOTEMPO").toString() : "";
		String cfAzSommEstera = record.containsKey("CODFISCAZESTERA") ? record.get("CODFISCAZESTERA").toString() : "";

		/*
		 * in caso di assunzione in somministrazione se si inserisce l'azienda utilizzatrice, occorre inserire anche i
		 * dati di missione e viceversa.
		 */
		String flgAssPropria = (String) record.get("FLGASSPROPRIA");
		boolean notAssPropria = "N".equalsIgnoreCase(flgAssPropria);
		BigDecimal prgAzUtil = (BigDecimal) record.get("PRGAZIENDAUTIL");
		BigDecimal prgUnitaUtil = (BigDecimal) record.get("PRGUNITAUTIL");
		String codTipoAzienda = record.get("CODAZTIPOAZIENDA") != null ? record.get("CODAZTIPOAZIENDA").toString() : "";
		if (codTipoAzienda.equalsIgnoreCase("INT") && notAssPropria) {
			String dataInizioRapLav = record.containsKey("DATINIZIORAPLAV") ? record.get("DATINIZIORAPLAV").toString()
					: "";
			String dataFineRapLav = record.containsKey("DATFINERAPLAV") ? record.get("DATFINERAPLAV").toString() : "";
			try {
				// Se non si tratta di un'assunzione propria controllo che le date di missione sono corrette
				if (!dataInizioRapLav.equals("") && !dataFineRapLav.equals("")
						&& DateUtils.compare(dataFineRapLav, dataInizioRapLav) < 0) {
					// controllo bloccante (data fine missione deve essere >= data inizio missione)
					return ProcessorsUtils.createResponse(prc, className,
							new Integer(MessageCodes.General.OPERATION_FAIL),
							"Data fine missione deve essere maggiore o uguale a data inizio missione", warnings, null);
				}
				/* La data di fine missione è obbligatoria se la missione è a TD */
				if (dataFineRapLav.equals("") && !dataInizioRapLav.equals("")) {
					if (codMonoTempo.equals("D")) {
						return ProcessorsUtils.createResponse(prc, className,
								new Integer(MessageCodes.ImportMov.ERR_DATFINE_MISSIONE), "", warnings, null);
					}
				}
				if (!(prgAzUtil != null && prgUnitaUtil != null && !prgAzUtil.equals("") && !prgUnitaUtil.equals(""))) {
					// DONA 17/07/2007: trasformato errore azienda utilizzatrice in un WARNING
					w = new Warning(MessageCodes.ImportMov.ERR_AZ_UTILIZ, "");
					warnings.add(w);
					// return ProcessorsUtils.createResponse(prc, className, new
					// Integer(MessageCodes.ImportMov.ERR_AZ_UTILIZ), "", warnings, null);
				}
				// Controllo Decreto 31/03/2010
				String codfiscAZ = record.get("STRAZCODICEFISCALE") != null
						? record.get("STRAZCODICEFISCALE").toString()
						: "";
				String codfiscAZUtil = record.get("STRAZINTCODICEFISCALE") != null
						? record.get("STRAZINTCODICEFISCALE").toString()
						: "";
				// POSSIAMO AVERE AZIENDA SOMMINISTRAZIONE ESTERA E AZIENDA UTILIZZATRICE ESTERA OPERANTE IN ITALIA
				// (codfiscAZ = codfiscAZUtil = CODICE FISCALE FITTIZIO, MA RISULTA VALORIZZATO IL CODFISCAZESTERA DELLA
				// AZ. SOMMINISTRAZIONE).
				// IN TAL CASO L'ERRORE NON DEVE ESSERE RILEVATO E L'INSERIMENTO DEL MOVIMENTO DEVE PROCEDERE
				if (codfiscAZ.equals(codfiscAZUtil) && cfAzSommEstera.equals("")) {
					return ProcessorsUtils.createResponse(prc, className,
							new Integer(MessageCodes.ImportMov.ERR_AZ_UTILIZ),
							"L'azienda utilizzatrice deve essere diversa dall'azienda di somministrazione.", warnings,
							null);
				}
				// Per rapporti di somministrazione, le aziende devono avere il numero di iscrizione all'albo
				if (strNumAlboInterinali.equals("")) {
					return ProcessorsUtils.createResponse(prc, className,
							new Integer(MessageCodes.ImportMov.ERR_NUM_ISCR_ALBO), "", warnings, null);
				}

				String flgAssObbl = record.get("FLGASSOBBL") != null ? (String) record.get("FLGASSOBBL") : "";
				if (flgAssObbl.equalsIgnoreCase(Values.FLAG_TRUE) && !dataInizioRapLav.equals("")) {
					String flgCatUniSomm = record.get("FLGCATOBBLIGOUNISOMM") != null
							? (String) record.get("FLGCATOBBLIGOUNISOMM")
							: "";
					if (flgCatUniSomm.equals("") || flgCatUniSomm.equalsIgnoreCase(Values.FLAG_FALSE)) {
						return ProcessorsUtils.createResponse(prc, className,
								new Integer(MessageCodes.ImportMov.ERR_CAT_LAV_ASS_OBBL_UNISOMM), null, warnings, null);
					}
				}
			} catch (Exception ex) {
				// ritorno errore nei controlli sulle date
				return ProcessorsUtils.createResponse(prc, className, new Integer(MessageCodes.General.OPERATION_FAIL),
						"Errore sul controllo data inizio missione e data fine missione", warnings, null);
			}
		}

		/*
		 * CONTROLLI TOLTI PER RICHIESA DEL CLIENTE //Controllo valorizzazione/correttezza INPS if (
		 * (record.get("STRPOSINPS") == null) ){ w = new Warning(MessageCodes.ImportMov.WAR_NUM_INPS_NOVALORIZ, "");
		 * warnings.add(w); } else { if ( !(PosInpsUtils.controllaInps(record.get("STRPOSINPS").toString())) ) { w = new
		 * Warning(MessageCodes.ImportMov.WAR_NUM_INPS_ERRATO, ""); warnings.add(w); } }
		 * 
		 * //Controllo valorizzazione/correttezza PAT INAIL if ( record.get("STRPATINAIL") == null ){ w = new
		 * Warning(MessageCodes.ImportMov.WAR_NUM_INAIL_NOVALORIZ, ""); warnings.add(w); } else { if (
		 * !(PatInailUtils.controllaInail(record.get("STRPATINAIL").toString())) ) { w = new
		 * Warning(MessageCodes.ImportMov.WAR_NUM_INAIL_ERRATO, ""); warnings.add(w); } }
		 */

		// La data di comunicazione non può essere futura
		if ((record.get("DATCOMUNICAZ") != null) && (ok)) {
			dataI = record.get("DATCOMUNICAZ").toString();
			String dataCorrente = DateUtils.getNow();
			currDate = new GregorianCalendar(Integer.parseInt(dataCorrente.substring(6, 10)),
					(Integer.parseInt(dataCorrente.substring(3, 5)) - 1),
					Integer.parseInt(dataCorrente.substring(0, 2)));
			if (!(dataI.equals("")) && (dataI.length() == 10)) {
				giorno = Integer.parseInt(dataI.substring(0, 2));
				mese = Integer.parseInt(dataI.substring(3, 5));
				anno = Integer.parseInt(dataI.substring(6, 10));
				dataInizio = new GregorianCalendar(anno, (mese - 1), giorno);
				// Errore se la data di comunicazione è futura
				if (dataInizio.after(currDate)) {
					ok = false;
					return ProcessorsUtils.createResponse(prc, className,
							new Integer(MessageCodes.ImportMov.ERR_DATA_COMUNICAZ_FUTURA), "", warnings, null);
				}
				// Warning se è precedente
				if (dataInizio.before(currDate)) {
					w = new Warning(MessageCodes.ImportMov.WAR_DATA_COMUNICAZ_PRECEDENTE, "");
					warnings.add(w);
				}
			}
		}

		/*
		 * Warning se il numero di gg tra la data di comunicaz e la data di inizio avviamento è maggiore del limite
		 * massimo memorizzato in TS_GENERALE
		 */
		// Calcolo del NUMGGTRAMOVCOMUNICAZIONE se l'utente non l'ha inserito e si
		// ha come provenienza "Da comunicazione obbligatoria"
		String datInizioMov = (String) record.get("DATINIZIOMOV");
		String datComunicaz = (String) record.get("DATCOMUNICAZ");
		String codMonoMovDich = (record.get("CODMONOMOVDICH") != null) ? (String) record.get("CODMONOMOVDICH") : "";
		Object cdnLavoratore = record.get("CDNLAVORATORE");
		if (codMonoMovDich.equals("O")) {
			/*
			 * Calcolo del numero di giorni che intercorrono tra la data di comunicazione e la data di inizio movimento.
			 */
			datComunicaz = (String) record.get("DATCOMUNICAZ");
			String codTipoAss = (String) record.get("CODAZTIPOAZIENDA");
			if ((codTipoAss != null) && (datComunicaz != null) && (datInizioMov != null) && !datComunicaz.equals("")
					&& !datInizioMov.equals("") && !codTipoAss.equals("")) {
				String numGiorni = String
						.valueOf(UtilityNumGGTraDate.getDateDiffNL(datComunicaz, datInizioMov, codTipoAss));
				if (record.get("NUMGGTRAMOVCOMUNICAZIONE") != null)
					record.remove("NUMGGTRAMOVCOMUNICAZIONE");
				record.put("NUMGGTRAMOVCOMUNICAZIONE", numGiorni);
			}
		}

		// Commentato il controllo del movimento in ritardo.

		/*
		 * if ( (record.get("NUMGGTRAMOVCOMUNICAZIONE") != null) && codMonoMovDich.equals("O") ) { String
		 * numGGTRAMovComunicaz = record.get("NUMGGTRAMOVCOMUNICAZIONE").toString(); String codMotAnnullamento =
		 * (record.get("CODMOTANNULLAMENTO")!=null)?(String)record.get("CODMOTANNULLAMENTO"):""; if
		 * (codTipoMov.equalsIgnoreCase("AVV") && ( codMotAnnullamento.equalsIgnoreCase("URG") ||
		 * codMotAnnullamento.equalsIgnoreCase("MAG")) ) { if (Integer.parseInt(numGGTRAMovComunicaz) > 5) { w = new
		 * Warning(MessageCodes.ImportMov.WAR_NUMGG_SUP,""); warnings.add(w); } } else { String codAtecoEdiliziaMov =
		 * ""; boolean controlloGiorniRitardo = false; String codAtecoAz =
		 * record.get("CODAZATECO")!=null?record.get("CODAZATECO").toString():""; if (!codAtecoAz.equals("") &&
		 * codAtecoAz.length() > 1) { codAtecoEdiliziaMov = codAtecoAz.substring(0,2); } try { if (datComunicaz != null
		 * && !datComunicaz.equals("") && datInizioMov != null && !datInizioMov.equals("")) { Vector vettInizioMov =
		 * StringUtils.split(datInizioMov, "/"); String meseSuccLegge2006 = ""; int meseInizioMov =
		 * Integer.parseInt(vettInizioMov.get(1).toString()); int annoInizioMov =
		 * Integer.parseInt(vettInizioMov.get(2).toString()); if (meseInizioMov == 12) { int annoSucc = annoInizioMov +
		 * 1; meseSuccLegge2006 = "20/01/" + annoSucc; } else { int meseSucc = meseInizioMov + 1; String strMeseSucc =
		 * meseSucc<10?("0"+meseSucc):""+meseSucc; meseSuccLegge2006 = "20/" + strMeseSucc + "/" + annoInizioMov; }
		 * SourceBean cmResult = null; String queryCM =
		 * "SELECT COUNT(*) LAVCOLLMIRATO FROM AM_CM_ISCR, AM_DOCUMENTO DOC, AM_DOCUMENTO_COLL COLL " +
		 * "WHERE AM_CM_ISCR.PRGCMISCR = COLL.STRCHIAVETABELLA AND COLL.PRGDOCUMENTO = DOC.PRGDOCUMENTO " +
		 * "AND DOC.CODTIPODOCUMENTO = 'L68' AND DOC.CODSTATOATTO = 'PR' AND DECRYPT(AM_CM_ISCR.CDNLAVORATORE, '" +
		 * encryptKey + "') = " + cdnLavoratore; try { cmResult = ProcessorsUtils.executeSelectQuery(queryCM, trans); }
		 * catch (Exception e) { return ProcessorsUtils.createResponse(prc, className, new
		 * Integer(MessageCodes.ImportMov.ERR_REC_DATI_DB), "Errore nell'esecuzione della query CM", warnings, null); }
		 * int numIscCollocamentoMirato = Integer.parseInt(cmResult.getAttribute("ROW.LAVCOLLMIRATO").toString()); if
		 * (codTipoMov.equalsIgnoreCase("AVV") && codAtecoEdiliziaMov.equals(CODATECOEDILIZIA) &&
		 * DateUtils.compare(datInizioMov, DATA_EDILIZIA_LEGGE) >= 0 && DateUtils.compare(datInizioMov,
		 * DATA_FINE_EDILIZIA_LEGGE) <= 0) { controlloGiorniRitardo = true; if (DateUtils.compare(datComunicaz,
		 * datInizioMov) >= 0) { w = new
		 * Warning(MessageCodes.ImportMov.WAR_NUMGG_SUP,"Comunicazione in ritardo > 1 giorno (edilizia - Legge 248/06)."
		 * ); warnings.add(w); } } else { //CONTROLLI AVV PER LE AGENZIE DIVERSE DA QUELLE DI SOMMINISTRAZIONE if
		 * (codTipoMov.equalsIgnoreCase("AVV") && !codTipoAzienda.equalsIgnoreCase("INT") &&
		 * DateUtils.compare(datInizioMov, DATA_LEGGE_FINANZIARIA_2006) >= 0) { controlloGiorniRitardo = true; if
		 * (DateUtils.compare(datComunicaz, datInizioMov) >= 0) { w = new
		 * Warning(MessageCodes.ImportMov.WAR_NUMGG_SUP,""); warnings.add(w); } } else { //CONTROLLI CES PER LAVORATORI
		 * IN COLLOCAMENTO MIRATO if (codTipoMov.equalsIgnoreCase("CES") && DateUtils.compare(datInizioMov,
		 * DATA_LEGGE_FINANZIARIA_2006) >= 0 && numIscCollocamentoMirato > 0) { controlloGiorniRitardo = true; if
		 * (Integer.parseInt(numGGTRAMovComunicaz) > 10) { w = new Warning(MessageCodes.ImportMov.WAR_NUMGG_SUP,"");
		 * warnings.add(w); } } else { //CONTROLLI PER AGENZIE DI SOMMINISTRAZIONE (AVV, PRO/TRA, CES) if
		 * (codTipoAzienda.equalsIgnoreCase("INT") && DateUtils.compare(datInizioMov, DATA_LEGGE_FINANZIARIA_2006) >= 0)
		 * { controlloGiorniRitardo = true; if (DateUtils.compare(datComunicaz, meseSuccLegge2006) > 0) { w = new
		 * Warning(MessageCodes.ImportMov.WAR_NUMGG_SUP,""); warnings.add(w); } } else { //CONTROLLI CES PER AGENZIE
		 * DIVERSE DA QUELLE DI SOMMINISTRAZIONE if (DateUtils.compare(datInizioMov, DATA_LEGGE_FINANZIARIA_2006) >= 0
		 * && !codTipoAzienda.equalsIgnoreCase("INT") && codTipoMov.equalsIgnoreCase("CES")) { controlloGiorniRitardo =
		 * true; if (Integer.parseInt(numGGTRAMovComunicaz) > 5) { w = new
		 * Warning(MessageCodes.ImportMov.WAR_NUMGG_SUP,""); warnings.add(w); } } else { //CONTROLLI PRO/TRA PER LE
		 * AGENZIE DIVERSE DA QUELLE DI SOMMINISTRAZIONE if (DateUtils.compare(datInizioMov,
		 * DATA_LEGGE_FINANZIARIA_2006) >= 0 && !codTipoAzienda.equalsIgnoreCase("INT") &&
		 * (codTipoMov.equalsIgnoreCase("PRO") || codTipoMov.equalsIgnoreCase("TRA")) ) { controlloGiorniRitardo = true;
		 * if (Integer.parseInt(numGGTRAMovComunicaz) > 5) { w = new Warning(MessageCodes.ImportMov.WAR_NUMGG_SUP,"");
		 * warnings.add(w); } } } } } } } } //CONTROLLI PER MOVIMENTI CON DATA INIZIO PRECEDENTE AL 01/01/2007 if
		 * (!controlloGiorniRitardo && DateUtils.compare(datInizioMov, DATA_LEGGE_FINANZIARIA_2006) < 0) { BigDecimal
		 * numMaxGgRit = null; numMaxGgRit = (BigDecimal) sbInfoGenerale.getAttribute("NUMGGPRIMARITARDOMOV"); if (
		 * numMaxGgRit != null && (Integer.parseInt(numGGTRAMovComunicaz) > numMaxGgRit.intValue() )){ w = new
		 * Warning(MessageCodes.ImportMov.WAR_NUMGG_SUP,""); warnings.add(w); } } } catch (Exception e) { return
		 * ProcessorsUtils.createResponse(prc, className, new Integer(MessageCodes.General.OPERATION_FAIL),
		 * "Errore nel confronto di date", warnings, null); } } }
		 */

		// Controlo se il codice qualifica è valorizzato ed esiste nel DB
		String strNote = null;
		if (record.get("CODMANSIONE") != null) {
			String codMansione = (String) record.get("CODMANSIONE");
			String queryStat = "select CODMANSIONE from DE_MANSIONE where CODMANSIONE = '" + codMansione + "'";
			// Eseguo la query
			result = null;
			try {
				result = ProcessorsUtils.executeSelectQuery(queryStat, trans);
			} catch (Exception e) {
				return ProcessorsUtils.createResponse(prc, className,
						new Integer(MessageCodes.ImportMov.ERR_REC_DATI_DB),
						"Errore nella query per recuperare la mansione", warnings, null);
			}

			// Esamino il risultato
			String numMaxGgRit = (String) result.getAttribute("ROW.CODMANSIONE");
			if (numMaxGgRit == null) {
				if (checkForzaValidazione) {
					// modifica la qualifica a NT
					record.put("CODMANSIONE", "NT");
					// nota da aggiungere
					String notaAdd = "<li>La qualifica specificata è errata. Il sistema l'ha modificata da "
							+ codMansione + " a 'NT'.</li>";
					strNote = (String) record.get("STRNOTE");
					if (strNote != null) {
						strNote = strNote + notaAdd;
					} else {
						strNote = notaAdd;
					}
					record.put("STRNOTE", strNote);
				} else {
					return ProcessorsUtils.createResponse(prc, className,
							new Integer(MessageCodes.ImportMov.ERR_CODMANSIONE_INESISTENTE),
							"Il codice " + codMansione + " non è presente nel DB", warnings, null);
				}
			}
		} else {
			// DONA 20/02/2007: par 3.5 Manca codice qualifica
			// Se nel movimento che si sta validando non esista la qualifica viene inserita la codifica NT
			// Viene inserita la nota del movimento. La qualifica è obbligatoria solo per gli avviamenti (Landi
			// 04/07/2007)
			if (checkForzaValidazione) {
				// modifica la qualifica a NT
				record.put("CODMANSIONE", "NT");
				// nota da aggiungere
				String notaAdd = "<li>Non è stata specificata la qualifica. Il sistema ha impostato automaticamente 'NT'.</li>";
				strNote = (String) record.get("STRNOTE");
				if (strNote != null) {
					strNote = strNote + notaAdd;
				} else {
					strNote = notaAdd;
				}
				record.put("STRNOTE", strNote);
			} else {
				// DONA 18/07/2007 per aziende interinali QUALIFICA non obbligatoria
				if (record.containsKey("CODAZTIPOAZIENDA")
						&& record.get("CODAZTIPOAZIENDA").toString().equalsIgnoreCase("INT") && notAssPropria) {
					w = new Warning(MessageCodes.ImportMov.ERR_CODMANSIONE_NULL, "");
					warnings.add(w);
				} else {
					return ProcessorsUtils.createResponse(prc, className,
							new Integer(MessageCodes.ImportMov.ERR_CODMANSIONE_NULL), null, warnings, null);
				}
			}
		}

		if (record.get("CODORARIO") == null) {
			if (checkForzaValidazione) {
				// modifica dell'orario
				record.put("CODORARIO", "F");
			} else {
				// DONA 18/07/2007 per aziende interinali il CODORARIO non è obbligatorio
				if (record.containsKey("CODAZTIPOAZIENDA")
						&& record.get("CODAZTIPOAZIENDA").toString().equalsIgnoreCase("INT") && notAssPropria) {
					w = new Warning(MessageCodes.ImportMov.WAR_CODORARIO_NULL, "");
					warnings.add(w);
				} else {
					return ProcessorsUtils.createResponse(prc, className,
							new Integer(MessageCodes.ImportMov.ERR_REC_DATI_DB),
							"Il campo Orario non può essere vuoto.", warnings, null);
				}

			}
		}

		/*
		 * Compenso, Contratto collettivo e livello sono obbligatori in maniera eslusiva nel senso che si devono
		 * indicare in alternativa: Contratto e livello oppure Compenso.
		 */
		// CONTROLLO POST FASE 3 : eliminare controllo ccnl + livello o compenso obbligatorie
		/*
		 * if( ( codCCNL.equals("") && livello.equals("") && decRetribuzioneMen == null ) || ( decRetribuzioneMen ==
		 * null && ( ( !codCCNL.equals("") && livello.equals("") ) || ( codCCNL.equals("") && !livello.equals("") ) ) )
		 * ) { if (!checkForzaValidazione) { // in caso di aziende interinali non sono obbligatori if (
		 * record.containsKey("CODAZTIPOAZIENDA") && record.get("CODAZTIPOAZIENDA").toString().equalsIgnoreCase("INT")
		 * && notAssPropria) { w = new Warning(MessageCodes.ImportMov.WAR_CCNL_LIVELLO_COMPENSO_NULL, "");
		 * warnings.add(w); } else { return ProcessorsUtils.createResponse(prc, className, new
		 * Integer(MessageCodes.ImportMov.ERR_CCNL_LIVELLO_COMPENSO), "", warnings, null); } } }
		 */

		/*
		 * Nel caso di un avviamento (o cessazione) in agricoltura è obbligatorio indicare il numero dei giorni presunti
		 * (effettivi) in agr. e la lavorazione. CONTROLLO VALIDO FINO AL 15 gennaio 2009 dal 16 il controllo
		 * sull'agricoltura verrà fatto sul campo FLGLAVOROAGR e non sul il tipo di contratto
		 */
		String codTipoContratto = (record.get("CODTIPOASS") != null) ? (String) record.get("CODTIPOASS") : "";
		String codLavorazione = (record.get("CODLAVORAZIONE") != null) ? (String) record.get("CODLAVORAZIONE") : "";

		try {
			// if( codTipoContratto.equals("H.01.00") && (!dataLavoroAgr.equals("") &&
			// (DateUtils.compare(DateUtils.getNow(), dataLavoroAgr) < 0) ) ) {
			// if ( codTipoMov.equals("AVV") && (codLavorazione.equals("") || record.get("NUMGGPREVISTIAGR") == null ||
			// record.get("NUMGGPREVISTIAGR") == "" )) {
			// if (!codRegione.equalsIgnoreCase(it.eng.sil.module.movimenti.constant.Properties.REGIONE_CALABRIA)) {
			// return ProcessorsUtils.createResponse(prc, className, new
			// Integer(MessageCodes.ImportMov.ERR_LAVORAZIONE_NUMGGAGR_NULL), "", warnings, null);
			// }
			// }
			if (codTipoMov.equals("CES") && (codLavorazione.equals("") || record.get("NUMGGEFFETTUATIAGR") == null
					|| record.get("NUMGGEFFETTUATIAGR") == "")) {
				// if (!codRegione.equalsIgnoreCase(it.eng.sil.module.movimenti.constant.Properties.REGIONE_CALABRIA)
				// || (!codRegione.equalsIgnoreCase(it.eng.sil.module.movimenti.constant.Properties.RER))) {
				// return ProcessorsUtils.createResponse(prc, className, new
				// Integer(MessageCodes.ImportMov.ERR_LAV_NUMGGAGREFF_NULL), "", warnings, null);
				// }
				// }
				// }
				// else {
				if (!codTipoMov.equals("CES")
						&& (!dataLavoroAgr.equals("") && (DateUtils.compare(DateUtils.getNow(), dataLavoroAgr) > 0))) {
					if (record.get("FLGLAVOROAGR") != null && record.get("FLGLAVOROAGR").equals("S")) {
						if (codLavorazione.equals("")) {
							w = new Warning(MessageCodes.ImportMov.WAR_CAMPO_LAVORAZIONE, "");
							warnings.add(w);
						}
						// if( codMonoTempo.equals("D") && numGGagricoltura == null && (
						// !codRegione.equalsIgnoreCase(it.eng.sil.module.movimenti.constant.Properties.REGIONE_CALABRIA)
						// || (!codRegione.equalsIgnoreCase(it.eng.sil.module.movimenti.constant.Properties.RER)))) {
						// return ProcessorsUtils.createResponse(prc, className, new
						// Integer(MessageCodes.ImportMov.ERR_CONTRATTO_AGR_NUMGG), "", warnings, null);
						// }
					}
					if (codTipoContratto.equals("H.01.00")) {
						if (numGGagricoltura != null) {
							record.put("FLGLAVOROAGR", "S");
						}
						// else {
						// if
						// (!codRegione.equalsIgnoreCase(it.eng.sil.module.movimenti.constant.Properties.REGIONE_CALABRIA))
						// {
						// return ProcessorsUtils.createResponse(prc, className, new
						// Integer(MessageCodes.ImportMov.ERR_CONTRATTO_AGR_NUMGG), "", warnings, null);
						// }
						// }
					}
					if ((numGGagricoltura != null || !codLavorazione.equals("")) && codMonoTempo.equals("D")
							&& (record.get("FLGLAVOROAGR") == null || record.get("FLGLAVOROAGR").equals("N"))
							&& !codTipoContratto.equals("H.01.00")) {
						// if
						// (!codRegione.equalsIgnoreCase(it.eng.sil.module.movimenti.constant.Properties.REGIONE_CALABRIA))
						// {
						// return ProcessorsUtils.createResponse(prc, className, new
						// Integer(MessageCodes.ImportMov.ERR_FLAG_LAVAGR), "", warnings, null);
						// }
						// else {
						if (record.containsKey("NUMGGPREVISTIAGR")) {
							record.remove("NUMGGPREVISTIAGR");
						}
						if (record.containsKey("CODLAVORAZIONE")) {
							record.remove("CODLAVORAZIONE");
						}
						// }
					}
				}
			}
		} catch (Exception e) {
			return ProcessorsUtils.createResponse(prc, className, new Integer(MessageCodes.General.OPERATION_FAIL),
					"Errore nel confronto di date", warnings, null);
		}

		// DAVIDE 01/06/2007: "mappatura" del "flag" rischio asbetosi derivante da comunicazione UNICA: da SI/NO a S/N
		// (se no che flag !!)
		// Se non si mappa da errore nell'inserimento del movimento nel processor InsertData
		if (record.containsKey("FLGRISCHIOSIAS")) {
			if (((String) record.get("FLGRISCHIOSIAS")).equalsIgnoreCase("SI")) {
				record.put("FLGRISCHIOSIAS", "S");
			} else if (((String) record.get("FLGRISCHIOSIAS")).equalsIgnoreCase("NO")) {
				record.put("FLGRISCHIOSIAS", "N");
			} else {
				w = new Warning(MessageCodes.ImportMov.WAR_NUMGG_SUP,
						"Il campo \"Rischio asbetosi o silicosi\" non è valorizzato correttamente "
								+ "per questo è stato annullato. Il valore inseserito è: "
								+ record.get("FLGRISCHIOSIAS"));
				warnings.add(w);
				record.remove("FLGRISCHIOSIAS");
			}

		}

		if (cfAzSommEstera.equals("")) {
			if (record.containsKey("FLGAZESTERA")) {
				record.remove("FLGAZESTERA");
			}
		} else {
			record.put("FLGAZESTERA", "S");
		}

		// End Control Session
		return ProcessorsUtils.createResponse(prc, className, null, "", warnings, null);
	}

}
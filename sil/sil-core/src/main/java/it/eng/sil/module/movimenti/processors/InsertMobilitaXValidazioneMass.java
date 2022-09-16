package it.eng.sil.module.movimenti.processors;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.base.SourceBeanException;

import it.eng.afExt.dbaccess.sql.DBKeyGenerator;
import it.eng.afExt.utils.MessageCodes;
import it.eng.afExt.utils.TransactionQueryExecutor;
import it.eng.sil.Values;
import it.eng.sil.module.movimenti.RecordProcessor;
import it.eng.sil.util.amministrazione.impatti.ControlliException;
import it.eng.sil.util.amministrazione.impatti.MobilitaException;
import it.eng.sil.util.amministrazione.impatti.SituazioneAmministrativaFactory;
import it.eng.sil.util.amministrazione.impatti.StatoOccupazionaleBean;
import it.eng.sil.util.amministrazione.impatti.StatoOccupazionaleManager;

public class InsertMobilitaXValidazioneMass implements RecordProcessor {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger
			.getLogger(InsertMobilitaXValidazioneMass.class.getName());

	public static final String motivoFineNonConcessa = "I";

	private String name;
	private String classname = this.getClass().getName();
	private TransactionQueryExecutor trans;
	private BigDecimal userId;
	private SourceBean request;
	private RequestContainer reqCont;
	private SourceBean sbGenerale = null;
	private boolean scattanoImpattiUpdate = false;

	public InsertMobilitaXValidazioneMass(String name, TransactionQueryExecutor transexec, BigDecimal user,
			RequestContainer reqCont, SourceBean sb) throws SAXException, FileNotFoundException, IOException,
			ParserConfigurationException, NullPointerException {
		this.name = name;
		trans = transexec;
		if (user == null) {
			throw new NullPointerException("Identificatore utente nullo");
		}
		userId = user;
		this.reqCont = reqCont;
		this.request = reqCont.getServiceRequest();
		this.sbGenerale = sb;
		this.scattanoImpattiUpdate = ((this.sbGenerale.containsAttribute("FLGIMPATTIVALMOBILITA"))
				&& (this.sbGenerale.getAttribute("FLGIMPATTIVALMOBILITA").toString().equalsIgnoreCase("S")));
	}

	public SourceBean processRecord(Map record) throws SourceBeanException {
		// vettore delle warnings da restituire
		ArrayList warnings = new ArrayList();
		// Vettore dei risultati annidati da restituire
		ArrayList nested = new ArrayList();
		BigDecimal prgMobilitaIscr = null;
		// Se il record è nullo non lo posso elaborare, ritorno l'errore
		if (record == null) {
			return ProcessorsUtils.createResponse(name, classname,
					new Integer(MessageCodes.LogOperazioniValidazioneMobilita.RECORD_VALIDAZIONE_NULLO),
					"Record da elaborare nullo.", warnings, nested);
		}
		String context = record.get("CONTEXT") != null ? record.get("CONTEXT").toString() : "";
		String dataInizioMob = "";
		if (context.equalsIgnoreCase("valida")) {
			// validazione manuale
			dataInizioMob = record.get("DATINIZIOHID") != null ? record.get("DATINIZIOHID").toString() : "";
		} else {
			// validazione massiva
			dataInizioMob = record.get("DATINIZIO") != null ? record.get("DATINIZIO").toString() : "";
		}
		String dataFineMob = record.get("DATFINE") != null ? record.get("DATFINE").toString() : "";
		String dataFineMobOrig = record.get("DATFINEORIG") != null ? record.get("DATFINEORIG").toString() : "";
		String codTipoMob = record.get("CODTIPOMOB") != null ? record.get("CODTIPOMOB").toString() : "";
		String codiceDomanda = record.get("CODDOMANDA") != null ? record.get("CODDOMANDA").toString() : "";
		String flgIndennita = record.get("FLGINDENNITA") != null ? record.get("FLGINDENNITA").toString() : "";
		String dataInizioIndennita = record.get("DATINIZIOINDENNITA") != null
				? record.get("DATINIZIOINDENNITA").toString()
				: "";
		String dataFineIndennita = record.get("DATFINEINDENNITA") != null ? record.get("DATFINEINDENNITA").toString()
				: "";
		String dataCRT = record.get("DATCRT") != null ? record.get("DATCRT").toString() : "";
		String numCRT = record.get("NUMCRT") != null ? record.get("NUMCRT").toString() : "";
		String dataMaxDiff = record.get("DATMAXDIFF") != null ? record.get("DATMAXDIFF").toString() : "";
		String strNote = record.get("STRNOTE") != null ? record.get("STRNOTE").toString() : "";
		String cdnlavoratoreMob = record.get("CDNLAVORATORE") != null ? record.get("CDNLAVORATORE").toString() : "";
		Object prgMovimento = record.get("PRGMOVIMENTOMOB");
		String strCodMonoProv = record.get("CODMONOPROVMOBILITA") != null ? record.get("CODMONOPROVMOBILITA").toString()
				: "";
		String codfisc = (String) record.get("STRCODICEFISCALE");
		String cognome = (String) record.get("STRCOGNOME");
		String nome = (String) record.get("STRNOME");
		String prgAziendaMob = record.get("PRGAZIENDA") != null ? record.get("PRGAZIENDA").toString() : "";
		String prgUnitaMob = record.get("PRGUNITAPRODUTTIVA") != null ? record.get("PRGUNITAPRODUTTIVA").toString()
				: "";
		String dataInizioMov = record.get("DATAINIZIOMOVORIG") != null ? record.get("DATAINIZIOMOVORIG").toString()
				: "";
		String dataCessazioneMov = record.get("DATAFINEMOVORIG") != null ? record.get("DATAFINEMOVORIG").toString()
				: "";
		String mansione = record.get("CODMANSIONE") != null ? record.get("CODMANSIONE").toString() : "";
		String grado = "";
		String dataDomanda = null;
		String numOreSett = null;
		if (context.equalsIgnoreCase("valida")) {
			// validazione manuale
			grado = record.get("CODGRADOHID") != null ? record.get("CODGRADOHID").toString() : "";
			dataDomanda = record.get("DATADOMANDA") != null ? record.get("DATADOMANDA").toString() : null;
			numOreSett = record.get("NUMORESETT") != null ? record.get("NUMORESETT").toString() : null;
		} else {
			// validazione massiva
			grado = record.get("CODGRADO") != null ? record.get("CODGRADO").toString() : "";
		}
		String ccnl = record.get("CODCCNL") != null ? record.get("CODCCNL").toString() : "";
		String livello = record.get("STRLIVELLO") != null ? record.get("STRLIVELLO").toString() : "";
		String motScorrDataMaxDiff = record.get("CODMOTIVODIFF") != null ? record.get("CODMOTIVODIFF").toString() : "";
		String regioneCRT = record.get("REGIONECRT") != null ? record.get("REGIONECRT").toString() : "";
		String provCRT = record.get("PROVCRT") != null ? record.get("PROVCRT").toString() : "";
		String flgNonImprenditore = record.get("FLGNONIMPRENDITORE") != null
				? record.get("FLGNONIMPRENDITORE").toString()
				: "";
		String flgCasoDubbio = record.get("FLGCASODUBBIO") != null ? record.get("FLGCASODUBBIO").toString() : "";
		String motivoFine = null;

		try {
			// INSERIMENTO O MODIFICA MOBILITA'
			boolean inserisci = (!record.containsKey("MOBILITAPRESENTE")) ? true : false;
			if (dataCRT.equals("") && numCRT.equals("")) {
				motivoFine = motivoFineNonConcessa;
				dataFineMob = dataInizioMob;
			}
			if (inserisci) {
				// INSERIMENTO
				prgMobilitaIscr = DBKeyGenerator.getNextSequence(Values.DB_SIL_DATI, "S_AM_MOBILITA_ISCR");
				Object paramsMob[] = new Object[34];
				paramsMob[0] = prgMobilitaIscr;
				paramsMob[1] = cdnlavoratoreMob;
				paramsMob[2] = prgMovimento;
				paramsMob[3] = codTipoMob;
				paramsMob[4] = dataInizioMob;
				paramsMob[5] = dataFineMob;
				paramsMob[6] = dataFineMobOrig;
				paramsMob[7] = flgIndennita;
				paramsMob[8] = dataInizioIndennita;
				paramsMob[9] = dataFineIndennita;
				paramsMob[10] = dataCRT;
				paramsMob[11] = numCRT;
				paramsMob[12] = dataMaxDiff;
				paramsMob[13] = strNote;
				paramsMob[14] = userId;
				paramsMob[15] = userId;
				paramsMob[16] = strCodMonoProv;
				paramsMob[17] = prgAziendaMob;
				paramsMob[18] = prgUnitaMob;
				paramsMob[19] = dataInizioMov;
				paramsMob[20] = dataCessazioneMov;
				paramsMob[21] = mansione;
				paramsMob[22] = grado;
				paramsMob[23] = ccnl;
				paramsMob[24] = livello;
				paramsMob[25] = motScorrDataMaxDiff;
				paramsMob[26] = regioneCRT;
				paramsMob[27] = provCRT;
				paramsMob[28] = dataDomanda;
				paramsMob[29] = numOreSett;
				paramsMob[30] = flgNonImprenditore;
				paramsMob[31] = flgCasoDubbio;
				paramsMob[32] = motivoFine;
				paramsMob[33] = codiceDomanda;
				Boolean resOp = null;
				resOp = (Boolean) trans.executeQuery("INS_MOBILITA_DA_VALIDAZIONE", paramsMob, "INSERT");
				if (!resOp.booleanValue()) {
					return ProcessorsUtils.createResponse(name, classname,
							new Integer(MessageCodes.General.INSERT_FAIL),
							"Impossibile inserire la mobilità da validare.", warnings, nested);
				}
				warnings.add(new Warning(MessageCodes.LogOperazioniValidazioneMobilita.INSERT_MOB_VALIDAZIONE_MASSIVA,
						"Il lavoratore '" + cognome + " " + nome + "' con codice fiscale: " + codfisc));
			} else {
				// UPDATE
				SourceBean sbMob = (SourceBean) record.get("MOBILITAPRESENTE");
				String message = controllaValoriCampi(record, sbMob);
				if (!message.equals("")) {
					warnings.add(new Warning(
							MessageCodes.LogOperazioniValidazioneMobilita.UPDATE_MOB_VALIDAZIONE_MASSIVA, message));
				}
				// Controlli presenza azienda e periodo lavorativo nella mobilità presente nel sil
				BigDecimal prgAziendaIscr = (BigDecimal) sbMob.getAttribute("ROW.PRGAZIENDA");
				if (prgAziendaIscr == null) {
					prgAziendaIscr = (BigDecimal) record.get("PRGAZIENDA");
				}
				BigDecimal prgUnitaIscr = (BigDecimal) sbMob.getAttribute("ROW.PRGUNITA");
				if (prgUnitaIscr == null) {
					prgUnitaIscr = (BigDecimal) record.get("PRGUNITAPRODUTTIVA");
				}
				String dataInizioMovIscr = sbMob.containsAttribute("ROW.DATINIZIOMOV")
						? sbMob.getAttribute("ROW.DATINIZIOMOV").toString()
						: "";
				if (dataInizioMovIscr.equals("")) {
					if (context.equalsIgnoreCase("valida")) {
						// validazione manuale
						dataInizioMovIscr = record.get("DATINIZIOMOVHID") != null
								? (String) record.get("DATINIZIOMOVHID")
								: "";
					} else {
						// validazione massiva
						dataInizioMovIscr = record.get("DATINIZIOMOV") != null ? (String) record.get("DATINIZIOMOV")
								: "";
					}
				}
				String dataFineMovIscr = sbMob.containsAttribute("ROW.DATFINEMOV")
						? sbMob.getAttribute("ROW.DATFINEMOV").toString()
						: "";
				if (dataFineMovIscr.equals("")) {
					if (context.equalsIgnoreCase("valida")) {
						// validazione manuale
						dataFineMovIscr = record.get("DATFINEMOVHID") != null ? (String) record.get("DATFINEMOVHID")
								: "";
					} else {
						// validazione massiva
						dataFineMovIscr = record.get("DATFINEMOV") != null ? (String) record.get("DATFINEMOV") : "";
					}
				}
				prgMobilitaIscr = (BigDecimal) sbMob.getAttribute("ROW.PRGMOBILITAISCR");
				BigDecimal numkloMobIscr = (BigDecimal) sbMob.getAttribute("ROW.NUMKLOMOBISCR");
				numkloMobIscr = numkloMobIscr.add(new BigDecimal("1"));
				Object paramsMob[] = new Object[17];
				paramsMob[0] = codTipoMob;
				paramsMob[1] = userId;
				paramsMob[2] = dataCRT;
				paramsMob[3] = numCRT;
				paramsMob[4] = regioneCRT;
				paramsMob[5] = provCRT;
				paramsMob[6] = prgAziendaIscr;
				paramsMob[7] = prgUnitaIscr;
				paramsMob[8] = dataInizioMovIscr;
				paramsMob[9] = dataInizioMovIscr;
				paramsMob[10] = dataFineMovIscr;
				paramsMob[11] = dataFineMovIscr;
				paramsMob[12] = numkloMobIscr;
				paramsMob[13] = motivoFine;
				paramsMob[14] = motivoFine;
				paramsMob[15] = motivoFine;
				paramsMob[16] = prgMobilitaIscr;
				Boolean res = (Boolean) trans.executeQuery("UPDATE_MOBILITA_DA_VALIDAZIONE", paramsMob, "UPDATE");
				if (!res.booleanValue()) {
					return ProcessorsUtils.createResponse(name, classname,
							new Integer(MessageCodes.General.UPDATE_FAIL),
							"Impossibile aggiornare la mobilità da validare.", warnings, nested);
				}
			}
			record.put("PRGMOBILITAISCR", prgMobilitaIscr);
			// RICALCOLO STATO OCCUPAZIONALE solo nel caso di inserimento o aggiornamento solo se scattanoImpattiUpdate
			// = true
			if (inserisci || scattanoImpattiUpdate) {
				StatoOccupazionaleBean nuovoStatoOcc = ricalcolaStatoOccupazionale(cdnlavoratoreMob.toString(),
						dataInizioMob, request, trans);
			} else {
				warnings.add(new Warning(MessageCodes.LogOperazioniValidazioneMobilita.UPDATE_MOB_VALIDAZIONE_MASSIVA,
						"Lo stato occupazionale non è stato ricalcolato"));
			}

			// CANCELLAZIONE DELLA MOBILITA NELLA TABELLA DI APPOGGIO
			Boolean resCancella = new Boolean(false);
			Object params[] = new Object[1];
			// CANCELLAZIONE DEI RISULTATI EVENTUALMENTE PRESENTI NELLA TABELLA
			// AM_MOBILITA_RIS_DETT
			// CHE FACEVANO RIFERIMENTO ALLA MOBILITA' CON CHIAVE
			// PRGMOBILITAISCRAPP
			params[0] = record.get("PRGMOBILITAISCRAPP");
			resCancella = (Boolean) trans.executeQuery("DELETE_RISULTATO_MOBILITA_APPOGGIO", params, "DELETE");
			if (!resCancella.booleanValue()) {
				return ProcessorsUtils.createResponse(name, classname,
						new Integer(MessageCodes.LogOperazioniValidazioneMobilita.FALLITA_CANCELLAZIONE_MOBILITA_APP),
						"Impossibile cancellare dalla tabella di appoggio la mobilità validata.", warnings, nested);
			}
			// CANCELLAZIONE DELLA MOBILITA' APPENA VALIDATA DALLA TABELLA
			// AM_MOBILITA_ISCR_APP
			resCancella = (Boolean) trans.executeQuery("DELETE_MOBILITA_VALIDATA", params, "DELETE");
			if (!resCancella.booleanValue()) {
				return ProcessorsUtils.createResponse(name, classname,
						new Integer(MessageCodes.LogOperazioniValidazioneMobilita.FALLITA_CANCELLAZIONE_MOBILITA_APP),
						"Impossibile cancellare dalla tabella di appoggio la mobilità validata.", warnings, nested);
			}
		}

		catch (MobilitaException me) {
			it.eng.sil.util.TraceWrapper.debug(_logger, "", (Exception) me);

			int code = me.getCode();
			if (context.equalsIgnoreCase("valida")) {
				// VALIDAZIONE MANUALE
				SourceBean puResult = ProcessorsUtils.createResponse(name, classname, new Integer(code),
						"Impossibile calcolare il nuovo stato occupazionale", warnings, nested);
				if (RequestContainer.getRequestContainer().getServiceRequest()
						.containsAttribute(StatoOccupazionaleManager.CONFIRM_STATO_OCCUPAZIONALE)) {
					String valoreFlagMsgStatoOccMan = RequestContainer.getRequestContainer().getServiceRequest()
							.containsAttribute("FLAG_MESSAGGI_STATO_OCC_MANUALE")
									? RequestContainer.getRequestContainer().getServiceRequest()
											.getAttribute("FLAG_MESSAGGI_STATO_OCC_MANUALE").toString()
									: "";
					ProcessorsUtils.addConfirm(puResult, "Vuoi forzare l' operazione?", "ripetiInserimento",
							new String[] { "", "", valoreFlagMsgStatoOccMan }, true);
				}
				return puResult;

			} else {
				// VALIDAZIONE MASSIVA
				return ProcessorsUtils.createResponse(name, classname, new Integer(code),
						"Validazione mobilità fallita.", warnings, nested);
			}
		}

		catch (ControlliException ce) {
			it.eng.sil.util.TraceWrapper.debug(_logger, "", (Exception) ce);

			int code = ce.getCode();
			if (context.equalsIgnoreCase("valida")) {
				// VALIDAZIONE MANUALE
				SourceBean puResult = ProcessorsUtils.createResponse(name, classname, new Integer(code),
						"Impossibile calcolare il nuovo stato occupazionale", warnings, nested);
				if (RequestContainer.getRequestContainer().getServiceRequest()
						.containsAttribute(StatoOccupazionaleManager.CONFIRM_STATO_OCCUPAZIONALE)) {
					if (code == MessageCodes.StatoOccupazionale.STATO_OCCUPAZIONALE_MANUALE
							|| code == MessageCodes.StatoOccupazionale.CONSERVA_STATO_OCCUPAZIONALE_MANUALE
							|| code == MessageCodes.StatoOccupazionale.STATO_OCC_CON_MESI_SOSP_ANZ_NON_CALCOLATI
							|| code == MessageCodes.StatoOccupazionale.STATO_OCC_CON_DATA_ANZIANITA_ERRATA) {
						String forzaRicostruzione = RequestContainer.getRequestContainer().getServiceRequest()
								.getAttribute(StatoOccupazionaleManager.CONFIRM_STATO_OCCUPAZIONALE).toString();
						ProcessorsUtils.addConfirm(puResult, "Vuoi forzare l' operazione?", "continuaRicalcolo",
								new String[] { forzaRicostruzione }, true);
					} else {
						String valoreFlagMsgStatoOccMan = RequestContainer.getRequestContainer().getServiceRequest()
								.containsAttribute("FLAG_MESSAGGI_STATO_OCC_MANUALE")
										? RequestContainer.getRequestContainer().getServiceRequest()
												.getAttribute("FLAG_MESSAGGI_STATO_OCC_MANUALE").toString()
										: "";
						ProcessorsUtils.addConfirm(puResult, "Vuoi forzare l' operazione?", "ripetiInserimento",
								new String[] { "", "", valoreFlagMsgStatoOccMan }, true);
					}
				}
				return puResult;
			} else {
				// VALIDAZIONE MASSIVA
				return ProcessorsUtils.createResponse(name, classname, new Integer(code),
						"Validazione mobilità fallita.", warnings, nested);
			}
		}

		catch (Exception e) {
			return ProcessorsUtils.createResponse(name, classname,
					new Integer(MessageCodes.LogOperazioniValidazioneMobilita.FALLITA_GESTIONE_MOBILITA),
					"Validazione mobilità fallita.", warnings, nested);
		}

		// Se ho warning o risultati annidati li inserisco nella risposta,
		// altrimenti non ritorno nulla.
		if ((warnings.size() > 0) || (nested.size() > 0)) {
			return ProcessorsUtils.createResponse(name, classname, null, null, warnings, nested);
		} else {
			return null;
		}
	}

	public StatoOccupazionaleBean ricalcolaStatoOccupazionale(String cdnlavoratoreMob, String dataInizioMob,
			SourceBean serviceRequest, TransactionQueryExecutor txExec) throws Exception {
		// ricalcolo impatti
		StatoOccupazionaleBean statoOccupazionale = SituazioneAmministrativaFactory
				.newInstance(cdnlavoratoreMob, dataInizioMob, reqCont, txExec).calcolaImpatti();
		return statoOccupazionale;
	}

	/**
	 * 
	 * @param record
	 *            contiene i dati della mobilità che si sta validando
	 * @param mobilita
	 *            contiene i valori della mobilità trovata sul db
	 */
	public String controllaValoriCampi(Map record, SourceBean mobilita) {
		String message = "";
		String context = record.get("CONTEXT") != null ? record.get("CONTEXT").toString() : "";
		// Valori DB
		String codiceFiscaleAzDB = mobilita.containsAttribute("ROW.STRCODICEFISCALE")
				? mobilita.getAttribute("ROW.STRCODICEFISCALE").toString()
				: "";
		String codComUAzDB = mobilita.containsAttribute("ROW.CODCOM") ? mobilita.getAttribute("ROW.CODCOM").toString()
				: "";
		String strIndirizzoUAzDB = mobilita.containsAttribute("ROW.STRINDIRIZZO")
				? mobilita.getAttribute("ROW.STRINDIRIZZO").toString()
				: "";
		String dataInizioMovDB = mobilita.containsAttribute("ROW.DATINIZIOMOV")
				? mobilita.getAttribute("ROW.DATINIZIOMOV").toString()
				: "";
		String dataFineMovDB = mobilita.containsAttribute("ROW.DATFINEMOV")
				? mobilita.getAttribute("ROW.DATFINEMOV").toString()
				: "";
		String dataFineMobilitaDB = mobilita.containsAttribute("ROW.DATFINE")
				? mobilita.getAttribute("ROW.DATFINE").toString()
				: "";
		String dataMaxDiffDB = mobilita.containsAttribute("ROW.DATMAXDIFF")
				? mobilita.getAttribute("ROW.DATMAXDIFF").toString()
				: "";
		String codMotivoFineDiffDB = mobilita.containsAttribute("ROW.CODMOTIVODIFF")
				? mobilita.getAttribute("ROW.CODMOTIVODIFF").toString()
				: "";
		String flgIndennitaDB = mobilita.containsAttribute("ROW.FLGINDENNITA")
				? mobilita.getAttribute("ROW.FLGINDENNITA").toString()
				: "";
		String dataInizioIndennitaDB = mobilita.containsAttribute("ROW.DATINIZIOINDENNITA")
				? mobilita.getAttribute("ROW.DATINIZIOINDENNITA").toString()
				: "";
		String dataFineIndennitaDB = mobilita.containsAttribute("ROW.DATFINEINDENNITA")
				? mobilita.getAttribute("ROW.DATFINEINDENNITA").toString()
				: "";
		String codMansioneDB = mobilita.containsAttribute("ROW.CODMANSIONE")
				? mobilita.getAttribute("ROW.CODMANSIONE").toString()
				: "";
		String codCcnlDB = mobilita.containsAttribute("ROW.CODCCNL") ? mobilita.getAttribute("ROW.CODCCNL").toString()
				: "";
		String codMotivoFineDB = mobilita.containsAttribute("ROW.CODMOTIVOFINE")
				? mobilita.getAttribute("ROW.CODMOTIVOFINE").toString()
				: "";
		// Valori mobilità da validare
		String codiceFiscaleAz = record.containsKey("STRAZCODICEFISCALE") ? record.get("STRAZCODICEFISCALE").toString()
				: "";
		String codComUAz = record.containsKey("CODUACOM") ? record.get("CODUACOM").toString() : "";
		String strIndirizzoUAz = record.containsKey("STRUAINDIRIZZO") ? record.get("STRUAINDIRIZZO").toString() : "";
		String dataInizioMov = "";
		String dataFineMov = "";
		if (context.equalsIgnoreCase("valida")) {
			// validazione manuale
			dataInizioMov = record.containsKey("DATINIZIOMOVHID") ? record.get("DATINIZIOMOVHID").toString() : "";
			dataFineMov = record.containsKey("DATFINEMOVHID") ? record.get("DATFINEMOVHID").toString() : "";
		} else {
			// validazione massiva
			dataInizioMov = record.containsKey("DATINIZIOMOV") ? record.get("DATINIZIOMOV").toString() : "";
			dataFineMov = record.containsKey("DATFINEMOV") ? record.get("DATFINEMOV").toString() : "";
		}
		String dataFineMobilita = record.containsKey("DATFINE") ? record.get("DATFINE").toString() : "";
		String dataMaxDiff = record.containsKey("DATMAXDIFF") ? record.get("DATMAXDIFF").toString() : "";
		String codMotivoFineDiff = record.containsKey("CODMOTIVODIFF") ? record.get("CODMOTIVODIFF").toString() : "";
		String flgIndennita = record.containsKey("FLGINDENNITA") ? record.get("FLGINDENNITA").toString() : "";
		String dataInizioIndennita = record.containsKey("DATINIZIOINDENNITA")
				? record.get("DATINIZIOINDENNITA").toString()
				: "";
		String dataFineIndennita = record.containsKey("DATFINEINDENNITA") ? record.get("DATFINEINDENNITA").toString()
				: "";
		String codMansione = record.containsKey("CODMANSIONE") ? record.get("CODMANSIONE").toString() : "";
		String codCcnl = record.containsKey("CODCCNL") ? record.get("CODCCNL").toString() : "";
		String codMotivoFine = record.containsKey("CODMOTIVOFINE") ? record.get("CODMOTIVOFINE").toString() : "";

		if ((!codiceFiscaleAzDB.equalsIgnoreCase(codiceFiscaleAz)) || (!codComUAzDB.equalsIgnoreCase(codComUAz))
				|| (!strIndirizzoUAzDB.equalsIgnoreCase(strIndirizzoUAz))) {
			message = message + "Dati azienda differenti";
			if (!codiceFiscaleAzDB.equalsIgnoreCase(codiceFiscaleAz)) {
				message = message + "<br>Codice fiscale azienda DB = " + codiceFiscaleAzDB
						+ " Codice fiscale azienda in validazione = " + codiceFiscaleAz;
			}
			if (!codComUAzDB.equalsIgnoreCase(codComUAz)) {
				message = message + "<br>Comune azienda DB = " + codComUAzDB + " Comune azienda in validazione = "
						+ codComUAz;
			}
			if (!strIndirizzoUAzDB.equalsIgnoreCase(strIndirizzoUAz)) {
				message = message + "<br>Indirizzo azienda DB = " + strIndirizzoUAzDB
						+ " Indirizzo azienda in validazione = " + strIndirizzoUAz;
			}
		}

		if ((!dataInizioMovDB.equalsIgnoreCase(dataInizioMov)) || (!dataFineMovDB.equalsIgnoreCase(dataFineMov))
				|| (!codMansioneDB.equalsIgnoreCase(codMansione)) || (!codCcnlDB.equalsIgnoreCase(codCcnl))) {
			message = message + "<br>Dati movimento differenti";
			if (!dataInizioMovDB.equalsIgnoreCase(dataInizioMov)) {
				message = message + "<br>Data inizio movimento DB = " + dataInizioMovDB
						+ " Data inizio movimento in validazione = " + dataInizioMov;
			}
			if (!dataFineMovDB.equalsIgnoreCase(dataFineMov)) {
				message = message + "<br>Data fine movimento DB = " + dataFineMovDB
						+ " Data fine movimento in validazione = " + dataFineMov;
			}
			if (!codMansioneDB.equalsIgnoreCase(codMansione)) {
				message = message + "<br>Qualifica movimento DB = " + codMansioneDB
						+ " Qualifica movimento in validazione = " + codMansione;
			}
			if (!codCcnlDB.equalsIgnoreCase(codCcnl)) {
				message = message + "<br>CCNL DB = " + codCcnlDB + " CCNL in validazione = " + codCcnl;
			}
		}

		if ((!dataFineMobilitaDB.equalsIgnoreCase(dataFineMobilita)) || (!dataMaxDiffDB.equalsIgnoreCase(dataMaxDiff))
				|| (!codMotivoFineDiffDB.equalsIgnoreCase(codMotivoFineDiff))
				|| (!flgIndennitaDB.equalsIgnoreCase(flgIndennita))
				|| (!dataInizioIndennitaDB.equalsIgnoreCase(dataInizioIndennita))
				|| (!dataFineIndennitaDB.equalsIgnoreCase(dataFineIndennita))
				|| (!codMotivoFineDB.equalsIgnoreCase(codMotivoFine))) {
			message = message + "<br>Dati mobilità differenti";
			if (!dataFineMobilitaDB.equalsIgnoreCase(dataFineMobilita)) {
				message = message + "<br>Data fine mobilità DB = " + dataFineMobilitaDB
						+ " Data fine mobilità in validazione = " + dataFineMobilita;
			}
			if (!dataMaxDiffDB.equalsIgnoreCase(dataMaxDiff)) {
				message = message + "<br>Data max differimento DB = " + dataMaxDiffDB
						+ " Data max differimento in validazione = " + dataMaxDiff;
			}
			if (!codMotivoFineDiffDB.equalsIgnoreCase(codMotivoFineDiff)) {
				message = message + "<br>Motivo scorrimento data max differimento DB = " + codMotivoFineDiffDB
						+ " Motivo scorrimento data max differimento in validazione = " + codMotivoFineDiff;
			}
			if (!flgIndennitaDB.equalsIgnoreCase(flgIndennita)) {
				message = message + "<br>Flag indennità DB = " + flgIndennitaDB + " Flag indennità in validazione = "
						+ flgIndennita;
			}
			if (!dataInizioIndennitaDB.equalsIgnoreCase(dataInizioIndennita)) {
				message = message + "<br>Data inizio indennità DB = " + dataInizioIndennitaDB
						+ " Data inizio indennità in validazione = " + dataInizioIndennita;
			}
			if (!dataFineIndennitaDB.equalsIgnoreCase(dataFineIndennita)) {
				message = message + "<br>Data fine indennità DB = " + dataFineIndennitaDB
						+ " Data fine indennità in validazione = " + dataFineIndennita;
			}
			if (!codMotivoFineDB.equalsIgnoreCase(codMotivoFine)) {
				message = message + "<br>Motivo decadenza DB = " + codMotivoFineDB
						+ " Motivo decadenza in validazione = " + codMotivoFine;
			}

		}
		return message;
	}

}
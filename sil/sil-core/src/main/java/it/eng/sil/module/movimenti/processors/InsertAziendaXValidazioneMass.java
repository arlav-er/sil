package it.eng.sil.module.movimenti.processors;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Map;
import java.util.Vector;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.base.SourceBeanAttribute;
import com.engiweb.framework.base.SourceBeanException;

import it.eng.afExt.utils.MessageCodes;
import it.eng.afExt.utils.TransactionQueryExecutor;
import it.eng.sil.module.movimenti.RecordProcessor;
import it.eng.sil.module.movimenti.enumeration.CodMonoTempoEnum;
import it.eng.sil.module.movimenti.enumeration.CodiceVariazioneEnum;
import it.eng.sil.module.movimenti.extractor.ManualValidationFieldExtractor;
import it.eng.sil.util.amministrazione.impatti.MovimentoBean;

/**
 * Processor che inserisce un'azienda nel DB se non è gia presente. Effettua la ricerca a partire dal codice fiscale e
 * dalla partita iva. Senon trova l'azienda modifica le tabelle AN_AZIENDA e AN_UNITA_AZIENDA per inserire la nuova
 * azienda. Ritorna nel record il prgAzienda associato all'azienda (trovata o inserita) associandolo alla proprieta
 * PRGAZIENDA.
 * 
 * NOTA: Questo processor è stato "creato" con il famoso metodo "CaP" (copy and paste) dal processor InsertAzienda
 * utilizzato in inserimento e validazione manuale. E' stato poi evirato di alcune parti che riguardano la ricerca e
 * inserimento delle unità azienda, per permettere la modifica della ricerca del movimento collegato che ora si basa
 * sulla testata azienda
 * 
 * <p>
 * 
 * @author Davide Giuliani 05/04/2005
 */
public class InsertAziendaXValidazioneMass implements RecordProcessor {

	private String name;
	private String classname = this.getClass().getName();
	/** TransactionQueryExecutor da utilizzare per la query di insert */
	private TransactionQueryExecutor trans;
	/** Identificatore utente */
	private BigDecimal userId;
	/** Processore per la ricerca del CPI dell'azienda */
	private RecordProcessor findCPiUnitaAzienda;
	/** Processori per l'inserimento dei dati */
	private RecordProcessor insertAzienda;
	private RecordProcessor insertUnitaProd;
	private RecordProcessor insertSedeLegale;
	private RecordProcessor insertAziendaUtil;
	private RecordProcessor insertUnitaUtil;
	/** Processori per l'aggiornamento dei dati */
	private RecordProcessor updateAzienda;
	private RecordProcessor updateUnitaProd;
	private RecordProcessor updateSedeLegale;
	private RecordProcessor updateAziendaUtil;
	private RecordProcessor updateUnitaUtil;
	private boolean checkForzaValidazione = false;
	private boolean aggiornaDatiAzienda = true;
	private SourceBean infoGenerale = null;
	static final String REGIONE_EMILIA_ROMAGNA = "8";

	/**
	 * Costruttore, occorre specificare un identificatore utente per inserire i dati nella tabella. Utilizza dei
	 * processori di classe InsertData per inserire i dati nelle tabelle quando necessario. Per configurarlo utilizza il
	 * file passato come ultimo parametro, esso deve contenere la configurazione per l' InsertData della tabella
	 * AN_AZIENDA e per gli InsertData che inseriscono L'unita produttiva e la sede legale.
	 * <p>
	 * 
	 * @param name
	 *            Nome del processore
	 * @param transexec
	 *            TransactionQueryExecutor da utilizzare per le query
	 * @param user
	 *            Identificatore utente per inserire i dati
	 * @param configFileName
	 *            file di configurazione per gli InsertData contenuti in questo processor
	 *            <p>
	 * @exception SAXException
	 *                Se avviene qualche errore nel parsing del file di configurazione.
	 * @exception FileNotFoundException
	 *                Se il file di configurazione non viene trovato.
	 * @exception IOException
	 *                Se avviene un errore in lettura del file di configurazione.
	 * @exception ParserConfigurationException
	 *                Se avviene un errore nella configurazione del parser.
	 * @exception NullPointerException
	 *                Se l'identificatore utente è nullo.
	 */
	public InsertAziendaXValidazioneMass(String name, TransactionQueryExecutor transexec, BigDecimal user,
			String configFileName, SourceBean sbGenerale) throws SAXException, FileNotFoundException, IOException,
			ParserConfigurationException, NullPointerException, SQLException {
		this.name = name;
		trans = transexec;
		if (user == null) {
			throw new NullPointerException("Identificatore utente nullo");
		}
		// Provo ad istanziare i processori InsertData per l'inserimento dei
		// dati
		insertAzienda = new InsertData("Inserimento Azienda", transexec, configFileName, "AZIENDA", user);
		insertUnitaProd = new InsertData("Inserimento Unità Produttiva", transexec, configFileName, "UNITA_PRODUTTIVA",
				user);
		insertSedeLegale = new InsertData("Inserimento Sede Legale", transexec, configFileName, "SEDE_LEGALE", user);
		insertAziendaUtil = new InsertData("Inserimento Azienda Utilizzatrice", transexec, configFileName,
				"AZIENDA_UTIL", user);
		insertUnitaUtil = new InsertData("Inserimento Unità Utilizzatrice", transexec, configFileName, "UNITA_UTIL",
				user);
		updateAzienda = new UpdateTestataAzienda("Aggiornamento Azienda", transexec, configFileName, "AZIENDA", user);
		updateUnitaProd = new UpdateUnitaAzienda("Aggiornamento Unità Produttiva", transexec, configFileName,
				"UNITA_PRODUTTIVA", user);
		updateSedeLegale = new UpdateUnitaAzienda("Aggiornamento Sede Legale", transexec, configFileName, "SEDE_LEGALE",
				user);
		updateAziendaUtil = new UpdateTestataAzienda("Aggiornamento Azienda Utilizzatrice", transexec, configFileName,
				"AZIENDA_UTIL", user);
		updateUnitaUtil = new UpdateUnitaAzienda("Aggiornamento Unità Utilizzatrice", transexec, configFileName,
				"UNITA_UTIL", user);
		findCPiUnitaAzienda = new SelectCPIAziendaFromComune("Ricerca Cpi Azienda dal Comune", transexec);
		checkForzaValidazione = ProcessorsUtils.checkForzaValidazione(trans);
		this.infoGenerale = sbGenerale;
		if (infoGenerale.containsAttribute("AGGIORNADATIAZIENDA")
				&& infoGenerale.getAttribute("AGGIORNADATIAZIENDA").toString().equals("FALSE")) {
			this.aggiornaDatiAzienda = false;
		}
	}

	/**
	 * Processa il record. Cerca l'azienda a partire dal Codice Fiscale e dalla P.IVA, se non la trova la inserisce.
	 * Ritorna il prgAzienda nella proprieta PRGAZIENDA del Record. Poi cerca nella tabella AN_UNITA_AZIENDA l'unita
	 * aziendale corrispondente a quella presente nel record (a partire dal prgAzienda, dall'indirizzo e dal codice del
	 * comune); se non la trova la inserisce. Ritorna il prgunita nella proprietà PRGUNITAPRODUTTIVA del Record. Stesso
	 * discorso vale per la sede legale (solo che il parametro si chiama PRGUNITALEGALE) ATTENZIONE! PRIMA DI INVOCARE
	 * QUESTO METODO OCCORRE INIZIARE ESPLICITAMENTE UNA TRANSAZIONE E CHIUDERLA SUCCESSIVAMENTE ALL'INVOCAZIONE DEL
	 * METODO!
	 * <p>
	 * 
	 * @exception SourceBeanException
	 *                Se avviene un errore nella creazione del SourceBean di risposta
	 */
	public SourceBean processRecord(Map record) throws SourceBeanException {

		// vettore delle warnings da restituire
		ArrayList warnings = new ArrayList();
		// Vettore dei risultati annidati da restituire
		ArrayList nested = new ArrayList();
		boolean utilizzoPrimaUnita = false;
		boolean utilizzoPrimaUnitaUtil = false;

		// Se il record è nullo non lo posso elaborare, ritorno l'errore
		if (record == null) {
			return ProcessorsUtils.createResponse(name, classname, new Integer(MessageCodes.ImportMov.ERR_INSERT_DATA),
					"Record da elaborare nullo.", warnings, nested);
		}

		// In tutti i casi cerco il CODCPI dell'azienda a partire dal Codice del
		// comune e lo inserisco nella map
		SourceBean findCPiUAzResult = findCPiUnitaAzienda.processRecord(record);
		if (findCPiUAzResult != null) {
			nested.add(findCPiUAzResult);
			if (ProcessorsUtils.isError(findCPiUAzResult)) {
				return ProcessorsUtils.createResponse(name, classname,
						new Integer(MessageCodes.ImportMov.ERR_INSERT_AZ),
						"Impossibile impostare il CPI dell'unita produttiva.", warnings, nested);
			}
		}

		// Se ho già i progressivi devo solo aggiornare i dati.
		/* In validazione Massiva ce l'ho i progressivi?!?? Mha! */
		BigDecimal prgAz = (BigDecimal) record.get("PRGAZIENDA");
		BigDecimal prgUnita = (BigDecimal) record.get("PRGUNITAPRODUTTIVA");

		String codTipoMov = (String) record.get("CODTIPOMOV");

		String strNumAlboInterinali = (record.get("STRAZNUMALBOINTERINALI") != null)
				? (String) record.get("STRAZNUMALBOINTERINALI")
				: "";
		String codTipoAss = record.get("CODTIPOASS") != null ? (String) record.get("CODTIPOASS") : "";
		boolean flgPubblicaAmm = record.get("FLGPA") != null && record.get("FLGPA").toString().equalsIgnoreCase("S")
				? true
				: false;

		// Mi dice se sono in validazione massiva
		boolean validazioneMassiva = record.get("CONTEXT").toString().equalsIgnoreCase("validazioneMassiva");

		// Parametro che indica se la sede legale è presente nel movimento
		// (per ora solo nei casi di validazione e validazione massiva)
		boolean sedeValorizzata = false;

		// Controllo se il comune della sede legale è valorizzato (in tal caso
		// la sede si considera valorizzata)
		String codComSedeLeg = (String) record.get("CODUALCOM");
		String indirSedeLeg = (String) record.get("STRUALINDIRIZZO");
		if (codComSedeLeg != null) {
			sedeValorizzata = true;
		}

		boolean prorogaMissioneTI = false;

		ManualValidationFieldExtractor extractor = new ManualValidationFieldExtractor();
		String codVariazione = extractor.estraiCodVariazione(record);
		String _codTipoAzienda = extractor.estraiTipoAzienda(record);
		String datinizioMis = extractor.estraiInizioMissione(record);
		String codMonoTmp = extractor.estraiCodMonoTempo(record);

		// GESTIONE DI PROROGHE DELLA MISSIONE PER CONTRATTI A TEMPO INDETERMINATO CON CODICE VARIAZIONE 2.03
		if (codTipoMov.equalsIgnoreCase(MovimentoBean.COD_PROROGA) && _codTipoAzienda.equalsIgnoreCase("INT")
				&& !datinizioMis.equals("") && codMonoTmp.equalsIgnoreCase(CodMonoTempoEnum.INDETERMINATO.getCodice())
				&& codVariazione.equalsIgnoreCase(
						CodiceVariazioneEnum.PROROGA_MISSIONE_RAPPORTO_TEMPO_INDETERMINATO.getCodice())) {
			prorogaMissioneTI = true;
		}

		if (!(prgAz != null && prgUnita != null)) {

			// Controllo che i parametri necessari per le query di ricerca ci
			// siano
			String codfisc = (String) record.get("STRAZCODICEFISCALE");
			String indirUnitaAz = (String) record.get("STRUAINDIRIZZO");
			String codComUnitaAz = (String) record.get("CODUACOM");
			String ragSocAz = (String) record.get("STRAZRAGIONESOCIALE");
			String pIvaAz = (String) record.get("STRAZPARTITAIVA");

			if (codfisc == null || codComUnitaAz == null) {
				String msg = "Impossibile trovare i seguenti dati dell'azienda: "
						+ (codfisc == null ? "codice fiscale" : "")
						+ ((codfisc == null && codComUnitaAz == null) ? ", " : "")
						+ (codComUnitaAz == null ? "codice del comune" : "");
				return ProcessorsUtils.createResponse(name, classname,
						new Integer(MessageCodes.ImportMov.ERR_INSERT_DATA), msg, warnings, nested);
			}

			// Parametro che indica se la sede legale deve essere gestita a
			// parte o coincide con l'unita produttiva
			boolean gestisciSede = false;
			/*
			 * if (sedeValorizzata) { if (!(codComSedeLeg.equals(codComUnitaAz))) { gestisciSede = true;
			 * record.put("FLGSEDE", "N"); } else { record.put("FLGSEDE", "S"); } }
			 */

			codfisc = codfisc.toUpperCase();
			// Guardo se l'azienda esiste con la query
			if (prgAz == null) {// non ho l'azienda //Davide 11/04/2005::
				String statement = "SELECT prgAzienda FROM AN_AZIENDA WHERE UPPER(strCodiceFiscale) = '" + codfisc
						+ "' ";

				// Provo a cercare l'azienda
				ArrayList prgAzList = null;
				try {
					prgAzList = getProgressivo(statement);
				} catch (Exception e) {
					return ProcessorsUtils.createResponse(name, classname,
							new Integer(MessageCodes.ImportMov.ERR_INSERT_DATA), "Impossibile cercare l'azienda",
							warnings, nested);
				}

				// devo agire in base al numero di prgAzienda che ho ripescato
				if (prgAzList.size() == 0) {
					if (codTipoMov.equalsIgnoreCase("AVV") || codTipoMov.equalsIgnoreCase("CES")
							|| codTipoMov.equalsIgnoreCase("TRA")) {
						// Sono in AVVIAMENTO e l'azienda non è presente: la
						// inserisco;
						// ci pensa il processor insert ad aggiungere il
						// prgAzienda nel record

						/*
						 * Se l'azienda ha un numero di iscrizione all'albo delle agenzie di somministrazione allora
						 * l'anagrafica azienda viene creata con tipologia INT = Somministrazione
						 */

						if (!strNumAlboInterinali.equals("")) {
							record.put("CODAZTIPOAZIENDA", "INT");
						} else {
							/*
							 * Se il contratto del rapporto di lavoro è H.01.00 allora l'anagrafica azienda viene creata
							 * con tipologia AGR = Agricola
							 */

							if (codTipoAss.equals("H.01.00")) {
								record.put("CODAZTIPOAZIENDA", "AGR");
							} else {
								/*
								 * Se il contratto del rapporto di lavoro è I.01.00 oppure I.02.00 allora
								 * l'anagrafica azienda viene creata con tipologia PA = Pubblica amministrazione
								 */
								if (flgPubblicaAmm || codTipoAss.equals("I.01.00") || codTipoAss.equals("I.02.00")) {
									record.put("CODAZTIPOAZIENDA", "PA");
								}
							}
						}

						SourceBean insertAzResult = insertAzienda.processRecord(record);
						if (insertAzResult != null) {
							nested.add(insertAzResult);
							if (ProcessorsUtils.isError(insertAzResult)) {
								return ProcessorsUtils.createResponse(name, classname,
										new Integer(MessageCodes.ImportMov.ERR_INSERT_DATA),
										"Impossibile inserire l'azienda '" + ragSocAz + "' nel DB.", warnings, nested);
							}
						}
						// Segnalo che ho aggiunto l'azienda con una warning
						warnings.add(new Warning(MessageCodes.ImportMov.AGGIUNTA_AZ, "L'azienda '" + ragSocAz
								+ "' con codice fiscale: " + codfisc + " è stata aggiunta al DB."));
						if (codTipoMov.equalsIgnoreCase("AVV")) {
							gestisciSede = true;
							record.put("FLGSEDE", "N");
						}
					} else {
						// Non sono in AVVIAMENTO (o in Avviamento da CESSAZIONE
						// veloce), è inutile proseguire in quanto per tutti gli
						// altri tipi di movimento in validazione
						// massiva E' NECESSARIO individuare il movimento
						// precedete. Se però non esiste nel DB la testata
						// azienda diventa
						// impossibile indivuduare un movimento precedete (non
						// chè alquanto molto poco probabile che ne esista uno
						// visto che,
						// in teoria, non è possibile inserire un movimento che
						// non abbia un azienda presente nel DB).
						// MORALE della favola, segnaliamo l'errore e ci
						// fermiamo (NdR: il comportamento è differente per la
						// validazione manuale)
						return ProcessorsUtils.createResponse(name, classname,
								new Integer(MessageCodes.ImportMov.ERR_FIND_MOV_PREC),
								"Impossibile individuare la testata azienda nel DB. Validare manualmente il movimento",
								warnings, nested);
					}

				} else if (prgAzList.size() == 1) {
					// Ho trovato l'azienda, aggiungo il prgAzienda al record
					record.put("PRGAZIENDA", prgAzList.get(0));

					// Aggiorno i dati dell'azienda se sono in validazione
					// massiva
					if (aggiornaDatiAzienda && validazioneMassiva) {
						SourceBean updateAzResult = updateAzienda.processRecord(record);
						if (updateAzResult != null) {
							nested.add(updateAzResult);
							if (ProcessorsUtils.isError(updateAzResult)) {
								return ProcessorsUtils.createResponse(name, classname,
										new Integer(MessageCodes.ImportMov.ERR_UPDATE_DATA),
										"Impossibile aggiornare i dati dell'azienda '" + ragSocAz + "' nel DB.",
										warnings, nested);
							}
						}
					}
				} else {
					// Ho trovato più di un'azienda, segnalo l'errore e termino
					return ProcessorsUtils.createResponse(name, classname,
							new Integer(MessageCodes.ImportMov.ERR_INSERT_DATA),
							"Impossibile identificare univocamente l'azienda, sono state individuate "
									+ prgAzList.size() + " testate aziendali compatibili con il C.F: " + codfisc,
							warnings, nested);
				}

			} // end if (prgAz == null) //non ho l'azienda

			// Se si valida un avviamento occorre inserire anche l'unità
			// aziendale (se non è presente)
			// L'operazione viene fatta anche per le trasformazioni e cessazioni
			// (nel caso di orfani
			// bisogna inserire l'unità aziendale se non è presente, altrimenti
			// non riusciamo a validare il movimento)
			if ((prgUnita == null) && (codTipoMov.equalsIgnoreCase("AVV") || codTipoMov.equalsIgnoreCase("TRA")
					|| codTipoMov.equalsIgnoreCase("CES") || prorogaMissioneTI)) {
				// Cerco l'unita produttiva
				Vector prgUnitaProdList = null;
				try {
					prgUnitaProdList = ProcessorsUtils.findUnita((BigDecimal) record.get("PRGAZIENDA"), codComUnitaAz,
							trans);
				} catch (Exception e) {
					return ProcessorsUtils.createResponse(name, classname,
							new Integer(MessageCodes.ImportMov.ERR_INSERT_DATA),
							"Impossibile cercare l'unita produttiva", warnings, nested);
				}

				// Devo agire in base al numero di unita produttive ripescate
				if (prgUnitaProdList.size() == 0) {
					// L'unita produttiva non esiste, la creo!
					SourceBean unitaProdResult = null;
					try {
						if (checkForzaValidazione) {
							if (record.get("STRUAINDIRIZZO") == null) {
								// modifica l'indirizzo dell'unità produttiva
								record.put("STRUAINDIRIZZO", "Indirizzo non dichiarato");
							}
							if (record.get("CODAZCCNL") == null) {
								// modifica il ccnl
								record.put("CODAZCCNL", "CCNA");
								String notaModifica = "<li>Manca il CCNL. Il sistema ha automaticamente impostato tale campo a 'CCNA' </li>";
								String strNote = (String) record.get("STRNOTE");
								if (strNote != null) {
									strNote = strNote + notaModifica;
								} else {
									strNote = notaModifica;
								}
								record.put("STRNOTE", strNote);
							}
						}
						unitaProdResult = insertUnita((BigDecimal) record.get("PRGAZIENDA"), insertUnitaProd,
								"PRGUNITAPRODUTTIVA", record);
					} catch (Exception e) {
						return ProcessorsUtils.createResponse(name, classname,
								new Integer(MessageCodes.ImportMov.ERR_INSERT_DATA),
								"Impossibile inserire l'unita produttiva", warnings, nested);
					}
					if (unitaProdResult != null) {
						nested.add(unitaProdResult);
						if (ProcessorsUtils.isError(unitaProdResult)) {
							return ProcessorsUtils.createResponse(name, classname,
									new Integer(MessageCodes.ImportMov.ERR_INSERT_DATA),
									"Impossibile inserire l'unita produttiva", warnings, nested);
						}
					}
					// Segnalo che ho aggiunto l'unita aziendale con una warning
					warnings.add(new Warning(MessageCodes.ImportMov.AGGIUNTA_UAZ,
							"L'unita aziendale '" + ragSocAz + "' con codice fiscale: " + codfisc + " e indirizzo "
									+ indirUnitaAz + " è stata aggiunta al DB."));
				} else {
					// Ho trovato una o più unita aziendali, controllo quante
					// matchano l'indirizzo passato
					ArrayList prgUnitaProdCheckedList = null;
					try {
						prgUnitaProdCheckedList = ProcessorsUtils.checkIndirizzo(prgUnitaProdList, indirUnitaAz);
					} catch (Exception e) {
						return ProcessorsUtils.createResponse(name, classname,
								new Integer(MessageCodes.ImportMov.ERR_INSERT_DATA),
								"Impossibile filtrare l'unita produttiva in base all'indirizzo", warnings, nested);
					}
					// Stefy -06/03/2006
					// Se nessuna unità fa match con l'indirizzo...
					if (prgUnitaProdCheckedList.size() == 0) {
						// ... si prende per default la prima unità produttiva
						// dello stesso comune
						try {
							prgUnitaProdCheckedList = ProcessorsUtils.findPrimaUnita(prgUnitaProdList);
							if (prgUnitaProdCheckedList.size() == 1) {
								utilizzoPrimaUnita = true;
							}
							warnings.add(new Warning(MessageCodes.ImportMov.WAR_MOV_PRIMA_UA, ""));
						} catch (Exception e) {
							return ProcessorsUtils.createResponse(name, classname,
									new Integer(MessageCodes.ImportMov.ERR_INSERT_DATA),
									"Impossibile recuperare la prima unita' produttiva dello stesso comune", warnings,
									nested);
						}
					}
					// Devo agire in base al numero di unita produttive
					// ripescate
					if (prgUnitaProdCheckedList.size() == 0) {
						// Nessuna unita produttiva matcha l'indirizzo, segnalo
						// l'errore e ritorno
						return ProcessorsUtils.createResponse(name, classname,
								new Integer(MessageCodes.ImportMov.ERR_INSERT_DATA),
								"L'indirizzo dell'unita aziendale non corrisponde con nessuno di quelli presenti nel DB.",
								warnings, nested);
					} else if (prgUnitaProdCheckedList.size() == 1) {
						// ho individuato univocamente l'unita aziendale, la
						// riporto nel record
						record.put("PRGUNITAPRODUTTIVA", prgUnitaProdCheckedList.get(0));
						// Aggiorno i dati dell'unita produttiva se sono in
						// validazione massiva
						// e solamente se non ho utilizzato il criterio di
						// selezione della prima
						// unità produttiva del comune
						if (aggiornaDatiAzienda && validazioneMassiva && !utilizzoPrimaUnita) {
							SourceBean updateUnitaResult = updateUnitaProd.processRecord(record);
							if (updateUnitaResult != null) {
								nested.add(updateUnitaResult);
								if (ProcessorsUtils.isError(updateUnitaResult)) {
									return ProcessorsUtils.createResponse(name, classname,
											new Integer(MessageCodes.ImportMov.ERR_UPDATE_DATA),
											"Impossibile aggiornare i dati dell'unita produttiva con indirizzo '"
													+ indirUnitaAz + "' dell'azienda '" + ragSocAz + "' nel DB.",
											warnings, nested);
								}
							}
						}
					} else {
						// Ho più di una unita produttiva con stesso indirizzo;
						// nel caso di forzatura
						// prendo la prima unità trovata, altrimenti segnalo e
						// ritorno
						if (checkForzaValidazione) {
							// prendo la prima e la riporto nel record
							record.put("PRGUNITAPRODUTTIVA", prgUnitaProdCheckedList.get(0));
							warnings.add(new Warning(MessageCodes.ImportMov.WAR_MOV_PRIMA_UA_INDIRIZZO, ""));
							// aggiorno le note del movimento
							String notaModifica = "<li>E' stata trovata più di una unità aziendale compatibile "
									+ "con i dati del movimento. Il sistema ha automaticamente inserito la prima. </li>";
							String strNote = (String) record.get("STRNOTE");
							if (strNote != null) {
								strNote = strNote + notaModifica;
							} else {
								strNote = notaModifica;
							}
							record.put("STRNOTE", strNote);
						} else {
							return ProcessorsUtils.createResponse(name, classname,
									new Integer(MessageCodes.ImportMov.ERR_INSERT_DATA),
									"Impossibile identificare univocamente l'unita aziendale, sono state individuate "
											+ prgUnitaProdCheckedList.size()
											+ " unita aziendali compatibili con l'indirizzo ed il comune indicato",
									warnings, nested);
						}
					}
				}
			} // end if AVVIAMENTO
				// ============================================================================

			// Se occorre cerco la sede legale e la inserisco se non esiste
			if (gestisciSede) {
				// Indica se ho avuto problemi nella gestione della sede, in
				// questo caso non proseguo nella sua gestione
				boolean okSede = true;

				// Cerco la sede legale
				Vector prgSedeLegalelist = null;
				try {
					prgSedeLegalelist = ProcessorsUtils.findUnita((BigDecimal) record.get("PRGAZIENDA"),
							(String) record.get("CODUALCOM"), trans);
				} catch (Exception e) {
					Warning w = new Warning(MessageCodes.ImportMov.ERR_REC_DATI_DB,
							"Impossibile cercare la sede legale");
					warnings.add(w);
					okSede = false;
				}

				// Se non la trovo la inserisco
				if (okSede && prgSedeLegalelist.size() == 0) {
					// La sede non c'è la inserisco
					SourceBean sedeLegaleResult = null;
					try {
						sedeLegaleResult = insertUnita((BigDecimal) record.get("PRGAZIENDA"), insertSedeLegale,
								"PRGSEDELEGALE", record);
					} catch (Exception e) {
						Warning w = new Warning(MessageCodes.ImportMov.ERR_INSERT_DATA,
								"Impossibile inserire la sede legale");
						warnings.add(w);
						okSede = false;
					}
					if (okSede && sedeLegaleResult != null) {
						nested.add(sedeLegaleResult);
						if (ProcessorsUtils.isError(sedeLegaleResult)) {
							Warning w = new Warning(MessageCodes.ImportMov.ERR_INSERT_DATA,
									"Impossibile inserire la sede legale");
							warnings.add(w);
						}
					} else if (okSede) {
						// Segnalo che ho aggiunto la sede legale con una
						// warning
						warnings.add(new Warning(MessageCodes.ImportMov.AGGIUNTA_UAZ,
								"La sede legale '" + ragSocAz + "' con codice fiscale: " + codfisc + " e indirizzo "
										+ indirSedeLeg + " è stata aggiunta al DB."));
					}
				} else if (okSede) {
					// Ho trovato una o più sedi legali, controllo quante
					// matchano l'indirizzo passato
					ArrayList prgSedeLegaleCheckedList = null;
					try {
						prgSedeLegaleCheckedList = ProcessorsUtils.checkIndirizzo(prgSedeLegalelist, indirSedeLeg);
					} catch (Exception e) {
						Warning w = new Warning(MessageCodes.ImportMov.ERR_RICERCA_SEDE,
								"Impossibile filtrare la sede legale in base all'indirizzo");
						warnings.add(w);
						okSede = false;
					}
					// Devo agire in base al numero di sedi ripescate
					if (okSede && prgSedeLegaleCheckedList.size() == 0) {
						// Nessuna sede matcha l'indirizzo, segnalo l'errore e
						// ritorno
						Warning w = new Warning(MessageCodes.ImportMov.ERR_RICERCA_SEDE,
								"L'indirizzo della sede legale non corrisponde con nessuno di quelli presenti nel DB.");
						warnings.add(w);
					} else if (okSede && prgSedeLegaleCheckedList.size() == 1) {
						// ho individuato univocamente la sede legale, la
						// riporto nel record
						record.put("PRGSEDELEGALE", prgSedeLegaleCheckedList.get(0));

						// Aggiorno i dati della sede legale se sono in
						// validazione massiva
						if (aggiornaDatiAzienda && validazioneMassiva) {
							SourceBean updateSedeResult = updateSedeLegale.processRecord(record);
							if (updateSedeResult != null) {
								nested.add(updateSedeResult);
								if (ProcessorsUtils.isError(updateSedeResult)) {
									Warning w = new Warning(MessageCodes.ImportMov.ERR_UPDATE_DATA,
											"Impossibile aggiornare i dati della sede legale con indirizzo '"
													+ indirSedeLeg + "' dell'azienda '" + ragSocAz + "' nel DB.");
									warnings.add(w);
									okSede = false;
								}
							}
						}

					} else if (okSede) {
						// Ho più di una sede legale con stesso indirizzo,
						// segnalo e ritorno
						Warning w = new Warning(MessageCodes.ImportMov.ERR_RICERCA_SEDE,
								"Impossibile identificare univocamente la sede legale, sono state individuate "
										+ prgSedeLegaleCheckedList.size()
										+ " sedi legali compatibili con l'indirizzo ed il comune indicato");
						warnings.add(w);
						okSede = false;
					}
				}
			}
		} else if (aggiornaDatiAzienda && validazioneMassiva) {
			// Eseguo solo l'aggiornamento dei dati

			// Aggiorno i dati dell'azienda
			SourceBean updateAzResult = updateAzienda.processRecord(record);
			if (updateAzResult != null) {
				nested.add(updateAzResult);
				if (ProcessorsUtils.isError(updateAzResult)) {
					return ProcessorsUtils.createResponse(name, classname,
							new Integer(MessageCodes.ImportMov.ERR_UPDATE_DATA),
							"Impossibile aggiornare i dati dell'azienda '" + record.get("STRAZRAGIONESOCIALE")
									+ "' nel DB.",
							warnings, nested);
				}
			}

			// Aggiorno i dati dell'unita produttiva
			SourceBean updateUnitaResult = updateUnitaProd.processRecord(record);
			if (updateUnitaResult != null) {
				nested.add(updateUnitaResult);
				if (ProcessorsUtils.isError(updateUnitaResult)) {
					return ProcessorsUtils.createResponse(name, classname,
							new Integer(MessageCodes.ImportMov.ERR_UPDATE_DATA),
							"Impossibile aggiornare i dati dell'unita produttiva con indirizzo '"
									+ (String) record.get("STRUAINDIRIZZO") + "' dell'azienda '"
									+ record.get("STRAZRAGIONESOCIALE") + "' nel DB.",
							warnings, nested);
				}
			}

			// Aggiorno i dati della sede legale
			SourceBean updateSedeResult = updateSedeLegale.processRecord(record);
			if (updateSedeResult != null) {
				nested.add(updateSedeResult);
				if (ProcessorsUtils.isError(updateSedeResult)) {
					Warning w = new Warning(MessageCodes.ImportMov.ERR_UPDATE_DATA,
							"Impossibile aggiornare i dati della sede legale con indirizzo '"
									+ (String) record.get("STRUALINDIRIZZO") + "' dell'azienda '"
									+ record.get("STRAZRAGIONESOCIALE") + "' nel DB.");
					warnings.add(w);
				}
			}
		}

		// Se la sede legale è valorizzata nel movimento ed è diversa da quella
		// del DB lancio una WARNING
		if (sedeValorizzata) {
			// Cerco la sede dell'azienda sul DB
			SourceBean sedeDB = null;
			try {
				sedeDB = findSede((BigDecimal) record.get("PRGAZIENDA"));
			} catch (SQLException e) {
				// Eccezione nella ricerca della sede aziendale, non segnalo
				// nulla all'utente e proseguo
			}

			// Se ho la sede legale sul DB controllo la coincidenza dei dati
			if (sedeDB != null) {
				// Controllo che il comune coincida
				if (!codComSedeLeg.equalsIgnoreCase((String) sedeDB.getAttribute("CODCOM"))) {
					warnings.add(new Warning(MessageCodes.ImportMov.WAR_SEDE_NON_COINCIDENTE, null));
				} else if (indirSedeLeg != null) {
					// Se nel movimento ho l'indirizzo della sede legale ne
					// controllo la coerenza
					if (!indirSedeLeg.equalsIgnoreCase((String) sedeDB.getAttribute("STRINDIRIZZO"))) {
						warnings.add(new Warning(MessageCodes.ImportMov.WAR_SEDE_NON_COINCIDENTE, null));
					}
				}
			}
		}

		// Guardo se non è un'assunzione propria
		String flgAssPropria = (String) record.get("FLGASSPROPRIA");
		boolean notAssPropria = "N".equalsIgnoreCase(flgAssPropria);
		boolean distacco = false;
		String flgDistaAzEstera = (String) record.get("FLGDISTAZESTERA");
		boolean notDistaAzEstera = "N".equalsIgnoreCase(flgDistaAzEstera);
		String codTipoTrasf = record.get("CODTIPOTRASF") != null ? (String) record.get("CODTIPOTRASF") : "";
		if (codTipoMov.equalsIgnoreCase("TRA") && codTipoTrasf.equalsIgnoreCase("DL")) {
			distacco = true;
		}
		String codMonoTipo = record.get("CODMONOTIPO") != null ? record.get("CODMONOTIPO").toString() : "";
		/*
		 * boolean controlliTirocinio = false; String codRegione = ""; if( infoGenerale.containsAttribute("CODREGIONE"))
		 * { codRegione = infoGenerale.getAttribute("CODREGIONE").toString(); } String codMonoTipo =
		 * record.get("CODMONOTIPO")!=null?record.get("CODMONOTIPO").toString():""; if (
		 * (codMonoTipo.equalsIgnoreCase("T")) && (codRegione.equals(REGIONE_EMILIA_ROMAGNA)) && (!codTipoAss.equals("")
		 * && !codTipoAss.equals("NB5")) ) { String codRegioneAzienda = ""; try { Object prgAziendaProd =
		 * record.get("PRGAZIENDA"); Object prgUnitaProd = record.get("PRGUNITAPRODUTTIVA"); codRegioneAzienda =
		 * ProcessorsUtils.getRegioneAzienda(prgAziendaProd, prgUnitaProd, trans); } catch (Exception e) { return
		 * ProcessorsUtils.createResponse(name, classname, new Integer(MessageCodes.ImportMov.ERR_REC_DATI_DB),
		 * "Impossibile recuperare la regione relativa all'azienda", warnings, null); } try { String dataTirocinio =
		 * infoGenerale.getAttribute("DATTIROCINIO") != null?infoGenerale.getAttribute("DATTIROCINIO").toString():"";
		 * if( (codRegioneAzienda.equals(REGIONE_EMILIA_ROMAGNA)) && (!dataTirocinio.equals("")) &&
		 * (DateUtils.compare(DateUtils.getNow(), dataTirocinio)>=0) ) { controlliTirocinio = true; } } catch (Exception
		 * e) { return ProcessorsUtils.createResponse(name, classname, new
		 * Integer(MessageCodes.ImportMov.ERR_REC_DATI_DB), "Impossibile completare i controlli sui tirocini", warnings,
		 * null); } }
		 */

		// Se non è un'assunzione propria, oppure è un distacco lavoratore ma non è stata specificata l'azienda estera
		// allora devo gestire l'azienda utilizzatrice
		String codfiscUtil = (String) record.get("STRAZINTCODICEFISCALE");
		if ((notAssPropria) || (distacco && notDistaAzEstera)
				|| (codMonoTipo.equalsIgnoreCase("T") && codfiscUtil != null && !codfiscUtil.equals(""))) {

			/*
			 * =======================================================================================================
			 * AZIENDA UTILIZZATRICE
			 * =======================================================================================================
			 */

			// Se ho già i progressivi devo solo aggiornare i dati in
			// validazione massiva.
			BigDecimal prgAzUtil = (BigDecimal) record.get("PRGAZIENDAUTIL");
			BigDecimal prgUnitaUtil = (BigDecimal) record.get("PRGUNITAUTIL");

			if (!(prgAzUtil != null && prgUnitaUtil != null)) {
				if (codTipoMov.equalsIgnoreCase("AVV") || codTipoMov.equalsIgnoreCase("TRA")
						|| codTipoMov.equalsIgnoreCase("CES") || prorogaMissioneTI) {
					// Cerco l'azienda utilizzatrice,
					// Controllo che i parametri necessari per le query di
					// ricerca ci siano

					String indirUnitaAzUtil = (String) record.get("STRUAINTINDIRIZZO");
					String codComUnitaAzUtil = (String) record.get("CODUAINTCOM");
					String ragSocAzUtil = (String) record.get("STRAZINTRAGIONESOCIALE");

					if (codfiscUtil == null || codComUnitaAzUtil == null) {
						String msgUtil = "Impossibile trovare i seguenti dati dell'azienda utilizzatrice: "
								+ (codfiscUtil == null ? "codice fiscale" : "")
								+ ((codfiscUtil == null && codComUnitaAzUtil == null) ? ", " : "")
								+ (codComUnitaAzUtil == null ? "codice del comune" : "");
						return ProcessorsUtils.createResponse(name, classname,
								new Integer(MessageCodes.ImportMov.ERR_INSERT_DATA), msgUtil, warnings, nested);
					}

					codfiscUtil = codfiscUtil.toUpperCase();
					// Guardo se l'azienda utilizzatrice esiste con la query
					String statementUtil = "SELECT prgAzienda FROM AN_AZIENDA WHERE UPPER(strCodiceFiscale) = '"
							+ codfiscUtil + "' ";

					// Provo a cercare l'azienda utilizzatrice
					ArrayList prgAzUtilList = null;
					try {
						prgAzUtilList = getProgressivo(statementUtil);
					} catch (Exception e) {
						return ProcessorsUtils.createResponse(name, classname,
								new Integer(MessageCodes.ImportMov.ERR_INSERT_DATA),
								"Impossibile cercare l'azienda utilizzatrice", warnings, nested);
					}

					// devo agire in base al numero di prgAzienda che ho
					// ripescato
					if (prgAzUtilList.size() == 0) {
						// l'azienda non è presente, la inserisco;
						// ci pensa il processor insert ad aggiungere il
						// prgAzienda nel record
						String codTipoAziUtiliz = record.get("CODAZINTTIPOAZIENDA") != null
								? record.get("CODAZINTTIPOAZIENDA").toString()
								: "";
						if (codMonoTipo.equalsIgnoreCase("T")) {
							if (codTipoAziUtiliz.equals("")) {
								record.put("CODAZINTTIPOAZIENDA", "AZI");
							}
						} else {
							if (flgPubblicaAmm) {
								record.put("CODAZINTTIPOAZIENDA", "PA");
							}
						}

						SourceBean insertAzUtilResult = insertAziendaUtil.processRecord(record);
						if (insertAzUtilResult != null) {
							nested.add(insertAzUtilResult);
							if (ProcessorsUtils.isError(insertAzUtilResult)) {
								return ProcessorsUtils.createResponse(name, classname,
										new Integer(MessageCodes.ImportMov.ERR_INSERT_DATA),
										"Impossibile inserire l'azienda utilizzatrice '" + ragSocAzUtil + "' nel DB.",
										warnings, nested);
							}
						}
						// Segnalo che ho aggiunto l'azienda con una warning
						warnings.add(new Warning(MessageCodes.ImportMov.AGGIUNTA_AZ, "L'azienda utilizzatrice '"
								+ ragSocAzUtil + "' con codice fiscale: " + codfiscUtil + " è stata aggiunta al DB."));
					} else if (prgAzUtilList.size() == 1) {
						// Ho trovato l'azienda utilizzatrice, aggiungo il
						// prgAzienda al record
						record.put("PRGAZIENDAUTIL", prgAzUtilList.get(0));

						// Aggiorno i dati dell'azienda utilizzatrice se sono in
						// validazione massiva
						if (aggiornaDatiAzienda && validazioneMassiva) {
							SourceBean updateAziendaUtilResult = updateAziendaUtil.processRecord(record);
							if (updateAziendaUtilResult != null) {
								nested.add(updateAziendaUtilResult);
								if (ProcessorsUtils.isError(updateAziendaUtilResult)) {
									return ProcessorsUtils.createResponse(name, classname,
											new Integer(MessageCodes.ImportMov.ERR_UPDATE_DATA),
											"Impossibile aggiornare i dati dell'azienda utilizzatrice '" + ragSocAzUtil
													+ "' nel DB.",
											warnings, nested);
								}
							}
						}
					} else {
						// Ho trovato più di un'azienda utilizzatrice, segnalo
						// l'errore e termino
						return ProcessorsUtils.createResponse(name, classname,
								new Integer(MessageCodes.ImportMov.ERR_INSERT_DATA),
								"Impossibile identificare univocamente l'azienda utilizzatrice, sono state individuate "
										+ prgAzUtilList.size() + " testate aziendali compatibili con il C.F: "
										+ codfiscUtil,
								warnings, nested);
					}

					// Cerco l'unita produttiva dell'azienda utilizzatrice
					Vector prgUnitaUtilList = null;
					try {
						prgUnitaUtilList = ProcessorsUtils.findUnita((BigDecimal) record.get("PRGAZIENDAUTIL"),
								codComUnitaAzUtil, trans);
					} catch (Exception e) {
						return ProcessorsUtils.createResponse(name, classname,
								new Integer(MessageCodes.ImportMov.ERR_INSERT_DATA),
								"Impossibile cercare l'unita produttiva dell'azienda utilizzatrice", warnings, nested);
					}

					// Inserisco l'unita produttiva dell'azienda utilizzatrice
					// se non esiste
					if (prgUnitaUtilList.size() == 0) {
						// L'unita non esiste, la aggiungo
						SourceBean unitaProdUtilResult = null;
						try {
							if ((record.get("CODAZINTCCNL") == null) && (checkForzaValidazione)) {
								// modifica il ccnl dell'unità dell'azienda
								// utilizzatrice
								record.put("CODAZINTCCNL", "CCNA");
								String notaModifica = "<li>Manca il CCNL dell'unita produttiva dell'azienda utilizzatrice. Il sistema ha automaticamente impostato tale campo a 'CCNA' </li>";
								String strNote = (String) record.get("STRNOTE");
								if (strNote != null) {
									strNote = strNote + notaModifica;
								} else {
									strNote = notaModifica;
								}
								record.put("STRNOTE", strNote);
							}
							unitaProdUtilResult = insertUnita((BigDecimal) record.get("PRGAZIENDAUTIL"),
									insertUnitaUtil, "PRGUNITAUTIL", record);
						} catch (Exception e) {
							return ProcessorsUtils.createResponse(name, classname,
									new Integer(MessageCodes.ImportMov.ERR_INSERT_DATA),
									"Impossibile inserire l'unita produttiva dell'azienda utilizzatrice", warnings,
									nested);
						}
						if (unitaProdUtilResult != null) {
							nested.add(unitaProdUtilResult);
							if (ProcessorsUtils.isError(unitaProdUtilResult)) {
								return ProcessorsUtils.createResponse(name, classname,
										new Integer(MessageCodes.ImportMov.ERR_INSERT_DATA),
										"Impossibile inserire l'unita produttiva dell'azienda utilizzatrice", warnings,
										nested);
							}
						}
						// Segnalo che ho aggiunto l'unita produttiva
						// dell'azienda utilizzatrice con una warning
						warnings.add(new Warning(MessageCodes.ImportMov.AGGIUNTA_UAZ,
								"L'unita aziendale dell'azienda utilizzatrice '" + ragSocAzUtil
										+ "' con codice fiscale: " + codfiscUtil + " e indirizzo " + indirUnitaAzUtil
										+ " è stata aggiunta al DB."));
					} else {
						// Ho trovato una o più unita utilizzatrici, controllo
						// quante matchano l'indirizzo passato
						ArrayList prgUnitaUtilCheckedList = null;
						try {
							prgUnitaUtilCheckedList = ProcessorsUtils.checkIndirizzo(prgUnitaUtilList,
									indirUnitaAzUtil);
						} catch (Exception e) {
							return ProcessorsUtils.createResponse(name, classname,
									new Integer(MessageCodes.ImportMov.ERR_INSERT_DATA),
									"Impossibile filtrare l'unita utilizzatrice in base all'indirizzo", warnings,
									nested);
						}

						// Stefy -06/03/2006
						// Se nessuna unità fa match con l'indirizzo...
						if (prgUnitaUtilCheckedList.size() == 0) {
							// ... si prende per default la prima unità
							// produttiva dello stesso comune
							try {
								prgUnitaUtilCheckedList = ProcessorsUtils.findPrimaUnita(prgUnitaUtilList);
								if (prgUnitaUtilCheckedList.size() == 1) {
									utilizzoPrimaUnitaUtil = true;
								}
								warnings.add(new Warning(MessageCodes.ImportMov.WAR_MOV_PRIMA_UA_UTIL, ""));
							} catch (Exception e) {
								return ProcessorsUtils.createResponse(name, classname,
										new Integer(MessageCodes.ImportMov.ERR_INSERT_DATA),
										"Impossibile recuperare la prima unita' produttiva dello stesso comune",
										warnings, nested);
							}
						}
						// Devo agire in base al numero di unita utilizzatrici
						// ripescate
						if (prgUnitaUtilCheckedList.size() == 0) {
							// Nessuna unita utilizzatrice matcha l'indirizzo,
							// segnalo l'errore e ritorno
							return ProcessorsUtils.createResponse(name, classname,
									new Integer(MessageCodes.ImportMov.ERR_INSERT_DATA),
									"L'indirizzo dell'unita utilizzatrice non corrisponde con nessuno di quelli presenti nel DB.",
									warnings, nested);
						} else if (prgUnitaUtilCheckedList.size() == 1) {
							// ho individuato univocamente l'unita
							// utilizzatrice, la riporto nel record
							record.put("PRGUNITAUTIL", prgUnitaUtilCheckedList.get(0));

							// Aggiorno i dati dell'unita produttiva
							// dell'azienda utilizzatrice se sono in validazione
							// massiva
							if (aggiornaDatiAzienda && validazioneMassiva && !utilizzoPrimaUnitaUtil) {
								SourceBean updateUnitaUtilResult = updateUnitaUtil.processRecord(record);
								if (updateUnitaUtilResult != null) {
									nested.add(updateUnitaUtilResult);
									if (ProcessorsUtils.isError(updateUnitaUtilResult)) {
										return ProcessorsUtils.createResponse(name, classname,
												new Integer(MessageCodes.ImportMov.ERR_UPDATE_DATA),
												"Impossibile aggiornare i dati dell'unita produttiva con indirizzo '"
														+ indirUnitaAzUtil + "' dell'azienda '" + ragSocAzUtil
														+ "' nel DB.",
												warnings, nested);
									}
								}
							}
						} else {
							// Ho più di una unita utilizzatrice con stesso
							// indirizzo; nel caso di forzatura
							// prendo la prima unità trovata, altrimenti segnalo
							// e ritorno
							if (checkForzaValidazione) {
								// prendo la prima e la riporto nel record
								record.put("PRGUNITAUTIL", prgUnitaUtilCheckedList.get(0));
								warnings.add(new Warning(MessageCodes.ImportMov.WAR_MOV_PRIMA_UA_UTIL_INDIRIZZO, ""));
								// aggiorno le note del movimento
								String notaModifica = "<li>E' stata trovata più di una unità aziendale relativa all' azienda "
										+ "utilizzatrice compatibile con i dati del movimento. "
										+ "Il sistema ha automaticamente inserito la prima. </li>";
								String strNote = (String) record.get("STRNOTE");
								if (strNote != null) {
									strNote = strNote + notaModifica;
								} else {
									strNote = notaModifica;
								}
								record.put("STRNOTE", strNote);
							} else {
								return ProcessorsUtils.createResponse(name, classname,
										new Integer(MessageCodes.ImportMov.ERR_INSERT_DATA),
										"Impossibile identificare univocamente l'unita utilizzatrice, sono state individuate "
												+ prgUnitaUtilCheckedList.size()
												+ " unita utilizzatrici compatibili con l'indirizzo ed il comune indicato",
										warnings, nested);
							}
						}
					}
				} // END if (codTipoMov.equalsIgnoreCase("AVV") ||
					// codTipoMov.equalsIgnoreCase("CES"))

			} else if (aggiornaDatiAzienda && validazioneMassiva) {
				// Aggiorno solo i dati se sono in validazione massiva

				// Aggiorno i dati dell'azienda utilizzatrice
				SourceBean updateAziendaUtilResult = updateAziendaUtil.processRecord(record);
				if (updateAziendaUtilResult != null) {
					nested.add(updateAziendaUtilResult);
					if (ProcessorsUtils.isError(updateAziendaUtilResult)) {
						return ProcessorsUtils.createResponse(name, classname,
								new Integer(MessageCodes.ImportMov.ERR_UPDATE_DATA),
								"Impossibile aggiornare i dati dell'azienda utilizzatrice '"
										+ record.get("STRAZINTRAGIONESOCIALE") + "' nel DB.",
								warnings, nested);
					}
				}

				// Aggiorno i dati dell'unita produttiva dell'azienda
				// utilizzatrice
				SourceBean updateUnitaUtilResult = updateUnitaUtil.processRecord(record);
				if (updateUnitaUtilResult != null) {
					nested.add(updateUnitaUtilResult);
					if (ProcessorsUtils.isError(updateUnitaUtilResult)) {
						return ProcessorsUtils.createResponse(name, classname,
								new Integer(MessageCodes.ImportMov.ERR_UPDATE_DATA),
								"Impossibile aggiornare i dati dell'unita produttiva con indirizzo '"
										+ record.get("STRUAINTINDIRIZZO") + "' dell'azienda '"
										+ record.get("STRAZINTRAGIONESOCIALE") + "' nel DB.",
								warnings, nested);
					}
				}
			}
			// Nel caso di distacco se non si tratta di un'azienda estera, i campi relativi all'azienda
			// utilizzatrice(PRGAZIENDAUTIL, PRGUNITAUTIL)
			// li devo riportare nei campi relativi all'azienda distaccataria (PRGAZIENDADIST, PRGUNITADIST)
			if (distacco && notDistaAzEstera) {
				if (record.containsKey("PRGAZIENDAUTIL")) {
					record.put("PRGAZIENDADIST", record.get("PRGAZIENDAUTIL"));
				}
				if (record.containsKey("PRGUNITAUTIL")) {
					record.put("PRGUNITADIST", record.get("PRGUNITAUTIL"));
				}
			}
		} // end if (notAssPropria || (distacco && notDistaAzEstera) ||
			// (codMonoTipo.equalsIgnoreCase("T") && codfiscUtil != null && !codfiscUtil.equals("")))

		/*
		 * // controllo se il lavoratore e' un lavoratore autonomo e se i codici fiscali del lavoratore e dell'azienda
		 * // coincidano, altrimenti mostro un warning. String codiceFiscaleAz = (String)
		 * record.get("STRAZCODICEFISCALE"); String codiceFiscaleLav = (String) record.get("STRCODICEFISCALE"); if
		 * (codiceFiscaleLav !=null && codiceFiscaleLav!=null && !codiceFiscaleLav.equals(codiceFiscaleAz)) { // leggere
		 * il codtipoavv String codTipoAss = (String)record.get("CODTIPOASS"); String stm = "select codMonoTipo from
		 * de_mv_tipo_ass where codTipoAss = '" +codTipoAss+"'"; try { SourceBean row =
		 * (SourceBean)trans.executeQuery(stm, new Object[]{codTipoAss}, "SELECT"); if
		 * (row.getAttribute("row.codMonoTipo")!=null && row.getAttribute("row.codMonoTipo").equals("N")){ Warning w =
		 * new Warning(MessageCodes.ImportMov.WAR_LAVORO_AUTONOMO,""); warnings.add(w); } } catch (EMFInternalError e) {
		 * }
		 * 
		 * 
		 * }
		 */

		// Se ho warning o risultati annidati li inserisco nella risposta,
		// altrimenti non ritorno nulla.
		if ((warnings.size() > 0) || (nested.size() > 0)) {
			return ProcessorsUtils.createResponse(name, classname, null, null, warnings, nested);
		} else {
			return null;
		}

	}

	/**
	 * Cerca la prima unita' produttiva dello stesso comune.
	 * <p>
	 * 
	 * @return La lista di prgUnita (BigDecimal) che matchano con gli argomenti.
	 */
	private ArrayList findPrimaUnita(BigDecimal prgAzienda, String codiceComune) throws SQLException {

		if (prgAzienda == null || codiceComune == null) {
			return new ArrayList();
		}

		codiceComune = codiceComune.toUpperCase();
		// Controllo se l'unita è presente nel DB con la query
		String statementUnitaAz = "SELECT prgUnita FROM AN_UNITA_AZIENDA WHERE prgAzienda = " + prgAzienda
				+ " AND UPPER(codCom) = '" + codiceComune + "' and rownum=1 order by prgUnita";

		// Provo a cercare l'unita aziendale
		return getProgressivo(statementUnitaAz);
	}

	/**
	 * Inserisce l'unita Produttiva o la sede legale nel DB, si procura un nuovo prgUnita. Per effettuare l'inserimento
	 * utilizza il processore di classe insertData fornito. E il nome con cui esso si aspetta il progressivo.
	 * <p>
	 * 
	 * @return Ritorna il SourceBean dei risultati dell'operazione, null se non ci sono problemi.
	 */
	private SourceBean insertUnita(BigDecimal prgAzienda, RecordProcessor insertUnitaAziendale, String nomeProgressivo,
			Map rec) throws SQLException, SourceBeanException {

		// Mi procuro un nuovo prgUnita
		String newPrgUnita = "SELECT NVL(MAX(prgUnita) + 1, 1) FROM AN_UNITA_AZIENDA WHERE prgAzienda = " + prgAzienda;
		ArrayList prgUnitaList = getProgressivo(newPrgUnita);

		// Se la query non ritorna nulla (o più valori) non posso inserire
		// l'unita Aziendale, ritorno l'errore
		if (prgUnitaList == null || prgUnitaList.size() != 1) {
			throw new SQLException("Impossibile ottenere un progressivo valido per inserire l'unita aziendale");
		} else {
			// Inserisco il prgunita nel Record
			rec.put(nomeProgressivo, prgUnitaList.get(0));

			// Provo ad inserire l'unita Aziendale
			return insertUnitaAziendale.processRecord(rec);
		}
	}

	/**
	 * Metodo per ottenere i progressivi che servono, basta fornire il testo della query da eseguire
	 * <p>
	 * 
	 * @return Se la query ritorna più progressivi essi vengono inseriti nell' ArrayList
	 */
	private ArrayList getProgressivo(String query) throws SQLException {
		ArrayList prgList = new ArrayList();
		SourceBean result = null;
		try {
			result = ProcessorsUtils.executeSelectQuery(query, trans);
		} catch (Exception e) {
			throw new SQLException();
		}

		Vector v = result.getAttributeAsVector("ROW");
		for (int i = 0; i < v.size(); i++) {
			// Inserisco i progressivi nell'arraylist
			SourceBean row = (SourceBean) v.get(i);
			Vector attributes = row.getContainedAttributes();
			for (int j = 0; j < attributes.size(); j++) {
				prgList.add(((SourceBeanAttribute) attributes.get(j)).getValue());
			}
		}
		return prgList;
	}

	/**
	 * Estrae i dati della sede legale a partire dal prgAzienda fornito
	 * <p>
	 * 
	 * @return Il sourceBean contenente comune e indirizzo della sede legale (come attributi), null in caso che la sede
	 *         non esista per il progressivo dell'azienda passato
	 */
	private SourceBean findSede(BigDecimal prgAzienda) throws SQLException {

		if (prgAzienda == null) {
			return null;
		}

		// Estrazione del comune e dell'indirizzo della sede
		String statementSedeAzienda = "SELECT CODCOM, STRINDIRIZZO " + "FROM AN_UNITA_AZIENDA " + "WHERE prgAzienda = "
				+ prgAzienda + " AND FLGSEDE = 'S'";

		SourceBean result = null;
		try {
			result = ProcessorsUtils.executeSelectQuery(statementSedeAzienda, trans);
		} catch (Exception e) {
			throw new SQLException();
		}

		return (SourceBean) result.getAttribute("ROW");
	}

}

/*
 * Creato nella sua versione attuale il 08-mar-2004
 * ultima modifica: 12-lug-2005  
 * Author: rolfini
 * 
 */
package it.eng.sil.bean.SilProLabor;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.SessionContainer;
import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.configuration.ConfigSingleton;

import it.eng.sil.util.Sottosistema;
import it.eng.sil.util.batch.BatchRunnable;
import oracle.sql.CLOB;

/**
 * @author rolfini
 * 
 */
public class DemoneCooperazione implements BatchRunnable {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(DemoneCooperazione.class.getName());

	private String urlScambio = null; // directory di scambio
	private ConfigSingleton configure;
	private String fileNameDest = ""; // memorizza il nome del file di
										// risposta che deve essere prodotto. Il
										// nome del file di risposta
	// è creato basandosi sul nome del file di input in processazione

	private String fileNameRenamed = "";

	// private TransactionQueryExecutor transEx=null;
	private FileSystemManager fileSystem = null;
	private Logger log = null;

	/**
	 * COSTRUTTORE Legge il singleton di configurazione identificando: - la directory di scambio - la necessità di
	 * tracciare il log
	 */
	public DemoneCooperazione() throws IOException {

		configure = ConfigSingleton.getInstance();
		RequestContainer requestContainer = new RequestContainer();
		SessionContainer sessionContainer = new SessionContainer(true);
		sessionContainer.setAttribute("_ENCRYPTER_KEY_", System.getProperty("_ENCRYPTER_KEY_"));
		String flag = (String) ConfigSingleton.getInstance().getAttribute("INTERRUTTORI.MO.attivo");
		boolean interruttoreMobilita = (flag.equalsIgnoreCase("true"));
		Sottosistema.setMO(interruttoreMobilita);
		SourceBean silProlaborConfig = (SourceBean) configure.getFilteredSourceBeanAttribute("SILPROLABOR.DIR", "NAME",
				"SCAMBIO");
		if (silProlaborConfig == null) {
			_logger.debug("SilProLabor.DemoneCooperazione: configurazione non trovata ");

			return;
		}

		// inizializzo il componente che si occupa della lettura/scrittura
		// del file system
		fileSystem = new FileSystemManager(configure);
		// componente del log
		log = new Logger(configure);

		/*
		 * //componente del db
		 * 
		 * 
		 * try { //Inizializzo la connessione transEx=new TransactionQueryExecutor(Values.DB_SIL_DATI); db=new
		 * DbManager(configure, transEx); this.start(); } catch (Exception ex) {
		 * log.writeLogAdm("Errore di connessione"); log.writeLogAdm("Demone arrestato"); }
		 */
	}

	// **************************************************
	// * FUNZIONI DI LOGGING E MESSAGGISTICA DI ERRORE *
	// **************************************************

	/**
	 * writeErrorMessage Scrive nella directory di scambio un messaggio di risposta contenente un codice di errore.
	 * Attualmente, l'unico messaggio di errore che ProLabor interpreta è un errore sui dati anagrafici (ERR).
	 * 
	 * @param nothing
	 * @trows Exception
	 * 
	 */
	private void writeErrorMessage() {

		try {

			TracciatoManager tracciato = new TracciatoManager();
			tracciato.setHeader("ERR", "Errore nell' elaborazione del tracciato");
			tracciato.write(fileSystem);
		} catch (Exception ioex2) {
			ioex2.printStackTrace();

		}

	}

	// **************************************************

	/**
	 * processAnag Chiama la stored procedure del package SILPROLABOR per l'elaborazione dei dati anagrafici. La stored
	 * procedure restituisce direttamente l'xml, che viene composto con un header e viene scritto nel file di
	 * destinazione.
	 * 
	 * 
	 * @param cf
	 *            Codice fiscale del soggetto da trattare
	 * @param dataModifica
	 *            data di ultima modifica presunta come risulta su ProLabor
	 * @throws Exception
	 * 
	 */
	private void processRichiestaAggiornamento(SourceBean richiesta) throws Exception {

		try {

			String cf = (String) ((SourceBean) richiesta.getAttribute("LAVANAG.LANA_CODFIS")).getCharacters();
			String dataMod = (String) ((SourceBean) richiesta.getAttribute("LAVANAG.LANA_DMOD")).getCharacters();
			String oraMod = (String) ((SourceBean) richiesta.getAttribute("LAVANAG.LANA_HMOD")).getCharacters();
			String dataOraMod = null;
			if (dataMod != null) {
				dataOraMod = dataMod + " " + oraMod;
			}

			DbManager db = new DbManager(configure);

			CLOB tracciatoXML = null;
			String risultato = null;
			String errPL = ""; // codice di errore da trasmettere a ProLabor
								// (può essere != da quello proveniente dalla
								// SP)
			String msgErr = "";
			tracciatoXML = db.getAggiornamentoLav(cf, dataOraMod);
			risultato = db.getOpResult(); // prelevo il codice risultato
											// dell'operazione
			// impostiamo il messaggio di errore
			if (risultato.equalsIgnoreCase("NOLAV")) {
				errPL = "ERR";
				msgErr = "Lavoratore non presente nel SIL";
			} else {
				errPL = risultato;
			}

			TracciatoManager tracciato = new TracciatoManager();

			tracciato.setHeader(errPL, msgErr);
			tracciato.setBodyCLOB(tracciatoXML);
			tracciato.writeCLOB(fileSystem);

			if (risultato.equals("OK")) {
				log.writeLogAdm("\tNon risultano aggiornamenti sui dati: OK");
				log.writeLogUsr("CF: " + cf + " Richiesta anagrafica aggiornata. OK Non risultano aggiornamenti");
			} else if (risultato.equals("MOD")) {
				log.writeLogAdm("\tVi sono stati aggiornamenti sui dati: MOD");
				log.writeLogUsr("CF: " + cf + " Richiesta anagrafica aggiornata. OK Risultano aggiornamenti");
			} else if (risultato.equals("NOLAV")) {
				log.writeLogAdm("\tLavoratore non presente sul SIL");
				log.writeLogUsr("CF: " + cf + " Richiesta anagrafica aggiornata. ERR Lavoratore non presente sul SIL");
			} else {
				throw new Exception("errore: nessun codice restituito");
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			log.writeLogAdm("\t***Elaborazione fallita: " + ex.getMessage());
			log.writeLogAdm("\t***File di richiesta:");
			log.writeLogAdm("\t***" + richiesta.toXML(false));
			// Scrivo il messaggio di errore a ProLabor
			writeErrorMessage();
			// System.out.println("Exception: "+ex.getMessage());
			// ex.printStackTrace();
			// writeErrorMessage();
		}

	}

	/**
	 * processRichiestaCFAggiornati effettua la chiamata alla stored procedure che restituisce la lista dei codici
	 * fiscali dei lavoratori che hanno subito modifiche sul SIL a partire dalla data specificata nel tracciato di
	 * richiesta
	 * 
	 * @param tracciato
	 *            tracciato XML letto dal file proveniente da ProLabor
	 * @throws Exception
	 */
	private void processRichiestaCFAggiornati(SourceBean tracciato) throws Exception {

		try {
			String ultimaData = (String) ((SourceBean) tracciato.getAttribute("RIK_ELENCO.ULT_DATA")).getCharacters();
			String ultimaOra = (String) ((SourceBean) tracciato.getAttribute("RIK_ELENCO.ULT_ORA")).getCharacters();

			String ultimaDataOra = ultimaData + " " + ultimaOra;
			CLOB listaCF = null;
			String risultato = null;

			DbManager db = new DbManager(configure);

			listaCF = db.getCFModified(ultimaDataOra);
			risultato = db.getOpResult();

			// chiamo direttamente il filesystem in quanto la risposta
			// non è un tracciato xml, ma un file plain-text
			fileSystem.writeTracciato("", listaCF, "");
			log.writeLogAdm("Lista correttamente generata");
			log.writeLogUsr("Lista correttamente generata");
		} catch (Exception ex) {
			fileSystem.writeTracciato("", "ERR: errore di elaborazione della lista " + ex.getMessage(), "");
			log.writeLogAdm(ex.getMessage());
			log.writeLogUsr("Errore di elaborazione lista codici fiscali lavoratori modificati");
		}

	}

	/**
	 * processCLA181
	 * 
	 * interroga il bean del db per reperire i dati relativi allo stato occupazionale e alla data di anzianità cosi'
	 * come sono stati calcolati dalla precedente elaborazione della mobilità.
	 * 
	 * Nel bean del db viene fatto un controllo di consistenza del codice fiscale con quello memorizzato: in caso siano
	 * difformi significa che la mobilità non è stata ancora presa in carico; cosi' è anche se i dati vengono trovati
	 * null. In entrambi i casi viene inviato un apposito messaggio di errore.
	 * 
	 * 
	 * @param cf
	 */

	private void processCLA181(String cf) throws Exception {

		TracciatoManager tracciato = new TracciatoManager();

		try {

			DbManager db = new DbManager(configure);
			String data = (new SimpleDateFormat("dd-MM-yyyy")).format(new Date());

			db.processCLA181(cf);
			String statoOcc = db.getStatoOccCOD();
			String dataAnz = db.getStatoOccDATAANZ();

			tracciato.setHeader("OK", "");

			String lavAnag = "<LAVANAG>\r\n" + "    <LANA_TMOD>S</LANA_TMOD>\r\n" + "    <LANA_DMOD>" + data
					+ "</LANA_DMOD>\r\n" + "    <LANA_HMOD></LANA_HMOD>\r\n" + "    <LANA_CODFIS>" + cf
					+ "</LANA_CODFIS>\r\n" + "    <LANA_STATOOCC>" + statoOcc + "</LANA_STATOOCC>\r\n"
					+ "</LAVANAG>\r\n";
			String lavAlt = "<LAVALT>\r\n" + "    <LALT_DATAANZ>" + dataAnz + "</LALT_DATAANZ>\r\n" + "</LAVALT>\r\n";

			String body = lavAnag + lavAlt;

			tracciato.setBody(body);
			tracciato.write(fileSystem);

			String dicituraDataAnz = dataAnz.equals("") ? "." : ". Data di anzianità: " + dataAnz;

			log.writeLogUsr("CF: " + cf + " Richiesta nuovo stato occupazionale: " + statoOcc + dicituraDataAnz);
		} catch (Exception ex) {
			ex.printStackTrace();
			tracciato.setHeader("ERR", ex.getMessage());
			tracciato.write(fileSystem);
			throw new Exception("Errore nell'elaborazione CLA181 (stato occupazionale):" + ex.getMessage());
		}
	}

	/**
	 * processL68
	 * 
	 * Processa i record del collocamento mirato provenienti da ProLabor. I record possono essere tipicamente di due
	 * tipi: categoria 1 e categoria 2. Essi sono molto simili, ma ProLabor li tratta come due tipologie diverse. Questa
	 * procedura, appoggiandosi a saveCM, li scrive entrambi, se presenti.
	 * 
	 * @param tracciato
	 *            tracciato XML letto dal file proveniente da ProLabor
	 * @throws Exception
	 * @see saveCM
	 */
	private void processL68(SourceBean tracciato) throws Exception {

		String risultato = null;
		// leggo il codice fiscale
		String cf = (String) ((SourceBean) tracciato.getAttribute("LAVANAG.LANA_CODFIS")).getCharacters();

		// leggo i parametri della l68
		String codCmTipoIscrCat1 = (String) ((SourceBean) tracciato.getAttribute("LavL68.Ll68_Cat1")).getCharacters();
		String codAccertSanitarioCat1 = (String) ((SourceBean) tracciato.getAttribute("LavL68.Ll68_Cat1Inserimento"))
				.getCharacters();
		String numPercInvaliditaCat1 = (String) ((SourceBean) tracciato.getAttribute("LavL68.Ll68_Cat1Perc"))
				.getCharacters();

		String stringTipoInvaliditaCat1 = (String) ((SourceBean) tracciato.getAttribute("LavL68.Ll68_Cat1Tipo"))
				.getCharacters();

		// *********************************************************************************
		// ATTENZIONE: IL DB ACCETTA SEMPRE UNO ED UN SOLO TIPO INVALIDITA': IN
		// CASO DI
		// INSERIMENTI SUCCESSIVI DI PIU' TIPI DI VALIDITA' IN RECORD DIVERSI,
		// I RECORD INSERITI VENGONO VIA VIA CHIUSI LASCIANDO ATTIVO SOLO
		// L'ULTIMO.

		// ANCHE SE PROLABOR ME NE PUO' TRASMETTERE PARECCHI (CAMPO
		// MULTIVALORE), A CAUSA DEL COMPORTAMENTO DEL SIL INSERIRO'
		// SEMPRE E SOLO IL TIPO DI INVALIDITA' ALL'INDICE 0 (ZERO) DEL VETTORE.
		//
		// BOLOGNA, 02/VII/MMIV D.C.
		// ALESSIO ROLFINI
		// **********************************************************************************

		String[] codTipoInvaliditaCat1 = null;

		if (stringTipoInvaliditaCat1 != null) { // se ho valori significativi li
												// tiro fuori

			// codTipoInvalidita è multivalore. Separatore: ';'
			// riempio un array con i valori passati
			// es: 02;03;04;05;
			// creo un bel vettore
			codTipoInvaliditaCat1 = new String[(stringTipoInvaliditaCat1.length() / 3)];

			// lo inizializzo (non si sa mai)
			for (int i = 0; i < (stringTipoInvaliditaCat1.length() / 3); i++) {
				codTipoInvaliditaCat1[i] = null;
			}

			// parsing della stringa ogni 3 caratteri
			for (int i = 0; i < (stringTipoInvaliditaCat1.length() / 3); i++) {
				codTipoInvaliditaCat1[i] = stringTipoInvaliditaCat1.substring(i * 3, i * 3 + 2);
			}
		} else { // altrimenti metto il tutto a null con buona pace.
			codTipoInvaliditaCat1 = new String[1];
			codTipoInvaliditaCat1[0] = null;
		}

		String datAccertSanitarioCat1 = (String) ((SourceBean) tracciato.getAttribute("LavL68.Ll68_Cat1DataAcc"))
				.getCharacters();
		String datDataInizioCat1 = (String) ((SourceBean) tracciato.getAttribute("LavL68.Ll68_Cat1DataIscr"))
				.getCharacters();

		// cat2
		String codCmTipoIscrCat2 = (String) ((SourceBean) tracciato.getAttribute("LavL68.Ll68_Cat2")).getCharacters();
		String datDataInizioCat2 = (String) ((SourceBean) tracciato.getAttribute("LavL68.Ll68_Cat2DataIscr"))
				.getCharacters();

		// nuovi campi richiesti da Modena
		String datIscrCat1 = (String) ((SourceBean) tracciato.getAttribute("LavL68.LL68_CAT1DATAC4")).getCharacters();
		String datIscrCat2 = (String) ((SourceBean) tracciato.getAttribute("LavL68.LL68_CAT2DATAC4")).getCharacters();
		String datAnzianitaCat1 = (String) ((SourceBean) tracciato.getAttribute("LavL68.LL68_CAT1DATAISCR"))
				.getCharacters();
		String datAnzianitaCat2 = (String) ((SourceBean) tracciato.getAttribute("LavL68.LL68_CAT2DATAISCR"))
				.getCharacters();

		// INSERIMENTO/AGGIORNAMENTO DI CAT1.
		// CHIAMO LA STORED
		String infoCM = "";
		if (cf != null && codCmTipoIscrCat1 != null && datDataInizioCat1 != null) {
			infoCM = "data inizio: " + datDataInizioCat1;
			log.writeLogAdm(
					"\tPresa in carico collocamento mirato - cat: " + codCmTipoIscrCat1 + " (Disabile) - " + infoCM);
			log.writeLogUsr("CF: " + cf + " Elaborazione Collocamento Mirato cat1");
			try {
				DbManager db = new DbManager(configure);
				db.saveCM(cf, codCmTipoIscrCat1, codAccertSanitarioCat1, numPercInvaliditaCat1,
						codTipoInvaliditaCat1[0], datAccertSanitarioCat1, datDataInizioCat1, datIscrCat1,
						datAnzianitaCat1);

				// gestione degli errori applicativi
				risultato = db.getOpResult();
				if (risultato.equals("OK")) {
					log.writeLogUsr("\tElaborazione effettuata. Esito: OK");
				} else if (risultato == null) {
					log.writeLogAdm("\tL68 cat1 - errore nel salvataggio");
					log.writeLogUsr("\tElaborazione effettuata. Esito: ERR");
				} else if (risultato.equals("ERR_LAVNOTFOUND")) {
					log.writeLogAdm("\tL68 cat1 - errore nel salvataggio: lavoratore non trovato");
					log.writeLogUsr("\tElaborazione effettuata. Esito: ERR - lavoratore non presente sul SIL");
				} else if (risultato.equals("NO_DATA")) {
					log.writeLogAdm("\tL68 cat1 - errore nel salvataggio: errore di comunicazione con il database");
					log.writeLogUsr("\tElaborazione effettuata. Esito: ERR");
				}
				//

			} catch (Exception ex) {
				log.writeLogAdm(ex.getMessage());
			}
		} else {
			if (cf == null && codCmTipoIscrCat1 == null && datDataInizioCat1 == null) {
				log.writeLogAdm("\tL68 Cat1 - record non presente");
			} else {
				String motivazione = "";
				if (cf == null)
					motivazione += " codice fiscale nullo; ";
				if (codCmTipoIscrCat1 == null)
					motivazione += " tipo di iscrizione cat1 non presente; ";
				if (datDataInizioCat1 == null)
					motivazione += " data di iscrizione cat1 non presente; ";
				log.writeLogAdm("\t***L68 Cat1 - record incompleto: " + motivazione);
			}
		}

		// INSERIMENTO/AGGIORNAMENTO DI CAT2
		if (codCmTipoIscrCat2 != null && datDataInizioCat2 != null) {
			infoCM = "data inizio: " + datDataInizioCat2;
			log.writeLogAdm("\tPresa in carico collocamento mirato - cat: " + codCmTipoIscrCat2
					+ " (Categoria protetta) - " + infoCM);
			log.writeLogUsr("CF: " + cf + " Elaborazione Collocamento Mirato cat1");
			try {
				DbManager db = new DbManager(configure);
				db.saveCM(cf, codCmTipoIscrCat2, null, null, null, null, datDataInizioCat2, datIscrCat2,
						datAnzianitaCat2);

				// gestione degli errori applicativi
				risultato = db.getOpResult();
				if (risultato.equals("OK")) {
					log.writeLogUsr("\tElaborazione effettuata. Esito: OK");
				} else if (risultato == null) {
					log.writeLogAdm("\tL68 cat2 - errore nel salvataggio");
					log.writeLogUsr("\tElaborazione effettuata. Esito: ERR");
				} else if (risultato.equals("ERR_LAVNOTFOUND")) {
					log.writeLogAdm("\tL68 cat2 - errore nel salvataggio: lavoratore non trovato");
					log.writeLogUsr("\tElaborazione effettuata. Esito: ERR - lavoratore non presente sul SIL");
				} else if (risultato.equals("NO_DATA")) {
					log.writeLogAdm("\tL68 cat2 - errore nel salvataggio: errore di comunicazione con il database");
					log.writeLogUsr("\tElaborazione effettuata. Esito: ERR");
				}
				//

			} catch (Exception ex) {
				log.writeLogAdm(ex.getMessage());
			}
		} else {
			if (codCmTipoIscrCat2 == null && datDataInizioCat2 == null) {
				log.writeLogAdm("\tL68 Cat2 - record non presente");
			} else {
				String motivazione = "";
				if (codCmTipoIscrCat2 == null)
					motivazione += " tipo di iscrizione cat2 non presente; ";
				if (datDataInizioCat2 == null)
					motivazione += " data di iscrizione cat2 non presente; ";
				log.writeLogAdm("\t***L68 Cat2 - record incompleto: " + motivazione);
			}

		}

	}

	/**
	 * processMob effettua la chiamata alla stored procedure per la presa in carico dei dati relativi alla mobilità
	 * provenienti da ProLabor.
	 * 
	 * @param tracciato
	 *            tracciato XML letto dal file proveniente da ProLabor
	 * @throws Exception
	 */
	private void processMob(SourceBean tracciato) throws Exception {

		String cf = (String) ((SourceBean) tracciato.getAttribute("LAVANAG.LANA_CODFIS")).getCharacters();
		String tipoLista = (String) ((SourceBean) tracciato.getAttribute("LAVMOB.LMOB_TIPOLISTA")).getCharacters(); // campo
																													// obbligatorio

		String datInizio = (String) ((SourceBean) tracciato.getAttribute("LAVMOB.LMOB_INIMOB")).getCharacters(); // campo
																													// obbligatorio
		String datFine = (String) ((SourceBean) tracciato.getAttribute("LAVMOB.LMOB_FINEMOB")).getCharacters();

		String dataAss = (String) ((SourceBean) tracciato.getAttribute("LAVMOB.LMOB_DATAASS")).getCharacters();
		String dataCes = (String) ((SourceBean) tracciato.getAttribute("LAVMOB.LMOB_DATACESS")).getCharacters();

		String flgIndennita = (String) ((SourceBean) tracciato.getAttribute("LAVMOB.LMOB_INDENN")).getCharacters();
		String datInizioIndennita = (String) ((SourceBean) tracciato.getAttribute("LAVMOB.LMOB_INDMOB"))
				.getCharacters();
		String datFineIndennita = (String) ((SourceBean) tracciato.getAttribute("LAVMOB.LMOB_FINEIND")).getCharacters();
		String maxDiff = (String) ((SourceBean) tracciato.getAttribute("LAVMOB.LMOB_MAXDIFF")).getCharacters();
		String motivoFine = (String) ((SourceBean) tracciato.getAttribute("LAVMOB.LMOB_MOTMOB")).getCharacters();

		String qualifica = (String) ((SourceBean) tracciato.getAttribute("LAVMOB.LMOB_QUALIFICA")).getCharacters();

		String dataCRT = (String) ((SourceBean) tracciato.getAttribute("LAVMOB.LMOB_DATACRI")).getCharacters();
		String provCRT = (String) ((SourceBean) tracciato.getAttribute("LAVMOB.LMOB_PROVCRI")).getCharacters();

		String codFisAzienda = (String) ((SourceBean) tracciato.getAttribute("LAVMOB.LMOB_PARTIVA")).getCharacters();

		log.writeLogAdm("\tPresa in carico mobilità - lista: " + tipoLista + " data inizio: " + datInizio
				+ " data fine: " + datFine);

		if (tipoLista != null && datInizio != null) { // dati obbligatori
			try {
				String infoMobilita = "";
				DbManager dbPrec = new DbManager(configure);
				infoMobilita = "lista: " + tipoLista + " data inizio: " + datInizio + " data fine: " + datFine;
				log.writeLogUsr("CF: " + cf + " Elaborazione mobilità - " + infoMobilita);

				// ai fini del log prelevo lo stato occupazionale precedente
				// all'elaborazione della mobilità
				dbPrec.processCLA181(cf);
				log.writeLogUsr("\tStato occupazionale prima dell'elaborazione: " + dbPrec.getStatoOccCOD());
				log.writeLogUsr("\tData di anzianità prima dell'elaborazione: " + dbPrec.getStatoOccDATAANZ());

				DbManager db = new DbManager(configure);
				db.saveMob(cf, tipoLista, datInizio, datFine, dataAss, dataCes, flgIndennita, datInizioIndennita,
						datFineIndennita, maxDiff, motivoFine, qualifica, dataCRT, provCRT, codFisAzienda);

				String nuovoStatoOcc = db.getStatoOccCOD();
				String dicituraStatoOcc = (nuovoStatoOcc.equals("")
						? ". Nessuna modifica effettuata sullo stato occupazionale. "
						: ". Nuovo stato occupazionale: " + nuovoStatoOcc);
				log.writeLogUsr("\tElaborazione effettuata. Esito: " + db.getOpResult() + dicituraStatoOcc);
				log.writeLogUsr("\tData di anzianità dopo l'elaborazione: " + db.getStatoOccDATAANZ());
				if (db.getForzaturaChiusuraMobilita()
						|| (!nuovoStatoOcc.trim().equals("A21") && !nuovoStatoOcc.trim().equals("B2"))) {
					log.writeLogUsr("\tUSCITA MOBILITA'");
				}

			} catch (Exception ex) {
				log.writeLogAdm(ex.getMessage());
				log.writeLogUsr("\tElaborazione effettuata. Esito: ERR");
				log.writeLogUsr("\tErrore riscontrato: " + ex.getMessage());
			}
		} else {
			if (tipoLista == null) {
				log.writeLogAdm("\t***Mobilità - record incompleto: tipo di lista non specificato");
			}
			if (datInizio == null) {
				log.writeLogAdm("\t***Mobilità - record incompleto: data di inizio non specificata");
			}
		}

	}

	/**
	 * start
	 * 
	 * Procedura principale per l'esecuzione del demone. Praticamente ne è il main. Viene impostato un thread che cicla
	 * ogni secondo circa (sleep 1000) cercando, nella directory designata per lo scambio dei dati, i file prodotti da
	 * Prolabor (estensizone "P.XML"). Ogni file trovato viene preso in carico (contestualmente viene cancellato
	 * indicando cosi' a ProLabor la presa in carico), viene letto l'XML in esso contenuto e si decide cosa fare. Le
	 * azioni possono essere le seguenti: - ANA: richiesta di aggiornamento da parte di ProLabor. I dati contenuti nel
	 * file di richiesta (soprattutto la data presunta di ultima modifica) vengono testati con i dati presenti nel SIL.
	 * - se i dati sul SIL sono stati modificati dopo la data trasmessa da ProLabor, viene prodotta una risposta
	 * (estensione "S.XML") contenente i dati aggiornati (codice MOD). - se i dati sul SIL sono allineati con quelli
	 * presenti su ProLabor, ovvero non hanno subito modifiche dopo la data trasmessa nella richiesta, viene trasmesso
	 * un record vuoto (codice OK). - se il soggetto richiesto non viene trovato viene restituito un codice di errore
	 * (ERR). - MOB: trasmissione, da ProLabor a SIL, degli aggiornamenti della gestione della mobilità. Il SIL prende
	 * in carico i dati e li inserisce in database senza produrre risposte. - L68: trasmissione, da ProLabor a SIL degli
	 * aggiornamenti della gestione del collocamento mirato (legge 68). Il SIL prende in carico i dati e li inserisce in
	 * database senza produrre risposte.
	 * 
	 * quanto accade è tracciato nell'apposito log, a meno che sia diversamente specificato nel file di configurazione.
	 * 
	 */
	public void start() {
		Thread t = new Thread() {
			public void run() {
				String[] filenames = null;
				int j = 0;
				while (true) {
					try {
						sleep(1000);
						// leggo il filesystem
						filenames = fileSystem.listFiles();
						for (int i = 0; i < filenames.length; i++) {
							// gestisco il file letto
							SourceBean argSourceBean = fileSystem.leggiFile(filenames[i]);

							// Faccio il dispatching leggendo il campo
							// Intesta.Int_Tipo
							// Se il campo è "ANA", allora ProLabor vuole
							// un'anagrafica.
							// Se il campo è vuoto, si tratta di una
							// trasmissione di aggiornamenti
							String tipo = (String) ((SourceBean) argSourceBean.getAttribute("INTESTA.INT_TIPO"))
									.getCharacters();
							String cf = (argSourceBean.containsAttribute("LAVANAG.LANA_CODFIS"))
									? (String) ((SourceBean) argSourceBean.getAttribute("LAVANAG.LANA_CODFIS"))
											.getCharacters()
									: "";
							if (!cf.equals("")) {
								// scrivo il log:
								log.writeLogAdm("\tCodice Fiscale: " + cf);
							}
							if ((tipo != null) && (tipo.equals("ANA"))) { // anagrafica
								log.writeLogAdm("\tRichiesta aggiornamento dati");
								// gestisco la richiesta
								processRichiestaAggiornamento(argSourceBean);
							} else if (tipo.equals("MOD")) { // mob e l68
								try {
									// memorizzazione mob e l68

									SourceBean cat1Sb = (SourceBean) argSourceBean.getAttribute("LavL68.Ll68_Cat1");
									SourceBean cat2Sb = (SourceBean) argSourceBean.getAttribute("LavL68.Ll68_Cat2");

									String cat1 = cat1Sb.getCharacters();
									String cat2 = cat2Sb.getCharacters();

									// La gestione del collocamento mirato viena
									// fatta dal SIL e per questo
									// si commenta la parte in cui il SIL prende
									// in carico i dati
									// e li inserisce nel database senza
									// produrre risposte.
									/*
									 * if ((cat1!=null || cat2!=null) ) { processL68(argSourceBean); }
									 */

									SourceBean tipoListaMobSb = (SourceBean) argSourceBean
											.getAttribute("LAVMOB.LMOB_TIPOLISTA");

									String mob = tipoListaMobSb.getCharacters();

									if (mob != null) {
										processMob(argSourceBean);
									}

								} catch (Exception ex) {
									ex.printStackTrace();
									log.writeLogAdm("\t***Elaborazione fallita: " + ex.getMessage());
									log.writeLogAdm("\t***File di richiesta:");
									log.writeLogAdm("\t***" + argSourceBean.toXML(false));
								}

							}

							else if (tipo.equals("STATOOCC")) {
								log.writeLogAdm("\tPresa in carico variazione stato occupazionale");
								log.writeLogAdm("\t***Funzionalità non ancora implementata sul SIL");
							} else if (tipo.equals("CLA181")) {
								log.writeLogAdm("\tRichiesta stato occupazionale aggiornato");
								processCLA181(cf);
							} else if (tipo.equals("RIKIESTA")) {
								log.writeLogAdm("\tRichiesta codici fiscali modificati");
								log.writeLogUsr("Richiesta codici fiscali lavoratori modificati");
								processRichiestaCFAggiornati(argSourceBean);

							}

							log.writeLogAdm("Fine elaborazione " + filenames[i]);

						}
					} catch (Exception ex) {
						ex.printStackTrace();
						log.writeLogAdm("\t***Elaborazione fallita: " + ex.getMessage());
						log.writeLogAdm("\tProbabile file XML malformato");
						// System.out.println("Exception: "+ex.getMessage());
						// ex.printStackTrace();
					}
				}

			}
		};
		t.setPriority(Thread.MIN_PRIORITY);
		t.start();
		log.writeLogAdm("Demone partito (running)");
		try {
			t.join();
		} catch (InterruptedException ie) {
			ie.printStackTrace();
			log.writeLogAdm("Demone terminato (terminated)");
		}
	}

}
package it.eng.sil.util.amministrazione.impatti;

import java.io.IOException;
import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Vector;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.SessionContainer;
import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.dbaccess.DataConnectionManager;
import com.engiweb.framework.dbaccess.SQLStatements;
import com.engiweb.framework.dbaccess.Utils;
import com.engiweb.framework.dbaccess.sql.DataConnection;
import com.engiweb.framework.dbaccess.sql.DataField;
import com.engiweb.framework.dbaccess.sql.SQLCommand;
import com.engiweb.framework.dbaccess.sql.command.std.StoredProcedureCommand;
import com.engiweb.framework.dbaccess.sql.result.DataResult;
import com.engiweb.framework.dbaccess.sql.result.InformationDataResult;
import com.engiweb.framework.dbaccess.sql.result.PunctualDataResult;
import com.engiweb.framework.dbaccess.sql.result.ScrollableDataResult;
import com.engiweb.framework.error.EMFErrorSeverity;
import com.engiweb.framework.error.EMFInternalError;
import com.engiweb.framework.error.EMFUserError;

import it.eng.afExt.dbaccess.sql.DBKeyGenerator;
import it.eng.afExt.utils.DateUtils;
import it.eng.afExt.utils.FileUtils;
import it.eng.afExt.utils.MessageCodes;
import it.eng.afExt.utils.ResultSet2SourceBean;
import it.eng.afExt.utils.SourceBeanUtils;
import it.eng.afExt.utils.StringUtils;
import it.eng.sil.module.StepByStepMarchable;
import it.eng.sil.security.User;

/**
 * Classe per eseguire l'esportazione dei movimenti (migrazioni). Viene lanciato solo da applicativo (vedi modulo
 * opportuno). Al ritorno si possono usare i metodi di "is" per sapere in che stato si trova.
 * 
 * @author Luigi Antenucci
 */
public class EsportaMigrazioni implements StepByStepMarchable {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(EsportaMigrazioni.class.getName());

	private static final String thisClassName = StringUtils.getClassName(EsportaMigrazioni.class);

	private static final String TIMESTAMP_PATTERN_JAVA = "dd/MM/yyyy HH:mm:ss";
	private static final String DATA_MIGRAZIONE_INIZIALE = "01/01/1970 00:00:00";

	// Variabiili lette da file di configurazione xml sotto tag
	// "ESPORTAMIGRAZIONI"
	private String pool;
	private String savetoPath;
	/*
	 * savetoPath: percordo alla directory di base da cui scrivere i file generati (può essere assouluto in locale
	 * ("C:\ggexport\pas") oppure assoluto in rete "\\cassandra\condivisa")
	 */
	private String transposerFile;
	/*
	 * transposerFile: nome del file XML (completo di path) per la configurazione del transposer
	 * (esportaMigrazioniTrans.xml) Valore di default: "WEB-INF\conf\export\esportaMigrazioniTrans.xml"
	 */
	private String reportFile;
	/*
	 * reportFile: nome del file RPT di Crystal Report (nella dir WEB-INF/conf/report) da usare come template nella
	 * generazione dei file PDF. Valore di default: "esportaMigrazioni.rpt"
	 */
	private boolean useDefaultLog;
	private boolean statusToConsole;
	/*
	 * useDefaultLog: se a TRUE esegue il LOG sul normale LOG di applicativo statusToConsole: se a TRUE stampa un
	 * riepilogo su console durante l'elaborazione
	 */

	// Variabili di stato dell'elaborazione (lette dal DB)
	private String codProvinciaSil;
	private String descProvinciaSil; // descrizione del codice provincia
	/**
	 * TS_MIGRAZIONE.DTMLANCIO. Data di lancio della migrazione corrente. Se la migrazione prende avvio (passati i
	 * controlli) e si inserisce quindi un nuovo record nel db, rappresenta la data lancio della migrazione in atto,
	 * quindi = alla data di sistema
	 */
	private String dataLancio;
	/**
	 * Data ultima migrazione conclusa. Viene usata come data inizio estrazione, e naturalmente in fase di
	 * visualizzazione delle informazioni della migrazione conclusa.
	 */
	private String dataUltimaMigrazione;
	/*
	 * Necessaria per il controllo iniziale sulla data di avvio (controllo 6.1.3 modificato del documento di analisi)
	 */
	private String dataLancioUltimaMigrazione;
	private boolean flgInCorso;
	private boolean flgInvio;

	/**
	 * Data elaborazione della migrazione in corso. Puo' valere data di sistema (data lancio) o data fine anticipata se
	 * l'operatore l'ha valorizzata. Verra' registrata nel db come dtmElaborazione
	 */
	private String dataQuestaMigrazione;
	/**
	 * La data fine anticipata inserita dall'operatore, null se non inserita. Questa variabile si usa solo per capire se
	 * si sta eseguendo una migrazione anticipata o meno. Se anticipata scattano dei controlli ulteriori.
	 */
	private String dataFineAnticipataDaOperatore;

	// Connessione al DB del SIL (vedi costante POOL).
	private transient DataConnection connection;

	private int workInProgress;
	private static final int WIP_NOT_INIT = 0;
	private static final int WIP_INIT = 1;
	private static final int WIP_WORKING = 2;
	private static final int WIP_RES_OK = 3;
	private static final int WIP_RES_ERR = 4;

	// Esportatore output
	private EsportaMigrazioniOutput emOutput;
	// Creeremo un'istanza di "emOutput" sull'inizializzazione
	// e la teniamo "serializable" poiché non voglio che venga
	// ricaricata la configurazione del "transposer" a ogni passo.

	// Lista dei CPI da esportare (recuperati dalla prima query e usati
	// uno a uno per ciascun ciclo dell'iterazione sulla seconda query).
	private List listaCpi; // Lista di oggetti CpiElem
	private int indexCpi; // Indice (nella lista) del Cpi successivo (!)
	private int faseCpi; // Fase di esportazione (0=in temp; 1=txt; 2=pdf).
	private static final int MAX_FASE_CPI = 2;

	// Totali e statistiche
	private int numCpiElab; // il numero di cpi esportati in esportazione (nella
							// fase 1)
	private int numMovElab; // il numero di movimenti esportati (in tabella
							// d'appoggio)
	private long startTimeMillis; // istante di inizio elaborazione (in
									// millisec.)
	private long deltaTimeMillis; // se è >= 0, è il deltaT tra inizio e fine
									// elaboraz.

	// Variabili per tenere le info della precedente esecuzione (se fatta)
	// Disponibili solo dopo l'inizializzazione (non permangono nel tempo
	// poiché sono utili sono in fase di INFO e non nelle successive).
	private transient int precNumCpiElab;
	private transient int precNumMovimentiElab;
	private transient long precNumSecElaborazione;
	private transient String precFattaDaUtente;

	/**
	 * Costruttore
	 */
	public EsportaMigrazioni() {
		connection = null;
		emOutput = null;

		workInProgress = WIP_NOT_INIT;
	}

	/**
	 * Parte di inizializzazione dell'oggetto (lettura da file XML di config)
	 */
	private void initConfig(SourceBean moduleConfig) throws InstantiationException, IOException {

		workInProgress = WIP_NOT_INIT;

		// Inizializzo conteggio
		numMovElab = 0;
		numCpiElab = 0;
		startTimeMillis = 0; // per ora così
		deltaTimeMillis = -1;
		listaCpi = new ArrayList();
		indexCpi = 0;
		faseCpi = 0;
		precNumCpiElab = 0;
		precNumMovimentiElab = 0;
		precNumSecElaborazione = 0;
		precFattaDaUtente = "";

		if (moduleConfig == null) {
			throw new InstantiationException(
					"non trovo la busta XML chiamata CONFIG nella configurazione del modulo di esportazione delle migrazioni!");
		}

		/* === POOL === */
		pool = getConfigAttribObblig(moduleConfig, "POOL");

		/* === LOG === */
		useDefaultLog = getConfigAttribBool(moduleConfig, "LOG.USEDEFAULTLOG", false);
		statusToConsole = getConfigAttribBool(moduleConfig, "LOG.STATUSTOCONSOLE", false);

		/* === TRANSPOSER === */
		// Recupero nome file XML di configurazione del transposer
		transposerFile = getConfigAttribObblig(moduleConfig, "TRANSPOSER.FILE");
		transposerFile = FileUtils.getWithAbsolutePath(transposerFile);
		FileUtils.checkExistsFile(transposerFile);

		/* === REPORT CRYSTAL === */
		// Recupero nome file del report
		reportFile = getConfigAttribObblig(moduleConfig, "REPORT.FILE");
	}

	private String getConfigAttrib(SourceBean config, String attribute) throws InstantiationException {

		return SourceBeanUtils.getAttrStrNotNull(config, attribute);
	}

	private String getConfigAttribObblig(SourceBean config, String attribute) throws InstantiationException {

		String value = (String) config.getAttribute(attribute);
		if (StringUtils.isEmpty(value)) {
			throw new InstantiationException("non trovo " + attribute + " nel file di configurazione!");
		}
		return value;
	}

	private boolean getConfigAttribBool(SourceBean config, String attribute, boolean predefinito)
			throws InstantiationException {

		String value = (String) config.getAttribute(attribute);
		if ("TRUE".equalsIgnoreCase(value))
			return true;
		else if ("FALSE".equalsIgnoreCase(value))
			return false;
		else
			return predefinito;
	}

	/**
	 * Apre la connessione verso il DB
	 */
	private void openConnection() throws EMFInternalError {

		DataConnectionManager dataConnectionManager = DataConnectionManager.getInstance();
		connection = dataConnectionManager.getConnection(pool);
	}

	/**
	 * Chiude la connessione verso il DB
	 */
	private void closeConnection() {

		Utils.releaseResources(connection, null, null);
		connection = null;
	}

	/**
	 * Rende la query SQL dello statement col nome passato.
	 */
	private String getStatement(String statementName) {

		return SQLStatements.getStatement(statementName);
	}

	private BigDecimal prgMigr = null; // sequence del record corrente (usato
										// dalle memorizzaStatoEsportatoreXxx")

	private void memorizzaStatoEsportatorePrimaInizio() throws EMFUserError {

		writeLog("STATO: salvo info di stato prima di inizio (flagInCorso := S)");

		SQLCommand sqlCommand = null;
		DataResult dataResult = null;
		try {

			// Recupero dalla sessione l'utente che sta facendo l'operazione
			RequestContainer requestContainer = RequestContainer.getRequestContainer();
			SessionContainer session = requestContainer.getSessionContainer();
			// Recupero utente
			User user = (User) session.getAttribute(User.USERID);
			int cdnUtMigrazione = user.getCodut();

			// Genero nuovo sequence
			prgMigr = DBKeyGenerator.getNextSequence(pool, "S_TS_MIGRAZIONE");

			// Recupero Statement SQL per il salvataggio
			String sqlStatoInsert = getStatement("ESPORTA_MIGRAZIONI_STATO_INSERT_AT_INIZIO");

			sqlCommand = connection.createInsertCommand(sqlStatoInsert);

			ArrayList inputParameters = new ArrayList();
			DataField field;

			field = connection.createDataField("", Types.INTEGER, prgMigr);
			inputParameters.add(field);
			field = connection.createDataField("", Types.INTEGER, new BigDecimal(cdnUtMigrazione));
			inputParameters.add(field);
			field = connection.createDataField("", Types.VARCHAR, dataQuestaMigrazione);
			inputParameters.add(field);
			field = connection.createDataField("", Types.VARCHAR, dataLancio);
			inputParameters.add(field);
			dataResult = sqlCommand.execute(inputParameters);

			InformationDataResult infoResult = (InformationDataResult) dataResult.getDataObject();
			if (infoResult.getAffectedRows() == 0) {
				throw new Exception("Nessuna riga inserita");
			}
		} catch (Exception ex) {
			it.eng.sil.util.TraceWrapper.debug(_logger, this.getClass().getName() + "::service "
					+ "STATO: ERRORE, non sono riuscito a creare il record di LOCK (con flgInCorso=S)", ex);

			throw new EMFUserError(EMFErrorSeverity.BLOCKING, MessageCodes.EsportaMigrazioni.ERR_GIA_IN_CORSO);
		} finally {
			Utils.releaseResources(null, sqlCommand, dataResult);
		}
	}

	private void memorizzaStatoEsportatoreDopoFine() throws EMFUserError {

		writeLog("STATO: salvo info di stato a fine esecuzione (nuova data e statistici)");

		// IMPORTANTE: assegno nuova data di migrazione!
		// Savino 01/02/2006: non capisco perche' dato che il passo successivo
		// e' la rilettura da db delle info
		dataUltimaMigrazione = dataQuestaMigrazione;
		// Aggiorno statistiche
		deltaTimeMillis = System.currentTimeMillis() - startTimeMillis;

		if (prgMigr == null) {
			_logger.debug(this.getClass().getName() + "::service "
					+ "prgMigr nullo: nessun record di stato salvato: impossibile aggiornarlo");

			return;
		}

		SQLCommand sqlCommand = null;
		DataResult dataResult = null;
		try {

			// Uso il vecchio sequence "prgMigr"

			// Recupero Statement SQL per il salvataggio
			String sqlStatoUpdate = getStatement("ESPORTA_MIGRAZIONI_STATO_UPDATE_AT_FINE");

			sqlCommand = connection.createUpdateCommand(sqlStatoUpdate);

			ArrayList inputParameters = new ArrayList();
			DataField field;
			field = connection.createDataField("", Types.VARCHAR, dataQuestaMigrazione);
			inputParameters.add(field);
			field = connection.createDataField("", Types.INTEGER, new BigDecimal(getNumCpiElab()));
			inputParameters.add(field);
			field = connection.createDataField("", Types.INTEGER, new BigDecimal(getNumMovimentiElab()));
			inputParameters.add(field);
			field = connection.createDataField("", Types.INTEGER, new BigDecimal(getNumSecElaborazione()));
			inputParameters.add(field);
			field = connection.createDataField("", Types.INTEGER, prgMigr);
			inputParameters.add(field);

			dataResult = sqlCommand.execute(inputParameters);

			InformationDataResult infoResult = (InformationDataResult) dataResult.getDataObject();
			if (infoResult.getAffectedRows() == 0) {
				throw new Exception("Nessuna riga modificata");
			}
		} catch (Exception ex) {
			it.eng.sil.util.TraceWrapper.debug(_logger, this.getClass().getName() + "::service "
					+ "STATO: ERRORE, non sono riuscito a aggiornare il record di LOCK (con flgInCorso=N)", ex);

			throw new EMFUserError(EMFErrorSeverity.BLOCKING, MessageCodes.EsportaMigrazioni.ERR_CANT_UPDATE_STATUS);
		} finally {
			Utils.releaseResources(null, sqlCommand, dataResult);
		}
	}

	private void cancellaStatoEsportatoreSuErrore() {

		writeLog("STATO: rimuovo ultime info di stato dopo errore");
		if (prgMigr == null) {
			_logger.debug(this.getClass().getName() + "::service "
					+ "prgMigr nullo: nessun record di stato salvato: impossibile cancellarlo");

			return;
		}

		SQLCommand sqlCommand = null;
		DataResult dataResult = null;
		try {

			// Uso il vecchio sequence "prgMigr"

			// Recupero Statement SQL per la cancellazione
			String sqlStatoDelete = getStatement("ESPORTA_MIGRAZIONI_STATO_DELETE_ON_ERROR");

			sqlCommand = connection.createUpdateCommand(sqlStatoDelete);

			ArrayList inputParameters = new ArrayList();
			DataField field = connection.createDataField("", Types.INTEGER, prgMigr);
			inputParameters.add(field);

			dataResult = sqlCommand.execute(inputParameters);
		} catch (Exception ex) {
			it.eng.sil.util.TraceWrapper.debug(_logger, this.getClass().getName() + "::service "
					+ "STATO: ERRORE, non sono riuscito a cancellare il record di LOCK", ex);

		} finally {
			Utils.releaseResources(null, sqlCommand, dataResult);
		}
	}

	private void svuotaTabellaTemporanea() throws EMFUserError {
		SQLCommand sqlCommand = null;
		DataResult dataResult = null;
		try {
			// Recupero Statement SQL per il salvataggio
			String sqlDelTemp = getStatement("ESPORTA_MIGRAZIONI_CANC_TABELLA_TEMP");
			sqlCommand = connection.createDeleteCommand(sqlDelTemp);
			dataResult = sqlCommand.execute();
		} catch (Exception ex) {
			it.eng.sil.util.TraceWrapper.debug(_logger, this.getClass().getName() + "::service "
					+ "STATO: ERRORE, non sono riuscito a svuotare la tabella temporanea", ex);

			throw new EMFUserError(EMFErrorSeverity.BLOCKING, MessageCodes.EsportaMigrazioni.ERR_CANT_UPDATE_STATUS);
		} finally {
			Utils.releaseResources(null, sqlCommand, dataResult);
		}
	}

	private void recuperaStatoEsportatore() throws Exception {

		writeLog("STATO: recupero info di stato");

		SQLCommand sqlCommand = null;
		DataResult dataResult = null;

		// PRIMO PASSO: recupero il CPI da TS_GENERALE
		try {
			String sqlStatoSelect = getStatement("ESPORTA_MIGRAZIONI_STATO_SELECT_STATO#1");

			sqlCommand = connection.createSelectCommand(sqlStatoSelect);
			dataResult = sqlCommand.execute();
			ScrollableDataResult sdr = (ScrollableDataResult) dataResult.getDataObject();
			if (!sdr.hasRows()) {
				throw new Exception("Nessuna riga recuperata!");
			}

			SourceBean row = sdr.getDataRow().getSourceBean();
			codProvinciaSil = (String) row.getAttribute("CODPROVINCIASIL");
			descProvinciaSil = (String) row.getAttribute("DESCPROVINCIASIL");

			// ORA LA RECUPERO DAL DB E NON DAL FILE XML:
			savetoPath = SourceBeanUtils.getAttrStrNotNull(row, "SAVETOPATH");

			savetoPath = FileUtils.appendFileSeparator(savetoPath);
			savetoPath = FileUtils.getWithAbsolutePath(savetoPath);
			// Il controllo di esistenza è spostato sull'INIZIO dell'esportatore
		} catch (EMFUserError ue) {
			throw ue;
		} catch (Exception ex) {
			throw new EMFUserError(EMFErrorSeverity.BLOCKING, MessageCodes.EsportaMigrazioni.ERR_CANT_GET_STATUS);
		} finally {
			Utils.releaseResources(null, sqlCommand, dataResult);
		}

		// SECONDO PASSO: recupero ULTIMA-DATA e FLG-IN-CORSO da TS_MIGRAZIONI
		sqlCommand = null;
		dataResult = null;
		try {
			String sqlStatoSelect = getStatement("ESPORTA_MIGRAZIONI_STATO_SELECT_STATO#2");

			sqlCommand = connection.createSelectCommand(sqlStatoSelect);
			dataResult = sqlCommand.execute();
			ScrollableDataResult sdr = (ScrollableDataResult) dataResult.getDataObject();
			SourceBean row;
			if (sdr.hasRows()) { // NB: se c'è prendo solo il PRIMO RECORD!
				row = sdr.getDataRow().getSourceBean();
			} else {
				writeLog("STATO: nessuna precedente migrazione --> uso dataUltimaMigrazione=[" + dataUltimaMigrazione
						+ "]");
				row = new SourceBean("row");
			}
			// Recupero i valori da ROW
			// (Se non caricato, usa i valori di default, tra cui la data
			// ancestrale e flag in corso "N")

			dataUltimaMigrazione = SourceBeanUtils.getAttrStr(row, "DATAULTIMAMIGRAZIONE", DATA_MIGRAZIONE_INIZIALE);
			dataLancio = SourceBeanUtils.getAttrStr(row, "DATALANCIO", DATA_MIGRAZIONE_INIZIALE);
			flgInCorso = SourceBeanUtils.getAttrBoolean(row, "FLGINCORSO", false);
			flgInvio = SourceBeanUtils.getAttrBoolean(row, "FLGINVIO", true);
			// vedere metodo controlliDaFareSullInizio()
			dataLancioUltimaMigrazione = dataLancio;

			// Recupero anche le vecchie info di stato (statistiche)
			precNumCpiElab = SourceBeanUtils.getAttrInt(row, "NUMCPIELAB", 0);
			precNumMovimentiElab = SourceBeanUtils.getAttrInt(row, "NUMMOVIMENTIELAB", 0);
			precNumSecElaborazione = SourceBeanUtils.getAttrLong(row, "NUMSECONDIELABORAZIONE", 0);
			precFattaDaUtente = SourceBeanUtils.getAttrStrNotNull(row, "PRECFATTADAUTENTE");
		} catch (Exception ex) {
			throw new EMFUserError(EMFErrorSeverity.BLOCKING, MessageCodes.EsportaMigrazioni.ERR_CANT_GET_STATUS);
		} finally {
			Utils.releaseResources(null, sqlCommand, dataResult);
		}

		writeLog("codProvinciaSil      = [" + codProvinciaSil + "]");
		writeLog("descProvinciaSil     = [" + descProvinciaSil + "]");
		writeLog("dataUltimaMigrazione = [" + dataUltimaMigrazione + "]");
		writeLog("dataLancio           = [" + dataLancio + "]");
		writeLog("flgInCorso           = [" + flgInCorso + "]");
		writeLog("flgInvio             = [" + flgInvio + "]");
	}

	/**
	 * Controlli addizionali da farsi al momento dell'INIZIO esportazione. Se non si passano i controlli, viene lanciata
	 * un'eccezione.
	 */
	private void controlliDaFareSullInizio() throws EMFUserError {

		// Controllo che flgInCorso *non* sia TRUE (se così, annullo
		// esportazione)
		if (flgInCorso) {
			throw new EMFUserError(EMFErrorSeverity.BLOCKING, MessageCodes.EsportaMigrazioni.ERR_GIA_IN_CORSO);
		}

		// Controllo che flgInvio *non* sia FALSE (se così, annullo
		// esportazione)
		if (!flgInvio) {
			throw new EMFUserError(EMFErrorSeverity.BLOCKING, MessageCodes.EsportaMigrazioni.ERR_NON_ANCORA_INVIATI);
		}
		// si deve controllare se la precedente esecuzione abbia utilizzato o
		// meno la data fine impostata dall'operatore
		String parteGiornoLancio1 = dataLancioUltimaMigrazione.substring(0, 10);
		// Controllo che dataQuestaMigrazione e dataUltimaMigrazione
		// non siano uguali nella parte gg/mm/aaaa.
		String parteGiorno1 = dataLancio.substring(0, 10); // a questo punto
															// vale la data di
															// sistema
		String parteGiorno2 = dataUltimaMigrazione.substring(0, 10);
		if (parteGiornoLancio1.equals(parteGiorno2) && parteGiorno1.equals(parteGiorno2)) {
			// stiamo lanciando la migrazione nello stesso giorno dell' ultima
			// volta.
			// Notare che non e' possibile eseguire una migrazione impostando la
			// data fine = data di sistema,
			// mentre potremmo avere le date di lancio coincidenti ma data
			// ultima migrazione diversa.
			Vector paramV = new Vector(1);
			paramV.add(parteGiorno1);
			throw new EMFUserError(EMFErrorSeverity.BLOCKING, MessageCodes.EsportaMigrazioni.ERR_GIA_FATTO_IN_DATA,
					paramV);
		}

		// Controllo di esistenza del "savetoPath"
		try {
			FileUtils.checkExistsPath(savetoPath); // genera eccezione se non
													// esiste
		} catch (Exception ex) {
			Vector paramV = new Vector(1);
			paramV.add(savetoPath);
			throw new EMFUserError(EMFErrorSeverity.BLOCKING, MessageCodes.EsportaMigrazioni.ERR_PATH_NOT_DEFINED,
					paramV);
		}

		// Controllo che non ci siano file dentro "savetoPath" (o sue sottodir)
		try {
			FileUtils.checkNoFilesInPath(savetoPath, 1); // genera eccezione
															// se almeno un file
															// esiste
		} catch (Exception ex) {
			Vector paramV = new Vector(1);
			paramV.add(savetoPath);
			throw new EMFUserError(EMFErrorSeverity.BLOCKING, MessageCodes.EsportaMigrazioni.ERR_PATH_NOT_EMPTY,
					paramV);
		}
		// Savino 01/02/2006: controllo sull'eventuale data fine anticipata
		// inserita dall'operatore
		java.util.Date dQuestaMigrazione, dUltimaMigrazione, dSistema;
		if (!(dataFineAnticipataDaOperatore == null)) {
			// il controllo lo faccio solo se l'utente ha inserito la data fine
			// elaborazione (anticipata)
			try {
				SimpleDateFormat df = DateUtils.getSimpleDateFormatFixBugMem(TIMESTAMP_PATTERN_JAVA);
				dQuestaMigrazione = df.parse(dataQuestaMigrazione);
				dUltimaMigrazione = df.parse(dataUltimaMigrazione);
				dSistema = df.parse(dataLancio);
			} catch (Exception e) {
				throw new EMFUserError(EMFErrorSeverity.BLOCKING, MessageCodes.General.OPERATION_FAIL);
			}
			if (!(dQuestaMigrazione.after(dUltimaMigrazione))) {
				Vector paramV = new Vector(2);
				try {
					paramV.add(dataQuestaMigrazione);
					paramV.add(dataUltimaMigrazione);
				} catch (Exception e1) {
				}
				throw new EMFUserError(EMFErrorSeverity.BLOCKING,
						MessageCodes.EsportaMigrazioni.ERR_DATA_FINE_LT_ULTIMA, paramV);
			}
			if (!(dQuestaMigrazione.before(dSistema))) {
				Vector paramV = new Vector(1);
				try {
					paramV.add(dataQuestaMigrazione);
					paramV.add(dataLancio);
				} catch (Exception e1) {
				}
				throw new EMFUserError(EMFErrorSeverity.BLOCKING,
						MessageCodes.EsportaMigrazioni.ERR_DATA_FINE_GT_SYSTEM, paramV);
			}
		}

	}

	/**
	 * Completa lo stato dell'esportatore. Chiamata in fase di INIZIO.
	 */
	private void completaStatoEsportatore() throws EMFUserError {

		// Imposto (e memorizzo) la data di INIZIO sincronizzazione: ossia
		// "ADESSO".
		Date adesso = new Date();
		// Savino 01/02/2006: gestione campo data fine ultima migrazione
		// (eventualmente inserito dall'operatore)
		DateFormat dateFormat = DateUtils.getSimpleDateFormatFixBugMem(TIMESTAMP_PATTERN_JAVA);
		String dataDiSistema = dateFormat.format(adesso);
		dataLancio = dataDiSistema;
		try {
			String dataFineUltimaMigrazione = (String) RequestContainer.getRequestContainer().getServiceRequest()
					.getAttribute("dataFineUltimaMigrazione");
			if (dataFineUltimaMigrazione == null || dataFineUltimaMigrazione.equals("")) {
				dataQuestaMigrazione = dataDiSistema;
				dataLancio = dataDiSistema;
				// ha una sola funzione: stabilire se l'utente ha inserito o
				// meno la data fine migrazione
				dataFineAnticipataDaOperatore = null;
			} else {
				dataQuestaMigrazione = dataFineUltimaMigrazione + " 23:59:59";
				dataFineAnticipataDaOperatore = dataFineUltimaMigrazione;
			}
		} catch (Exception e) {
			throw new EMFUserError(EMFErrorSeverity.BLOCKING, MessageCodes.EsportaMigrazioni.ERR_CANT_INITIALIZE);
		}
		// Delego la scrittura dei dati all'oggetto "EsportaMigrazioniOutput"
		try {
			Date dQuestaMigrazione = dateFormat.parse(dataQuestaMigrazione);
			emOutput = new EsportaMigrazioniOutput(savetoPath, false, dQuestaMigrazione, transposerFile, reportFile,
					useDefaultLog, statusToConsole);
		} catch (Exception ex) {
			throw new EMFUserError(EMFErrorSeverity.BLOCKING, MessageCodes.EsportaMigrazioni.ERR_CANT_INITIALIZE);
		}
		writeLog("dataQuestaMigrazione = [" + dataQuestaMigrazione + "]");
	}

	/**
	 * Metodo chiamato durante l'esecuzione della fase di INIZIO. Imposta la partenza (INIZIO) dell'esecutore. Esegue i
	 * controlli di partenza: - dataQuestaMigrazione <> dataUltimaMigrazione - flgInCorso = false Se controlli falliti,
	 * lancia eccezione con messaggio. Inoltre prepara l'esecutore all'avvio (inizializza variabili statistiche).
	 */
	public void callbackOnInizioMarchable() throws EMFUserError {

		try {
			openConnection();

			completaStatoEsportatore(); // imposta dati e oggetti usati nel
										// futuro ciclo

			controlliDaFareSullInizio(); // Controlli addizionali sull'INIZIO

			recuperaTuttiCpi(); // recupera tutti i CPI da usare.

			// Creo nuovo record con "S" in flgInCorso (LOCK dell'esportazione!)
			memorizzaStatoEsportatorePrimaInizio();

			// Cancello la tabella temporanea che conterrà i dati recuperati
			svuotaTabellaTemporanea();
		} catch (EMFUserError eu) {
			throw eu;
		} catch (Exception ex) {
			it.eng.sil.util.TraceWrapper.debug(_logger, this.getClass().getName() + "::service ", ex);

			throw new EMFUserError(EMFErrorSeverity.BLOCKING, MessageCodes.EsportaMigrazioni.ERR_DURANTE_INIZIO);
		} finally {
			closeConnection();
		}

		startTimeMillis = System.currentTimeMillis(); // inizializza variabili
														// statistiche
		deltaTimeMillis = -1;
	}

	/**
	 * Metodo chiamato al termine dell'esecuzione dell'ULTIMA SUCC. Se ho elaborato l'ultimo CPI della lista (non ce ne
	 * sono più), imposto il nuovo TIME-STAMP di ultima modifica con la data di OGGI (per l'esattezza, uso la
	 * dataQuestaMigrazione!) e quindi memorizzerà questo nuovo stato di elaborazione.
	 */
	public void callbackOnLastSuccMarchable() throws EMFUserError {

		try {
			openConnection();

			memorizzaStatoEsportatoreDopoFine();

			writeLog("Riepilogo Operazione Fatta:");
			writeLog(" >> Numero Totale Movimenti Esportati: " + numMovElab);
			writeLog(" >> Numero Totale CPI Elaborati: " + numCpiElab);
			writeLog("Operazione Terminata");

			workInProgress = WIP_RES_OK;
		} catch (EMFUserError eu) {
			throw eu;
		} catch (Exception ex) {
			it.eng.sil.util.TraceWrapper.debug(_logger, this.getClass().getName() + "::service ", ex);

			throw new EMFUserError(EMFErrorSeverity.BLOCKING, MessageCodes.EsportaMigrazioni.ERR_DURANTE_INIZIO);
		} finally {
			closeConnection();
		}
	}

	/**
	 * Metodo chiamato in caso di errore durante una SUCC. può essere utile per annullare dei "lock" iniziali o
	 * riportare l'errore. per altre vie. Viene passata l'eccezione che si è avuta. può essere utile o no.
	 */
	public void callbackOnErrorMarchable(Exception e) {
		try {
			openConnection();

			cancellaStatoEsportatoreSuErrore();
		} catch (Exception ignored) {
		} finally {
			closeConnection();
		}
	}

	private void writeLog(String str) {
		if (useDefaultLog) {
			_logger.debug(str);

		}
	}

	private void writeOut(String str) {
		if (statusToConsole) {
			System.out.print(str);
		}
	}

	/**
	 * Inizializza l'esportatore, caricando la configurazione dai file XML e i parametri dal database. Esegue in
	 * sequenza le seguenti chiamate: - initConfig(); - openConnection(); - recuperaStatoEsportatore(); -
	 * closeConnection(); Utile anche per sapere in anticipo i parametri della futura esportazione.
	 */
	public void initMarchable(SourceBean moduleConfig) throws EMFUserError {

		workInProgress = WIP_NOT_INIT;
		try {

			try {
				initConfig(moduleConfig); // inizializzazione (lettura file
											// XML)

				openConnection(); // Apre connessione
			} catch (Exception ex) {
				it.eng.sil.util.TraceWrapper.debug(_logger,
						this.getClass().getName() + "::service " + "ERRORE DURANTE L'INIZIALIZZAZIONE!\nMESSAGGIO: "
								+ ex.getMessage() + "\nESECUZIONE ABORTITA!",
						ex);

				throw ex;
			}

			try {
				recuperaStatoEsportatore(); // lettura info da DB
				workInProgress = WIP_INIT; // Ok, inizializzato!
			} catch (Exception ex) {
				it.eng.sil.util.TraceWrapper.debug(_logger,
						this.getClass().getName() + "::service "
								+ "ERRORE DURANTE IL RECUPERO DELLO STATO!\nMESSAGGIO: " + ex.getMessage()
								+ "\nESECUZIONE ABORTITA!",
						ex);

				throw ex;
			} finally {
				closeConnection(); // Chiude connessione (in ogni caso!)
			}
		} catch (EMFUserError ue) {
			throw ue;
		} catch (Exception ex) {
			throw new EMFUserError(EMFErrorSeverity.BLOCKING, MessageCodes.EsportaMigrazioni.ERR_CANT_INITIALIZE);
		}
	}

	/**
	 * Interna. Recupera tutti i CPI per cui creare i file. Questi CPI saranno usati dalla "doNextStepMarchable()".
	 */
	private void recuperaTuttiCpi() throws Exception {

		PreparedStatement statement = null;
		ResultSet rs = null;

		try {

			// Recupero Statement SQL per il recupero dei record
			String sqlRecuperaTuttiCPI = getStatement("ESPORTA_MIGRAZIONI_RECUPERA_TUTTI_CODCPI");

			Connection intConnection = connection.getInternalConnection();

			statement = intConnection.prepareStatement(sqlRecuperaTuttiCPI, ResultSet.TYPE_FORWARD_ONLY,
					ResultSet.CONCUR_READ_ONLY);
			statement.setString(1, codProvinciaSil);
			statement.setString(2, dataUltimaMigrazione);
			// lettura cpi ESPORTA_MIGRAZIONI_RECUPERA_TUTTI_CODCPI
			statement.setString(3, dataQuestaMigrazione);

			rs = statement.executeQuery();

			listaCpi.clear(); // Svuoto la lista
			indexCpi = 0;
			faseCpi = 0;
			// Riempio la lista di quelli da fare
			while (rs.next()) {

				String codCpi = rs.getString("CODCPI");
				String siglaProvCpi = rs.getString("SIGLAPROVCPI");
				String email = rs.getString("EMAIL");
				String codTipoFile = rs.getString("CODTIPOFILE");

				// Memorizzo informazioni in un elemento e lo metto in lista
				CpiElem cpiElem = new CpiElem(codCpi, siglaProvCpi, email, codTipoFile);
				listaCpi.add(cpiElem);
			}
			writeLog("Recuperati " + listaCpi.size() + " codici CPI");
		} catch (Exception ex) {
			it.eng.sil.util.TraceWrapper.debug(_logger, this.getClass().getName() + "::service "
					+ "ERRORE DURANTE IL RECUPERO DI TUTTI I CODICI CPI DA USARE!", ex);

			ex.printStackTrace();
			throw ex;
		} finally {
			// Chiude tutto
			if (rs != null)
				try {
					rs.close();
				} catch (SQLException e) {
				}
			if (statement != null)
				try {
					statement.close();
				} catch (SQLException e) {
				}
		}
	}

	/**
	 * Rende una stringa che identifica il prossimo CPI che verrà esportato dal metodo "doNextStepMarchable()" Utile per
	 * informare l'utente. NB: non genera eccezione; se non c'è alcun CPI rende una stringa vuota!
	 */
	public String getInfoCpiSucc() {
		try {
			// Recupero il primo elemento della lista (se esiste).
			CpiElem cpiElem = (CpiElem) listaCpi.get(indexCpi);
			String f = "";
			switch (faseCpi) {
			case 0:
				f = "esporto ";
				break;
			case 1:
				f = "genero TXT per ";
				break;
			case 2:
				f = "genero PDF per ";
				break;
			}
			return f + cpiElem.getCodCpi() + " (" + cpiElem.getSiglaProvCpi() + ")";
		} catch (IndexOutOfBoundsException ioobe) {
			return "";
		}
	}

	/**
	 * Rende la percentuale di elaborazione fatta.
	 */
	public double getProgPercMarchable() {

		int cur = getNumQuantiElab();
		int tot = cur + getNumQuantiRimasti();

		if (tot == 0)
			return 100;
		else
			return ((double) (cur * 100)) / ((double) tot);
	}

	/**
	 * Rende TRUE se c'è almeno un CPI ancora da esportare col metodo "doNextStepMarchable()". Serve per controllare se
	 * si è terminata l'elaborazione.
	 */
	public boolean hasNextStepMarchable() {
		return (faseCpi <= MAX_FASE_CPI) && (listaCpi.size() > 0);
	}

	/**
	 * Elabora tutti i record del CPI successivo. può essere invocato solo dopo una "initMarchable()". In caso di
	 * errore, genera un eccezione.
	 */
	public void doNextStepMarchable() throws EMFUserError {

		if (workInProgress == WIP_NOT_INIT) {
			// Non e' mai stato inizializzato l'oggetto: usare il metodo
			// initMarchable()
			throw new EMFUserError(EMFErrorSeverity.BLOCKING, MessageCodes.EsportaMigrazioni.ERR_NO_NEXT_CPI);
		}
		workInProgress = WIP_WORKING; // Attendere, prego!

		// Recupero (rimuovendolo) il primo elemento della lista (se esiste).
		CpiElem cpiElem;
		try {
			// Prendo il corrente
			cpiElem = (CpiElem) listaCpi.get(indexCpi);
		} catch (IndexOutOfBoundsException ioobe) {
			// Non ci sono ulteriori CPI da esportare!
			throw new EMFUserError(EMFErrorSeverity.BLOCKING, MessageCodes.EsportaMigrazioni.ERR_NO_NEXT_CPI);
		}

		try {

			openConnection();

			switch (faseCpi) {
			case 0:
				// FASE 0: esporto da DB a tabella di APPOGGIO
				doEsportaInTabAppoggioPerCpi(cpiElem);
				break;
			case 1:
				// FASE 1: genero TXT da tabella di APPOGGIO
				doGeneraTxtDaAppoggioPerCpi(cpiElem);
				break;
			case 2:
				// FASE 2: genero PDF da tabella di APPOGGIO
				doGeneraPdfDaAppoggioPerCpi(cpiElem);
				break;
			}
			// Il controllo sull'ultimo è fatto con la "callback"
		} catch (EMFUserError eu) {
			it.eng.sil.util.TraceWrapper.debug(_logger,
					this.getClass().getName() + "::service "
							+ "ERRORE DURANTE L'ESPORTAZIONE RELATIVA AL CPI DI CODICE [" + cpiElem.getCodCpi() + "]!",
					(Exception) eu);

			workInProgress = WIP_RES_ERR;
			throw eu;
		} catch (Exception ex) {
			it.eng.sil.util.TraceWrapper.debug(_logger,
					this.getClass().getName() + "::service "
							+ "ERRORE DURANTE L'ESPORTAZIONE RELATIVA AL CPI DI CODICE [" + cpiElem.getCodCpi() + "]!",
					ex);

			workInProgress = WIP_RES_ERR;

			int errCode = MessageCodes.EsportaMigrazioni.ERR_DURANTE_ESPORTA_CPI_F0;
			switch (faseCpi) {
			case 1:
				errCode = MessageCodes.EsportaMigrazioni.ERR_DURANTE_ESPORTA_CPI_F1;
				break;
			case 2:
				errCode = MessageCodes.EsportaMigrazioni.ERR_DURANTE_ESPORTA_CPI_F2;
				break;
			}
			Vector paramV = new Vector(1);
			paramV.add(cpiElem.getCodCpi());
			throw new EMFUserError(EMFErrorSeverity.BLOCKING, errCode, paramV);
		} finally {
			closeConnection();
		}

		// Passo al successivo per le prossime richieste
		indexCpi++;
		if (indexCpi == listaCpi.size()) { // Se esco, riparto da 0 passando
											// alla fase successiva
			indexCpi = 0;
			faseCpi++;
		}

	}

	/**
	 * Rende TRUE se l'esportatore è in fase di esecuzione dell'operazione di esportazione dati (esecuzione query o
	 * scrittura risultati sui file); rende FALSE quando non sta facendo nulla (operazione non ancora cominciata o
	 * terminata correttamente o abortita).
	 */
	public boolean isWorkInProgress() {
		return (workInProgress == WIP_WORKING);
	}

	/**
	 * Rende TRUE se l'esportatore è stato inizializzato (appena prima o già da tempo).
	 */
	public boolean isInitialized() {
		return (workInProgress != WIP_NOT_INIT);
	}

	/**
	 * Rende TRUE se l'esportatore ha terminato l'esecuzione e non si sono verificati errori bloccanti (ossia
	 * l'operazione è stata eseguita).
	 */
	public boolean isEndedOk() {
		return (workInProgress == WIP_RES_OK);
	}

	/**
	 * Rende TRUE se l'esportatore ha terminato l'esecuzione MA non ha portato a termine l'operazione (e probabilmente
	 * non l'ha proprio cominciata).
	 */
	public boolean isEndedWithError() {
		return (workInProgress == WIP_RES_ERR);
	}

	/**
	 * Rende il codice della provincia in uso. può essere chiamato solo DOPO l'invocazione del metodo "initMarchable()"
	 * o, in ogni caso, al termine dell'elaborazione di "doNextStepMarchable()".
	 */
	public String getCodProvinciaSil() {
		return codProvinciaSil;
	}

	/**
	 * Rende la descrizione del codice della provincia in uso. può essere chiamato solo DOPO l'invocazione del metodo
	 * "initMarchable()" o, in ogni caso, al termine dell'elaborazione di "doNextStepMarchable()".
	 */
	public String getDescProvinciaSil() {
		return descProvinciaSil;
	}

	/**
	 * Rende la data dell'ultima migrazione. può essere chiamato solo DOPO l'invocazione del metodo "initMarchable()" o,
	 * in ogni caso, al termine dell'elaborazione di "doNextStepMarchable()".
	 */
	public String getDataUltimaMigrazione() {
		return dataUltimaMigrazione;
	}

	public String getDataLancio() {
		return dataLancio;
	}

	/**
	 * Rende true se la "dataUltimaMigrazione" non è quella primordiale (01/01/1970).
	 */
	public boolean isNeverDoneMigr() {
		return DATA_MIGRAZIONE_INIZIALE.equals(dataUltimaMigrazione);
	}

	/**
	 * Rende la data di questa migrazione, ovviamente da usare solo DOPO che è stata avviata l'esportazione oppure al
	 * suo termine (ma non prima!). può essere chiamato solo DOPO l'invocazione del metodo "initMarchable()" o, in ogni
	 * caso, al termine dell'elaborazione di "doNextStepMarchable()".
	 */
	public String getDataQuestaMigrazione() {
		return dataQuestaMigrazione;
	}

	public boolean getFlgInvio() {
		return flgInvio;
	}

	public boolean getFlgInCorso() {
		return flgInCorso;
	}

	/**
	 * Esegue l'esportazione delle migrazioni per il SINGOLO CPI dato.
	 */
	private void doEsportaInTabAppoggioPerCpi(CpiElem cpiElem) throws Exception {

		writeLog("doEsportaMigrazioniPerCpi con codCpi=[" + cpiElem.getCodCpi() + "]");

		// --------------------------------------------------------------------
		// NOTA MOLTO BENE: SE NON DEVO PRODURRE NE' UN FILE TXT NE' UN PDF,
		// E' ASSOLUTAMENTE INUTILE RECUPERARE I DATI!!!
		// --------------------------------------------------------------------
		emOutput.initForCpi(cpiElem);
		if (!emOutput.isEnabledTxt() && !emOutput.isEnabledPdf()) {
			_logger.debug(
					this.getClass().getName() + "::service " + "Non recupero i dati per il CPI " + cpiElem.getCodCpi()
							+ " poiché esso non ha abilitata la generazione nè del file TXT nè del PDF!!!");

			return;
		}

		StoredProcedureCommand command = null;
		DataResult dr = null;

		try {

			String statement = getStatement("ESPORTA_MIGRAZIONI_CPI_IN_APPOGGIO");

			command = (StoredProcedureCommand) connection.createStoredProcedureCommand(statement);

			// imposto i parametri
			List parameters = new ArrayList(4);
			parameters.add(connection.createDataField("result", Types.BIGINT, null));
			command.setAsOutputParameters(0);
			parameters.add(connection.createDataField("codCpi", Types.VARCHAR, cpiElem.getCodCpi()));
			command.setAsInputParameters(1);
			parameters.add(connection.createDataField("datUlt", Types.VARCHAR, dataUltimaMigrazione));
			command.setAsInputParameters(2);
			// chiamata procedure ESPORTA_MIGRAZIONI_CPI_IN_APPOGGIO
			parameters.add(connection.createDataField("datQue", Types.VARCHAR, dataQuestaMigrazione));
			command.setAsInputParameters(3);

			dr = command.execute(parameters); // puo' generare
												// EMFInternalError

			// VALORE DI RITORNO (result)
			PunctualDataResult pdr = (PunctualDataResult) dr.getDataObject();
			DataField df = pdr.getPunctualDatafield();
			long numScrittiOra = ((Number) df.getObjectValue()).longValue();

			numMovElab += numScrittiOra;
			numCpiElab++;
			// Tutto OK

		} catch (EMFInternalError emfi) {
			it.eng.sil.util.TraceWrapper.debug(_logger, this.getClass().getName() + "::service ", (Exception) emfi);

			throw emfi;
		} finally {
			// Chiudo tutto
			Utils.releaseResources(null, command, dr);
		}

	}

	/**
	 * Genera i file di tipo TXT per il SINGOLO CPI dato. Il file di testo viene generato solo se abilitata per il CPI
	 * in esame. I dati vengono letti dalla tabella di appoggio. Per la mappatura in una riga di testo si usa il
	 * Transposer.
	 */
	private void doGeneraTxtDaAppoggioPerCpi(CpiElem cpiElem) throws Exception {

		emOutput.initForCpi(cpiElem);
		if (!emOutput.isEnabledTxt()) {
			_logger.debug(this.getClass().getName() + "::service " + "CPI con codice =[" + cpiElem.getCodCpi()
					+ "] non abilitato per generazione file TXT");

			return;
		}

		PreparedStatement statement = null;
		ResultSet rs = null;

		try {

			// Recupero Statement SQL per il recupero dei record
			String sqlEsportaMigr = getStatement("ESPORTA_MIGRAZIONI_CPI_DA_APPOGGIO");

			// uso l'InternalConnection e i ResultSet al posto del caro vecchio
			// Framework:
			Connection intConnection = connection.getInternalConnection();

			statement = intConnection.prepareStatement(sqlEsportaMigr, ResultSet.TYPE_FORWARD_ONLY,
					ResultSet.CONCUR_READ_ONLY);

			statement.setString(1, cpiElem.getCodCpi()); // Uso il CODICE CPI
															// passato!

			rs = statement.executeQuery();

			ResultSet2SourceBean rs2sb = new ResultSet2SourceBean(connection, rs);

			emOutput.openOutputFileTxt();

			while (rs.next()) {
				try {

					SourceBean row = rs2sb.getSourceBean();

					BigDecimal prgMovimento = (BigDecimal) row.getAttribute("PRGMOVIMENTO");
					writeLog("Elaborazione movimento con prgMovimento=["
							+ ((prgMovimento == null) ? "?" : prgMovimento.toString()) + "]");

					emOutput.appendOutputFileTxt(row);
				} catch (Exception ex) {
					it.eng.sil.util.TraceWrapper.debug(_logger, this.getClass().getName() + "::service ", ex);

				}
			}
		} catch (Exception ex) {
			it.eng.sil.util.TraceWrapper.debug(_logger, this.getClass().getName() + "::service ", ex);

			throw ex;
		} finally {
			// Chiude tutto, file TXT compreso
			emOutput.closeOutputFileTxt();

			if (rs != null)
				try {
					rs.close();
				} catch (SQLException e) {
				}
			if (statement != null)
				try {
					statement.close();
				} catch (SQLException e) {
				}
		}
	}

	/**
	 * Genera i file di tipo PDF per il SINGOLO CPI dato. Il file di testo viene generato solo se abilitata per il CPI
	 * in esame. I dati vengono letti dalla tabella di appoggio. Per la generazione del PDF si usa il caro vecchio e
	 * buon Crystal.
	 */
	private void doGeneraPdfDaAppoggioPerCpi(CpiElem cpiElem) throws Exception {

		emOutput.initForCpi(cpiElem);
		if (!emOutput.isEnabledPdf()) {
			_logger.debug(this.getClass().getName() + "::service " + "CPI con codice =[" + cpiElem.getCodCpi()
					+ "] non abilitato per generazione file PDF");

			return;
		}

		emOutput.generateFullOutputFilePdf(connection);
	}

	/**
	 * Rende il numero di CPI già elaborati dalla sola fase 1.
	 */
	public int getNumCpiElab() {
		return numCpiElab;
	}

	/**
	 * Rende il numero di PASSI già fatti (complessivamente, per l'intera operazione).
	 */
	public int getNumQuantiElab() {
		return indexCpi + (faseCpi * listaCpi.size());
	}

	/**
	 * Rende il numero di PASSI ancora da fare (complessivamente, per l'intera operazione).
	 */
	public int getNumQuantiRimasti() {
		return (listaCpi.size() - indexCpi) + ((MAX_FASE_CPI - faseCpi) * listaCpi.size());
	}

	/**
	 * Rende il numero totale dei "movimenti" esportati al momento della chiamata. può essere chiamato solo DOPO
	 * l'invocazione del metodo "initMarchable()" o di una invocazione di "doNextStepMarchable()". Nota: rende il numero
	 * totale progressivo, non gli ultimi elaborati.
	 */
	public int getNumMovimentiElab() {
		return numMovElab;
	}

	/**
	 * Rende il numero di secondi complessivi trascorsi o impiegati. Se chiamato durante l'esportazione, rende il tempo
	 * trascorso dall'inizio dell'elaborazione al momento della chiamata. Se chiamato dopo il termine dell'elaborazione,
	 * rende il tempo impiegato dall'inizio alla fino dell'elaborazione.
	 */
	public long getNumSecElaborazione() {
		long myDelta;
		if (deltaTimeMillis >= 0)
			myDelta = deltaTimeMillis;
		else if (startTimeMillis == 0)
			myDelta = 0;
		else
			myDelta = System.currentTimeMillis() - startTimeMillis;
		return myDelta / 1000;
	}

	/**
	 * Rende una rappresentazione in "ore:min:sec" del tempo trascorso
	 */
	public String getStrNumSecElaborazione() {
		return getStrTempo(getNumSecElaborazione());
	}

	private String getStrTempo(long deltaSec) {
		long hou = deltaSec / 3600;
		long min = (deltaSec - hou * 60) / 60;
		long sec = deltaSec - (hou + min) * 60;
		String value = String.valueOf(hou) + (min < 10 ? ":0" : ":") + String.valueOf(min) + (sec < 10 ? ":0" : ":")
				+ String.valueOf(sec);
		return value;
	}

	/**
	 * Info relative alla PRECEDENTE elaborazione.
	 */
	public int getPrecNumCpiElab() {
		return precNumCpiElab;
	}

	/**
	 * Info relative alla PRECEDENTE elaborazione.
	 */
	public int getPrecNumMovimentiElab() {
		return precNumMovimentiElab;
	}

	/**
	 * Info relative alla PRECEDENTE elaborazione.
	 */
	public long getPrecNumSecElaborazione() {
		return precNumSecElaborazione;
	}

	/**
	 * Info relative alla PRECEDENTE elaborazione.
	 */
	public String getStrPrecNumSecElaborazione() {
		return getStrTempo(getPrecNumSecElaborazione());
	}

	/**
	 * Info relative alla PRECEDENTE elaborazione.
	 */
	public String getPrecFattaDaUtente() {
		return precFattaDaUtente;
	}

	public String toString() {
		try {
			return new StringBuffer().append("EsportaMigrazioni.java: ").append("Data Ultima Migrazione=")
					.append(dataUltimaMigrazione).append(", Data Lancio=").append(dataLancio)
					.append(", Data Questa Migrazione (se in corso)=").append(dataQuestaMigrazione)
					.append(", Data Inizio=").append(dataUltimaMigrazione).append(", Data Fine=")
					.append(dataQuestaMigrazione).append(", Num. Mov. Elaborati=").append(numMovElab)
					.append(", Num. CPI Elab.=").append(numCpiElab).append(", CPI elaborato=")
					.append(listaCpi != null && listaCpi.size() >= 0 ? listaCpi.get(indexCpi) : "nessuno")
					.append(", fase=").append(faseCpi).toString();
		} catch (Exception e) {
			it.eng.sil.util.TraceWrapper.debug(_logger,
					this.getClass().getName() + ":: impossibile costruire la stringa della toString() ", e);

		}
		return "EsportaMigrazioni.java: errore generazione toString() ";
	}

	/**
	 * CLASSE privata interna costituente un singolo elemento della lista "listCpi". (visibilità a livello di pacchetto,
	 * perché la veda la EsportaMigrazioniOutput)
	 */
	class CpiElem implements Serializable {

		private final String codCpi;
		private final String siglaProvCpi;
		private final String email;
		private final String codTipoFile;

		public CpiElem(String codCpi, String siglaProvCpi, String email, String codTipoFile) {
			this.codCpi = StringUtils.notNull(codCpi);
			this.siglaProvCpi = StringUtils.notNull(siglaProvCpi);
			this.email = StringUtils.notNull(email);
			this.codTipoFile = StringUtils.notNull(codTipoFile);
		}

		public String getCodCpi() {
			return codCpi;
		}

		public String getSiglaProvCpi() {
			return siglaProvCpi;
		}

		public String getEmail() {
			return email;
		}

		public String getCodTipoFile() {
			return codTipoFile;
		}

		public String toString() {
			return new StringBuffer("EsportaMigrazioni$CpiElem.java: ").append("CodCpi=").append(codCpi)
					.append(", siglaProvCpi=").append(siglaProvCpi).append(", email=").append(email)
					.append(", codTipoFile=").append(codTipoFile).toString();
		}
	} // classe CpiElem

}
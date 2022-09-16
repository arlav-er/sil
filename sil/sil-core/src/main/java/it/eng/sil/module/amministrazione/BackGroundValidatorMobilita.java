package it.eng.sil.module.amministrazione;

import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Vector;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.SessionContainer;
import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.configuration.ConfigSingleton;

import it.eng.sil.Values;
import it.eng.sil.cig.bean.ImportaIscrizioneMBOBean;
import it.eng.sil.module.mobilita.ValidazioneXMLMbo;
import it.eng.sil.module.movimenti.AppoggioExtractor;
import it.eng.sil.module.movimenti.MultipleTransactionQueryExecutor;
import it.eng.sil.module.movimenti.RecordExtractor;
import it.eng.sil.module.movimenti.RecordProcessor;
import it.eng.sil.module.movimenti.ResultLogger;
import it.eng.sil.module.movimenti.processors.ControllaCampiValidazioneMobilita;
import it.eng.sil.module.movimenti.processors.ControllaEsistenzaMobilita;
import it.eng.sil.module.movimenti.processors.GestioneMovimentoXValidazioneMobilita;
import it.eng.sil.module.movimenti.processors.IdentificaMobilita;
import it.eng.sil.module.movimenti.processors.InsertAziendaXValidazioneMass;
import it.eng.sil.module.movimenti.processors.InsertLavoratore;
import it.eng.sil.module.movimenti.processors.InsertMobilitaXValidazioneMass;
import it.eng.sil.security.User;

public class BackGroundValidatorMobilita implements Runnable {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger
			.getLogger(BackGroundValidatorMobilita.class.getName());
	private MultipleTransactionQueryExecutor trans = null;
	private RequestContainer reqCont = null;
	private ArrayList prgMobAppArray = new ArrayList();
	private int cdnut;
	private ValidatorNew validator = null;
	private ResultLogger logger = null;
	private Vector vettConifGenerale = null;
	private SourceBean sbTsGenerale = null;
	private SourceBean sbConfigLoc = null;
	private boolean isImport = false;

	// per gestire l'importazione di mobilita
	private BigDecimal prgAmMobIscrApp;
	private ImportaIscrizioneMBOBean importaIscrMBOBean;

	public BackGroundValidatorMobilita(RequestContainer reqContainer, ValidatorNew validator, ResultLogger logger,
			ArrayList prgMobAppArray, Vector vettConfig, boolean isImport) {
		super();
		this.prgMobAppArray = prgMobAppArray;
		this.reqCont = reqContainer;
		User usr = (User) this.reqCont.getSessionContainer().getAttribute(User.USERID);
		cdnut = usr.getCodut();
		this.validator = validator;
		this.logger = logger;
		this.vettConifGenerale = vettConfig;
		this.sbTsGenerale = (SourceBean) this.vettConifGenerale.get(0);
		SourceBean sbApp = (SourceBean) this.vettConifGenerale.get(1);
		if (sbApp != null) {
			this.sbConfigLoc = sbApp;
		}
		this.isImport = isImport;
	}

	public void run() {
		// Imposto il Request container in modo da evitare di perderlo (viene
		// utilizzato dai processors).
		// Deve essere fatto dopo aver avviato il thread perche e legato al nome
		// del Thread
		RequestContainer.setRequestContainer(reqCont);
		SourceBean request = RequestContainer.getRequestContainer().getServiceRequest();
		SessionContainer sessione = RequestContainer.getRequestContainer().getSessionContainer();

		// Istanzio l'estrattore
		RecordExtractor extr;
		try {
			// 11/06/2008 savino: e' ora possibile passare la transazione.
			// usata solo nel trasferimento ramo azienda
			extr = new AppoggioExtractor((BigDecimal[]) prgMobAppArray.toArray(new BigDecimal[prgMobAppArray.size()]),
					"GET_DETT_MOBILITA_APPOGGIO", null);
		} catch (Exception e) {
			it.eng.sil.util.TraceWrapper.debug(_logger, "Impossibile istanziare l'estrattore per i record. ", e);

			// INSERISCO L'EVIDENZA
			if (isImport) {
				ValidazioneXMLMbo.insertEvidenza(getPrgAmMobIscrApp(), getImportaIscrMBOBean());
			}

			return;
		}

		// Creo il MultipleTransactionQueryexecutor da utilizzare
		try {
			trans = new MultipleTransactionQueryExecutor(Values.DB_SIL_DATI);
		} catch (Exception e) {
			it.eng.sil.util.TraceWrapper.debug(_logger,
					"BackGroundValidatorMobilita::run(): Eccezione nella creazione del MultipleTransactionQueryExecutor()",
					e);

			// INSERISCO L'EVIDENZA
			if (isImport) {
				ValidazioneXMLMbo.insertEvidenza(getPrgAmMobIscrApp(), getImportaIscrMBOBean());
			}

			return;
		}

		// Path dei file di configurazione
		String configbase = ConfigSingleton.getRootPath() + File.separator + "WEB-INF" + File.separator + "conf"
				+ File.separator + "import" + File.separator + "processors" + File.separator;

		try {
			sbTsGenerale.setAttribute("AGGIORNADATIAZIENDA", "FALSE");
			BigDecimal user = (BigDecimal) RequestContainer.getRequestContainer().getSessionContainer()
					.getAttribute("_CDUT_");
			validator.addProcessor(new ControllaCampiValidazioneMobilita("Controlli campi", "MASSIVA", trans));
			// Identificazione mobilita
			validator.addProcessor(new IdentificaMobilita());
			// Inserimento Lavoratore
			RecordProcessor insLav = new InsertLavoratore("Inserimento Lavoratore nel DB", trans, user,
					configbase + "insertLavoratore.xml", sbTsGenerale);
			validator.addProcessor(insLav);
			// Inserimento Azienda
			RecordProcessor insAz = new InsertAziendaXValidazioneMass("Inserimento Azienda nel DB", trans, user,
					configbase + "insertAzienda.xml", sbTsGenerale);
			validator.addProcessor(insAz);
			// Processor che controlla l'esistenza o meno della mobilita nel db
			RecordProcessor esistenzaMob = new ControllaEsistenzaMobilita("Controllo esistenza mobilita nel DB", trans);
			validator.addProcessor(esistenzaMob);
			// Processor che gestisce il movimento che ha portato il lavoratore
			// in mobilita
			RecordProcessor gestioneMovimento = new GestioneMovimentoXValidazioneMobilita(
					"Inserimento movimento nel DB", sbConfigLoc, trans, request, user, sbTsGenerale, logger);
			validator.addProcessor(gestioneMovimento);
			// Processor che inserisce la mobilita
			RecordProcessor insMobilita = new InsertMobilitaXValidazioneMass("Inserimento Mobilita nel DB", trans, user,
					reqCont, sbTsGenerale);
			validator.addProcessor(insMobilita);

		} catch (Exception e) {
			it.eng.sil.util.TraceWrapper.debug(_logger, "Impossibile configurare i "
					+ "processori per la validazione delle mobilita, controllare i file XML di configurazione. ", e);

			trans.closeConnection();

			// INSERISCO L'EVIDENZA
			if (isImport) {
				ValidazioneXMLMbo.insertEvidenza(getPrgAmMobIscrApp(), getImportaIscrMBOBean());
			}

			return;
		}

		// Imposto i dati necessari nell'importer
		validator.setIdRecordKey("PRGMOBILITAISCRAPP");
		validator.setRecordExtractor(extr);
		try {
			String bloccaSeStatoOccMan = "";
			if (sbTsGenerale != null) {
				bloccaSeStatoOccMan = sbTsGenerale.containsAttribute("FLGBLOCCASESOMANUALE")
						? sbTsGenerale.getAttribute("FLGBLOCCASESOMANUALE").toString()
						: "";
			}
			if (bloccaSeStatoOccMan.equalsIgnoreCase("N") && RequestContainer.getRequestContainer() != null
					&& RequestContainer.getRequestContainer().getServiceRequest() != null && !RequestContainer
							.getRequestContainer().getServiceRequest().containsAttribute("CONTINUA_CALCOLO_SOCC")) {
				RequestContainer.getRequestContainer().getServiceRequest().setAttribute("CONTINUA_CALCOLO_SOCC",
						"true");
			}
			// AVVIO DELLA VALIDAZIKONE
			validator.importRecords(logger, trans);

		} catch (Exception e) {
			trans.closeConnection();
			// Qualsiasi cosa succeda ne faccio il tracing e vado avanti per
			// chiudere la connessione e rimuovere gli oggetti dalla sessione
			it.eng.sil.util.TraceWrapper.debug(_logger,
					"BackGroundValidatorMobilita::run(): Eccezione nella validazione massiva delle mobilita", e);

			// INSERISCO L'EVIDENZA
			if (isImport) {
				ValidazioneXMLMbo.insertEvidenza(getPrgAmMobIscrApp(), getImportaIscrMBOBean());
			}

		}
		// chiusura della connessione
		trans.closeConnection();
		_logger.debug("BackGroundValidatorMobilita::run(): Connessione chiusa al termine della validazione massiva");

		// rimozione dalla sessione dei riferimenti
		RequestContainer.getRequestContainer().getSessionContainer().delAttribute("VALIDATORMOBILITACORRENTE");
		RequestContainer.getRequestContainer().getSessionContainer().delAttribute("VALIDATOREMASSIVOMOBILITACORRENTE");
		_logger.debug("BackGroundValidatorMobilita::run(): Oggetti per validazione massiva eliminati dalla sessione");

		// INSERISCO L'EVIDENZA: traccio gli eventuali errori nel ResultLogger
		if (isImport) {
			// a questo punto, gli unici errori che posso avere sono quelli dei singoli Processor
			/*
			 * NB: importRecords ritorna un SourceBean, si potrebbe usare per stampare un'evidenza piu' significativa
			 * passando il SourceBean all'HtmlResultLogger e gestendo solo gli errori
			 */
			if (validator.getErrorValidator()) {
				ValidazioneXMLMbo.insertEvidenza(getPrgAmMobIscrApp(), getImportaIscrMBOBean());
			}
		}

	}

	/**
	 * Se scade la sessione utente e questo oggetto viene cancellato dal Garbage Collector chiude comunque la
	 * connessione
	 */
	protected void finalize() throws Throwable {
		super.finalize();
		trans.closeConnection();
		_logger.debug("BackGroundValidatorMobilita::run(): Connessione chiusa dal Garbage Collector!!!");

	}

	public BigDecimal getPrgAmMobIscrApp() {
		return prgAmMobIscrApp;
	}

	public void setPrgAmMobIscrApp(BigDecimal prgAmMobIscrApp) {
		this.prgAmMobIscrApp = prgAmMobIscrApp;
	}

	public ImportaIscrizioneMBOBean getImportaIscrMBOBean() {
		return importaIscrMBOBean;
	}

	public void setImportaIscrMBOBean(ImportaIscrizioneMBOBean importaIscrMBOBean) {
		this.importaIscrMBOBean = importaIscrMBOBean;
	}

}
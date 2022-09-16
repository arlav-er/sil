/*
 * Creato il 4-nov-04
 */
package it.eng.sil.module.movimenti;

import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.SessionContainer;
import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.configuration.ConfigSingleton;

import it.eng.afExt.utils.StringUtils;
import it.eng.sil.Values;
import it.eng.sil.module.movimenti.processors.ComunicaInsLavoratoreToIR;
import it.eng.sil.module.movimenti.processors.ControllaMovimenti;
import it.eng.sil.module.movimenti.processors.ControllaTipoComunicazione;
import it.eng.sil.module.movimenti.processors.ControlliDecreto;
import it.eng.sil.module.movimenti.processors.ControlliDecreto2014;
import it.eng.sil.module.movimenti.processors.ControlliRettifica;
import it.eng.sil.module.movimenti.processors.ControlloDurataTD;
import it.eng.sil.module.movimenti.processors.ControlloMovimentoSimile;
import it.eng.sil.module.movimenti.processors.ControlloPermessi;
import it.eng.sil.module.movimenti.processors.ControlloTipoAssunzione;
import it.eng.sil.module.movimenti.processors.CrossController;
import it.eng.sil.module.movimenti.processors.EseguiImpatti;
import it.eng.sil.module.movimenti.processors.GestioneVA18;
import it.eng.sil.module.movimenti.processors.GestisciCVEValidazione;
import it.eng.sil.module.movimenti.processors.GestisciDocumentoCVE;
import it.eng.sil.module.movimenti.processors.IdentificaMovimento;
import it.eng.sil.module.movimenti.processors.InsertAgevolazioni;
import it.eng.sil.module.movimenti.processors.InsertApprendistato;
import it.eng.sil.module.movimenti.processors.InsertAziendaXValidazioneMass;
import it.eng.sil.module.movimenti.processors.InsertData;
import it.eng.sil.module.movimenti.processors.InsertDatiMissione;
import it.eng.sil.module.movimenti.processors.InsertDocumento;
import it.eng.sil.module.movimenti.processors.InsertLavoratore;
import it.eng.sil.module.movimenti.processors.InsertLavoratoriL68FromMovimento;
import it.eng.sil.module.movimenti.processors.InsertTirocinio;
import it.eng.sil.module.movimenti.processors.InviaMigrazioneInCOOP;
import it.eng.sil.module.movimenti.processors.PrevalorizzaCampi;
import it.eng.sil.module.movimenti.processors.ProcControlloMbCmEtaLav;
import it.eng.sil.module.movimenti.processors.Protocollazione;
import it.eng.sil.module.movimenti.processors.RemoveMovimentoAppoggio;
import it.eng.sil.module.movimenti.processors.SelectCodContratto;
import it.eng.sil.module.movimenti.processors.SelectMovimentoPrecValidazioneMassiva;
import it.eng.sil.module.movimenti.processors.SelectMovimentoSucc;
import it.eng.sil.module.movimenti.processors.UpdateMovimentoPrec;
import it.eng.sil.module.movimenti.processors.UpdateRettificaPeriodo;
import it.eng.sil.module.movimenti.trasferimentoRamoAz.TrasferimentoRamoAzRequestParams;
import it.eng.sil.util.Utils;
import it.eng.sil.util.amministrazione.impatti.DBLoad;
import it.eng.sil.util.amministrazione.impatti.LogBatch;

/**
 * Oggetto runnable che esegue la validazione massiva dei record in BackGround.
 * <p/>
 * 
 * @author roccetti
 */
public class BackGroundValidator implements Runnable {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(BackGroundValidator.class.getName());

	private MultipleTransactionQueryExecutor trans = null;
	private RequestContainer reqCont = null;
	private ResultLogger logger = null;
	private ArrayList prgMovAppArray = new ArrayList();
	private Validator validator = null;

	/**
	 * costruttore
	 */
	public BackGroundValidator(RequestContainer reqContainer, Validator validator, ResultLogger logger,
			ArrayList prgMovAppArray) {
		super();
		this.validator = validator;
		this.prgMovAppArray = prgMovAppArray;
		this.reqCont = reqContainer;
		this.logger = logger;
	}

	/**
	 * Si occupa di lanciare la validazione massiva, di chiudere la connessione al suo termine e di rimuovere questo
	 * oggetto e il validator dalla sessione
	 */
	public void run() {
		// Imposto il Request container in modo da evitare di perderlo (viene utilizzato dai processors).
		// Deve essere fatto dopo aver avviato il thread perché è legato al nome del Thread
		RequestContainer.setRequestContainer(reqCont);

		// Estrazione request e sessione
		SourceBean request = RequestContainer.getRequestContainer().getServiceRequest();
		SessionContainer sessione = RequestContainer.getRequestContainer().getSessionContainer();
		boolean batchTerminato = false;
		boolean isProvenienzaTrasferimento = false;
		// Istanzio l'estrattore
		RecordExtractor extr;
		try {
			// 11/06/2008 savino: FASE2
			// se siamo in un trasferimento ramo azienda devo usare la transazione esterna dato che il movimento nella
			// am_moviemnto_appoggio
			// e' stato inserito nella stessa transazione a cui partecipa la validazione.
			// Questa chiamata da trasferimento ramo azienda non viene piu' utilizzata
			trans = (MultipleTransactionQueryExecutor) reqCont.getAttribute("TQE_OBJECT");
			if (trans != null) {
				isProvenienzaTrasferimento = true;
			}
			// se non siamo in un trasf. ramo az. allora "trans" e' null e l'AppoggioExtractor utilizzera' una nuova
			// connessione.
			extr = new AppoggioExtractor((BigDecimal[]) prgMovAppArray.toArray(new BigDecimal[prgMovAppArray.size()]),
					"GET_MOVIMENTO_DA_APPOGGIO", trans);
		} catch (Exception e) {
			it.eng.sil.util.TraceWrapper.debug(_logger, "Impossibile istanziare l'estrattore per i record. ", e);

			batchTerminato = true;
			return;
		}

		// Path dei file di configurazione
		String configbase = ConfigSingleton.getRootPath() + File.separator + "WEB-INF" + File.separator + "conf"
				+ File.separator + "import" + File.separator + "processors" + File.separator;

		// LETTURA DALLA TS_GENERALE
		SourceBean sbGenerale = null;
		try {
			sbGenerale = DBLoad.getInfoGenerali();
			String bloccaSeStatoOccMan = "";
			if (sbGenerale != null) {
				bloccaSeStatoOccMan = sbGenerale.containsAttribute("FLGBLOCCASESOMANUALE")
						? sbGenerale.getAttribute("FLGBLOCCASESOMANUALE").toString()
						: "";
			}
			if (bloccaSeStatoOccMan.equalsIgnoreCase("N") && RequestContainer.getRequestContainer() != null
					&& RequestContainer.getRequestContainer().getServiceRequest() != null && 
					!RequestContainer.getRequestContainer().getServiceRequest().containsAttribute("CONTINUA_CALCOLO_SOCC")) {
				RequestContainer.getRequestContainer().getServiceRequest().setAttribute("CONTINUA_CALCOLO_SOCC", "true");
			}
			// forza in validazione massiva chiusura della did
			if (RequestContainer.getRequestContainer() != null && RequestContainer.getRequestContainer().getServiceRequest() != null) {
		  		if (!RequestContainer.getRequestContainer().getServiceRequest().containsAttribute("FORZA_INSERIMENTO")){
		  			RequestContainer.getRequestContainer().getServiceRequest().setAttribute("FORZA_INSERIMENTO", "true");
		  		}
		  	}
		} catch (Exception e) {
			it.eng.sil.util.TraceWrapper.debug(_logger, "Impossibile leggere dalla ts_generale. ", e);

			batchTerminato = true;
			return;
		}
		int numIterazione = 0;
		// Imposto i dati necessari nell'importer
		validator.setIdRecordKey("PRGMOVIMENTOAPP");
		validator.setRecordExtractor(extr);
		LogBatch logBatch = validator.getLogBatch();
		while (!batchTerminato) {
			numIterazione = numIterazione + 1;
			if (numIterazione > 1)
				validator.clearProcessor();
			try {
				// 11/06/2008 savino: la validazione puo' anche essere chiamata nel trasferiemnto ramo azienda (servizio
				// di importazione di comunicazioni obbligatorie)
				// In questo caso la validazione e' in transazione con l'importazione nella tabella di appoggio.
				// Quindi si usa un oggetto MultipleTransactionQueryWrapper che ha sia la commit che la rollback
				// disabilitati
				// per permettere al controllore della transazione di proseguirla con la chiamata ad altri moduli.
				// Il MultipleTransactionQueryWrapper estende MultipleTransactionQueryExecutor.
				if (!isProvenienzaTrasferimento) {
					trans = new MultipleTransactionQueryExecutor(Values.DB_SIL_DATI);
				}
			} catch (Exception e) {
				it.eng.sil.util.TraceWrapper.debug(_logger,
						"BackGroundValidator::run(): Eccezione nella creazione del MultipleTransactionQueryExecutor()",
						e);
				batchTerminato = true;
				return;
			}

			// Aggiungo i processori al validatore
			try {
				_logger.debug("BackGroundValidator::run(): Connessione creata");

				if (logBatch != null) {
					logBatch.writeLog("Connessione creata");
				}

				BigDecimal user = (BigDecimal) RequestContainer.getRequestContainer().getSessionContainer()
						.getAttribute("_CDUT_");

				// Identificazione movimento
				validator.addProcessor(new IdentificaMovimento());
				validator.addProcessor(new ControllaTipoComunicazione("Controlla_Tipo_Comunicazione", request, null,
						sessione, reqCont, trans));
				// Le rettifiche di CO vengono inoltrate a tutti i poli provinciali. Per questo motivo bisogna
				// controllare, prima di procedere all'inserimento della nuova CO, che quel polo abbia la competenza
				// amministrativa.
				// In caso contrario la CO di rettifica viene cancellata e il processo di validazione viene bloccato.
				validator.addProcessor(new ControlliRettifica("Controlli_Rettifica", trans));
				// Selezione codice contratto
				RecordProcessor selectCodContr = new SelectCodContratto("Seleziona_Codice_Contratto", trans);
				validator.addProcessor(selectCodContr);
				// Recupero i dati per eseguire la protocollazione e l'inserisco nella Map attraverso il processore
				// Protocollazione
				String numProt = StringUtils.getAttributeStrNotNull(request, "numProt");
				String annoProt = StringUtils.getAttributeStrNotNull(request, "numAnnoProt");
				String dataProt = StringUtils.getAttributeStrNotNull(request, "dataProt");
				String oraProt = StringUtils.getAttributeStrNotNull(request, "oraProt");
				String tipoProt = StringUtils.getAttributeStrNotNull(request, "tipoProt");
				validator.addProcessor(new Protocollazione(numProt, annoProt, dataProt, oraProt, tipoProt));

				// Prevalorizzazione campi mancanti
				validator.addProcessor(new PrevalorizzaCampi("Default campi mancanti"));
				// Inserimento Lavoratore
				RecordProcessor insLav = new InsertLavoratore("Inserimento Lavoratore nel DB", trans, user,
						configbase + "insertLavoratore.xml", sbGenerale);
				validator.addProcessor(insLav);

				/*
				 * Le seguenti righe di codice sono state modificate per permettere di inserire un movimento collegato a
				 * partire dalla testa azienda e non dell'unità come avveniva prima. *
				 */
				// Inserimento Azienda
				RecordProcessor insAz = new InsertAziendaXValidazioneMass("Inserimento Azienda nel DB", trans, user,
						configbase + "insertAzienda.xml", sbGenerale);
				validator.addProcessor(insAz);

				// Processor che gestisce i movimenti di AVV da CVE
				RecordProcessor gestoreCVE = new GestisciCVEValidazione("gestione CVE validazione massiva", sbGenerale,
						trans, user, null);
				// Processore che seleziona il movimento precedente
				validator.addProcessor(new SelectMovimentoPrecValidazioneMassiva("Seleziona Precedente", trans,
						gestoreCVE, user, configbase));
				// validazioni decreto gennaio 2013
				validator.addProcessor(new ControlliDecreto(trans));
				// validazioni decreto gennaio 2014
				validator.addProcessor(new ControlliDecreto2014(trans));
				// Controlli sulle autorizzazioni
				validator.addProcessor(new ControlloPermessi("Autorizzazione per impatti", trans));
				// controllo dei dati sensibili del lavoratore
				validator.addProcessor(new ProcControlloMbCmEtaLav("Controllo dati lavoratore", trans));
				// Processore per il controllo dell'esistenza di movimenti simili a quello in inserimento
				validator.addProcessor(new ControlloMovimentoSimile("Controllo movimenti simili", trans, sbGenerale));

				// controllo sul tipo di assunzione
				validator.addProcessor(new ControlloTipoAssunzione("Controllo tipo assunzione", trans));

				// Processor che gestisce i movimenti di AVV da CVE
				// RecordProcessor gestoreCVE = new GestisciCVEValidazione("gestione CVE validazione massiva", trans,
				// user);
				// Processore che seleziona il movimento precedente
				// validator.addProcessor(new SelectMovimentoPrecValidazioneMassiva("Seleziona Precedente", trans,
				// gestoreCVE,user,configbase));

				// Processore che controlla la durata dei movimenti a TD
				validator.addProcessor(new ControlloDurataTD("Controlla durata movimenti a TD"));
				// Processore che controlla i dati del movimento.
				validator.addProcessor(new ControllaMovimenti(sbGenerale, trans, user));
				// Processore per ulteriori controlli che di solito sono svolti nella jsp
				validator.addProcessor(new CrossController("Controllore Incrociato"));
				// Processore per l'esecuzione degli impatti
				RecordProcessor eseguiImpatti = new EseguiImpatti("Esecuzione impatti", sbGenerale, trans, user);
				validator.addProcessor(eseguiImpatti);
				// Inserimento Movimento
				validator.addProcessor(new InsertData("Inserimento Movimento", trans,
						configbase + "insertMovimento.xml", "INSERT_MOVIMENTO", user));
				// Processore per l'aggiornamento dei periodi lavorativi di rettifiche di avviamenti
				validator.addProcessor(
						new UpdateRettificaPeriodo("Aggiorna periodo in rettifica avviamento", trans, user));
				// Processore per l'inserimento in am_movimento_missione delle info relative alla missione
				validator.addProcessor(new InsertDatiMissione(user, trans, sbGenerale));
				// Processore che aggiorna il movimento precedente
				validator.addProcessor(new UpdateMovimentoPrec("Aggiorna Precedente", trans, user));
				// Processore per l'inserimento in am_movimento_apprendist
				validator.addProcessor(new InsertApprendistato(user, trans));
				// Processore per l'inserimento in am_movimento_apprendist delle info relative al tirocinio
				validator.addProcessor(new InsertTirocinio(user, trans, sbGenerale));
				// Processore per l'inserimento in AM_MOVIMENTO_AGEVOLAZIONI dei benefici
				validator.addProcessor(new InsertAgevolazioni(user, trans, sbGenerale));
				validator.addProcessor(new SelectMovimentoSucc("CercaMovimentoSuccessivo", trans));
				validator.addProcessor(new GestioneVA18("Gestione VA18", trans, sbGenerale, reqCont));
				String automaticTRA = Utils
						.notNull(request.getAttribute(TrasferimentoRamoAzRequestParams.AUTOMATIC_TRA));
				// if (!automaticTRA.equalsIgnoreCase("TRUE")) {
				validator.addProcessor(new RemoveMovimentoAppoggio("Rimozione da AM_MOVIMENTO_APPOGGIO", trans));
				// }
				// Processors per l'inserimento del documento(quello da validare e eventualmente l'avviamento
				// creato dalla cessazione)
				validator.addProcessor(new GestisciDocumentoCVE(user, trans));

				// processor per l'automatismo sui lavoratori L68 dei prospetti
				validator.addProcessor(new InsertLavoratoriL68FromMovimento(user, trans));

				// NOTA: gli ultimi processor DEVEONO essere nell'ordine :
				// InsertDocumento() -> InviaMigrazioneInCOOP() -> ComunicaInsLavoratoreToIR()
				validator.addProcessor(new InsertDocumento(user, trans));
				validator.addProcessor(new InviaMigrazioneInCOOP(trans));
				validator.addProcessor(new ComunicaInsLavoratoreToIR(trans));

			} catch (Exception e) {
				it.eng.sil.util.TraceWrapper.debug(_logger, "Impossibile configurare i "
						+ "processori per la validazione dei movimenti, controllare i file XML di configurazione. ", e);
				trans.closeConnection();
				batchTerminato = true;
				return;
			}
			// Avvio della validazione
			try {
				SourceBean result = validator.importRecords(logger, trans);
				if (validator.getValidatorTerminato()) {
					batchTerminato = true;
				}
			} catch (Exception e) {
				// Qualsiasi cosa succeda ne faccio il tracing e vado avanti per chiudere la connessione e rimuovere gli
				// oggetti dalla sessione
				it.eng.sil.util.TraceWrapper.debug(_logger,
						"BackGroundValidator::run(): Eccezione nella validazione massiva dei movimenti", e);
				batchTerminato = true;
			} finally {
				// chiusura della connessione
				trans.closeConnection();
				_logger.debug("BackGroundValidator::run(): Connessione chiusa");
			}
			if (logBatch != null) {
				logBatch.writeLog("Connessione chiusa");
			}
		} // end while

		// rimozione dalla sessione dei riferimenti
		RequestContainer.getRequestContainer().getSessionContainer().delAttribute("VALIDATORCORRENTE");
		RequestContainer.getRequestContainer().getSessionContainer().delAttribute("VALIDATOREMASSIVOCORRENTE");

		_logger.debug("BackGroundValidator::run(): Oggetti per validazione massiva eliminati dalla sessione");

	}

	/**
	 * Se scade la sessione utente e questo oggetto viene cancellato dal Garbage Collector chiude comunque la
	 * connessione
	 */
	protected void finalize() throws Throwable {
		super.finalize();
		trans.closeConnection();
		_logger.debug("BackGroundValidator::run(): Connessione chiusa dal Garbage Collector!!!");

	}

}
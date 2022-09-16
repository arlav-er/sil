/*
 * Creato il 9-lug-04
 */
package it.eng.sil.module.movimenti;

import java.io.File;
import java.math.BigDecimal;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.SessionContainer;
import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.base.SourceBeanException;
import com.engiweb.framework.configuration.ConfigSingleton;

import it.eng.afExt.utils.ReportOperationResult;
import it.eng.sil.module.AbstractSimpleModule;
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
import it.eng.sil.module.movimenti.processors.InsertAgevolazioni;
import it.eng.sil.module.movimenti.processors.InsertApprendistato;
import it.eng.sil.module.movimenti.processors.InsertAzienda;
import it.eng.sil.module.movimenti.processors.InsertData;
import it.eng.sil.module.movimenti.processors.InsertDatiMissione;
import it.eng.sil.module.movimenti.processors.InsertDocumento;
import it.eng.sil.module.movimenti.processors.InsertLavoratore;
import it.eng.sil.module.movimenti.processors.InsertTirocinio;
import it.eng.sil.module.movimenti.processors.InviaMigrazioneInCOOP;
import it.eng.sil.module.movimenti.processors.PrevalorizzaCampi;
import it.eng.sil.module.movimenti.processors.ProcControlloMbCmEtaLav;
import it.eng.sil.module.movimenti.processors.ProcessorsUtils;
import it.eng.sil.module.movimenti.processors.RemoveMovimentoAppoggio;
import it.eng.sil.module.movimenti.processors.SelectCodContratto;
import it.eng.sil.module.movimenti.processors.SelectMovimentoPrecManuale;
import it.eng.sil.module.movimenti.processors.SelectMovimentoSucc;
import it.eng.sil.module.movimenti.processors.UpdateMovimentoPrec;
import it.eng.sil.module.movimenti.processors.UpdateRettificaPeriodo;
import it.eng.sil.util.NavigationCache;
import it.eng.sil.util.amministrazione.impatti.DBLoad;

/**
 * @author roccetti
 * 
 *         Per modificare il modello associato al commento di questo tipo generato, aprire
 *         Finestra&gt;Preferenze&gt;Java&gt;Generazione codice&gt;Codice e commenti
 */
public class M_MovValidaMov extends AbstractSimpleModule {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(M_MovValidaMov.class.getName());

	/** Informazioni di connessione col DB */
	String pool = null;

	public M_MovValidaMov() {
	}

	/**
	 * Inserimento del movimento. Esegue prima i controlli necessari e poi esegue l'inserimento. Se l'inserimento va a
	 * buon fine viene chiamato il publisher per la consultazione, altrimenti ritorna alla pagina di inserimento.
	 */
	public void service(SourceBean request, SourceBean response) throws SourceBeanException {

		ReportOperationResult reportOperation = new ReportOperationResult(this, response);
		pool = (String) getConfig().getAttribute("POOL");

		// String prgMovApp = StringUtils.getAttributeStrNotNull(request,
		// "PRGMOVIMENTOAPP");
		RequestContainer req = RequestContainer.getRequestContainer();
		SessionContainer ses = req.getSessionContainer();
		NavigationCache mov = (NavigationCache) ses.getAttribute("MOVIMENTOCORRENTE");
		// Controllo che l'oggetto sia abilitato, in caso contrario lo abilito
		if (!mov.isEnabled()) {
			mov.enable();
		}

		// estraggo i dati dell'oggetto come SourceBean
		SourceBean sessionOrigin = mov.getFieldsAsSourceBean();
		// Creazione del record extractor che si occupa di estrarre i dati dalla
		// request,
		// dall'oggetto in sessione e dal DB.
		String configbase = ConfigSingleton.getRootPath() + File.separator + "WEB-INF" + File.separator + "conf"
				+ File.separator + "import" + File.separator;
		String configFileName = configbase + "ManualValidationFieldsMapping.xml";
		RecordExtractor extr = null;
		try {
			extr = new ManualExtractor(request,
					(SourceBean) request.getAttribute("M_MovGetDettInizialeMovApp.ROWS.ROW"), sessionOrigin,
					configFileName);
		} catch (Exception e) {
			it.eng.sil.util.TraceWrapper.debug(_logger,
					"Impossibile istanziare l'estrattore per i movimenti da validare, "
							+ "controllare i file XML di configurazione. ",
					e);
			return;
		}

		// LETTURA DALLA TS_GENERALE
		SourceBean sbGenerale = null;
		try {
			sbGenerale = DBLoad.getInfoGenerali();
		} catch (Exception e) {
			it.eng.sil.util.TraceWrapper.debug(_logger, "Impossibile leggere dalla ts_generale. ", e);
			return;
		}

		// creazione del validatore e configurazione dei processori
		Validator validator = new Validator();
		// Path dei file di configurazione
		String processorbase = configbase + "processors" + File.separator;
		MultipleTransactionQueryExecutor trans = null;
		SourceBean result = null;

		// Aggiungo i processori al validator
		try {
			trans = new MultipleTransactionQueryExecutor(pool);
			BigDecimal user = (BigDecimal) getRequestContainer().getSessionContainer().getAttribute("_CDUT_");
			//
			validator.addProcessor(
					new ControllaTipoComunicazione("Controlla_Tipo_Comunicazione", request, response, ses, req, trans));
			// Le rettifiche di CO vengono inoltrate a tutti i poli provinciali. Per questo motivo bisogna
			// controllare, prima di procedere all'inserimento della nuova CO, che quel polo abbia la competenza
			// amministrativa.
			// In caso contrario la CO di rettifica viene cancellata e il processo di validazione viene bloccato.
			validator.addProcessor(new ControlliRettifica("Controlli_Rettifica", trans));
			// Selezione codice contratto
			RecordProcessor selectCodContr = new SelectCodContratto("Seleziona_Codice_Contratto", trans);
			validator.addProcessor(selectCodContr);
			// Prevalorizzazione campi mancanti
			validator.addProcessor(new PrevalorizzaCampi("Default campi mancanti"));
			// Inserimento Lavoratore
			RecordProcessor insLav = new InsertLavoratore("Inserimento Lavoratore nel DB", trans, user,
					processorbase + "insertLavoratore.xml", sbGenerale);
			validator.addProcessor(insLav);
			// Inserimento Azienda
			RecordProcessor insAz = new InsertAzienda("Inserimento Azienda nel DB", trans, user,
					processorbase + "insertAzienda.xml", sbGenerale);
			validator.addProcessor(insAz);
			// Controlli sulle autorizzazioni
			validator.addProcessor(new ControlloPermessi("Autorizzazione per impatti", trans));
			// Processore per il controllo dell'esistenza di movimenti simili a
			// quello in inserimento
			// controllo dei dati sensibili del lavoratore
			validator.addProcessor(new ProcControlloMbCmEtaLav("Controllo dati lavoratore", trans));
			validator.addProcessor(new ControlloMovimentoSimile("Controllo movimenti simili", trans, sbGenerale));
			// controllo sul tipo di assunzione
			validator.addProcessor(new ControlloTipoAssunzione("Controllo tipo assunzione", trans));
			// Processor che gestisce i movimenti di AVV da CVE
			RecordProcessor gestoreCVE = new GestisciCVEValidazione("gestione CVE validazione manuale", sbGenerale,
					trans, user, request);
			// Processore che seleziona il movimento precedente
			validator.addProcessor(new SelectMovimentoPrecManuale("Seleziona Precedente", trans, gestoreCVE));
			// validazioni decreto gennaio 2013
			validator.addProcessor(new ControlliDecreto(trans));
			// validazioni decreto gennaio 2014
			validator.addProcessor(new ControlliDecreto2014(trans));
			// Processore che controlla la durata dei movimenti a TD
			validator.addProcessor(new ControlloDurataTD("Controlla durata movimenti a TD"));
			// Processore che controlla i dati del movimento.
			validator.addProcessor(new ControllaMovimenti(sbGenerale, trans, user));
			// Processore per ulteriori controlli che di solito sono svolti
			// nella jsp
			validator.addProcessor(new CrossController("Controllore Incrociato"));
			// Processore per l'esecuzione degli impatti
			RecordProcessor eseguiImpatti = new EseguiImpatti("Esecuzione impatti", sbGenerale, trans, user);
			validator.addProcessor(eseguiImpatti);
			// Inserimento Movimento
			validator.addProcessor(new InsertData("Inserimento Movimento", trans, processorbase + "insertMovimento.xml",
					"INSERT_MOVIMENTO", user));

			// Processore per l'aggiornamento dei periodi lavorativi di rettifiche di avviamenti
			validator.addProcessor(new UpdateRettificaPeriodo("Aggiorna periodo in rettifica avviamento", trans, user));

			// Processore per l'inserimento in am_movimento_missione delle info relative alla missione
			validator.addProcessor(new InsertDatiMissione(user, trans, sbGenerale));

			// Processore che aggiorna il movimento precedente
			validator.addProcessor(new UpdateMovimentoPrec("Aggiorna Precedente", trans, user));
			// Processore per l'inserimento in am_movimento_apprendist
			validator.addProcessor(new InsertApprendistato(user, trans));
			// Processore per l'inserimento in am_movimento_apprendist delle
			// info relative al tirocinio
			validator.addProcessor(new InsertTirocinio(user, trans, sbGenerale));
			// Processore per l'inserimento in AM_MOVIMENTO_AGEVOLAZIONI dei benefici
			validator.addProcessor(new InsertAgevolazioni(user, trans, sbGenerale));
			validator.addProcessor(new SelectMovimentoSucc("CercaMovimentoSuccessivo", trans));
			validator.addProcessor(new GestioneVA18("Gestione VA18", trans, sbGenerale, req));
			validator.addProcessor(
					new RemoveMovimentoAppoggio("Rimozione del record dalla tabella AM_MOVIMENTO_APPOGGIO", trans));
			// Processors per l'inserimento del documento(quello da validare e
			// eventualmente l'avviamento
			// creato dalla cessazione)
			validator.addProcessor(new GestisciDocumentoCVE(user, trans));

			// NOTA: gli ultimi processor DEVEONO essere nell'ordine :
			// InsertDocumento() -> InviaMigrazioneInCOOP() ->
			// ComunicaInsLavoratoreToIR()
			validator.addProcessor(new InsertDocumento(user, trans));
			validator.addProcessor(new InviaMigrazioneInCOOP(trans));
			validator.addProcessor(new ComunicaInsLavoratoreToIR(trans));

			// Imposto i dati necessari nel validator
			validator.setIdRecordKey("PRGMOVIMENTO");
			validator.setRecordExtractor(extr);

			// Esegue la processazione del record
			/*
			 * Non eseguo più la tracciatura del LOG // (faccio il log sia su file sia su DB) ResultLogger resultLogger
			 * = new HtmlResultLogger(); resultLogger.addChildResultLogger(new SingleResultLogger()); // (x)->Html->DB
			 * 
			 * SourceBean result = validator.importRecords(resultLogger, trans);
			 */

			result = validator.importRecords(null, trans);

			// inserimento dei dati nella response del modulo
			if (result != null) {
				response.setAttribute(result);

				// Controllo se l'inserimento ha avuto successo
				SourceBean recordResult = (SourceBean) result.getAttribute("RECORD");
				boolean isError = ProcessorsUtils.isError(recordResult);
				String codTipoMov = request.getAttribute("CODTIPOMOV").toString();
				response.setAttribute("CODTIPOMOV", codTipoMov);
				if (!isError) {
					// Estraggo dalla risposta l'eventuale progressivo del movimento
					BigDecimal prgMov = (BigDecimal) result.getAttribute("RECORD.RECORDID");
					if (prgMov != null) {
						// Segnalo che la validazione è andata bene, per cambiare
						// contesto
						response.setAttribute("VALIDA_OK", "TRUE");
						response.setAttribute("PRGMOVIMENTO", prgMov);
					}
				}
			}
		} catch (Exception e) {
			it.eng.sil.util.TraceWrapper.debug(_logger,
					"Impossibile configurare i processori per la validazione dei movimenti, controllare i file XML di configurazione. ",
					e);
			return;
		} finally {
			if (trans != null) {
				// Chiudo la connessione del MultipleTransactionQueryExecutor
				trans.closeConnection();
			}
		}

	}
}
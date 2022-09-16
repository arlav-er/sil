package it.eng.sil.module.movimenti;

import java.io.File;
import java.math.BigDecimal;
import java.util.Vector;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.SessionContainer;
import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.base.SourceBeanException;
import com.engiweb.framework.configuration.ConfigSingleton;

import it.eng.afExt.utils.ReportOperationResult;
import it.eng.sil.module.AbstractSimpleModule;
import it.eng.sil.module.movimenti.processors.ControllaMovimenti;
import it.eng.sil.module.movimenti.processors.ControlliDecreto;
import it.eng.sil.module.movimenti.processors.ControlliDecreto2014;
import it.eng.sil.module.movimenti.processors.ControlloDurataTD;
import it.eng.sil.module.movimenti.processors.ControlloMovimentoSimile;
import it.eng.sil.module.movimenti.processors.ControlloPermessi;
import it.eng.sil.module.movimenti.processors.CrossController;
import it.eng.sil.module.movimenti.processors.EseguiImpatti;
import it.eng.sil.module.movimenti.processors.InsertAgevolazioni;
import it.eng.sil.module.movimenti.processors.InsertApprendistato;
import it.eng.sil.module.movimenti.processors.InsertData;
import it.eng.sil.module.movimenti.processors.InsertDatiMissione;
import it.eng.sil.module.movimenti.processors.InsertDocumento;
import it.eng.sil.module.movimenti.processors.InsertIncentiviArt_13;
import it.eng.sil.module.movimenti.processors.InsertTirocinio;
import it.eng.sil.module.movimenti.processors.PrevalorizzaCampi;
import it.eng.sil.module.movimenti.processors.ProcControlloMbCmEtaLav;
import it.eng.sil.module.movimenti.processors.ProcessorsUtils;
import it.eng.sil.module.movimenti.processors.RettificaMovimento;
import it.eng.sil.module.movimenti.processors.SelectCodContratto;
import it.eng.sil.module.movimenti.processors.SelectMovimentoPrecManuale;
import it.eng.sil.module.movimenti.processors.SelectMovimentoSucc;
import it.eng.sil.module.movimenti.processors.UpdateMovimentoPrec;
import it.eng.sil.module.movimenti.processors.UpdateRettificaPeriodo;
import it.eng.sil.util.NavigationCache;
import it.eng.sil.util.amministrazione.impatti.DBLoad;

public class MovEffettuaRettifica extends AbstractSimpleModule {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(MovEffettuaRettifica.class.getName());
	/** Informazioni di connessione col DB */
	String pool = null;

	/**
	 * Rettifica del movimento. Se la rettifica va a buon fine viene chiamato il publisher per la consultazione,
	 * altrimenti ritorna alla pagina di rettifica.
	 */
	public void service(SourceBean request, SourceBean response) throws SourceBeanException {
		Vector prgDocs = null;

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

		// Se ho i prgdocumento (per alcuni movimenti provenienti da porting non
		// ho il documento associato) in sessione
		// e nel source bean sessionOrigin è assente oppure è = null allora
		// copio in sessionOrigin i valore che ho in sessione

		// BRAVO!! allora visto che lo sai perché prima di copiare qualcosa non
		// controlli anche che 'il qualcosa' non sia vuoto!!
		// DAVIDE 14/02/2006: aggiunta una condizione nell'if. Modifica
		// effettuata per correggere il bug 1762:
		// mancata protocollazione in caso di rettifica di un movimento che NON
		// presenta un documento associato.
		// Questo avviene perché in consultazione viene comunque messo in
		// sessione l'oggetto 'PRGDOCUMENTI' solo che è VUOTO!!!
		// Quindi le prime due condizioni sono verificate ma la terza (aggiunta)
		// fallisce.
		if (ses.getAttribute("PRGDOCUMENTI") != null && ses.getAttribute("PRGDOCUMENTI") instanceof Vector
				&& ((Vector) (ses.getAttribute("PRGDOCUMENTI"))).size() > 0) {

			if (!sessionOrigin.containsAttribute("PRGDOCUMENTI")
					|| sessionOrigin.getAttribute("PRGDOCUMENTI") == null) {
				Vector prgDoc = (Vector) ses.getAttribute("PRGDOCUMENTI");
				sessionOrigin.setAttribute("PRGDOCUMENTI", prgDoc);

			}
		}

		// Creazione del record extractor che si occupa di estrarre i dati dalla
		// request,
		// dall'oggetto in sessione e dal DB.
		String configbase = ConfigSingleton.getRootPath() + File.separator + "WEB-INF" + File.separator + "conf"
				+ File.separator + "import" + File.separator;
		String configFileName = configbase + "ManualValidationFieldsMapping.xml";
		RecordExtractor extr = null;
		try {
			extr = new ManualExtractor(request, null, sessionOrigin, configFileName);
		} catch (Exception e) {
			it.eng.sil.util.TraceWrapper.debug(_logger,
					"Impossibile istanziare l'estrattore per la rettifica del movimento, ", e);
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
			// Cambio dello stato atto del movimento originale
			validator.addProcessor(new RettificaMovimento("Registra rettifica", trans, user, request, response, req));
			// Selezione codice contratto
			RecordProcessor selectCodContr = new SelectCodContratto("Seleziona_Codice_Contratto", trans);
			validator.addProcessor(selectCodContr);
			// Prevalorizzazione campi mancanti
			validator.addProcessor(new PrevalorizzaCampi("Default campi mancanti"));
			// Controlli sulle autorizzazioni
			validator.addProcessor(new ControlloPermessi("Autorizzazione per impatti", trans));
			// controllo dei dati sensibili del lavoratore
			validator.addProcessor(new ProcControlloMbCmEtaLav("Controllo dati lavoratore", trans));
			// Processore per il controllo dell'esistenza di movimenti simili a
			// quello in inserimento
			validator.addProcessor(new ControlloMovimentoSimile("Controllo movimenti simili", trans, sbGenerale));
			// Processore che seleziona il movimento precedente
			validator.addProcessor(new SelectMovimentoPrecManuale("Seleziona Precedente", trans, null));
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
			RecordProcessor eseguiImpatti = new EseguiImpatti("Esecuzione impatti", sbGenerale, trans,
					(BigDecimal) getRequestContainer().getSessionContainer().getAttribute("_CDUT_"));
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
			// 07/02/2007 savino: CM incentivi art. 13
			validator.addProcessor(new InsertIncentiviArt_13(trans));
			// DAVIDE 14/02/2006: aggiunta una condizione nell'if. Modifica
			// effettuata per correggere il bug 1762:
			// mancata protocollazione in caso di rettifica di un movimento che
			// NON presenta un documento associato.
			// (per alcuni movimenti provenienti da porting non ho il documento
			// associato)
			// Questo avviene perché in consultazione viene comunque messo in
			// sessione l'oggetto 'PRGDOCUMENTI' solo che è VUOTO!!!
			// Quindi le prime due condizioni sono verificate ma la terza
			// (aggiunta) fallisce.
			if (ses.getAttribute("PRGDOCUMENTI") != null && ses.getAttribute("PRGDOCUMENTI") instanceof Vector
					&& ((Vector) (ses.getAttribute("PRGDOCUMENTI"))).size() > 0) {
				prgDocs = (Vector) (ses.getAttribute("PRGDOCUMENTI"));
				// Processore per l'inserimento del documento
				validator.addProcessor(new InsertDocumento(user, trans, prgDocs.size()));
			} else {

				// Processore per l'inserimento del documento
				validator.addProcessor(new InsertDocumento(user, trans));
			}

			// Imposto i dati necessari nel validator
			validator.setIdRecordKey("PRGMOVIMENTO");
			validator.setRecordExtractor(extr);

			// Esegue la processazione del record
			// NON FACCIO LOG SU FILE:
			// ResultLogger resultLogger = new HtmlResultLogger();
			// resultLogger.addChildResultLogger(new SingleResultLogger()); //
			// (x)->Html->DB
			// NON faccio LOG!
			// ResultLogger resultLogger = new SingleResultLogger();

			// SourceBean result = validator.importRecords(resultLogger, trans);
			result = validator.importRecords(null, trans);

			if (result != null) {
				// inserimento dei dati nella response del modulo
				response.setAttribute(result);

				// Controllo se l'inserimento ha avuto successo
				SourceBean recordResult = (SourceBean) result.getAttribute("RECORD");

				boolean isError = ProcessorsUtils.isError(recordResult);

				String codTipoMov = (String) request.getAttribute("CODTIPOMOV");
				if (!isError) {
					// Estraggo dalla risposta l'eventuale progressivo del movimento
					BigDecimal prgMov = (BigDecimal) result.getAttribute("RECORD.RECORDID");
					if (prgMov != null) {
						// Segnalo che la validazione è andata bene, per cambiare
						// contesto
						response.setAttribute("INSERT_OK", "TRUE");
						response.setAttribute("PRGMOVIMENTO", prgMov);
						// settare il contesto nella response
						response.setAttribute("CODTIPOMOV", codTipoMov);
						it.eng.sil.util.amministrazione.impatti.StatoOccupazionaleBean sOcc = null;
						sOcc = (it.eng.sil.util.amministrazione.impatti.StatoOccupazionaleBean) RequestContainer
								.getRequestContainer().getServiceRequest().getAttribute("stato_occupazionale");
						if (sOcc != null && sOcc.ischanged())
							response.setAttribute("CAMBIOSTATOOCC", sOcc.msgNuovoStato());
					}

					// Se è andato tutto bene allora cancello il campo in sessione
					ses.delAttribute("PRGDOCUMENTI");

				} else {
					response.setAttribute("INSERT_OK", "FALSE");
					// settare il contesto nella response
					response.setAttribute("CODTIPOMOV", codTipoMov);
				}
			}
		}

		catch (Exception e) {
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
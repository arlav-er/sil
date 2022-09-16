package it.eng.sil.module.amministrazione;

import java.math.BigDecimal;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.SessionContainer;
import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.base.SourceBeanException;

import it.eng.afExt.utils.MessageCodes;
import it.eng.sil.module.movimenti.MultipleTransactionQueryExecutor;
import it.eng.sil.module.movimenti.RecordExtractor;
import it.eng.sil.module.movimenti.RecordProcessor;
import it.eng.sil.module.movimenti.ResultLogger;
import it.eng.sil.module.movimenti.processors.ProcessorsUtils;

public class ValidatorNew {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(ValidatorNew.class.getName());
	private Vector processors = new Vector();
	private String idRecordKey = null;
	private RecordExtractor extractor = null;
	private boolean errorValidator = false;
	private int contesto;
	private BigDecimal prgValidazione = null;

	/**
	 * Variabile condivisa per fermare la validazione, va acceduta sempre in un blocco synchronized
	 */
	private boolean stop;

	/**
	 * Costruttore.
	 */
	public ValidatorNew() {
	}

	/**
	 * Metodo che processa un insieme di records dal RecordExtractor indicato come sorgente. <br>
	 * Ogni record è processato all'interno di una transazione. Se qualche processor restituisce errore viene fatto il
	 * rollback. Ogni processor ritorna un SourceBean che indica l'esito dell'operazione, tale SourceBean puo contenere
	 * un attributo RESULT che indica l'esito dell'operazione. Se tale attributo ha valore "ERROR" l'elaborazione del
	 * Record viene sospesa e si passa al successivo. Se l'attributo RESULT ha valore WARNING il risultato della
	 * processazione viene inserito nella risposta, ma l'elaborazione del record prosegue normalmente. Se il SourceBean
	 * non contiene tale attributo l'elaborazione procede senza alcuna risposta per l'utente.
	 * 
	 * @return Il SourceBean generato dal metodo ha la struttura riportata di seguito come DTD: <br>
	 *         &lt;!ELEMENT RECORDS (RECORD*)&gt; - Se vuoto l'elaborazione e andata a buon fine<br>
	 *         &lt;!ELEMENT RECORD ANY&gt; - Se presente ci sono stati errori o warning, contiene i tag specifici
	 *         dell'errore o della warning generata dal processore<br>
	 *         <br>
	 *         &lt;!ATTLIST RECORDS EXTRACTOR CDATA #REQUIRED&gt; - indica la classe dell'estrattore utilizzata<br>
	 *         &lt;!ATTLIST RECORDS PROCESSORS CDATA #REQUIRED&gt; - indica il numero di processori allocati<br>
	 *         &lt;!ATTLIST RECORDS PROCESSED CDATA #REQUIRED&gt; - indica il numero totale di record processati<br>
	 *         &lt;!ATTLIST RECORDS CORRECTPROCESSED CDATA #REQUIRED&gt; - indica il numero di record processati
	 *         correttamente<br>
	 *         &lt;!ATTLIST RECORDS WARNINGPROCESSED CDATA #REQUIRED&gt; - indica il numero di record processati con
	 *         problemi(WARNING)<br>
	 *         &lt;!ATTLIST RECORDS ERRORPROCESSED CDATA #REQUIRED&gt; - indica il numero di record che hanno generato
	 *         errori(ERROR)<br>
	 *         &lt;!ATTLIST RECORDS WARNINGTOTAL CDATA #REQUIRED&gt; - indica il numero totale di WARNING generate<br>
	 *         &lt;!ATTLIST RECORD RESULT (ERROR|WARNING) #REQUIRED&gt; - indica il livello di problemi riscontrato <br>
	 *         <p/>
	 * @param logger
	 *            Oggetto per la memorizzazione del log dell'operazione, se è null il log non viene fatto
	 */
	public SourceBean importRecords(ResultLogger logger, MultipleTransactionQueryExecutor trans)
			throws SourceBeanException {
		// SourceBean che contiene il risultato della validazione
		SourceBean result = new SourceBean("RECORDS");

		SessionContainer sessione = RequestContainer.getRequestContainer().getSessionContainer();

		if (extractor == null) {
			result.setAttribute("EXTRACTOR", "ERROR");
			SourceBean error = new SourceBean("ERROR");
			error.setAttribute("MESSAGE", "RecordExtractor per l'importazione nullo");
			result.setAttribute(error);
			_logger.debug("Impossibile importare i movimenti, RecordExtractor nullo");

		} else {
			result.setAttribute("EXTRACTOR", extractor.getClass().getName());
		}

		result.setAttribute("PROCESSORS", new Integer(processors.size()));

		// Mi dice quanti record ho processato, quanti non hanno presentato
		// errori, quanti presentano warning e quanti presentano
		// errori ed il numero di warning totale
		int processedTotali = 0;
		int processedOk = 0;
		int processedError = 0;
		int warningTotali = 0;

		// Itero sull'estrattore e processo ogni record con l'intero array di
		// RecordProcessor,
		// collezionando i risultati nel SourceBean
		boolean stopUtente = false;
		synchronized (this) {
			stopUtente = this.stop;
		}
		while (extractor.hasNext() && !stopUtente) {

			try {
				// Recupero i dati dalla tabella di appoggio
				Map recordData = (Map) extractor.next();

				// Se devo loggare il risultato salvo la map originale
				SourceBean mapLog = (logger != null ? getMapLog(recordData) : null);

				// Creo il SourceBean per i risultati
				SourceBean recordResult = new SourceBean("RECORD");

				boolean error = false;
				boolean warning = false;
				boolean stop = false;
				try {
					// Inizio una nuova transazione
					trans.initTransaction();
					// Ciclo sui processors
					for (int i = 0; (i < processors.size()) && (!error) && (!stop); i++) {
						// Richiamo di ogni processor
						SourceBean processResult = null;
						try {
							processResult = ((RecordProcessor) processors.get(i)).processRecord(recordData);
						} catch (Exception e) {
							processResult = ProcessorsUtils.createResponse("", "",
									new Integer(MessageCodes.LogOperazioniValidazioneMobilita.RECORD_VALIDAZIONE_NULLO),
									"", null, null);
						}
						// Esamino il singolo risultato
						if (processResult != null) {
							// Controllo se si tratta di errori
							if (ProcessorsUtils.isError(processResult)) {
								recordResult.setAttribute(processResult);
								error = true;
								this.setErrorValidator(true);
							}
							// Controllo se si tratta di warning
							else if (ProcessorsUtils.isWarning(processResult)) {
								recordResult.setAttribute(processResult);
								warning = true;
								warningTotali += 1;
								// Davide 20/09/2005: la gestione del mov doppio
								// è cambiata
								// Se troviamo un mov doppio con certe
								// caratteristiche aggiorniamo il mov in DB e
								// fermiamo
								// la processazione. NON è un errore però quindi
								// non dobbiamo fare ROLLBACK.
								if (ProcessorsUtils.isWarningAndSTOP(processResult)) {
									stop = true;
								}
							}
							// Altrimenti lo inserisco nelle warning e basta
							else {
								recordResult.setAttribute(processResult);
							}
						}
					}

					// Se non ho errori commit, altrimenti rollback
					if (!error) {
						trans.commitTransaction();
					} else {
						trans.rollBackTransaction();
					}
					// Traccio il risultato dell'elaborazione
					_logger.debug("Elaborazione del record numero " + (processedTotali + 1) + " conclusa con "
							+ (error ? "rollback" : "commit"));

					// Aggiungo l'eventuale identificatore del Record processato
					if ((idRecordKey != null) && (recordData.containsKey(idRecordKey))) {
						recordResult.setAttribute("RECORDID", recordData.get(idRecordKey));
					}

					BigDecimal prgID = null;
					// Imposto il risultato
					if (error) {
						recordResult.setAttribute("RESULT", "ERROR");
						processedError += 1;
						recordResult.setAttribute("PRGMOBILITAISCRAPP", recordData.get("PRGMOBILITAISCRAPP"));
					} else if (warning) {
						recordResult.setAttribute("RESULT", "WARNING");
						processedOk += 1;
						prgID = (BigDecimal) recordData.get("PRGMOBILITAISCR");
						recordResult.setAttribute("PRGMOBILITAISCR", prgID);
					} else {
						recordResult.setAttribute("RESULT", "OK");
						processedOk += 1;
						prgID = (BigDecimal) recordData.get("PRGMOBILITAISCR");
						recordResult.setAttribute("PRGMOBILITAISCR", prgID);
					}

					// Scrivo i risultati sul log se il logger non è nullo
					if (logger != null) {
						// Creo il sourceBean da serializzare nel log
						SourceBean recordLog = new SourceBean("LOG");
						recordLog.setAttribute(mapLog);
						recordLog.setAttribute(recordResult);
						try {
							// Lo serializzo
							logger.logResultGen(getPrgValidazione(), prgID,
									(BigDecimal) recordData.get("PRGMOBILITAISCRAPP"), recordLog, contesto);
							// Traccio che ho memorizzato su DB il log della
							// processazione
							_logger.debug("Log dell'elaborazione del " + "record numero " + (processedTotali + 1)
									+ " conclusa correttamente");

						} catch (Exception e) {
							// Traccio l'eccezione
							_logger.debug("Log dell'elaborazione del " + "record numero " + (processedTotali + 1)
									+ " fallito!!! " + "(messaggio:'" + e.getMessage() + "')");

							// Indico il fallimento della scrittura del log
							// nella risposta utente
							ProcessorsUtils.setLogError(recordResult,
									MessageCodes.LogOperazioniValidazioneMobilita.ERRORE_SCRITTURA_LOG);
						}
					}
				} catch (Exception e) {
					// Tracing dell'eccezione
					it.eng.sil.util.TraceWrapper.debug(_logger, "::importRecords():", e);

					// Rollback della transazione in caso di problemi
					trans.rollBackTransaction();
				}

				processedTotali += 1;

				// Inserisco comunque la risposta per notifica all'utente
				result.setAttribute(recordResult);

			} catch (Exception e) {
				it.eng.sil.util.TraceWrapper.debug(_logger,
						"::importRecords(): Eccezione durante l'elaborazione del record numero "
								+ (processedTotali + 1),
						e);

				// Ritorno comunque il risultato dell'elaborazione
				return result;
			}

			// controllo se mi devo fermare
			synchronized (this) {
				stopUtente = this.stop;
			}

			// FINE della processazione di UN singolo movimento

		} // end while

		// Se mi sono fermato per uno stop dell'utente registro la cosa nei
		// risultati globali della processazione
		if (stopUtente && logger != null) {
			try {
				logger.setStopUser(getContesto());
			} catch (Exception e) {
				// Se non ce la faccio a loggare proseguo comunque segnalando la
				// cosa
				it.eng.sil.util.TraceWrapper.debug(_logger,
						"::importRecords(): Errore nella registrazione dell'interruzione utente nel Log su DB", e);

			}

		}

		// setto le statistiche dell'elaborazione
		result.setAttribute("PROCESSED", new Integer(processedTotali));
		result.setAttribute("NUMRECORD", new Integer(extractor.getNumRecord()));
		result.setAttribute("FLGSTOPUSER", (stopUtente ? "S" : "N"));
		result.setAttribute("CORRECTPROCESSED", new Integer(processedOk));
		result.setAttribute("ERRORPROCESSED", new Integer(processedError));
		result.setAttribute("WARNINGTOTAL", new Integer(warningTotali));

		return result;

	}

	/**
	 * Metodo che setta i recordProcessor da utilizzare, i RecordProcessor così aggiunti saranno chiamti nell'ordine in
	 * cui sono aggiunti
	 * 
	 * @param processor
	 *            processore da aggiungere in fondo alla lista, se processor è null non lo aggiunge.
	 */
	public void addProcessor(RecordProcessor processor) {
		if (processor != null) {
			processors.add(processor);
		}
	}

	/**
	 * Metodo che rimuove tutti i recordProcessor da utilizzare, serve per cancellare il pool di processori istanziati.
	 * <p>
	 */
	public void clearProcessor() {
		processors = new Vector();
	}

	/**
	 * Metodo che setta i recordProcessor da utilizzare, i RecordProcessor così aggiunti saranno chiamti nell'ordine in
	 * cui sono aggiunti
	 * 
	 * @param processors
	 *            Vettore di RecordProcessor processore da aggiungere in fondo alla lista.
	 */
	public void addProcessor(Vector processorsVector) {
		if (processorsVector != null) {
			for (int i = 0; i < processorsVector.size(); i++) {
				Object proc = processorsVector.get(i);
				if (proc instanceof RecordProcessor) {
					this.addProcessor((RecordProcessor) proc);
				}
			}
		}
	}

	/**
	 * @param idRecordKey
	 *            Chiave che riferisce l'oggetto che identifica il record all'interno della Map, esso verrà inserito
	 *            automaticamente dal validator nella proprietà RECORDID del SourceBean creato come risposta. Se la
	 *            proprietà non è presente non verrà inserito nulla. Se il parametro è nullo non inserisce nulla.
	 *            L'inserimento viene effettuato al termine dell'elaborazione del record, in modo da poter essere
	 *            controllato dai processori utilizzati.
	 */
	public void setIdRecordKey(String key) {
		this.idRecordKey = key;
	}

	/**
	 * @param extractor
	 *            Estrattore da utilizzare per l'importazione
	 */
	public void setRecordExtractor(RecordExtractor extractor) {
		this.extractor = extractor;
	}

	/**
	 * Estrae dalla Map del record il SourceBean per il log
	 */
	private SourceBean getMapLog(Map record) throws SourceBeanException {
		SourceBean map = new SourceBean("MAP");
		Iterator i = record.keySet().iterator();
		while (i.hasNext()) {
			Object key = i.next();
			map.setAttribute(key.toString(), record.get(key));
		}
		return map;
	}

	/**
	 * Arresta la validazione corrente
	 */
	public synchronized void stop() {
		this.stop = true;
	}

	public void setErrorValidator(boolean valore) {
		this.errorValidator = valore;
	}

	public boolean getErrorValidator() {
		return errorValidator;
	}

	public void setContesto(int valore) {
		this.contesto = valore;
	}

	public int getContesto() {
		return contesto;
	}

	public void setPrgValidazione(BigDecimal valore) {
		this.prgValidazione = valore;
	}

	public BigDecimal getPrgValidazione() {
		return prgValidazione;
	}

}
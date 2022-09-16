package it.eng.sil.module.movimenti;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Vector;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.base.SourceBeanException;

import it.eng.afExt.utils.MessageCodes;
import it.eng.sil.module.movimenti.processors.ProcessorsUtils;

/**
 * Classe che si occupa di IMPORTARE (cioè inserire sulla tabella AM_MOVIMENTO_APPOGGIO) i movimenti da una sorgente:
 * per estrarli utilizza un RecordExtractor, una volta estratti li processa attraverso l'insieme di RecordProcessor
 * inseriti.
 * <p>
 * 
 * @author Paolo Roccetti
 */
public class Importer {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(Importer.class.getName());
	private Vector processors = new Vector();
	private String idRecordKey = null;
	private RecordExtractor extractor = null;

	/**
	 * Costruttore.
	 */
	public Importer() {
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
	 * @param trans
	 *            oggetto per l'esecuzione delle query in transazione
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
	 */
	public SourceBean importRecords(MultipleTransactionQueryExecutor trans) throws SourceBeanException {
		// SourceBean che contiene il risultato dell'importazione
		SourceBean result = new SourceBean("RECORDS");

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

		// Mi dice quanti record ho processato, quanti non hanno presentato errori, quanti presentano warning e quanti
		// presentano
		// errori ed il numero di warning totale
		int processedTotali = 0;
		int processedOk = 0;
		int processedError = 0;
		int warningTotali = 0;

		// Itero sull'estrattore e processo ogni record con l'intero array di RecordProcessor,
		// collezionando i risultati nel SourceBean
		boolean stopUtente = false;
		while (extractor.hasNext() && !stopUtente) {

			try {
				// Recupero i dati dalla tabella di appoggio
				Map recordData = (Map) extractor.next();

				// Creo il SourceBean per i risultati
				SourceBean recordResult = new SourceBean("RECORD");

				boolean error = false;
				boolean warning = false;
				try {
					// Inizio una nuova transazione
					trans.initTransaction();

					// Ciclo sui processors
					for (int i = 0; (i < processors.size()) && (!error); i++) {
						// Richiamo di ogni processor
						SourceBean processResult = null;
						try {
							processResult = ((RecordProcessor) processors.get(i)).processRecord(recordData);
						} catch (Exception e) {
							processResult = ProcessorsUtils.createResponse("", "",
									new Integer(MessageCodes.General.OPERATION_FAIL), "", null, null);
						}
						// Esamino il singolo risultato
						if (processResult != null) {
							// Controllo se si tratta di errori
							if (ProcessorsUtils.isError(processResult)) {
								recordResult.setAttribute(processResult);
								error = true;
							}
							// Controllo se si tratta di warning
							else if (ProcessorsUtils.isWarning(processResult)) {
								recordResult.setAttribute(processResult);
								warning = true;
								warningTotali += 1;
							}
							// Altrimenti lo inserisco nelle warning e basta
							else {
								recordResult.setAttribute(processResult);
							}
						}
					}

					// Se non ho errori allora commit, altrimenti rollback
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

					BigDecimal prgMovimento = null;
					// Imposto il risultato
					if (error) {
						recordResult.setAttribute("RESULT", "ERROR");
						processedError += 1;
					} else if (warning) {
						recordResult.setAttribute("RESULT", "WARNING");
						processedOk += 1;
						prgMovimento = (BigDecimal) recordData.get("PRGMOVIMENTO");
					} else {
						recordResult.setAttribute("RESULT", "OK");
						processedOk += 1;
						prgMovimento = (BigDecimal) recordData.get("PRGMOVIMENTO");
					}

				} catch (Exception e) {
					// Tracing dell'eccezione
					it.eng.sil.util.TraceWrapper.debug(_logger, "::importRecords():", e);

					// Rollback della transazione in caso di problemi
					trans.rollBackTransaction();
					SourceBean resultError = ProcessorsUtils.createResponse("", "",
							new Integer(MessageCodes.General.OPERATION_FAIL), "Errore connessione", null, null);
					recordResult.setAttribute(resultError);
					recordResult.setAttribute("RESULT", "ERROR");
					processedError += 1;
				}

				processedTotali += 1;

				// inserisco la risposta per l'utente se non è nulla
				if (recordResult != null) {
					result.setAttribute(recordResult);
				}

			} catch (Exception e) {
				it.eng.sil.util.TraceWrapper.debug(_logger,
						"::importRecords(): Eccezione durante l'elaborazione del record numero "
								+ (processedTotali + 1),
						e);

				// Ritorno comunque il risultato dell'elaborazione
				return result;
			}
			// FINE della processazione di UN singolo movimento

		} // end while

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
	 *            automaticamente dall'importer nella proprietà RECORDID del SourceBean creato come risposta. Se la
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

}
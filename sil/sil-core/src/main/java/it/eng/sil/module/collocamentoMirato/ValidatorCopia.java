package it.eng.sil.module.collocamentoMirato;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.base.SourceBeanException;

import it.eng.afExt.utils.MessageCodes;
import it.eng.sil.module.collocamentoMirato.constant.ProspettiConstant;
import it.eng.sil.module.collocamentoMirato.processors.RecordProcessor;
import it.eng.sil.module.movimenti.MultipleTransactionQueryExecutor;
import it.eng.sil.module.movimenti.ResultLogger;
import it.eng.sil.module.movimenti.processors.ProcessorsUtils;

public class ValidatorCopia {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(ValidatorCopia.class.getName());
	private Vector processors = new Vector();
	private String idRecordKey = null;
	private String idRecordNewKey = null;
	private ArrayList<BigDecimal> extractor = null;
	private BigDecimal prgValidazione = null;

	/**
	 * Variabile condivisa per fermare la validazione, va acceduta sempre in un blocco synchronized
	 */
	private boolean stop;

	public ValidatorCopia() {
	}

	public SourceBean importRecords(ResultLogger logger, MultipleTransactionQueryExecutor trans)
			throws SourceBeanException {
		// SourceBean che contiene il risultato della copia dei prospetti
		SourceBean result = new SourceBean("RECORDS");

		if (extractor == null) {
			result.setAttribute("EXTRACTOR", "ERROR");
			SourceBean error = new SourceBean("ERROR");
			error.setAttribute("MESSAGE", "Extractor per la copia prospetti nullo");
			result.setAttribute(error);
			_logger.debug("Impossibile effettuare la copia prospetti, Extractor nullo");

		} else {
			result.setAttribute("EXTRACTOR", "OK");
		}

		result.setAttribute("PROCESSORS", new Integer(processors.size()));

		int processedTotali = 0;
		int processedSCOk = 0;
		int processedACOk = 0;
		int processedError = 0;
		int warningTotali = 0;

		boolean stopUtente = false;
		synchronized (this) {
			stopUtente = this.stop;
		}
		int numProspettiTrattati = extractor.size();
		for (int nProsp = 0; nProsp < numProspettiTrattati; nProsp++) {
			try {
				// Recupero il prospetto da trattare
				BigDecimal keyRecord = extractor.get(nProsp);

				Map recordCurr = new HashMap();
				recordCurr.put(getIdRecordKey(), keyRecord);

				// Se devo loggare il risultato salvo la map originale
				SourceBean mapLog = (logger != null ? getMapLog(recordCurr) : null);

				// Creo il SourceBean per i risultati
				SourceBean recordResult = new SourceBean("RECORD");

				boolean error = false;
				boolean warning = false;
				boolean stop = false;
				boolean commitRollbackTrans = false;
				try {
					// Inizio una nuova transazione
					trans.initTransaction();
					// Ciclo sui processors
					for (int i = 0; (i < processors.size()) && (!error) && (!stop); i++) {
						// Chiamata al processor i-esimo
						SourceBean processResult = null;
						try {
							processResult = ((RecordProcessor) processors.get(i)).processRecord(recordCurr);
						} catch (Exception e) {
							recordCurr.put(ProspettiConstant.FIELD_CODICE, ProspettiConstant.COD_ERRORE_GENERICO);
							processResult = ProcessorsUtils.createResponse("", "",
									new Integer(MessageCodes.LogOperazioniCopiaProspetti.RECORD_COPIA_PROSPETTO_NULLO),
									"", null, null);
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
								if (ProcessorsUtils.isWarningAndSTOP(processResult)) {
									stop = true;
								}
							} else {
								recordResult.setAttribute(processResult);
							}
						}
					}

					// Se non ho errori commit, altrimenti rollback
					if (!error) {
						trans.commitTransaction();
						commitRollbackTrans = true;
					} else {
						trans.rollBackTransaction();
						commitRollbackTrans = true;
					}

					// Traccio il risultato dell'elaborazione
					_logger.debug("Elaborazione del record numero " + (processedTotali + 1) + " conclusa con "
							+ (error ? "rollback" : "commit"));

					// Aggiungo l'eventuale identificatore del Record processato
					if (keyRecord != null) {
						recordResult.setAttribute("RECORDID", keyRecord);
					}

					BigDecimal prgIDNew = null;
					String codRisultato = "";
					String descRisultato = "";
					// Imposto il risultato
					if (error) {
						recordResult.setAttribute("RESULT", "ERROR");
						processedError += 1;
						recordResult.setAttribute(getIdRecordKey(), keyRecord);
						codRisultato = recordCurr.containsKey(ProspettiConstant.FIELD_CODICE)
								? recordCurr.get(ProspettiConstant.FIELD_CODICE).toString()
								: "";
					} else {
						if (warning) {
							recordResult.setAttribute("RESULT", "WARNING");
						} else {
							recordResult.setAttribute("RESULT", "OK");
						}
						prgIDNew = (BigDecimal) recordCurr.get(getIdRecordNewKey());
						recordResult.setAttribute(getIdRecordNewKey(), prgIDNew);
						codRisultato = recordCurr.containsKey(ProspettiConstant.FIELD_CODICE)
								? recordCurr.get(ProspettiConstant.FIELD_CODICE).toString()
								: "";
						if (codRisultato.equalsIgnoreCase(ProspettiConstant.COD_STORICIZZATO_COPIATO)) {
							processedSCOk += 1;
						} else {
							processedACOk += 1;
						}
					}

					descRisultato = recordCurr.containsKey(ProspettiConstant.FIELD_DESC_RISULTATO)
							? recordCurr.get(ProspettiConstant.FIELD_DESC_RISULTATO).toString()
							: "";

					// Scrivo i risultati sul log se il logger non è nullo
					if (logger != null) {
						// Creo il sourceBean da serializzare nel log
						SourceBean recordLog = new SourceBean("LOG");
						recordLog.setAttribute(mapLog);
						recordLog.setAttribute(recordResult);
						try {
							logger.logResultGenCopiaProsp(getPrgValidazione(), codRisultato, descRisultato, prgIDNew,
									keyRecord, recordLog);
							// Traccio che ho memorizzato su DB il log della processazione
							_logger.debug("Log dell'elaborazione del " + "record numero " + (processedTotali + 1)
									+ " conclusa correttamente");

						} catch (Exception e) {
							// Traccio l'eccezione
							_logger.debug("Log dell'elaborazione del " + "record numero " + (processedTotali + 1)
									+ " fallito!!! " + "(messaggio:'" + e.getMessage() + "')");

							// Indico il fallimento della scrittura del log
							// nella risposta utente
							ProcessorsUtils.setLogError(recordResult,
									MessageCodes.LogOperazioniCopiaProspetti.ERRORE_SCRITTURA_LOG);
						}
					}
				} catch (Exception e) {
					// Tracing dell'eccezione
					it.eng.sil.util.TraceWrapper.debug(_logger, "::importRecords():", e);
					// Rollback della transazione in caso di problemi
					if (!commitRollbackTrans) {
						processedError += 1;
						trans.rollBackTransaction();
					}
				}

				processedTotali += 1;

				// Inserisco comunque la risposta per notifica all'utente
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

			// controllo se mi devo fermare
			synchronized (this) {
				stopUtente = this.stop;
			}

			// FINE della processazione di UN singolo prospetto

		} // fine FOR

		// setto le statistiche dell'elaborazione
		result.setAttribute("PROCESSED", new Integer(processedTotali));
		result.setAttribute("NUMRECORD", new Integer(numProspettiTrattati));
		result.setAttribute("FLGSTOPUSER", (stopUtente ? "S" : "N"));
		result.setAttribute("CORRECTPROCESSEDSC", new Integer(processedSCOk));
		result.setAttribute("CORRECTPROCESSEDAC", new Integer(processedACOk));
		result.setAttribute("ERRORPROCESSED", new Integer(processedError));
		result.setAttribute("WARNINGTOTAL", new Integer(warningTotali));

		return result;

	}

	/**
	 * Aggiunge i processor da invocare
	 * 
	 * @param processor
	 */
	public void addProcessor(RecordProcessor processor) {
		if (processor != null) {
			processors.add(processor);
		}
	}

	/**
	 * Metodo che rimuove tutti i recordProcessor da utilizzare
	 */
	public void clearProcessor() {
		processors = new Vector();
	}

	public void setIdRecordKey(String key) {
		this.idRecordKey = key;
	}

	public String getIdRecordKey() {
		return this.idRecordKey;
	}

	public void setIdRecordNewKey(String key) {
		this.idRecordNewKey = key;
	}

	public String getIdRecordNewKey() {
		return this.idRecordNewKey;
	}

	public void setRecordExtractor(ArrayList<BigDecimal> extractor) {
		this.extractor = extractor;
	}

	private SourceBean getMapLog(Map record) throws SourceBeanException {
		SourceBean map = new SourceBean("MAP");
		Iterator i = record.keySet().iterator();
		while (i.hasNext()) {
			Object key = i.next();
			map.setAttribute(key.toString(), record.get(key));
		}
		return map;
	}

	public synchronized void stop() {
		this.stop = true;
	}

	public void setPrgValidazione(BigDecimal valore) {
		this.prgValidazione = valore;
	}

	public BigDecimal getPrgValidazione() {
		return prgValidazione;
	}

}
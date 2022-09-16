/*
 * Creato il 28-dic-04
 */
package it.eng.sil.module.movimenti;

import java.util.Iterator;
import java.util.Map;
import java.util.Vector;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.base.SourceBeanException;

import it.eng.afExt.utils.MessageCodes;
import it.eng.afExt.utils.TransactionQueryExecutor;
import it.eng.sil.module.movimenti.processors.ProcessorsUtils;

/**
 * Classe che effettua l'inserimento/validazione dell'assunzione da cessazione utilizzando i RecordProcessor impostati
 * <p>
 * 
 * @author Paolo Roccetti
 */
public class ValidatorAssunzioneCVE {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(ValidatorAssunzioneCVE.class.getName());
	private Vector processors = new Vector();
	private boolean stopAvvVeloce = false;

	/**
	 * Costruttore.
	 */
	public ValidatorAssunzioneCVE() {
	}

	/**
	 * Questo metodo effettua l'inserimento/validazione dell'assunzione da cessazione passata come argomento
	 */
	public SourceBean validaAvvDaCVE(Map avviamento, ResultLogger logger, TransactionQueryExecutor trans)
			throws SourceBeanException {
		// Se devo loggare il risultato salvo la map originale
		// SourceBean mapLog = (logger != null ? getMapLog(avviamento) : null);

		// Creo il SourceBean per i risultati
		SourceBean recordResult = new SourceBean("RECORD");

		boolean error = false;
		boolean warning = false;

		try {
			// Ciclo sui processors
			for (int i = 0; (i < processors.size()) && (!error) && (!getStopAvvVeloce()); i++) {
				// Richiamo di ogni processor
				SourceBean processResult = null;
				try {
					processResult = ((RecordProcessor) processors.get(i)).processRecord(avviamento);
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
						if (ProcessorsUtils.isWarningAndSTOP(processResult)) {
							setStopAvvVeloce(true);
						}
					}
					// Altrimenti lo inserisco nelle warning e basta
					else {
						recordResult.setAttribute(processResult);
					}
				}
			}

			// Traccio il risultato dell'elaborazione
			_logger.debug(
					"Elaborazione dell'avviamento da CEV" + " conclusa " + (error ? "con errori" : "correttamente"));

			// BigDecimal prgMovimento = null;
			// Imposto il risultato
			if (error) {
				recordResult.setAttribute("RESULT", "ERROR");
			} else if (warning) {
				recordResult.setAttribute("RESULT", "WARNING");
				// prgMovimento = (BigDecimal) avviamento.get("PRGMOVIMENTO");
			} else {
				recordResult.setAttribute("RESULT", "OK");
				// prgMovimento = (BigDecimal) avviamento.get("PRGMOVIMENTO");
			}

			/*
			 * DISABILITATO PER PROBLEMI DI DEADLOCK, magari crearne uno che usi la transazione globale relativa alla
			 * cessazione. Il DEADLOCK è dovuto al campo prgmovimento che deve essere inserito nel log. Non essendo
			 * stato ancora committato il record dell'avviamento tale campo viene visto da ORACLE ma la query attenda
			 * che venga committato per terminare... generando il deadlock
			 * 
			 * 
			 * //Scrivo i risultati sul log se il logger non è nullo if (logger != null) { //Creo il sourceBean da
			 * serializzare nel log SourceBean recordLog = new SourceBean("LOG"); recordLog.setAttribute(mapLog);
			 * recordLog.setAttribute(recordResult); try { //Lo serializzo logger.logResult(null, prgMovimento,
			 * (BigDecimal) avviamento.get("PRGMOVIMENTOAPP"), recordLog); //Traccio che ho memorizzato su DB il log
			 * della processazione _logger.debug( "Log dell'elaborazione dell'avviamento da CEV "+ " conclusa
			 * correttamente"); } catch (Exception e) { //Traccio l'eccezione _logger.debug(
			 * "Log dell'elaborazione dell'avviamento da CEV"+ " fallito!!! (messaggio:'" + e.getMessage() + "')"););
			 * 
			 * //Indico il fallimento della scrittura del log nella risposta utente
			 * ProcessorsUtils.setLogError(recordResult, MessageCodes.LogOperazioniMovimenti.ERRORE_SCRITTURA_LOG); } }
			 */
		} catch (Exception e) {
			// Tracing dell'eccezione
			it.eng.sil.util.TraceWrapper.debug(_logger, "::validaAvvDaCVE():", e);

		}
		return recordResult;
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

	public void setStopAvvVeloce(boolean val) {
		this.stopAvvVeloce = val;
	}

	public boolean getStopAvvVeloce() {
		return this.stopAvvVeloce;
	}
}

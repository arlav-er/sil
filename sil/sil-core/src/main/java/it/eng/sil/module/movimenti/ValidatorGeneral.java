package it.eng.sil.module.movimenti;

import java.util.Map;
import java.util.Vector;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.base.SourceBeanException;

import it.eng.afExt.utils.MessageCodes;
import it.eng.sil.module.movimenti.processors.ProcessorsUtils;

public class ValidatorGeneral {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(ValidatorGeneral.class.getName());
	private Vector processors = new Vector();
	private Map recordData = null;
	private boolean erroreValidatorGeneral = false;

	public ValidatorGeneral(Map record) {
		this.recordData = record;
	}

	/**
	 * 
	 * @param logger
	 * @param trans
	 * @return
	 * @throws SourceBeanException
	 */
	public SourceBean importRecords(MultipleTransactionQueryExecutor trans) throws SourceBeanException {
		// Mi dice quanti record ho processato, quanti non hanno presentato
		// errori, quanti presentano warning e quanti presentano
		// errori ed il numero di warning totale
		int processedTotali = 0;
		int processedOk = 0;
		int processedError = 0;
		int warningTotali = 0;
		SourceBean recordResult = null;

		try {
			// Creo il SourceBean per i risultati
			recordResult = new SourceBean("RECORD");
			boolean error = false;
			boolean warning = false;
			boolean stop = false;

			// Ciclo sui processors
			for (int i = 0; (i < processors.size()) && (!error) && (!stop); i++) {
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

			if (error) {
				setErroreValidatorGeneral(true);
				recordResult.setAttribute("RESULT", "ERROR");
			} else if (warning) {
				recordResult.setAttribute("RESULT", "WARNING");
			} else {
				recordResult.setAttribute("RESULT", "OK");
			}

		}

		catch (Exception e) {
			setErroreValidatorGeneral(true);
			recordResult.setAttribute("RESULT", "ERROR");
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

	public void setErroreValidatorGeneral(boolean value) {
		erroreValidatorGeneral = value;
	}

	public boolean getErroreValidatorGeneral() {
		return erroreValidatorGeneral;
	}

}
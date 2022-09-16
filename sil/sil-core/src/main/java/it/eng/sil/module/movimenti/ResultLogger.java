/*
 * Creato il 4-nov-04
 */
package it.eng.sil.module.movimenti;

import java.math.BigDecimal;

import com.engiweb.framework.base.SourceBean;

/**
 * Interfaccia per il log dei risultati della validazione/inserimento dei movimenti
 * 
 * @author Roccetti / Luigi Antenucci
 */
public abstract class ResultLogger {
	protected ResultLogger childResultLogger = null; // il logger figlio

	/**
	 * metodo per il log del risultato
	 * 
	 * @param prgValidazioneMassiva
	 * @param prgID
	 * @param prgIDApp
	 * @param result
	 * @param contesto
	 * @throws LogException
	 */
	public void logResultGen(BigDecimal prgValidazioneMassiva, BigDecimal prgID, BigDecimal prgIDApp, SourceBean result,
			int contesto) throws LogException {

		LogException myLE = null; // Qua salvo l'eccezione generata.

		// Eseguo il LOG tramite questa classe (uso logResultImpl)
		try {
			logResultImplGen(prgValidazioneMassiva, prgID, prgIDApp, result, contesto);
		} catch (LogException le) {
			// Prendo e salvo l'eccezione per poterla rilanciare soltanto
			// dopo aver fatto fare il LOG anche a logger figlio e seguenti.
			myLE = le;
		}

		// Faccio fare il log anche al LOGGER FIGLIO (e ricorsivamente ai
		// sottofigli)
		// Se mi genera un'eccezione, la rilancio solo se non ne avevo già una
		// in myLE
		// (cioè così rilancio la prima eccezione generata dalla catena).
		try {
			if (childResultLogger != null) {
				childResultLogger.logResultGen(prgValidazioneMassiva, prgID, prgIDApp, result, contesto);
			}
		} catch (LogException le) {
			if (myLE == null) {
				myLE = le;
			}
		}

		// Solo ora rilancio l'ECCEZIONE (se ce n'é stata una)!
		if (myLE != null) {
			throw myLE;
		}
	}

	public void logResultGenCopiaProsp(BigDecimal prgValidazioneMassiva, String codRisultato, String descRisultato,
			BigDecimal prgIDNew, BigDecimal prgID, SourceBean result) throws LogException {

		LogException myLE = null; // Qua salvo l'eccezione generata.

		// Eseguo il LOG tramite questa classe (uso logResultImpl)
		try {
			logResultImplGenCopiaProsp(prgValidazioneMassiva, codRisultato, descRisultato, prgIDNew, prgID, result);
		} catch (LogException le) {
			myLE = le;
		}

		try {
			if (childResultLogger != null) {
				childResultLogger.logResultGenCopiaProsp(prgValidazioneMassiva, codRisultato, descRisultato, prgIDNew,
						prgID, result);
			}
		} catch (LogException le) {
			if (myLE == null) {
				myLE = le;
			}
		}

		// Solo ora rilancio l'ECCEZIONE (se ce n'è stata una)!
		if (myLE != null) {
			throw myLE;
		}
	}

	/**
	 * Metodo per il log di un risultato E' il metodo che viene invocato effettivamente per fare il LOG. Per la
	 * particolare implementazione dell'algoritmo di logging viene invocato il metodo "logResult" (specializzato nella
	 * sottoclasse). Questo metodo va a invocare il "logResult" dell'eventuale oggetto di logger figlio di questa
	 * classe.
	 */
	public void logResult(BigDecimal prgValidazioneMassiva, BigDecimal prgMovimento, BigDecimal prgMovimentoApp,
			SourceBean result) throws LogException {

		LogException myLE = null; // Qua salvo l'eccezione generata.

		// Eseguo il LOG tramite questa classe (uso logResultImpl)
		try {
			logResultImpl(prgValidazioneMassiva, prgMovimento, prgMovimentoApp, result);
		} catch (LogException le) {
			// Prendo e salvo l'eccezione per poterla rilanciare soltanto
			// dopo aver fatto fare il LOG anche a logger figlio e seguenti.
			myLE = le;
		}

		// Faccio fare il log anche al LOGGER FIGLIO (e ricorsivamente ai
		// sottofigli)
		// Se mi genera un'eccezione, la rilancio solo se non ne avevo già una
		// in myLE
		// (cioè così rilancio la prima eccezione generata dalla catena).
		try {
			if (childResultLogger != null) {
				childResultLogger.logResult(prgValidazioneMassiva, prgMovimento, prgMovimentoApp, result);
			}
		} catch (LogException le) {
			if (myLE == null) {
				myLE = le;
			}
		}

		// Solo ora rilancio l'ECCEZIONE (se ce n'è stata una)!
		if (myLE != null) {
			throw myLE;
		}
	}

	/**
	 * Metodo per il log di un risultato, da implementare nelle sottoclassi! Questo metodo viene invocato da
	 * "logResult".
	 * 
	 * @param prgMovimentoApp
	 *            progressivo del movimento sulla tabella di appoggio (null per inserimento)
	 * @param prgValidazioneMassiva
	 *            progressivo del gruppo di risultati in validazione massiva
	 * @param prgMovimento
	 *            progressivo del movimento inserito/validato
	 * @param result
	 *            oggetto da serializzare nel log (null se il movimento non è stato inserito/validato)
	 * 
	 * @throws LogException
	 *             quando si verificano problemi nella scrittura del log
	 */
	public abstract void logResultImplGen(BigDecimal prgValidazioneMassiva, BigDecimal prgID, BigDecimal prgIDApp,
			SourceBean result, int contesto) throws LogException;

	public abstract void logResultImplGenCopiaProsp(BigDecimal prgValidazioneMassiva, String codRisultato,
			String descRisultato, BigDecimal prgIDNew, BigDecimal prgID, SourceBean result) throws LogException;

	/**
	 * Metodo per il log di un risultato, da implementare nelle sottoclassi! Questo metodo viene invocato da
	 * "logResult".
	 * 
	 * @param prgMovimentoApp
	 *            progressivo del movimento sulla tabella di appoggio (null per inserimento)
	 * @param prgValidazioneMassiva
	 *            progressivo del gruppo di risultati in validazione massiva
	 * @param prgMovimento
	 *            progressivo del movimento inserito/validato
	 * @param result
	 *            oggetto da serializzare nel log (null se il movimento non è stato inserito/validato)
	 * 
	 * @throws LogException
	 *             quando si verificano problemi nella scrittura del log
	 */
	public abstract void logResultImpl(BigDecimal prgValidazioneMassiva, BigDecimal prgMovimento,
			BigDecimal prgMovimentoApp, SourceBean result) throws LogException;

	/**
	 * Metodo per il log dell'interruzione da parte dell'utente; da implementare nelle sottoclassi!
	 * 
	 * @throws LogException
	 *             quando si verificano problemi nella scrittura del log
	 */
	public abstract void setStopUser() throws LogException;

	/**
	 * metodo per il log dell'interruzione da parte dell'utente
	 * 
	 * @param contesto
	 * @throws LogException
	 */
	public abstract void setStopUser(int contesto) throws LogException;

	/**
	 * Accoda un nuovo logger (della medesima classe ResultLogger) alla catena a cui appartiene questo logger. Ogni
	 * logger può avere un solo figlio, il quale può a sua volta averne uno. Quindi: Se questo logger non ha un figlio,
	 * si prende come tale quello dato in ingresso; altrimenti, chiede al suo figlio attuale di accodare a sè il nuovo
	 * logger.
	 */
	public void addChildResultLogger(ResultLogger newResultLogger) {

		// se non ho un logger figlio, imposto come figlio quello dato
		if (childResultLogger == null) {
			childResultLogger = newResultLogger;
		}
		// altrimenti, chiedo al figlio di accodare a sé il nuovo logger.
		else {
			childResultLogger.addChildResultLogger(newResultLogger);
		}
	}
}

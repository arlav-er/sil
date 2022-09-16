/*
 * Creato il Nov 9, 2004
 *
 * Per modificare il modello associato a questo file generato, aprire
 * Finestra&gt;Preferenze&gt;Java&gt;Generazione codice&gt;Codice e commenti
 */
package it.eng.sil.module.movimenti.processors;

import java.util.ArrayList;
import java.util.Map;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.base.SourceBeanException;
import com.engiweb.framework.base.XMLObject;

import it.eng.afExt.utils.TransactionQueryExecutor;
import it.eng.sil.module.movimenti.RecordProcessor;

/**
 * @author savino
 * 
 *         Classe che implementa alcune funzionalita' di base del processor: 1) generazione delle risposte di errore e/o
 *         di warning 2) scrittura nel log applicativo
 * 
 */
public abstract class BaseProcessor implements RecordProcessor {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(BaseProcessor.class.getName());
	protected String name;
	protected String className = getClass().getName();
	protected TransactionQueryExecutor txExec;
	/**
	 * Bisogna istanziare ad ogni chiamata i vettori dei risultati. Questo per evitare che nella validazione massiva le
	 * informazioni rimangano "sporche".
	 */
	protected ArrayList warnings = null;
	// Vettore dei risultati annidati da restituire
	protected ArrayList nested = null;

	public BaseProcessor(String name, TransactionQueryExecutor txExec) {
		this.name = name;
		this.txExec = txExec;
	}

	public abstract SourceBean processRecord(Map record) throws SourceBeanException;

	/**
	 * Crea la risposta contenente un errore.
	 * 
	 * @param code
	 *            il codice di errore
	 * @param msg
	 *            il messaggio di errore
	 * @return
	 * @throws SourceBeanException
	 */
	protected SourceBean createResponse(int code, String msg) throws SourceBeanException {
		return ProcessorsUtils.createResponse(name, className, new Integer(code), msg, warnings, nested);
	}

	/**
	 * Crea la risposta di warning.
	 * 
	 * @return
	 * @throws SourceBeanException
	 */
	protected SourceBean createResponse() throws SourceBeanException {
		return ProcessorsUtils.createResponse(name, className, null, null, warnings, nested);
	}

	protected void logErr(String s) {
		_logger.debug(className + ":" + s);

	}

	protected void logErr(String s, Exception e) {
		it.eng.sil.util.TraceWrapper.debug(_logger, className + ":" + s, e);

	}

	protected void logErr(String s, XMLObject e) {
		it.eng.sil.util.TraceWrapper.debug(_logger, className + ":" + s, e);

	}

	protected void addWarning(int code, String msg) {
		warnings.add(new Warning(code, msg));
	}
	/**
	 * Le informazioni relative ai risultati del processor vengono rimosse.
	 */
	/*
	 * protected void clear() { if (this.warnings !=null) this.warnings.clear(); if (this.nested !=null)
	 * this.nested.clear(); }
	 */
}
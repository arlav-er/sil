/*
 * Creato il 14-ott-05
 *
 * Per modificare il modello associato a questo file generato, aprire
 * Finestra&gt;Preferenze&gt;Java&gt;Generazione codice&gt;Codice e commenti
 */
package it.eng.sil.action.report;

import com.engiweb.framework.error.EMFUserError;

/**
 * @author dauria
 * 
 *         Per modificare il modello associato al commento di questo tipo generato, aprire
 *         Finestra&gt;Preferenze&gt;Java&gt;Generazione codice&gt;Codice e commenti
 */
public class ReportException extends EMFUserError {

	/**
	 * @param severity
	 * @param code
	 */
	public ReportException(String severity, int code) {
		super(severity, code);
	}

}

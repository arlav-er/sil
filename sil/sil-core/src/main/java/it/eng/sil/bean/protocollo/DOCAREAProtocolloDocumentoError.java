/*
 * Created on Mar 7, 2007
 */
package it.eng.sil.bean.protocollo;

import java.util.Vector;

import com.engiweb.framework.error.EMFUserError;

/**
 * Eccezione lanciata quando il web service di docarea ritorna un codice di errore.
 * 
 * @author savino
 */
public class DOCAREAProtocolloDocumentoError extends EMFUserError {

	/**
	 * @param severity
	 *            EMFErrorSeverity.ERROR|BLOCKING
	 * @param code
	 *            codice mappato nel file messages_it_IT.properties
	 * @param codErroreDOCAREA
	 *            il codice errore ritornato dal web service. E' mappato in un file xml.
	 * @param v
	 *            il vettore dei parametri del messaggio
	 */
	public DOCAREAProtocolloDocumentoError(String severity, int code, long codErroreDOCAREA, Vector v) {
		super(severity, code, v);
		setDescription(getDescription() + " Codice errore ricevuto: " + codErroreDOCAREA);
	}

}

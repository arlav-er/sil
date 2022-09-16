/*
 * Creato il 30-apr-04
 * Author: vuoto
 * 
 */
package it.eng.sil.util.grabber;

/**
 * @author vuoto
 * 
 */
public class PersonaFiscaleException extends Exception {

	public static int ERR_PERSONA_NON_PRESENTE_IN_ARCHIVIO = 1;
	public static int ERR_OPERAZIONE_GIA_EFFETTUATA = 2;
	public static int ERR_NELLA_COMUNICAZIONE = 3;
	public static int ERR_CF_NON_VALIDO = 4;
	public static int ERR_CF_NON_ATTRIBUITO = 5;

	int errorCode = 0;

	public PersonaFiscaleException() {
		super();
	}

	public PersonaFiscaleException(String msg) {
		super();
	}

	public PersonaFiscaleException(int errorCode, String msg) {
		super(msg);
		this.errorCode = errorCode;

	}

	/**
	 * @return
	 */
	public int getErrorCode() {
		return errorCode;
	}

}

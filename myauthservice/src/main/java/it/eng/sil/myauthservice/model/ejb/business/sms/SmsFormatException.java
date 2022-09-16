package it.eng.sil.myauthservice.model.ejb.business.sms;

/**
 * @author vuoto
 * 
 * Per modificare il modello associato al commento di questo tipo generato,
 * aprire Finestra&gt;Preferenze&gt;Java&gt;Generazione codice&gt;Codice e
 * commenti
 */
public class SmsFormatException extends Exception {
	public static final int ERR_N_CELL_NULLO = 1;
	public static final int ERR_N_CELL_NON_VALIDO = 2;
	public static final int ERR_TESTO_NULLO = 3;
	public static final int ERR_TESTO_TROPPO_LUNGO = 4;

	private int errorCode = 0;

	public SmsFormatException(int codice, String errorMsg) {
		super(errorMsg);
		errorCode = codice;
	}

	/**
	 * @return
	 */
	public int getErrorCode() {
		return errorCode;
	}

}

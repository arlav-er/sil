package it.eng.sil.myauthservice.model.ejb.business.sms;

/**
 * @author vuoto
 * 
 * Per modificare il modello associato al commento di questo tipo generato,
 * aprire Finestra&gt;Preferenze&gt;Java&gt;Generazione codice&gt;Codice e
 * commenti
 */

public class SmsException extends Exception {

	private static final long serialVersionUID = 6284699589758045048L;
	


	public SmsException(Exception e) {
		super(e);
	}

	public SmsException(String e) {
		super(e);
	}


}

package it.eng.sil.myauthservice.model.ejb.business.onesignal;

public class NoRecipientException extends ProviderNotificationException {

	private static final long serialVersionUID = 3587500201572155883L;

	public NoRecipientException(String message) {
		super(message);
	}

}

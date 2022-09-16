package it.eng.myportal.rest.app.exception;

public class NoRecipientException extends ProviderNotificationException {

	private static final long serialVersionUID = 3587500201572155883L;

	public NoRecipientException(String message) {
		super(message);
	}

}

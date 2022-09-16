package it.eng.myportal.rest.app.exception;

public class OneSignalException extends ProviderNotificationException {

	private static final long serialVersionUID = 1773187401478847218L;

	public OneSignalException(String messaggio) {
		super(messaggio);
	}

	public OneSignalException(String message, Throwable cause) {
		super(message, cause);
	}
}

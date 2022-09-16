package it.eng.sil.coop.webservices.statooccupazionale;

public class InputValidationException extends Exception {
	public InputValidationException(String message) {
		super(message);
	}

	public InputValidationException(String message, Throwable cause) {
		super(message, cause);
	}

}

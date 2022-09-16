package it.eng.myportal.rest.app.exception;

public class GenericException extends AppEjbException {
	
	private static final long serialVersionUID = 9002468832197781500L;

	public GenericException(String message) {
		super(GENERIC_CODE, message);
	}

}

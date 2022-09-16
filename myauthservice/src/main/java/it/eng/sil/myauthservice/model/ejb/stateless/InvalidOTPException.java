package it.eng.sil.myauthservice.model.ejb.stateless;

public class InvalidOTPException extends Exception {

	public InvalidOTPException(String string) {
		super(string);
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1753515233207539347L;

}

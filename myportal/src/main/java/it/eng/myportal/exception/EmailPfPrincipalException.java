package it.eng.myportal.exception;

import javax.ejb.EJBException;

public class EmailPfPrincipalException extends EJBException {

	
	private static final long serialVersionUID = -4571862980241295972L;

	public EmailPfPrincipalException() {
		super();
	}

	public EmailPfPrincipalException(String message) {
		super(message);
	}

	
}

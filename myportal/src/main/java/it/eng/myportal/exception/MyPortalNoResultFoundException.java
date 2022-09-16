package it.eng.myportal.exception;

import javax.ejb.ApplicationException;

@ApplicationException
public class MyPortalNoResultFoundException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3769697629885882139L;

	public MyPortalNoResultFoundException(Exception ex) {
		super(ex);
	}

	public MyPortalNoResultFoundException() {
		super();
	}

	public MyPortalNoResultFoundException(String string) {
		super(string);
	}

}

package it.eng.myportal.exception;

import javax.ejb.ApplicationException;

@ApplicationException
public class MyPortalNotFoundException extends Exception {

	public MyPortalNotFoundException(String string) {
		super(string);
	}

	public MyPortalNotFoundException() {
		super();
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = -6207483297609708166L;

}

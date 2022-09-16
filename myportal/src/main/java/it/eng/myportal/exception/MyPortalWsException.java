package it.eng.myportal.exception;

import javax.ejb.ApplicationException;

@ApplicationException
public class MyPortalWsException extends Exception {

	public MyPortalWsException(String string) {
		super(string);
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = -7062957338650284322L;

}

package com.engiweb.framework.base;

/**
 * La classe <code>SourceBeanException</code> implementa un eccezione generica lanciata in conseguenza di errori nei
 * servizi degli oggetti <code>SourceBeanAttribute</code> e <code>SourceBean</code>.
 * 
 * @version 1.0, 11/03/2002
 * @author Luigi Bellio
 * @see SourceBeanAttribute
 * @see SourceBean
 */
public class SourceBeanException extends Exception {
	/**
	 * Costruisce ed inizializza l'eccezione.
	 */
	public SourceBeanException(String message) {
		if (message == null)
			_message = "NOT DEFINED";
		else
			_message = message;
	} // public SourceBeanException(String message)

	/**
	 * Ritorna il messaggio associato all'eccezione.
	 */
	public String getMessage() {
		return _message;
	} // public String getMessage()

	/**
	 * Messaggio associato all'eccezione.
	 */
	private String _message = null;
} // public class SourceBeanException extends Exception

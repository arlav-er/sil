package com.engiweb.framework.error;

/**
 * Questa classe gestisce le possibili severity utilizzabili all'atto della costruzione di un errore.
 * 
 * @author Luigi Bellio
 * @see EMFErrorHandler
 */
public class EMFErrorSeverity {
	/**
	 * Ritorna un <code>boolean</code> con il seguente significato: <em>true</em> il parametro severity in input è
	 * valido. <em>false</em> il parametro severity in input non è valido.
	 * 
	 * @return <code>boolean</code> la validità della severity passata in input.
	 */
	public static boolean isSeverityValid(String severity) {
		if ((severity == null) || ((severity != null) && !severity.equals(EMFErrorSeverity.INFORMATION)
				&& !severity.equals(EMFErrorSeverity.WARNING) && !severity.equals(EMFErrorSeverity.ERROR)
				&& !severity.equals(EMFErrorSeverity.BLOCKING)))
			return false;
		return true;
	} // public static boolean isSeverityValid(String severity)

	public static final String INFORMATION = "INFORMATION";
	public static final String WARNING = "WARNING";
	public static final String ERROR = "ERROR";
	public static final String BLOCKING = "BLOCKING";
} // public class EMFErrorSeverity

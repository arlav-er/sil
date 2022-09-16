package it.eng.sil.module;

import java.io.PrintStream;
import java.io.PrintWriter;

/**
 * Classe Exception specifica per i QueryStrategy, opzionalmente può incorporare l'exception che ha originato l'errore.
 * 
 * Compilando col JDK 1.3.1 ho implementato la gestione dell'exception aggiuntiva, se si compilasse con JDK 1.4.1 o
 * superiore, questa gestione sarebbe già incorporata nella classe Exception (exception-cause).
 * 
 * @author Corrado Vaccari
 * @created December 1, 2003
 */
public class QueryStrategyException extends java.lang.Exception {

	private Throwable ex;

	/**
	 * Constructor for the QueryStrategyException object
	 */
	public QueryStrategyException() {
	}

	/**
	 * Constructor for the QueryStrategyException object
	 * 
	 * @param string
	 *            Descrizione del problema
	 */
	public QueryStrategyException(String string) {
		super(string);
	}

	/**
	 * Constructor for the QueryStrategyException object
	 * 
	 * @param string
	 *            Descrizione del problema
	 * @param throwable
	 *            Exception correlata al problema
	 */
	public QueryStrategyException(String string, Throwable throwable) {
		super(string);
		ex = throwable;
	}

	/**
	 * Gets the exception attribute of the QueryStrategyException object
	 * 
	 * @return The exception value
	 */
	public Throwable getException() {
		return ex;
	}

	/**
	 * Description of the Method
	 */
	public void printStackTrace() {
		printStackTrace(System.err);
	}

	/**
	 * Description of the Method
	 * 
	 * @param printstream
	 *            Description of the Parameter
	 */
	public void printStackTrace(PrintStream printstream) {
		synchronized (printstream) {
			if (ex != null) {
				printstream.print("java.lang.QueryStrategyException: ");
				ex.printStackTrace(printstream);
			} else {
				super.printStackTrace(printstream);
			}
		}
	}

	/**
	 * Description of the Method
	 * 
	 * @param printwriter
	 *            Description of the Parameter
	 */
	public void printStackTrace(PrintWriter printwriter) {
		synchronized (printwriter) {
			if (ex != null) {
				printwriter.print("it.eng.sil.module.QueryStrategyException: ");
				ex.printStackTrace(printwriter);
			} else {
				super.printStackTrace(printwriter);
			}
		}
	}
}

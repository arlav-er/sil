package it.eng.afExt.utils;

import java.io.PrintWriter;
import java.io.StringWriter;

import javax.servlet.ServletException;

/**
 * Utilità per il Logging.
 * 
 * Un log può essere di tipo Debug, Warning o Error.
 * 
 * Il log viene generato seguendo un formato convenzionale: <class name>::<method name>:<message> * *
 * 
 * @author Corrado Vaccari
 */
public abstract class LogUtils {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(LogUtils.class.getName());

	/**
	 * Genera un log di debug.
	 * 
	 * @param methodName
	 * @param message
	 * @param caller
	 *            Chi richiama il metodo (es. this), può essere null
	 */
	public static void logDebug(String methodName, String message, Object caller) {

		String className = caller != null ? caller.getClass().toString() : "";

		_logger.debug(className + "::" + methodName + ":" + message);

	}

	/**
	 * Genera un log di warning.
	 * 
	 * @param methodName
	 * @param message
	 * @param caller
	 *            Chi richiama il metodo (es. this), può essere null
	 */
	public static void logWarning(String methodName, String message, Object caller) {

		String className = caller != null ? caller.getClass().toString() : "";

		_logger.warn(className + "::" + methodName + ":" + message);

	}

	/**
	 * Genera un log di errore.
	 * 
	 * @param methodName
	 * @param message
	 * @param caller
	 *            Chi richiama il metodo (es. this), può essere null
	 */
	public static void logError(String methodName, String message, Object caller) {
		logError(methodName, message, null, caller);
	}

	/**
	 * Genera un log di errore.
	 * 
	 * @param methodName
	 * @param message
	 * @param exp
	 *            Exception che ha generato l'errore, opzionale (può essere null)
	 * @param caller
	 *            Chi richiama il metodo (es. this), può essere null
	 */
	public static void logError(String methodName, String message, Exception exp, Object caller) {

		String className = caller != null ? caller.getClass().toString() : "";

		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);

		if (exp != null) {

			exp.printStackTrace(pw);
			if (exp instanceof ServletException) {

				Throwable t = ((ServletException) exp).getRootCause();
				if (t != null) {
					sw.write("\nRoot cause:");
					t.printStackTrace(pw);
				}
			}
		}

		_logger.error(className + "::" + methodName + ":" + message + "\n" + sw.toString());

	}
}
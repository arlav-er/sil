package com.engiweb.framework.util;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * La classe <code>JavaScript</code> è una classe di utility che mette a disposizione dei metodi statici che
 * implementano le stesse funzionalità di alcune funzioni in javascript.
 * 
 * @author Luigi Bellio
 */
public class JavaScript {
	/**
	 * Questo metodo implementa la stessa logica della funzione javascript <em>escape</em>.
	 * 
	 * @param input
	 *            stringa da manipolare.
	 */
	public static String escape(String input) {	
		try {
			input = URLEncoder.encode(input, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException("Errore nel metodo escape: " + e.getMessage());
		}
		return input;
	} // public static String escape(String input)

	/**
	 * Questo metodo manipola la stringa in input permettendo le seguenti sostituzioni: da <em>"\n"</em> a
	 * <em>"\\n"</em>, da <em>"\""</em> a <em>"'"</em>
	 * 
	 * @param input
	 *            stringa da manipolare.
	 */
	public static String escapeText(String input) {
		input = replace(input, "\n", "\\n");
		input = replace(input, "\"", "'");
		return input;
	} // public static String escapeCarrigeReturn(String input)

	/**
	 * Questo metodo permette di sostituire una parte di una stringa con un'altra.
	 * 
	 * @param toParse
	 *            stringa da manipolare.
	 * @param replacing
	 *            parte di stringa da sostituire.
	 * @param replaced
	 *            stringa nuova.
	 */
	public static String replace(String toParse, String replacing, String replaced) {
		// System.out.println("JavaScript::replace: invocato");
		if (toParse == null) {
			// System.out.println("PagePresentation::replace: toParse è nullo");
			return toParse;
		} // if (toParse == null)
			// System.out.println("JavaScript::replace: toParse [" + toParse + "]");
		if (replacing == null) {
			// System.out.println("PagePresentation::replace: replacing è
			// nullo");
			return toParse;
		} // if (replacing == null)
			// System.out.println("JavaScript::replace: replacing [" + replacing +
			// "]");
		if (replaced == null) {
			// System.out.println("JavaScript::replace: replaced è nullo");
		} // if (replaced == null)
		else {
			// System.out.println("JavaScript::replace: replaced [" + replaced +
			// "]");
			int parameterIndex = toParse.indexOf(replacing);
			while (parameterIndex != -1) {
				String newToParse = toParse.substring(0, parameterIndex);
				newToParse += replaced;
				newToParse += toParse.substring(parameterIndex + replacing.length(), toParse.length());
				// System.out.println("JavaScript::replace: newToParse [" +
				// newToParse + "]");
				toParse = newToParse;
				parameterIndex = toParse.indexOf(replacing, parameterIndex + replaced.length());
			} // while (parameterIndex != -1)
		} // if (replaced == null)
		return toParse;
	} // public static String replace(String toParse, String replacing, String
		// replaced)
} // public class JavaScript

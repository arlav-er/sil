package it.eng.afExt.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

/**
 * Code Separated String Utils. Classe di utilità per le stringhe che contengono un insieme di codici separati da un
 * carattere di separatore opportuno.
 * 
 * @author Luigi Antenucci
 */
public abstract class CodSepStrUtils {

	public static final char SEPARATOR = '#'; // Il separatore

	/**
	 * Data la stringa contenente i codici separati dal carattere di "separatore" rende una lista con tutti i codici
	 * (lista di stringhe).
	 */
	public static final List getList(String sepStr) {

		List list = new ArrayList();
		StringTokenizer tokenizer = new StringTokenizer(sepStr, "" + SEPARATOR, false);
		while (tokenizer.hasMoreTokens()) {
			String cod = tokenizer.nextToken();
			list.add(cod);
		}
		return list;
	}

	/**
	 * Aggiunge alla stringa sepStr il codice contenuto in singleCod. Se sepStr è nulla o vuota, rende una stringa col
	 * solo singleCod (senza alcun separatore iniziale), altrimenti rende il contenuto di sepStr a cui è stato accodato
	 * il separatore e il contenuto di singleCod.
	 */
	public static final String addCodToSepStr(String sepStr, String singleCod) {
		if (sepStr == null) {
			return singleCod;
		} else if (sepStr.length() == 0) {
			return singleCod; // non aggiungo separatore al primo inserito
		} else {
			return sepStr + SEPARATOR + singleCod;
		}
	}

}

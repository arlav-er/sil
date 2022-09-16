package it.eng.sil.coordinamento.utils;

import java.io.File;
import java.io.IOException;

/**
 * Contiene la logica generica di accesso alle proprietà di una configurazione. Viene ereditata dalle classi di
 * ConfigDeadQPollerBatch e ConfigDB. NB: Classe visibile solo al pacchetto!
 * 
 * @author Luigi Antenucci
 */
public abstract class ConfigAbstract {

	private static final String PIPE = "|";

	/**
	 * DA IMPLEMENTARE Rende il valore della porprietà di chiave "key".
	 */
	public abstract String getProperty(String key);

	/**
	 * Rende TRUE se la proprietà con chiave "key" è valorizzata (ossia ha associato un valore non vuoto)
	 */
	public boolean isPropertyDefined(String key) {
		String value = getProperty(key);
		return (value != null) && (value.trim().length() > 0);
	}

	/**
	 * Rende un valore booleano (convertendolo dalla stringa)
	 */
	public boolean getPropertyBoolean(String key) {
		String value = getProperty(key);
		if ((value == null) || value.length() == 0)
			return false;
		else {
			value = value.toLowerCase();
			char chr1 = Character.toLowerCase(value.charAt(0));
			return (chr1 == 's') || (chr1 == 'y') || value.equalsIgnoreCase("true") || value.equalsIgnoreCase("on");
		}
	}

	/**
	 * Rende un numero intero (convertendolo dalla stringa)
	 */
	public int getPropertyInt(String key) throws NumberFormatException {
		String value = getProperty(key);
		return Integer.parseInt(value);
	}

	/**
	 * Rende un numero "long" (convertendolo dalla stringa)
	 */
	public long getPropertyLong(String key) throws NumberFormatException {
		String value = getProperty(key);
		return Long.parseLong(value);
	}

	/**
	 * Rende un path, ossia aggiungo un carattere di "file-separator" in ultima posizione, se non è già presente.
	 */
	public String getPath(String key) throws IOException {

		String tempDir = getProperty(key);

		// Aggiungo un carattere di "file-separator" in ultima posizione,
		// se non è già presente.
		char c = tempDir.charAt(tempDir.length() - 1);
		if ((c != File.separatorChar) && (c != '/') && (c != '\\'))
			tempDir += File.separator;
		return tempDir;
	}

	/**
	 * Rende TRUE se la proprietà "key" contiene un valore al cui interno è presente un sottovalore "subValue", separato
	 * a destra e sinistra da un carattere di "pipe" (|) oppure dal carattere SEPARATORE dato. Es. key="uno|due|quattro
	 * e subValue="due" rende TRUE; per subValue="tre" rende FALSE.
	 */
	public boolean propertyContains(String key, String subValue) {
		return propertyContains(key, subValue, PIPE);
	}

	public boolean propertyContains(String key, String subValue, String SEPARATORE) {
		String value = getProperty(key);
		if ((value == null) || value.equals("") || (subValue == null) || subValue.equals("")) {
			return false;
		} else {
			value = SEPARATORE + value.toUpperCase() + SEPARATORE;
			subValue = SEPARATORE + subValue.toUpperCase() + SEPARATORE;

			boolean contains = value.indexOf(subValue) >= 0;
			return contains;
		}
	}

}

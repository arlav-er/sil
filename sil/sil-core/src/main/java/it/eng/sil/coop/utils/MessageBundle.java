package it.eng.sil.coop.utils;

import java.util.Locale;
import java.util.ResourceBundle;
import java.util.Vector;

import javax.naming.NamingException;

import org.apache.log4j.Logger;

public class MessageBundle {

	/**
	 * Reperisce il messaggio attraverso un codice e sostituisce i parametri al posto dei placeholders "%"<br>
	 * Il nome del file di proprieta' e': messages[_${contesto}]_${language}_${country}.properties<br>
	 * Il default e' messages_it_IT.properties
	 * 
	 * @param code
	 * @param params
	 */
	public static String getMessage(int code, Vector params) {
		Logger log = Logger.getLogger(MessageBundle.class.getName());
		String codeStr = String.valueOf(code);
		String language = null, country = null, contesto = null;
		javax.naming.InitialContext ctx;
		try {
			ctx = new javax.naming.InitialContext();
		} catch (Exception sex) {
			log.fatal(
					"Eccezione nell'inizializzazione del contesto JNDI. Impossibile accedere al file dei messaggi utente messages_xx_XX.properties",
					sex);
			return null;
		}
		try {
			language = (String) ctx.lookup("java:comp/env/language");
			country = (String) ctx.lookup("java:comp/env/country");
			contesto = (String) ctx.lookup("java:comp/env/contesto");
		} catch (NamingException ne) {
			log.warn(
					"Eccezione nel recupero delle variabili di localizzazione del file dei messaggi messages_xx_XX.properties. Verranno usati i valori di default",
					ne);
		}
		String fileName = "messages";
		if (contesto != null && !contesto.equals(""))
			fileName += ("_" + contesto);
		if (country == null || country.equals(""))
			country = "IT";
		if (language == null || language.equals(""))
			language = "it";
		Locale currentLocale = new Locale(language, country);
		ResourceBundle messages = ResourceBundle.getBundle(fileName, currentLocale);
		if (messages == null) {
			return null;
		}
		String description = codeStr;
		try {
			description = messages.getString(codeStr);
		} catch (Exception ex) {
			log.warn("MessageBundle::getMessage: codice [" + code + "] non trovato");
		}
		if ((params != null) && (description != null)) {
			for (int i = 0; i < params.size(); i++) {
				String toParse = description;
				String replacing = "%" + i;
				String replaced = (String) params.elementAt(i);
				StringBuffer parsed = new StringBuffer();
				int parameterIndex = toParse.indexOf(replacing);
				while (parameterIndex != -1) {
					parsed.append(toParse.substring(0, parameterIndex));
					parsed.append(replaced);
					toParse = toParse.substring(parameterIndex + replacing.length(), toParse.length());
					parameterIndex = toParse.indexOf(replacing);
				}
				parsed.append(toParse);
				description = parsed.toString();
			}
		}
		return description;
	}
}

package it.eng.sil.module.movimenti.processors;

import java.util.Vector;

import com.engiweb.framework.message.MessageBundle;

/**
 * Classe che rappresenta una warning, contiene la stringa che rappresenta i codice della waning e un eventuale
 * ulteriore messaggio per l'utente.
 * 
 * <p>
 * 
 * @author Paolo Roccetti
 */
public class Warning {
	private int code;
	private String message = "";

	/**
	 * Costruttore
	 * <p>
	 * 
	 * @param code
	 *            Costante della classe MessageCodes che contiene il codice della warning avvenuta
	 * @param message
	 *            Ulteriore messaggio per l'utente
	 */
	public Warning(int code, String message) {
		this.code = code;
		if (message != null) {
			this.message = message.trim();
		}
	}

	public Warning(int code, int codeDettaglio, Vector params) {
		this.code = code;
		String description = MessageBundle.getMessage(String.valueOf(codeDettaglio));
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
				} // while (parameterIndex != -1)
				parsed.append(toParse);
				description = parsed.toString();
			} // for (int i = 0; i < params.size(); i++)
		}
		this.message = description.trim();
	}

	public int getCode() {
		return code;
	}

	public String getMessage() {
		return message;
	}

	/** Metodo per la rappresentazione sui file di log */
	public String toString() {
		return "WARNING! code: " + code + ", message: " + message + ". \n";
	}

}
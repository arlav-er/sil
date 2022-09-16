package com.engiweb.framework.message;

import java.util.Locale;
import java.util.ResourceBundle;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.SessionContainer;

/**
 * Questa classe permette il recupero di stringhe censite in alcuni files di properties.
 */
public class MessageBundle {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(MessageBundle.class.getName());

	private MessageBundle() {
		super();
	} // private MessageBundle()

	/**
	 * Overload per l'utilizzo con un intero come input
	 * 
	 * @param codice
	 * @return
	 */
	public static String getMessage(Integer codice) {
		if (codice == null)
			return null;
		else
			return getMessage(codice.toString());
	}

	/**
	 * Questo metodo recupera una stringa individuata da un codice e da alcune proprietà dell'utente che ha richiesto il
	 * servizio (language,country).
	 * <p>
	 * 
	 * @param code
	 *            il codice associato alla stringa.
	 * @return la stringa recuperata da un file di properties.
	 */
	public static String getMessage(String code) {
		String language = null;
		String country = null;
		RequestContainer requestContainer = RequestContainer.getRequestContainer();
		if (requestContainer == null)
			_logger.debug("MessageBundle::getMessage: requestContainer nullo");

		else {
			// Modificata in data 24-05-2004 su proposta di Savino
			// SessionContainer sessionContainer =
			// requestContainer.getSessionContainer().getPermanentContainer();
			SessionContainer sessionContainer = requestContainer.getSessionContainer();
			language = (String) sessionContainer.getAttribute("AF_LANGUAGE");
			country = (String) sessionContainer.getAttribute("AF_COUNTRY");
		} // if (requestContainer != null)
		if (language == null) {
			_logger.debug("MessageBundle::getMessage: language non specificato");

			language = "it";
		} // if (language == null)
		if (country == null) {
			_logger.debug("MessageBundle::getMessage: country non specificato");

			country = "IT";
		} // if (country == null)
		Locale currentLocale = new Locale(language, country);
		ResourceBundle messages = ResourceBundle.getBundle("messages", currentLocale);
		if (messages == null) {
			return null;
		} // if (messages == null)
		String message = code;
		try {
			message = messages.getString(code);
		} // try
		catch (Exception ex) {
			_logger.debug("MessageBundle::getMessage: codice [" + code + "] non trovato");

		} // catch (Exception ex)
		return message;
	} // public String getMessage(String code)

} // public class MessageBundle

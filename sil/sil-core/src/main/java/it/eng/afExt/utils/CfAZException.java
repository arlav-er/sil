package it.eng.afExt.utils;

/**
 * Si occupa di sollevare gli errori nel codice fiscale delle aziende.
 * 
 * @author Rolfini_A
 * @version 1.0, 17/02/2003
 */
public class CfAZException extends Exception {

	private int code = 0;

	public CfAZException(int _code) {
		super();
		if (!MessageCodes.CodiceFiscaleAzienda.isInRange(_code)) // se il
																	// codice
																	// non è nei
																	// limiti
																	// definiti
		{
			throw new IllegalArgumentException("codice di eccezione illegale::" + _code);
		} // mi segnala l'errore
		else {
			code = _code;
		} // altrimenti mi assegna il codice corretto
	}

	public int getMessageIdFail() {
		return code;
	}

}
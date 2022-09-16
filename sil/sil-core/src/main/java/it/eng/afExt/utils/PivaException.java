package it.eng.afExt.utils;

/**
 * Si occupa di sollevare gli errori nel codice fiscale. Gli errori possibili sono: ERR_LUNGHEZZA = 0,
 * ERR_CARATTERI_NUMERICI=1, ERR_CARATTERI_ALFABETICI=2, ERR_SESSO=3, ERR_CHECK_DIGIT=4, ERR_COGNOME=5, ERR_NOME=6,
 * ERR_MESE=7, ERR_ANNO=8, ERR_COMUNE=9,
 * 
 * l'errore: ERR_CODICE_ILLEGALE=10; viene richiamato quando l'eccezione viene sollevata con un codice non corretto. La
 * descrizione dell'errore è nel messaggio.
 * 
 * @author Rolfini_A
 * @version 1.0, 17/07/2003
 */
public class PivaException extends Exception {

	private int code = 0;

	private PivaException() {
	}

	public PivaException(int _code) {
		super();
		if (!MessageCodes.CodiceFiscale.isInRange(_code)) // se il codice non
															// è nei limiti
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
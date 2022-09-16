package it.eng.afExt.utils;

/**
 * Gestione errore provocato dall'inserimento multiplo delle azioni nei percorsi concordati per un colloquio. L'errore
 * viene richiamato quando l'eccezione viene sollevata con un codice non corretto. La descrizione dell'errore è nel
 * messaggio.
 * 
 * @author De Simone Giuseppe, 12/08/2004
 */
public class PercorsoConcordatoException extends Exception {

	private int code = 0;

	public PercorsoConcordatoException(int _code) {
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

package it.eng.sil.module.patto;

import it.eng.afExt.utils.MessageCodes;

public class PercorsoIscrizioneCorsiException extends Exception {
	private int code = 0;

	public PercorsoIscrizioneCorsiException(int _code) {
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

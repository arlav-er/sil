package it.eng.sil.coop.webservices.clicLavoro.ricercaPersonale;

import com.engiweb.framework.error.EMFUserError;

public class CLRicercaPersonaleException extends EMFUserError {
	private static final long serialVersionUID = 1L;

	public CLRicercaPersonaleException(int codice, String message) {
		super(message, codice);
	}
}

package it.eng.sil.coop.webservices.clicLavoro.candidatura;

import com.engiweb.framework.error.EMFUserError;

public class CLCandidaturaException extends EMFUserError {
	private static final long serialVersionUID = 1L;

	public CLCandidaturaException(int codice, String message) {
		super(message, codice);
	}
}

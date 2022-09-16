package it.eng.sil.myauthservice.exception;

import it.eng.sil.base.exceptions.ProfilaturaException;

public class ProfilaturaMissingParameterException extends ProfilaturaException {

	private static final long serialVersionUID = 3096823595114554878L;

	public ProfilaturaMissingParameterException(String message) {
		super(message);
	}

}

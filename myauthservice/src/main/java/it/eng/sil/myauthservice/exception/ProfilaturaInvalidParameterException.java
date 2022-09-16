package it.eng.sil.myauthservice.exception;

import it.eng.sil.base.exceptions.ProfilaturaException;

public class ProfilaturaInvalidParameterException extends ProfilaturaException {

	private static final long serialVersionUID = 6156323875494938428L;

	public ProfilaturaInvalidParameterException(String message) {
		super(message);
	}

}

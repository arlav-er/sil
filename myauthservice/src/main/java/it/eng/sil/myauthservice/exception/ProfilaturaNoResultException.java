package it.eng.sil.myauthservice.exception;

import it.eng.sil.base.exceptions.ProfilaturaException;

public class ProfilaturaNoResultException extends ProfilaturaException {
	private static final long serialVersionUID = -810912669396593553L;

	public ProfilaturaNoResultException(String e) {
		super(e);
	}

	public ProfilaturaNoResultException(Exception e) {
		super(e);
	}
}

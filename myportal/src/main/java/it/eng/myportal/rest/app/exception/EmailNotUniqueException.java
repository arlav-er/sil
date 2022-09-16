package it.eng.myportal.rest.app.exception;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

public class EmailNotUniqueException extends AppEjbException {

	private static final long serialVersionUID = 7313836963066650503L;

	public EmailNotUniqueException(String email) {
		super(EMAIL_NOT_UNIQUE_CODE, EMAIL_NOT_UNIQUE_DES + ": " + email);
	}

	@Override
	public Status getResponseStatus() {
		return Response.Status.BAD_REQUEST;
	}
}

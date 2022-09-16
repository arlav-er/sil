package it.eng.myportal.rest.app.exception;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

public class ExpiredPasswordException extends AppEjbException {

	private static final long serialVersionUID = -8120732269555438692L;

	public ExpiredPasswordException(String username) {
		super(EXPIRED_PASSWORD_CODE, EXPIRED_PASSWORD_DES + ": " + username);
	}

	@Override
	public Status getResponseStatus() {
		return Response.Status.BAD_REQUEST;
	}
}

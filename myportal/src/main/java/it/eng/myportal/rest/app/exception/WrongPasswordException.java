package it.eng.myportal.rest.app.exception;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

public class WrongPasswordException extends AppEjbException {

	private static final long serialVersionUID = -8120732269555438692L;

	public WrongPasswordException(String username) {
		super(WRONG_PASSWORD_CODE, WRONG_PASSWORD_DES + ": " + username);
	}

	@Override
	public Status getResponseStatus() {
		return Response.Status.BAD_REQUEST;
	}
}

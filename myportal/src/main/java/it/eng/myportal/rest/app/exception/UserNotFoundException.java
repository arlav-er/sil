package it.eng.myportal.rest.app.exception;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

public class UserNotFoundException extends AppEjbException {

	private static final long serialVersionUID = -3459356209305695960L;

	public UserNotFoundException(String username) {
		super(USER_NOT_FOUND_CODE, USER_NOT_FOUND_DES + ": " + username);
	}

	@Override
	public Status getResponseStatus() {
		return Response.Status.BAD_REQUEST;
	}
}

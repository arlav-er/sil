package it.eng.myportal.rest.app.exception;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

public class BlockedLoginException extends AppEjbException {

	private static final long serialVersionUID = -8017012565291213094L;

	public BlockedLoginException() {
		super(BLOCKED_LOGIN_CODE, BLOCKED_LOGIN_DES);
	}

	@Override
	public Status getResponseStatus() {
		return Response.Status.BAD_REQUEST;
	}
}

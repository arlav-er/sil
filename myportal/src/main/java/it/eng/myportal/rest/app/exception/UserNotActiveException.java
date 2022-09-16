package it.eng.myportal.rest.app.exception;

public class UserNotActiveException extends AppEjbException {

	private static final long serialVersionUID = -173545336278669907L;

	public UserNotActiveException(String username) {
		super(USER_NOT_ACTIVE_CODE, USER_NOT_ACTIVE_DES + ": " + username);
	}
}

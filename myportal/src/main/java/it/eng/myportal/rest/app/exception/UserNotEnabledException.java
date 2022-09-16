package it.eng.myportal.rest.app.exception;

public class UserNotEnabledException extends AppEjbException {

	private static final long serialVersionUID = -2115352465419804566L;

	public UserNotEnabledException(String username) {
		super(USER_NOT_ENABLED_CODE, USER_NOT_ENABLED_DES + ": " + username);
	}

}

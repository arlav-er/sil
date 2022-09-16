package it.eng.myportal.rest.app.exception;

public class UserServAmmNotEnabledException extends AppEjbException {

	private static final long serialVersionUID = 6725829193975877714L;

	public UserServAmmNotEnabledException(String username) {
		super(USER_SERV_AMM_NOT_ENABLED_CODE, USER_SERV_AMM_NOT_ENABLED_DES + ": " + username);
	}
}

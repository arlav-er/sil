package it.eng.myportal.rest.app.exception;

public class NotifyNotFoundException extends AppEjbException {

	private static final long serialVersionUID = 3078679395300974662L;

	public NotifyNotFoundException(String idNotifica) {
		super(NOTIFY_NOT_FOUND_CODE, NOTIFY_NOT_FOUND_DES + ": " + idNotifica);
	}

}

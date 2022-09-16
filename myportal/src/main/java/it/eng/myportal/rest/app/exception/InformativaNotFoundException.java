package it.eng.myportal.rest.app.exception;

public class InformativaNotFoundException extends AppEjbException {

	private static final long serialVersionUID = -8209681159090731701L;

	public InformativaNotFoundException(String txtInformativaKey) {
		super(INFORMATIVA_NOT_FOUND_CODE, INFORMATIVA_NOT_FOUND_DES + ":" + txtInformativaKey);
	}
}

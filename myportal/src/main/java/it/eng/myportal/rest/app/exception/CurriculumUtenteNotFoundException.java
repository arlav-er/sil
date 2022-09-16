package it.eng.myportal.rest.app.exception;

public class CurriculumUtenteNotFoundException extends AppEjbException {

	private static final long serialVersionUID = -5551602447548230730L;

	public CurriculumUtenteNotFoundException(Integer idCv) {
		super(CURRICULUM_UTENTE_NOT_FOUND_CODE, CURRICULUM_UTENTE_NOT_FOUND_DES + ": " + idCv);
	}

}

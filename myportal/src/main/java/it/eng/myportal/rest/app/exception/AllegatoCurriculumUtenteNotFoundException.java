package it.eng.myportal.rest.app.exception;

public class AllegatoCurriculumUtenteNotFoundException extends AppEjbException {

	private static final long serialVersionUID = -5551602447548230730L;

	public AllegatoCurriculumUtenteNotFoundException(Integer idCv) {
		super(ALLEGATO_CURRICULUM_UTENTE_NOT_FOUND_CODE, ALLEGATO_CURRICULUM_UTENTE_NOT_FOUND_DES + ": " + idCv);
	}

}

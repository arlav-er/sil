package it.eng.myportal.rest.app.exception;

public class RicercheSalvateNotFoundException extends AppEjbException {

	private static final long serialVersionUID = 5265013617575511581L;

	public RicercheSalvateNotFoundException(Integer idRicercaSalvata) {
		super(RICERCA_SALVATA_NOT_FOUND_CODE, RICERCA_SALVATA_NOT_FOUND_DES + ": " + idRicercaSalvata);
	}

}

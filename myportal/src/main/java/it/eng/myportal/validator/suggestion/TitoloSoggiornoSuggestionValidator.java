package it.eng.myportal.validator.suggestion;

import it.eng.myportal.dtos.DeTitoloSoggiornoDTO;

import javax.faces.validator.FacesValidator;

/**
 * 
 * @author turrini
 *
 * Classe concreta che valida l'autosuggestion per la tabella DE_TITOLO_SOGGIORNO
 *
 */
@FacesValidator(value="titoloSoggiornoSuggestionValidator")
public class TitoloSoggiornoSuggestionValidator extends AbstractSuggestionValidator<DeTitoloSoggiornoDTO> {

	public TitoloSoggiornoSuggestionValidator() {
		super("DeTitoloSoggiornoHome");		
	}
	
}

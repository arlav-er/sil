package it.eng.myportal.validator.suggestion;

import it.eng.myportal.dtos.DeProfessioneDTO;

import javax.faces.validator.FacesValidator;

/**
 * 
 * @author Stefano Turrini
 *
 * Classe concreta che valida l'autosuggestion per la tabella DE_PROFESSIONE
 *
 */
@FacesValidator(value="professioneSuggestionValidator")
public class ProfessioneSuggestionValidator extends AbstractSuggestionValidator<DeProfessioneDTO> {

	
	public ProfessioneSuggestionValidator() {
		super("DeProfessioneHome");		
	}
		
}

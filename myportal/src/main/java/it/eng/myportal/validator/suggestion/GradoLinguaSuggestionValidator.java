package it.eng.myportal.validator.suggestion;

import it.eng.myportal.dtos.DeGradoLinDTO;

import javax.faces.validator.FacesValidator;

/**
 * 
 * @author enrico
 *
 * Classe concreta che valida l'autosuggestion per la tabella DE_MANSIONE
 *
 */
@FacesValidator(value="gradoLinguaSuggestionValidator")
public class GradoLinguaSuggestionValidator extends AbstractSuggestionValidator<DeGradoLinDTO> {
	
	public GradoLinguaSuggestionValidator() {
		super("DeGradoLinHome");		
	}

}

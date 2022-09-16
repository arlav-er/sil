package it.eng.myportal.validator.suggestion;

import it.eng.myportal.dtos.DeCorsoDTO;

import javax.faces.validator.FacesValidator;

/**
 * 
 * @author enrico
 *
 * Classe concreta che valida l'autosuggestion per la tabella DE_CORSO
 *
 */
@FacesValidator(value="corsoSuggestionValidator")
public class CorsoSuggestionValidator extends AbstractSuggestionValidator<DeCorsoDTO> {

	
	public CorsoSuggestionValidator() {
		super("DeCorsoHome");		
	}
		
}

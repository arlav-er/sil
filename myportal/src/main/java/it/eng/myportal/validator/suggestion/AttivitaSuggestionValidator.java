package it.eng.myportal.validator.suggestion;

import it.eng.myportal.dtos.DeAttivitaDTO;

import javax.faces.validator.FacesValidator;

/**
 * 
 * @author enrico
 *
 * Classe concreta che valida l'autosuggestion per la tabella DE_ATTIVITA
 *
 */
@FacesValidator(value="attivitaSuggestionValidator")
public class AttivitaSuggestionValidator extends AbstractSuggestionValidator<DeAttivitaDTO> {

	
	public AttivitaSuggestionValidator() {
		super("DeAttivitaHome");		
	}

}

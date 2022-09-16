package it.eng.myportal.validator.suggestion;

import it.eng.myportal.dtos.DeAgevolazioneDTO;

import javax.faces.validator.FacesValidator;

/**
 * 
 * @author enrico
 *
 * Classe concreta che valida l'autosuggestion per la tabella DE_AGEVOLAZIONE
 *
 */
@FacesValidator(value="agevolazioneSuggestionValidator")
public class AgevolazioneSuggestionValidator extends AbstractSuggestionValidator<DeAgevolazioneDTO> {
	
	public AgevolazioneSuggestionValidator() {
		super("DeAgevolazioneHome");		
	}

}

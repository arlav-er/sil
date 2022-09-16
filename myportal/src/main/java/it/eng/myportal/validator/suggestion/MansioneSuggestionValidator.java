package it.eng.myportal.validator.suggestion;

import it.eng.myportal.dtos.DeMansioneDTO;

import javax.faces.validator.FacesValidator;

/**
 * 
 * @author enrico
 *
 *         Classe concreta che valida l'autosuggestion per la tabella DE_MANSIONE
 *
 */
@FacesValidator(value = "mansioneSuggestionValidator")
public class MansioneSuggestionValidator extends AbstractSuggestionValidator<DeMansioneDTO> {

	public MansioneSuggestionValidator() {
		super("DeMansioneHome");
	}

}

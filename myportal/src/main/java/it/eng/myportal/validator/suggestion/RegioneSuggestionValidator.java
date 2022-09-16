package it.eng.myportal.validator.suggestion;

import it.eng.myportal.dtos.DeRegioneDTO;

import javax.faces.validator.FacesValidator;

/**
 * 
 * @author enrico
 *
 * Classe concreta che valida l'autosuggestion per la tabella DE_COMUNE
 *
 */
@FacesValidator(value="regioneSuggestionValidator")
public class RegioneSuggestionValidator extends AbstractSuggestionValidator<DeRegioneDTO> {

	public RegioneSuggestionValidator() {
		super("DeRegioneHome");		
	}
	
}

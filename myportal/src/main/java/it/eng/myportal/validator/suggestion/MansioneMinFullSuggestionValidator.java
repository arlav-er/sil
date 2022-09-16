package it.eng.myportal.validator.suggestion;

import it.eng.myportal.dtos.DeMansioneMinDTO;

import javax.faces.validator.FacesValidator;

/**
 * 
 * @author Enrico D'Angelo
 * 
 */
@FacesValidator(value = "mansioneMinFullSuggestionValidator")
public class MansioneMinFullSuggestionValidator extends AbstractSuggestionValidator<DeMansioneMinDTO> {
	
	public MansioneMinFullSuggestionValidator() {
		super("DeMansioneMinHome");	 
	}
}

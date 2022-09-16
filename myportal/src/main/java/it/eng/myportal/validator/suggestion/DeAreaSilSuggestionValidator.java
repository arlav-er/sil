package it.eng.myportal.validator.suggestion;

import it.eng.myportal.dtos.DeAreaSilDTO;

import javax.faces.validator.FacesValidator;

@FacesValidator(value = "deAreaSilSuggestionValidator")
public class DeAreaSilSuggestionValidator extends AbstractSuggestionValidator<DeAreaSilDTO> {

	public DeAreaSilSuggestionValidator() {
		super("DeAreaSilHome");
	}

}

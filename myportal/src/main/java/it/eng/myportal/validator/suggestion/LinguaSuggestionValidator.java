package it.eng.myportal.validator.suggestion;

import it.eng.myportal.dtos.DeLinguaDTO;
import it.eng.myportal.entity.home.decodifiche.DeLinguaHome;

import javax.faces.validator.FacesValidator;

/**
 * 
 * @author enrico
 *
 * Classe concreta che valida l'autosuggestion per la tabella DEL_LINGUA
 *
 */
@FacesValidator(value="linguaSuggestionValidator")
public class LinguaSuggestionValidator extends AbstractSuggestionValidator<DeLinguaDTO> {

	
	public LinguaSuggestionValidator() {
		super(DeLinguaHome.class.getSimpleName());	
	}

}

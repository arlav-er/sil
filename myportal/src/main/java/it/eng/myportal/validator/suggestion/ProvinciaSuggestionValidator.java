package it.eng.myportal.validator.suggestion;

import it.eng.myportal.dtos.DeProvinciaDTO;

import javax.faces.validator.FacesValidator;

/**
 * 
 * @author enrico
 *
 * Classe concreta che valida l'autosuggestion per la tabella DE_COMUNE
 *
 */
@FacesValidator(value="provinciaSuggestionValidator")
public class ProvinciaSuggestionValidator extends AbstractSuggestionValidator<DeProvinciaDTO> {

	public ProvinciaSuggestionValidator() {
		super("DeProvinciaHome");		 
	}		
}

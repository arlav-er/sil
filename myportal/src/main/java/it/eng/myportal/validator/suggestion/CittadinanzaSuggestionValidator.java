package it.eng.myportal.validator.suggestion;

import it.eng.myportal.dtos.DeCittadinanzaDTO;

import javax.faces.validator.FacesValidator;

/**
 * 
 * @author Turrini
 *
 * Classe concreta che valida l'autosuggestion per la tabella DE_CITTADINANZA
 *
 */
@FacesValidator(value="cittadinanzaSuggestionValidator")
public class CittadinanzaSuggestionValidator extends AbstractSuggestionValidator<DeCittadinanzaDTO> {
	
	public CittadinanzaSuggestionValidator() {
		super("DeCittadinanzaHome");		
	}

}

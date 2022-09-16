package it.eng.myportal.validator.suggestion;

import it.eng.myportal.dtos.DeTitoloDTO;

import javax.faces.validator.FacesValidator;

/**
 * 
 * @author enrico
 *
 * Classe concreta che valida l'autosuggestion per la tabella DE_TITOLO
 *
 */
@FacesValidator(value="titoloSuggestionValidator")
public class TitoloSuggestionValidator extends AbstractSuggestionValidator<DeTitoloDTO> {

	public TitoloSuggestionValidator() {
		super("DeTitoloHome");		
	}

}

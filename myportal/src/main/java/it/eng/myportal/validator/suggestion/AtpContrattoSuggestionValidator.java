package it.eng.myportal.validator.suggestion;

import it.eng.myportal.dtos.DeAtpContrattoDTO;

import javax.faces.validator.FacesValidator;

/**
 * 
 * @author enrico
 *
 * Classe concreta che valida l'autosuggestion per la tabella DE_ATP_CONTRATTO
 *
 */
@FacesValidator(value="atpContrattoSuggestionValidator")
public class AtpContrattoSuggestionValidator extends AbstractSuggestionValidator<DeAtpContrattoDTO> {

	
	public AtpContrattoSuggestionValidator() {
		super("DeAtpContrattoHome");	
	}

}

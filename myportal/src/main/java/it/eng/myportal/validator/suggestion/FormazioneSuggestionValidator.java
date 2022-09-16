package it.eng.myportal.validator.suggestion;

import it.eng.myportal.dtos.DeTipoFormazioneDTO;

import javax.faces.validator.FacesValidator;

/**
 * 
 * @author Stefano Turrini
 *
 * Classe concreta che valida l'autosuggestion per la tabella DE_TIPO_FORMAZIONE
 *
 */
@FacesValidator(value="formazioneSuggestionValidator")
public class FormazioneSuggestionValidator extends AbstractSuggestionValidator<DeTipoFormazioneDTO> {

	
	public FormazioneSuggestionValidator() {
		super("DeTipoFormazioneHome");		
	}
		
}

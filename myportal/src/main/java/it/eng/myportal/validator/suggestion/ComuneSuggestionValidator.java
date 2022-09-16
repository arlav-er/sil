package it.eng.myportal.validator.suggestion;

import it.eng.myportal.dtos.DeComuneDTO;
import it.eng.myportal.dtos.ISuggestible;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.ValidatorException;

import org.apache.commons.lang3.StringUtils;

/**
 * 
 * @author enrico
 * 
 *         Classe concreta che valida l'autosuggestion per la tabella DE_COMUNE
 * 
 */
@FacesValidator(value = "comuneSuggestionValidator")
public class ComuneSuggestionValidator extends AbstractSuggestionValidator<DeComuneDTO> {

	private static final String REG_EXP = "\\([A-Z]*\\)?";

	public ComuneSuggestionValidator() {
		super("DeComuneHome");
	}

	@Override
	public String getGenericErrorMessage() {
		return "Comune non trovato.";
	}

	@Override
	public String getTooManyErrorMessage() {
		return "Esistono più comuni con lo stesso nome, selezionarne uno dalla lista.";
	}

	@Override
	public void validate(FacesContext context, UIComponent component, Object value) throws ValidatorException {
		String valueLabel = (String) value;
		if (StringUtils.isNotBlank(valueLabel)) {
			valueLabel = valueLabel.replaceAll(REG_EXP, "").trim();
		}
		// se non trovo nulla con la descrizione
		super.validate(context, component, valueLabel);
	}

	@Override
	public String estraiDescrizioneDTO(ISuggestible dto) {
		return dto.getDescrizione().replaceAll(REG_EXP, "").trim();
	}
}

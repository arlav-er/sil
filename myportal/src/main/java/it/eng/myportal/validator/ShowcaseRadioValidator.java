package it.eng.myportal.validator;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;

@FacesValidator(value = "showcaseRadioValidator")
public class ShowcaseRadioValidator implements Validator {

	@Override
	public void validate(FacesContext context, UIComponent arg1, Object value) throws ValidatorException {
		String val = (String) value;
		if (!"06".equals(val)) {
			// Aggiunta di un messaggio di errore associato ad un tag della pagina
			context.addMessage("myForm:inputHidden01", new FacesMessage("Errore dimostrativo sul campo hidden"));

			// Fallimento della validazione sul componente
			FacesMessage msg = new FacesMessage("Seleziona il valore ccc per eliminare questi errori");
			msg.setSeverity(FacesMessage.SEVERITY_ERROR);
			throw new ValidatorException(msg);
		}

		// Validazione avvenuta con successo
		return;
	}

}

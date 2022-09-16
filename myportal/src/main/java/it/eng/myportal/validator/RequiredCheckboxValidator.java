package it.eng.myportal.validator;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;

/**
 * 
 * @author rodi
 *
 * Validatore dalla checkbox per l'informativa sulla privacy.
 *
 */
@FacesValidator(value="requiredCheckboxValidator")
public class RequiredCheckboxValidator implements Validator {
	
	@Override
	public void validate(FacesContext context, UIComponent component, Object value) throws ValidatorException {
		if (value != null) {
			Boolean checkbox = (Boolean) value;			
			if (!checkbox) {
				String message = "E' obbligatorio accettare l'informativa sulla privacy";
				FacesMessage msg = new FacesMessage(message);
				msg.setSeverity(FacesMessage.SEVERITY_ERROR);
				throw new ValidatorException(msg);
			}			
		}
	}	
}

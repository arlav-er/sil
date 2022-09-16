package it.eng.myportal.validator;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;

/**
 * @author enrico
 * 
 *         Il campo deve rappresentara un'ora, cioe' essere un valore tra 00 e 23
 * 
 */
@FacesValidator(value = "dateOreValidator")
public class DateOreValidator implements Validator {

	/**
	 * Main method of the validator
	 * 
	 * @param context
	 *            FacesContext
	 * @param toValidate
	 *            UIComponent
	 * @param value
	 *            Object
	 * @throws ValidatorException
	 */
	@Override
	public void validate(FacesContext context, UIComponent component, Object value) throws ValidatorException {
		/* input value */
		String ore = (String) value;

		if (ore == null) {
			return;
		}

		Integer oraeInt = Integer.parseInt(ore);
		if (oraeInt >= 0 && oraeInt <= 23) {
			return;
		} else {
			String message = "devi inserire un valore per le ore compreso tra 00 e 23";
			FacesMessage msg = new FacesMessage(message);
			msg.setSeverity(FacesMessage.SEVERITY_ERROR);

			throw new ValidatorException(msg);
		}
	}
}

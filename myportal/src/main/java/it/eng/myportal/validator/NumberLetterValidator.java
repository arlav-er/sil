package it.eng.myportal.validator;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;

/**
 * @author Rodi
 * 
 */
@FacesValidator(value = "numberLetterValidator")
public class NumberLetterValidator implements Validator {

	private static final String PATTERN_VALIDATOR = "^([a-zA-Z0-9])+$";

	/**
	 * Metodo per la validazione della password. Va utilizzato sempre questo
	 * 
	 * @param context
	 *            FacesContext
	 * @param toValidate
	 *            UIComponent
	 * @param value
	 *            Object
	 */
	@Override
	public void validate(FacesContext context, UIComponent toValidate, Object value) {

		String username = (String) value;

		if (username != null) {
			Pattern p = Pattern.compile(PATTERN_VALIDATOR);
			Matcher m = p.matcher(username);
			boolean matchFound = m.matches();

			// Costruisco
			if (!matchFound) {
				String message = "Sono consentiti solo numeri e lettere";
				FacesMessage msg = new FacesMessage(message);
				msg.setSeverity(FacesMessage.SEVERITY_ERROR);
				throw new ValidatorException(msg);
			}
		}
	}
}

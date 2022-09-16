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
 * @author pegoraro
 * 
 */
@FacesValidator(value = "usernameValidator")
public class UsernameValidator implements Validator {


	private Pattern pattern;
	private Matcher matcher;

	/**
	 * Nessuno spazio concesso
	 */
	private static final String USERNAME_PATTERN = "\\s";

	public UsernameValidator() {
		pattern = Pattern.compile(USERNAME_PATTERN);
	}

	
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
		matcher = pattern.matcher(username);
		if (matcher.find()) {
			String message = "Username non valido. Lo Username non può contenere alcuno spazio bianco.";
			FacesMessage msg = new FacesMessage(message);
			msg.setSeverity(FacesMessage.SEVERITY_ERROR);
			throw new ValidatorException(msg);
		}
	}
}

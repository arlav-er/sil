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
@FacesValidator(value = "passwordValidator")
public class PasswordValidator implements Validator {


	private Pattern pattern;
	private Matcher matcher;

	/**
	 * Almeno una digit
	 * Almeno un carattere maiuscolo
	 * Nessuno spazio
	 * Tra gli 8 ed i 32 caratteri
	 */
	private static final String PASSWORD_PATTERN = "(?=.*\\d)(?=.*[A-Z])[^\\s]{8,32}";

	public PasswordValidator() {
		pattern = Pattern.compile(PASSWORD_PATTERN);
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

		String password = (String) value;
		matcher = pattern.matcher(password);
		if (!matcher.matches()) {
			String message = "Password non valida. Almeno un numero. Almeno una lettera maiuscola. 8-32 caratteri.";
			FacesMessage msg = new FacesMessage(message);
			msg.setSeverity(FacesMessage.SEVERITY_ERROR);
			throw new ValidatorException(msg);
		}
	}
}

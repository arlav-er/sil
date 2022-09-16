/**
 *
 */
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
@FacesValidator(value = "emailValidator")
public class EmailValidator implements Validator {

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.faces.validator.Validator#validate (javax.faces.context.FacesContext,
	 * javax.faces.component.UIComponent, java.lang.Object)
	 */
	/**
	 * Metodo per la validazione di emails. Va utilizzato sempre questo
	 *
	 * @param context
	 *            FacesContext
	 * @param component
	 *            UIComponent
	 * @param value
	 *            Object
	 */
	@Override
	public void validate(FacesContext context, UIComponent component, Object value) {
		if (value == null)
			return;
		// Questo credo non serva, lo metto per type sureness
		// e 'sticazzi se mi venite a dire qualcosa
		if (!(value instanceof String)) {
			String message = "Formato in input errato. " + "Errore di tipo: valore non-stringa: " + value.toString()
					+ "";
			FacesMessage msg = new FacesMessage(message);
			msg.setSeverity(FacesMessage.SEVERITY_ERROR);
			context.addMessage(component.getClientId(), msg);
			throw new ValidatorException(msg);
		}
		String email = (String) value;
		// regular expression di email valida
		Pattern p = Pattern.compile(
				"([A-Za-z0-9!#-'\\*\\+\\-/=\\?\\^_`\\{-~]+(\\.[A-Za-z0-9!#-'\\*\\+\\-/=\\?\\^_`\\{-~]+)*@[A-Za-z0-9!#-'\\*\\+\\-/=\\?\\^_`\\{-~]+(\\.[A-Za-z0-9!#-'\\*\\+\\-/=\\?\\^_`\\{-~]+)*)?");
		Matcher m = p.matcher(email);
		boolean matchFound = m.matches();
		// Costruisco
		if (!matchFound) {
			String message = "Formato email errato";
			FacesMessage msg = new FacesMessage(message);
			msg.setSeverity(FacesMessage.SEVERITY_ERROR);
			// context.addMessage(component.getClientId(), msg);
			throw new ValidatorException(msg);
		}
	}

}

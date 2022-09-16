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
 * Classe per la validazione dei numeri di telefono
 *
 * @author pegoraro
 *
 */
@FacesValidator(value="numeroTelefonicoValidator" )
public class NumeroTelefonicoValidator implements Validator {

	/**
	 * Metodo per la validazione di Numero Telefonico.
	 * Va utilizzato sempre questo
	 *
	 * @param context FacesContext
	 * @param toValidate UIComponent
	 * @param value Object
	 */
	@Override
	public void validate(FacesContext context, UIComponent toValidate,
			Object value) {

		if (value == null) return;
		//Questo credo non serva, lo metto per type sureness
		//e 'sticazzi se mi venite a dire qualcosa
		if (!(value instanceof String)){
			String message = "Formato in input errato. " +
					"Errore di tipo: valore non-stringa: "+value.toString()+".";
			FacesMessage msg = new FacesMessage(message);
			msg.setSeverity(FacesMessage.SEVERITY_ERROR);
			context.addMessage(toValidate.getClientId(), msg);
			throw new ValidatorException(msg);
		}
		//Tolgo trattini spazi e slash, che ci possono essere
		String email = (String) value;
		email = email.replaceAll("/", "");
		email = email.replaceAll(" ", "");
		email = email.replaceAll("-", "");
		//regular expression per numero telefonico, che una volta
		//strippati spazi e slash può iniziare col +
		Pattern p = Pattern.compile("(\\+)?([0-9])*");
		Matcher m = p.matcher(email);
		boolean matchFound = m.matches();

		//Costruisco
		if (!matchFound) {
			String message = "Formato Numero Telefonico Errato.";
			FacesMessage msg = new FacesMessage(message);
			msg.setSeverity(FacesMessage.SEVERITY_ERROR);
			context.addMessage(toValidate.getClientId(), msg);
			throw new ValidatorException(msg);
		}
		return;
	}

}

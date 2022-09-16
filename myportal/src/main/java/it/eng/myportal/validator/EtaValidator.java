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
 * Classe per la validazione dell'età
 *
 * @author pegoraro
 *
 */
@FacesValidator(value="etaValidator" )
public class EtaValidator implements Validator {

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
		String eta = (String) value;
		
		Pattern p = Pattern.compile("^[1-9]\\d$");
		Matcher m = p.matcher(eta);
		boolean matchFound = m.matches();

		//Costruisco
		if (!matchFound) {
			String message = "Età non valida.";
			FacesMessage msg = new FacesMessage(message);
			msg.setSeverity(FacesMessage.SEVERITY_ERROR);
			context.addMessage(toValidate.getClientId(), msg);
			throw new ValidatorException(msg);
		}
		return;
	}

}

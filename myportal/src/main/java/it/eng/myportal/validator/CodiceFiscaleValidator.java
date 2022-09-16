package it.eng.myportal.validator;

import it.eng.myportal.utils.Utils;

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
@FacesValidator(value = "codiceFiscaleValidator")
public class CodiceFiscaleValidator implements Validator {

	/**
	 * Metodo per la validazione di codici fiscali. Va utilizzato sempre questo
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
		if (value == null) {
			return;
		}
		// Questo credo non serva, lo metto per type sureness
		// e 'sticazzi se mi venite a dire qualcosa
		if (!(value instanceof String)) {
			String message = "Formato in input errato. " + "Errore di tipo: valore non-stringa: " + value.toString()
			        + ".";
			FacesMessage msg = new FacesMessage(message);
			msg.setSeverity(FacesMessage.SEVERITY_ERROR);
			throw new ValidatorException(msg);
		}

		String cf = (String) value;

		String errorMessage = Utils.CodiceFiscale.verificaPersona(cf);
		if (!errorMessage.isEmpty()) {
			FacesMessage msg = new FacesMessage(errorMessage);
			msg.setSeverity(FacesMessage.SEVERITY_ERROR);
			throw new ValidatorException(msg);
		}
	}
}

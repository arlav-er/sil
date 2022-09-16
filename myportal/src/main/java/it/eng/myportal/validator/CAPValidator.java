/**
 *
 */
package it.eng.myportal.validator;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;

/**
 *
 * Validatore di un CAP.
 * Controlla che sia composto da cinque cifre.
 *
 * @author Rodi A.
 *
 */
@FacesValidator(value="capValidator" )
public class CAPValidator implements Validator {
	public void validate(FacesContext context, UIComponent component, Object arg2)
			throws ValidatorException {

		boolean contienelettere = false;
		if (arg2==null) {
			return;
		}
		String campo = arg2.toString();

		if (campo.length() != 5) {
			FacesMessage message = new FacesMessage();
			message.setSummary("Il CAP deve contenere cinque cifre");
			message.setSeverity(FacesMessage.SEVERITY_ERROR);
			context.addMessage(component.getClientId(), message);
			throw new ValidatorException(message);
		}

		for (int i = 0 ; i < campo.length() ; i++) {
			if (!Character.isDigit(campo.charAt(i))) {
				contienelettere = true;
				break;	/*tralascia il resto*/
			}
		}
		if (contienelettere) {
			FacesMessage message = new FacesMessage();
	        message.setSummary("CAP non valido");
	        message.setSeverity(FacesMessage.SEVERITY_ERROR);
	        context.addMessage(component.getClientId(), message);
	        throw new ValidatorException(message);
	    }

	}

}

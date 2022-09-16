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
 * @author turro
 *
 */
@FacesValidator(value="mittenteSareValidator" )
public class MittenteSareValidator implements Validator {

	/* (non-Javadoc)
	 * @see javax.faces.validator.Validator#validate
	 * (javax.faces.context.FacesContext, javax.faces.component.UIComponent, java.lang.Object)
	 */
	/**
	 * Metodo per la validazione del mittente Sare.
	 * Va utilizzato sempre questo
	 *
	 * @param context FacesContext
	 * @param component UIComponent
	 * @param value Object
	 */
	@Override
	public void validate(FacesContext context, UIComponent component,
			Object value) {
		if (value == null) return;
		if (!(value instanceof String)){
			String message = "Formato in input errato. " +
					"Errore di tipo: valore non-stringa: "+value.toString()+"";
			FacesMessage msg = new FacesMessage(message);
			msg.setSeverity(FacesMessage.SEVERITY_ERROR);
			context.addMessage(component.getClientId(), msg);
			throw new ValidatorException(msg);
		}
		String mittente = (String) value;
		if (mittente.contains("'")) {
			String message = "Formato mittente errato";
			FacesMessage msg = new FacesMessage(message);
			msg.setSeverity(FacesMessage.SEVERITY_ERROR);
			context.addMessage(component.getClientId(), msg);
			throw new ValidatorException(msg);
		}
	}

}

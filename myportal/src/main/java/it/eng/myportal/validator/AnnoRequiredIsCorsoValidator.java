/**
 * 
 */
package it.eng.myportal.validator;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;

@FacesValidator(value = "annoRequiredIsCorsoValidator")
public class AnnoRequiredIsCorsoValidator implements Validator {

	@Override
	public void validate(FacesContext facesContext, UIComponent component, Object value) throws ValidatorException {
		if (value == null) {
			return;
		}

		UIInput annoConseguimento = (UIInput) component.getAttributes().get("annoConseg");
		Boolean flgCorso = (Boolean) value;
		String message = "";
		if (!flgCorso.booleanValue()) {// flgInCorso impostato a false
			if (annoConseguimento.getValue() == null) {
				message = "L' anno di conseguimento è obbligatorio se non in corso";
				FacesMessage msg = new FacesMessage(message);
				msg.setSeverity(FacesMessage.SEVERITY_ERROR);
				facesContext.addMessage(component.getClientId(), msg);
				throw new ValidatorException(msg);
			}
		} else { // flgInCorso impostato a true
			if (annoConseguimento.getValue() != null) {
				message = "Selezionare In corso oppure inserire anno conseguimento: entrambi non sono consentiti";
				FacesMessage msg = new FacesMessage(message);
				msg.setSeverity(FacesMessage.SEVERITY_ERROR);
				facesContext.addMessage(component.getClientId(), msg);
				throw new ValidatorException(msg);
			}
		}
		
	}
}
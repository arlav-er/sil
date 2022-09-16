package it.eng.myportal.validator;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;

import it.eng.myportal.utils.ConstantsSingleton;

@FacesValidator(value = "checkBoxRequiredNoDiscriminazioneValidator")
public class CheckBoxRequiredNoDiscriminazioneValidator implements Validator {

	@Override
	public void validate(FacesContext facesContext, UIComponent component, Object value) throws ValidatorException {
		if (value == null) {
			return;
		}

		Boolean flgTratt = (Boolean) value;

		if (!flgTratt.booleanValue()) {
			String message = ConstantsSingleton.CHECK_ACCETT_COND_SERV;
			FacesMessage msg = new FacesMessage(message);
			msg.setSeverity(FacesMessage.SEVERITY_ERROR);
			facesContext.addMessage(component.getClientId(), msg);
			throw new ValidatorException(msg);
		}

	}
}

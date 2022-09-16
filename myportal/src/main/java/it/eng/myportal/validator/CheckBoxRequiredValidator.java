package it.eng.myportal.validator;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;

@FacesValidator(value = "checkBoxRequiredValidator")
public class CheckBoxRequiredValidator implements Validator {

	@Override
	public void validate(FacesContext facesContext, UIComponent component, Object value) throws ValidatorException {
		if (value == null) {
			return;
		}

	
	
		Boolean flgTratt = (Boolean) value;

		if (!flgTratt.booleanValue()) {
		
				String message = "Check trattamento dati Personali è obbligatorio";
				FacesMessage msg = new FacesMessage(message);
				msg.setSeverity(FacesMessage.SEVERITY_ERROR);
				facesContext.addMessage(component.getClientId(), msg);
				throw new ValidatorException(msg);
			}
		
	}
}

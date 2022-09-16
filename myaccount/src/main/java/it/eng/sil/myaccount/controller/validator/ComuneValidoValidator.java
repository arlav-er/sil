package it.eng.sil.myaccount.controller.validator;

import it.eng.sil.mycas.model.entity.decodifiche.DeComune;

import java.util.Calendar;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;

@FacesValidator("comuneValidoValidator")
public class ComuneValidoValidator implements Validator {

	public ComuneValidoValidator() {
	}

	/**
	 * Check if there's already registerd email address
	 */
	@Override
	public void validate(FacesContext context, UIComponent component, Object value) throws ValidatorException {
		if (value == null)
			return;

		DeComune deComune = (DeComune) value;

		if (Calendar.getInstance().getTime().before(deComune.getDtInizioVal())
				|| Calendar.getInstance().getTime().after(deComune.getDtFineVal())) {
			String message = "Il comune non è valido";
			FacesMessage msg = new FacesMessage(message);
			msg.setSeverity(FacesMessage.SEVERITY_ERROR);
			context.addMessage(component.getClientId(), msg);
			throw new ValidatorException(msg);
		}
	}
}

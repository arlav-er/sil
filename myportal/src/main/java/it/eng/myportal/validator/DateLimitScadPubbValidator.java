package it.eng.myportal.validator;

import java.util.Date;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;

@FacesValidator(value = "dateLimitScadPubbValidator")
public class DateLimitScadPubbValidator implements Validator {

	/**
	 * Main method of the validator
	 * 
	 * @param context
	 *            FacesContext
	 * @param toValidate
	 *            UIComponent
	 * @param value
	 *            Object
	 * @throws ValidatorException
	 */
	@Override
	public void validate(FacesContext context, UIComponent component, Object value) throws ValidatorException {
		/* input value */
		Date date = (Date) value;
		if (date == null)
			return;

		Object dataA = component.getAttributes().get("limitDate");

		Date dateRif = (Date) dataA;

		if (dateRif == null)
			return;
		/*
		 * if the date is after the limit date, throw error
		 */
		if (date.after(dateRif)) {
			String message = "La data di pubblicazione non deve essere successiva alla data di scadenza pubblicazione";
			FacesMessage msg = new FacesMessage(message);
			msg.setSeverity(FacesMessage.SEVERITY_ERROR);

			throw new ValidatorException(msg);
		}
	}
}

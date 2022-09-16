package it.eng.myportal.validator;

import java.util.Date;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author hatemalimam
 * 
 *         Restrict Date input after a specific date.
 * 
 */
@FacesValidator(value = "dateLimitValidator")
public class DateLimitValidator implements Validator {

	private Log log = LogFactory.getLog(this.getClass());

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

		/* getting the attribute passed limitDate */
		Date limitDate = (Date) component.getAttributes().get("limitDate");

		if (date == null)
			return;

		/*
		 * if the date is after the limit date, throw error
		 */
		if (date.after(limitDate)) {
			String message = "La data non deve essere successiva a oggi";
			FacesMessage msg = new FacesMessage(message);
			msg.setSeverity(FacesMessage.SEVERITY_ERROR);

			throw new ValidatorException(msg);
		}
	}
}

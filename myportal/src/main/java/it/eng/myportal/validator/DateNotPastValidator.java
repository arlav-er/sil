package it.eng.myportal.validator;

import java.util.Calendar;
import java.util.Date;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;

/**
 * @author enrico
 * 
 *         Classe per la validazione delle date. Controlla che la data inserita non sia passata.
 */
@FacesValidator(value = "dateNotPastValidator")
public class DateNotPastValidator implements Validator {

	/**
	 * Metodo di validazione che controlla che la data insertia non sia futura.
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
	public void validate(FacesContext context, UIComponent toValidate, Object value) throws ValidatorException {
		/* la data da validare */
		Date date = (Date) value;

		/* ottengo la data di ieri */
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DATE, -1);
		Date yesterday = cal.getTime();

		if (date == null) {
			return;
		}

		/*
		 * se la data da validare e' successiva alla data odierna sollevo l'errore
		 */
		if (!date.after(yesterday)) {
			String message = "La data non può essere passata.";
			FacesMessage msg = new FacesMessage(message);
			msg.setSeverity(FacesMessage.SEVERITY_ERROR);

			throw new ValidatorException(msg);
		}
	}
}

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
 * @author Rodi Validatore degli 'anni'. Verifica se l'anno inserito è valido
 *         (quattro cifre), che non sia futuro, e che sia dopo il 1900.
 */
@FacesValidator(value = "annoPassatoValidator")
public class AnnoPassatoValidator implements Validator {

	/**
	 * Metodo di validazione che controlla l'anno
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
	public void validate(FacesContext context, UIComponent toValidate,
			Object value) throws ValidatorException {

		if (value != null) {
			String anno = value.toString();
			
			if (!anno.isEmpty()) {
				Integer numAnno = 0;
				try {
					numAnno = Integer.valueOf(anno);
				} catch (NumberFormatException e) {
					throwError("Valore anno non valido.");
				}

				Calendar.getInstance().setTime(new Date());
				Integer thisYear = Calendar.getInstance().get(Calendar.YEAR);
				if (numAnno > thisYear) {
					throwError("Anno futuro non valido.");
				} else if (numAnno < 1900) {
					throwError("L'anno deve essere successivo al 1900.");
				}
			}
		}
		return;
	}

	private void throwError(String message) {
		FacesMessage msg = new FacesMessage(message);
		msg.setSeverity(FacesMessage.SEVERITY_ERROR);
		throw new ValidatorException(msg);
	}
}

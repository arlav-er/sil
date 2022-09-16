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
 * Validatore per la data di nascita nella pagina di adesione alla Garanzia Over 30. Controlla che il cittadino sia
 * effettivamente nato almeno 30 anni fa.
 * 
 * @author gicozza
 */
@FacesValidator(value = "dataNascitaGaranziaOverValidator")
public class DataNascitaGaranziaOverValidator implements Validator {

	@Override
	public void validate(FacesContext context, UIComponent toValidate, Object value) throws ValidatorException {
		FacesMessage errorMsg = new FacesMessage();
		errorMsg.setSeverity(FacesMessage.SEVERITY_ERROR);

		// Mi assicuro che l'oggetto passato sia effettivamente una data.
		if (!(value instanceof Date)) {
			errorMsg.setSummary("L'oggetto che sto validando non è una data");
			throw new ValidatorException(errorMsg);
		}

		// Controllo che sia una data precedente a 30 anni fa.
		Date dataInserita = (Date) value;
		Calendar trentAnniFa = Calendar.getInstance();
		trentAnniFa.add(Calendar.YEAR, -30);

		if (dataInserita.after(trentAnniFa.getTime())) {
			errorMsg.setSummary("Siamo spiacenti, ma per aderire al progetto Umbriattiva Adulti devi avere 30 o più anni");
			throw new ValidatorException(errorMsg);
		}

	}

}

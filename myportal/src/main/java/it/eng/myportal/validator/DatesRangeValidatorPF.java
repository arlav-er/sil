/**
 * 
 */
package it.eng.myportal.validator;

import java.util.Calendar;
import java.util.Date;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

@FacesValidator(value = "datesRangeValidatorPF")
public class DatesRangeValidatorPF implements Validator {

	protected Log log = LogFactory.getLog(this.getClass());

	/**
	 * @param context
	 *            FacesContext
	 * @param component
	 *            UIComponent
	 * @param value
	 *            Object
	 */
	@Override
	public void validate(FacesContext context, UIComponent component, Object value) {
		if (value == null) {
			return;
		}

		String rangeUnit = (String) component.getAttributes().get("range_unit");
		String rangeValueString = (String) component.getAttributes().get("range_value");
		
		Date dataA = (Date) value;

		Object dataRif = component.getAttributes().get("data_da_id");
		Date dataDa = (Date) dataRif;

		Integer rangeValue = 0;
		try {
			rangeValue = Integer.parseInt(rangeValueString);
		} catch (NumberFormatException nfe) {
			log.error("Formato errato dell'attributo \"range_value\" "
					+ "per il validatore \"DatesRangeValidator\", deve essere un numero intero");
		}

		if ((dataA == null) || (dataDa == null)) {
			/*
			 * Se non sono presenti entrambe le date la validazione ha successo. Ci sono 2 casi possibili: - le date
			 * potrebbero non essere tutte obbligatorie - la chiamata al validatore viene fatta diverse volte (on blur),
			 * non in tutte le chiamate entrambe le date sono definite Nel primo caso se ne occupano altri validatori,
			 * nel secondo caso bisogna evitare le chiamate in piu'.
			 */
			return;
		}

		if (dataA.before(dataDa)) {
			String message = "La data di scadenza non può essere precedente alla data di pubblicazione.";
			FacesMessage msg = new FacesMessage(message);
			msg.setSeverity(FacesMessage.SEVERITY_ERROR);
			throw new ValidatorException(msg);

		}

		Calendar maxDataA = Calendar.getInstance();
		maxDataA.setTime(dataDa);
		if (rangeUnit.toUpperCase().equals("ANNO")) {
			maxDataA.set(Calendar.YEAR, maxDataA.get(Calendar.YEAR) + rangeValue);
		} else if (rangeUnit.toUpperCase().equals("MESE")) {
			maxDataA.set(Calendar.MONTH, maxDataA.get(Calendar.MONTH) + rangeValue);
		} else if (rangeUnit.toUpperCase().equals("GIORNO")) {
			maxDataA.set(Calendar.DAY_OF_YEAR, maxDataA.get(Calendar.DAY_OF_YEAR) + rangeValue);
		}

		/*
		 * Controllo di validazione, dataDa deve essere precedente a dataA
		 */
		if (dataA.after(maxDataA.getTime())) {
			String message = "";
			if (rangeValue == 1) {
				message = "Data fuori range massimo, il massimo è " + rangeValue + " " + rangeUnit;
			} else {
				message = "Non può essere superiore + " + rangeValue + " giorni dalla data di pubblicazione";
			}

			FacesMessage msg = new FacesMessage(message);
			msg.setSeverity(FacesMessage.SEVERITY_ERROR);
			throw new ValidatorException(msg);
		}

		return;
	}
}
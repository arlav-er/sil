/**
 * 
 */
package it.eng.myportal.validator;

import java.util.Calendar;
import java.util.Date;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.component.html.HtmlInputText;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 
 * @author Enrico D'Angelo
 * 
 *         Classe per la validazione del range tra due date (da ... a ...). Le
 *         due date devono indicare un periodo non piu' lungo del range passato
 * @author
 * 
 */
@FacesValidator(value = "datesRangeValidator")
public class DatesRangeValidator implements Validator {

	/**
	 * Logger di classe. Utilizzare questo log per scrivere messaggi di errore,
	 * info e debug. L'attributo "range_unit" indica l'unita' di misura del
	 * range e puo' assumere i valori: giorno, mese o anno (case insensitive).
	 */
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
		UIComponent view = context.getViewRoot();

		// id completo del componente ([form_id]:[id componente]:inputText)
		String componentId = component.getClientId();
		// id dell'inputText a cui e' agganciato il validatore (inputText)
		String inputTextId = component.getId();
		// id del componente inputText scritto da noi ([form_id]:[id
		// componente])
		String customComponentId = componentId.substring(0, componentId.indexOf(inputTextId));
		// il componente inputText scritto da noi
		UIComponent customComponent = (UIComponent) view.findComponent(customComponentId);
		// id del componente data_da da validare insieme a data_a
		String dataDaId = (String) customComponent.getAttributes().get("data_da_id");

		// Unita' di misura del range tra le date. Puo' essere: giorni, mesi,
		// anni
		String rangeUnit = (String) customComponent.getAttributes().get("range_unit");

		// Valore del range massimo tra le date
		String rangeValueString = (String) customComponent.getAttributes().get("range_value");

		if (dataDaId == null) {
			log.error("Non e' stato definito l'attributo \"data_da_id\" " + "per il validatore \"DatesRangeValidator\"");
		}
		if (rangeUnit == null) {
			log.error("Non e' stato definito l'attributo \"range_unit\" " + "per il validatore \"DatesRangeValidator\"");
		}
		if (rangeValueString == null) {
			log.error("Non e' stato definito l'attributo \"range_value\" "
					+ "per il validatore \"DatesRangeValidator\"");
		}

		/*
		 * Ottengo i due componenti data da validare
		 */
		HtmlInputText htmlInputText = (HtmlInputText) view.findComponent(dataDaId);
		Date dataDa = null;
		if (htmlInputText != null) {
			dataDa = (Date) htmlInputText.getValue();
			// dataDa = (Date) htmlInputText.getSubmittedValue();
		}
		Date dataA = (Date) value;

		Integer rangeValue = 0;
		try {
			rangeValue = Integer.parseInt(rangeValueString);
		} catch (NumberFormatException nfe) {
			log.error("Formato errato dell'attributo \"range_value\" "
					+ "per il validatore \"DatesRangeValidator\", deve essere un numero intero");
		}

		if ((dataA == null) || (dataDa == null)) {
			/*
			 * Se non sono presenti entrambe le date la validazione ha successo.
			 * Ci sono 2 casi possibili: - le date potrebbero non essere tutte
			 * obbligatorie - la chiamata al validatore viene fatta diverse
			 * volte (on blur), non in tutte le chiamate entrambe le date sono
			 * definite Nel primo caso se ne occupano altri validatori, nel
			 * secondo caso bisogna evitare le chiamate in piu'.
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
				message = "Data fuori range massimo, il massimo è " + rangeValue + " " + rangeUnit.substring(0, rangeUnit.length() - 1) + "i";	
			}
			
			FacesMessage msg = new FacesMessage(message);
			msg.setSeverity(FacesMessage.SEVERITY_ERROR);
			throw new ValidatorException(msg);
		}

		return;
	}
}
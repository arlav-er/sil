package it.eng.myportal.validator;

import java.util.Date;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;

import org.primefaces.component.calendar.Calendar;

/**
 * Validatore che controlla che una data sia precedente al valore del componente.
 * Da usare nelle pagine che NON usano i Custom Component.
 *
 * Il componente che contiene la data da confrontare va passato con l'attributo "otherDate".
 * 
 * Tramite l'attributo "lessOrEqual" è possibile specificare se si vuole che due date
 * uguali passino il controllo o meno. Di default lo passano.
 * Esempio: <f:attribute name="lessOrEqual" value="false" /> se si vuole che non lo passino.
 * 
 * @author gicozza
 *
 */
@FacesValidator(value = "twoDatesValidatorPrimefaces")
public class TwoDatesValidatorPrimefaces implements Validator{

	@Override
	public void validate(FacesContext context, UIComponent component, Object value)
			throws ValidatorException {
		// Controllo che il valore esista e sia di tipo corretto.
		if (value == null) {
			return;
		}
		
		if (!(value instanceof Date)) {
			FacesMessage msg = new FacesMessage("Errore di validazione: non è una data.");
			msg.setSeverity(FacesMessage.SEVERITY_ERROR);
			throw new ValidatorException(msg);
		}
		
		// Estraggo il valore e l'altra data.
		Date date = (Date) value;
		Date otherDate = null;
		Object otherDateObj = component.getAttributes().get("otherDate");
		if (otherDateObj != null && otherDateObj instanceof UIInput) {
			UIInput otherDateComponent = (UIInput)otherDateObj;
			if (otherDateComponent.getValue() instanceof Date) {
				otherDate = (Date) otherDateComponent.getValue();
			}
		}
		
		// Se manca una delle due date, non do errore: potrebbe non essere ancora stata inserita.
		if (date == null || otherDate == null)
			return;
		
		// Controllo l'attributo lessOrEqual
		Boolean lessOrEqual = true;
		Object lessOrEqualObj = component.getAttributes().get("lessOrEqual");
		if (lessOrEqualObj != null && lessOrEqualObj instanceof String) {
			String lessOrEqualStr = (String) lessOrEqualObj;
			if (lessOrEqualStr.equalsIgnoreCase("false"))
				lessOrEqual = false;
		}
		
		// Faccio il confronto tra le due date.
		if (lessOrEqual) {
			if (date.before(otherDate)) {
				FacesMessage msg = new FacesMessage("La data di fine periodo non può essere "
						+ "antecedente alla data di inizio.");
				msg.setSeverity(FacesMessage.SEVERITY_ERROR);
				throw new ValidatorException(msg);
			}
		} else {
			if (!otherDate.before(date)) {
				FacesMessage msg = new FacesMessage("La data di inizio periodo deve essere "
						+ "antecedente alla data di fine.");
				msg.setSeverity(FacesMessage.SEVERITY_ERROR);
				throw new ValidatorException(msg);
			}
		}
	}

}

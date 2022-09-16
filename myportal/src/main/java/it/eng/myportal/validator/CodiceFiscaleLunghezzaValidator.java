package it.eng.myportal.validator;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;

/**
 * Validatore usato per i parametri di ricerca che possono essere sia un codice
 * fiscale normale che un codice fiscale temporaneo.
 * 
 * Controlla solo la lunghezza del codice: 16 caratteri alfanumerici o 11
 * caratteri numerici.
 * 
 * @author gicozza
 */
@FacesValidator(value = "codiceFiscaleLunghezzaValidator")
public class CodiceFiscaleLunghezzaValidator implements Validator {

	@Override
	public void validate(FacesContext context, UIComponent toValidate, Object value) throws ValidatorException {
		if (value == null) {
			return;
		}

		// Controllo che il valore sia una stringa.
		if (!(value instanceof String)) {
			FacesMessage msg = new FacesMessage("Errore di validazione: input non è stringa.");
			msg.setSeverity(FacesMessage.SEVERITY_ERROR);
			throw new ValidatorException(msg);
		}

		// Controllo la lunghezza del codice fiscale (a seconda se è temporaneo
		// o no).
		String codiceFiscale = (String) value;
		if (codiceFiscale.matches("\\d+")) {
			if (codiceFiscale.length() != 11) {
				FacesMessage msg = new FacesMessage("Un codice fiscale temporaneo deve essere lungo 11 caratteri.");
				msg.setSeverity(FacesMessage.SEVERITY_ERROR);
				throw new ValidatorException(msg);
			}
		} else {
			if (codiceFiscale.length() != 16) {
				FacesMessage msg = new FacesMessage("Un codice fiscale deve essere lungo 16 caratteri.");
				msg.setSeverity(FacesMessage.SEVERITY_ERROR);
				throw new ValidatorException(msg);
			}
		}
	}

}

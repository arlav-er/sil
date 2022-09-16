package it.eng.myportal.validator;

import it.eng.myportal.utils.Utils;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;

/**
 * Valida il codice fiscale di un'azienda
 * @author Rodi A.
 * 
 */
@FacesValidator(value = "codiceFiscaleAziendaValidator")
public class CodiceFiscaleAziendaValidator implements Validator {


	/**
	 * Metodo per la validazione di codici fiscali delle aziende 
	 * Va utilizzato sempre questo
	 * 
	 * @param context
	 *            FacesContext
	 * @param toValidate
	 *            UIComponent
	 * @param value
	 *            Object
	 */
	@Override
	public void validate(FacesContext context, UIComponent component, Object value) {
		if (value == null) return;
		//Questo credo non serva, lo metto per type sureness
		//e 'sticazzi se mi venite a dire qualcosa
		if (!(value instanceof String)){
			String message = "Formato in input errato. " +
					"Errore di tipo: valore non-stringa: "+value.toString()+"";
			FacesMessage msg = new FacesMessage(message);
			msg.setSeverity(FacesMessage.SEVERITY_ERROR);
			context.addMessage(component.getClientId(), msg);
			throw new ValidatorException(msg);
		}
		String codiceFiscale = (String) value;
		
		String errorMessage = Utils.CodiceFiscale.verificaAzienda(codiceFiscale);
		if (!errorMessage.isEmpty()) {
			FacesMessage msg = new FacesMessage(errorMessage);
			msg.setSeverity(FacesMessage.SEVERITY_ERROR);
			throw new ValidatorException(msg);
		}
	}
}

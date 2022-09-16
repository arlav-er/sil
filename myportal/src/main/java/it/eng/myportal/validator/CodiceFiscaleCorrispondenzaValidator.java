package it.eng.myportal.validator;

import it.eng.myportal.utils.CfUtils;
import it.eng.myportal.utils.Utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;

/**
 * Questa classe controlla non solo che il codice fiscale sia corretto, ma anche che corrisponda ai parametri "nome",
 * "cognome" e "sesso". (Questi parametri devono essere inclusi nel componente che usa il validatore, per esempio
 * <f:attribute name="sesso" value="M"/> )
 * 
 * @author gicozza
 *
 */
@FacesValidator(value = "codiceFiscaleCorrispondenzaValidator")
public class CodiceFiscaleCorrispondenzaValidator implements Validator {

	@Override
	public void validate(FacesContext context, UIComponent component, Object value) throws ValidatorException {
		// Controllo che il valore esista e sia di tipo corretto.
		if (value == null) {
			return;
		}
		
		if (!(value instanceof String)) {
			FacesMessage msg = new FacesMessage("Errore di validazione: oggetto non stringa");
			msg.setSeverity(FacesMessage.SEVERITY_ERROR);
			throw new ValidatorException(msg);
		}
		
		String codiceFiscale = (String) value;
		codiceFiscale = codiceFiscale.trim().toUpperCase();
		
		// Se è un codice fiscale temporaneo, controllo solo che sia ben formato.
		if (CfUtils.isCodiceTemporaneo(codiceFiscale)) {
			if (!CfUtils.checkNumericoCF(codiceFiscale)) {
				FacesMessage msg = new FacesMessage("Codice fiscale temporaneo non valido.");
				msg.setSeverity(FacesMessage.SEVERITY_ERROR);
				throw new ValidatorException(msg);
			}
			return;
		}
		
		// Controllo che il codice fiscale sia ben formato.
		String validaCF = Utils.CodiceFiscale.verificaPersona(codiceFiscale);
		if (!validaCF.isEmpty()) {
			FacesMessage msg = new FacesMessage("Errore nel codice fiscale: " + validaCF);
			msg.setSeverity(FacesMessage.SEVERITY_ERROR);
			throw new ValidatorException(msg);
		}
		
		// Estraggo i parametri.
		String nome = "";
		String cognome = "";
		String sesso = "";
		Date dataNascita = null;
		
		Object nomeObj = component.getAttributes().get("nome");
		if (nomeObj != null && nomeObj instanceof UIInput) {
			UIInput nomeComponent = (UIInput)nomeObj;
			if (nomeComponent.isLocalValueSet()) {
				nome = nomeComponent.getValue().toString();
			} else {
			nome = nomeComponent.getSubmittedValue().toString();
		}
		}
		nome = nome.toUpperCase();
		
		Object cognomeObj = component.getAttributes().get("cognome");
		if (cognomeObj != null && cognomeObj instanceof UIInput) {
			UIInput cognomeComponent = (UIInput)cognomeObj;
			if (cognomeComponent.isLocalValueSet()) {
				cognome = cognomeComponent.getValue().toString();
			} else {
			cognome = cognomeComponent.getSubmittedValue().toString();
		}
		}
		cognome = cognome.toUpperCase();
		
		Object sessoObj = component.getAttributes().get("sesso");
		if (sessoObj != null && sessoObj instanceof UIInput) {
			UIInput sessoComponent = (UIInput)sessoObj;
			if (sessoComponent.isLocalValueSet()) {
				sesso = sessoComponent.getValue().toString();
			} else {
			sesso = sessoComponent.getSubmittedValue().toString();
		}
		}
		
		Object dataNascitaObj = component.getAttributes().get("dataNascita");
		if (dataNascitaObj != null && dataNascitaObj instanceof UIInput) {
			UIInput dataNascitaComponent = (UIInput) dataNascitaObj;
			if (dataNascitaComponent.isLocalValueSet()) {
				Object dataComponentValue = dataNascitaComponent.getValue();
				if (dataComponentValue instanceof Date) {
					dataNascita = (Date) dataComponentValue;
				}
			} else {
				try {
					DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
					dataNascita = df.parse(dataNascitaComponent.getSubmittedValue().toString());
				} catch (Exception e) {
					dataNascita = null;
				}
			}
		}

		// Controllo la corrispondenza dei parametri.
		if (!nome.isEmpty()) {
			if (!CfUtils.checkNome(codiceFiscale, nome)) {
				FacesMessage msg = new FacesMessage("Il codice fiscale ed il nome non corrispondono");
				msg.setSeverity(FacesMessage.SEVERITY_ERROR);
				throw new ValidatorException(msg);
			}
		}
		
		if (!cognome.isEmpty()) {
			if (!CfUtils.checkCognome(codiceFiscale, cognome)) {
				FacesMessage msg = new FacesMessage("Il codice fiscale ed il cognome non corrispondono");
				msg.setSeverity(FacesMessage.SEVERITY_ERROR);
				throw new ValidatorException(msg);
			}
		}
		
		if (!sesso.isEmpty()) {
			if (!sesso.equalsIgnoreCase(CfUtils.getSesso(codiceFiscale))) {
				FacesMessage msg = new FacesMessage("Il codice fiscale ed il sesso non corrispondono");
				msg.setSeverity(FacesMessage.SEVERITY_ERROR);
				throw new ValidatorException(msg);
			}
		}

		if (dataNascita != null) {
			if (!dataNascita.equals(CfUtils.getDataNascita(codiceFiscale))) {
				FacesMessage msg = new FacesMessage("Il codice fiscale e la data di nascita non corrispondono");
				msg.setSeverity(FacesMessage.SEVERITY_ERROR);
				throw new ValidatorException(msg);
			}
	}	
	
}
}

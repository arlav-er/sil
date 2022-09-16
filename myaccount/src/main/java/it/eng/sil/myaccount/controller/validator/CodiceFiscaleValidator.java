package it.eng.sil.myaccount.controller.validator;


import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import it.eng.sil.base.utils.CFUtils;

@FacesValidator(value = "codiceFiscaleValidator")
public class CodiceFiscaleValidator implements Validator {

	/**
	 * Logger di classe. Utilizzare questo log per scrivere messaggi di errore, info e debug.
	 */
	protected Log log = LogFactory.getLog(this.getClass());

	private final String TYPE_AZIENDA = "azienda";
	private final String TYPE_PERSONA = "persona";

	@Override
	public void validate(FacesContext context, UIComponent toValidate, Object value) {
		if (value == null) {
			return;
		}

		String cf = (String) value;
		String type = (String) toValidate.getAttributes().get("type");

		if (type == null) {
			log.error("Missing attribute type, persona has been set");
			type = TYPE_PERSONA;
		}

		String errorMessage = null;
		if (type.equals(TYPE_AZIENDA)) {
			errorMessage = CFUtils.verificaAzienda(cf);
		} else {
			errorMessage = CFUtils.verificaPersona(cf);
		}

		if (!errorMessage.isEmpty()) {
			FacesMessage msg = new FacesMessage(errorMessage);
			msg.setSeverity(FacesMessage.SEVERITY_ERROR);
			throw new ValidatorException(msg);
		}
	}

}

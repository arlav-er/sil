package it.eng.sil.myaccount.controller.validator;

import it.eng.sil.myaccount.model.ejb.stateless.profile.PfPrincipalMyAccountEJB;
import it.eng.sil.mycas.utils.ConstantsCommons;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

@FacesValidator("uniqueEmailValidator")
public class UniqueEmailValidator implements Validator {

	private static final Log log = LogFactory.getLog(UniqueEmailValidator.class);

	private PfPrincipalMyAccountEJB pfPrincipalMyAccountEJB;

	public UniqueEmailValidator() {
		try {
			InitialContext ic = new InitialContext();
			pfPrincipalMyAccountEJB = (PfPrincipalMyAccountEJB) ic.lookup(ConstantsCommons.JNDI_BASE
					+ PfPrincipalMyAccountEJB.class.getSimpleName());
		} catch (NamingException e) {
			log.error(e);
		}
	}

	/**
	 * Check if there's already registerd email address
	 */
	@Override
	public void validate(FacesContext context, UIComponent component, Object value) throws ValidatorException {
		if (value == null || value.toString().isEmpty())
			return;

		String email = value.toString();
		Boolean pec = Boolean.FALSE;
		boolean unique = Boolean.FALSE;

		if (component.getAttributes().get("skipValue") != null) {
			if (email.equals(component.getAttributes().get("skipValue"))) {
				return;
			}
		}

		if (component.getAttributes().get("pec") != null)
			pec = Boolean.valueOf((String) component.getAttributes().get("pec"));

		if (pec) {
			unique = pfPrincipalMyAccountEJB.isPECUniqueEmail(email);
		} else {
			unique = pfPrincipalMyAccountEJB.isUniqueEmail(email);
		}

		if (!unique) {
			String message = "Questo indirizzo e-mail è già in uso. Provane un altro";
			FacesMessage msg = new FacesMessage(message);
			msg.setSeverity(FacesMessage.SEVERITY_ERROR);
			context.addMessage(component.getClientId(), msg);
			throw new ValidatorException(msg);
		}
	}

}

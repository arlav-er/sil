package it.eng.sil.myaccount.controller.validator;

import it.eng.sil.myaccount.model.ejb.stateless.profile.PfPrincipalMyAccountEJB;

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

@FacesValidator(value = "uniqueUsernameValidator")
public class UniqueUsernameValidator implements Validator {

	private static final Log log = LogFactory
			.getLog(UniqueUsernameValidator.class);

	private PfPrincipalMyAccountEJB pfPrincipalMyAccountEJB;

	public UniqueUsernameValidator() {
		try {
			InitialContext ic = new InitialContext();
			pfPrincipalMyAccountEJB = (PfPrincipalMyAccountEJB) ic
					.lookup("java:module/PfPrincipalMyAccountEJB");
		} catch (NamingException e) {
			log.error(e);
		}
	}

	@Override
	public void validate(FacesContext context, UIComponent component,
			Object value) throws ValidatorException {
		if (value != null) {
			String username = value.toString();
			if (pfPrincipalMyAccountEJB.exists(username)) {
				String message = "Username non disponibile";
				FacesMessage msg = new FacesMessage(message);
				msg.setSeverity(FacesMessage.SEVERITY_ERROR);
				context.addMessage(component.getClientId(), msg);
				throw new ValidatorException(msg);
			} else if(username.length() < 3) {
				String message = "Lo Username deve avere almeno 3 caratteri";
				FacesMessage msg = new FacesMessage(message);
				msg.setSeverity(FacesMessage.SEVERITY_ERROR);
				context.addMessage(component.getClientId(), msg);
				throw new ValidatorException(msg);
			}
		}
	}

}

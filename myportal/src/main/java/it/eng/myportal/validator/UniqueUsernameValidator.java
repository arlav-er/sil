package it.eng.myportal.validator;

import it.eng.myportal.entity.home.PfPrincipalHome;

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

/**
 *
 * @author rodi
 *
 * Validatore del nome utente. Il nome utente non deve essere già presente su DB.
 *
 */
@FacesValidator(value="uniqueUsernameValidator")
public class UniqueUsernameValidator implements Validator {

	private static final Log log = LogFactory.getLog(UniqueUsernameValidator.class);

	private PfPrincipalHome pfPrincipalHome;

	public UniqueUsernameValidator() {
		try {
			InitialContext ic = new InitialContext();
			pfPrincipalHome = (PfPrincipalHome) ic.lookup("java:module/PfPrincipalHome");
		} catch (NamingException e) {
			log.error(e);
		}
	}

	@Override
	public void validate(FacesContext context, UIComponent component, Object value) throws ValidatorException {
		if (value != null) {
			String username = value.toString();
			if (pfPrincipalHome.exists(username)) {
				String message = "Username non disponibile";
				FacesMessage msg = new FacesMessage(message);
				msg.setSeverity(FacesMessage.SEVERITY_ERROR);
				context.addMessage(component.getClientId(), msg);
				throw new ValidatorException(msg);
			}
			String message = "Username disponibile";
			FacesMessage msg = new FacesMessage(message);
			msg.setSeverity(FacesMessage.SEVERITY_INFO);
			context.addMessage(component.getClientId(), msg);

		}
	}

}

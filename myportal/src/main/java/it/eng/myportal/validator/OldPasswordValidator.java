package it.eng.myportal.validator;


import it.eng.myportal.beans.SessionBean;
import it.eng.myportal.entity.home.PfPrincipalHome;
import it.eng.myportal.utils.Utils;

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
 * @author hatemalimam
 * 
 */
@FacesValidator(value = "oldPasswordValidator")
public class OldPasswordValidator implements Validator {
	private static final Log log = LogFactory.getLog(OldPasswordValidator.class);
	public OldPasswordValidator() {
	}

	
	/**
	 * In order to change the password the user has to pass this validator.
	 * compare the provided password with the current password.
	 * 
	 * 
	 * @param context
	 *            FacesContext
	 * @param toValidate
	 *            UIComponent
	 * @param value
	 *            Object
	 */
	@Override
	public void validate(FacesContext context, UIComponent toValidate, Object value) {
		PfPrincipalHome pfPrincipalHome = null;
		try {
			pfPrincipalHome = (PfPrincipalHome) new InitialContext().lookup("java:module/PfPrincipalHome");
		} catch (NamingException e) {
			String msg="Errore nel recupero del validatore della password";
			log.error(msg, e);
			throw new RuntimeException(msg);
		}

		SessionBean session = (SessionBean) context.getApplication().evaluateExpressionGet(context, "#{sessionBean}", SessionBean.class);
		String suppliedPassword = (String) value;
		String currentPassword = pfPrincipalHome.findByUsername(session.getUsername()).getPassWord();
		if (!Utils.SHA1.compare(suppliedPassword, currentPassword)) {
			String message = "La password non corrisponde";
			FacesMessage msg = new FacesMessage(message);
			msg.setSeverity(FacesMessage.SEVERITY_ERROR);
			throw new ValidatorException(msg);												
		}
	}
}

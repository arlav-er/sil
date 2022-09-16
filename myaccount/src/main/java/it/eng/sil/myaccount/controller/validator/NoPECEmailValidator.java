package it.eng.sil.myaccount.controller.validator;

import it.eng.sil.myaccount.model.ejb.stateless.myportal.DePecDominiEJB;
import it.eng.sil.myaccount.model.entity.myportal.DePecDomini;
import it.eng.sil.mycas.utils.ConstantsCommons;

import java.util.List;

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

@FacesValidator("noPECEmailValidator")
public class NoPECEmailValidator implements Validator {

	private static final Log log = LogFactory.getLog(NoPECEmailValidator.class);

	private DePecDominiEJB dePecDominiEJB;

	public NoPECEmailValidator() {
		try {
			InitialContext ic = new InitialContext();
			dePecDominiEJB = (DePecDominiEJB) ic.lookup(ConstantsCommons.JNDI_BASE
					+ DePecDominiEJB.class.getSimpleName());
		} catch (NamingException e) {
			log.error(e);
		}
	}

	@Override
	public void validate(FacesContext context, UIComponent component, Object object) throws ValidatorException {
		if (object == null)
			return;

		String email = (String) object;
		String dominio = email.substring(email.indexOf("@") + 1);

		// se il dominio �� fra quelli PEC blocco
		List<DePecDomini> results = dePecDominiEJB.findByDescription(dominio);
		if (results != null && !results.isEmpty()) {
			String message = "Non �� possibile inserire in questo campo un indirizzo mail PEC";
			FacesMessage msg = new FacesMessage(message);
			msg.setSeverity(FacesMessage.SEVERITY_ERROR);
			context.addMessage(component.getClientId(), msg);
			throw new ValidatorException(msg);
		}
	}

}

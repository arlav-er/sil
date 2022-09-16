package it.eng.myportal.validator;

import it.eng.myportal.dtos.DePecDominiDTO;
import it.eng.myportal.entity.home.decodifiche.DePecDominiHome;
import it.eng.myportal.utils.ConstantsSingleton;

import java.util.List;

import javax.ejb.EJB;
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

@FacesValidator(value = "noPecEmailValidator")
public class NoPECEmailValidator implements Validator {

	private static final Log log = LogFactory.getLog(NoPECEmailValidator.class);

	@EJB
	private DePecDominiHome dePecDominiHome;

	public NoPECEmailValidator() {
		try {
			InitialContext ic = new InitialContext();
			dePecDominiHome = (DePecDominiHome) ic.lookup(ConstantsSingleton.JNDI_BASE
					+ DePecDominiHome.class.getSimpleName());
		} catch (NamingException e) {
			log.error(e);
		}
	}

	@Override
	public void validate(FacesContext context, UIComponent component, Object value) throws ValidatorException {
		if (value == null)
			return;

		String email = (String) value;
		String dominio = email.substring(email.indexOf("@") + 1);

		// se il dominio è fra quelli PEC blocco
		List<DePecDominiDTO> results = dePecDominiHome.findByDescription(dominio);
		if (results != null && !results.isEmpty()) {
			String message = "Non è possibile inserire in questo campo un indirizzo mail PEC";
			FacesMessage msg = new FacesMessage(message);
			msg.setSeverity(FacesMessage.SEVERITY_ERROR);
			context.addMessage(component.getClientId(), msg);
			throw new ValidatorException(msg);
		}

	}

}

package it.eng.myportal.validator;

import it.eng.myportal.entity.home.PfPrincipalHome;
import it.eng.myportal.utils.ConstantsSingleton;

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
 * @author Enrico D'Angleo
 * 
 *         Validatore dell'indirizzo email PEC. Controlla che non sia gia' stato
 *         inserito lo stesso indirizzo email PEC nel DB
 * 
 */
@FacesValidator(value = "uniquePECEmailValidator")
public class UniquePECEmailValidator implements Validator {

	private static final Log log = LogFactory.getLog(UniquePECEmailValidator.class);

	private PfPrincipalHome pfPrincipalHome;

	public UniquePECEmailValidator() {
		try {
			InitialContext ic = new InitialContext();
			pfPrincipalHome = (PfPrincipalHome) ic.lookup(ConstantsSingleton.JNDI_BASE
					+ PfPrincipalHome.class.getSimpleName());
		} catch (NamingException e) {
			log.error(e);
		}
	}

	@Override
	public void validate(FacesContext context, UIComponent component, Object value) throws ValidatorException {
		if (value != null) {
			
			UIComponent view = context.getViewRoot();

			// id completo del componente ([form_id]:[id componente]:inputText)
			String componentId = component.getClientId();
			// id dell'inputText a cui e' agganciato il validatore (inputText)
			String inputTextId = component.getId();
			// id del componente inputText scritto da noi ([form_id]:[id
			// componente])
			String customComponentId = componentId.substring(0, componentId.indexOf(inputTextId));
			// il componente inputText scritto da noi
			UIComponent customComponent = (UIComponent) view.findComponent(customComponentId);
			// Ottengo idPfPrincipal passato eventualmente come parametro
			Integer idPfPrincipal = (Integer) customComponent.getAttributes().get("id_pf_principal");
			
			String email = value.toString();
			boolean ok = pfPrincipalHome.checkUniquePECEmail(idPfPrincipal, email);
			
			if (ok) {
				String message = "Indirizzo email disponibile";
				FacesMessage msg = new FacesMessage(message);
				msg.setSeverity(FacesMessage.SEVERITY_INFO);
				context.addMessage(component.getClientId(), msg);
			} else {
				String message = "Indirizzo email non disponibile";
				FacesMessage msg = new FacesMessage(message);
				msg.setSeverity(FacesMessage.SEVERITY_ERROR);
				context.addMessage(component.getClientId(), msg);
				throw new ValidatorException(msg);
			}
		}
	}
}

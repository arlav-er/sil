package it.eng.myportal.validator.suggestion;

import javax.ejb.EJBException;
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

import it.eng.myportal.entity.home.PfIdentityDeviceHome;
import it.eng.myportal.utils.ConstantsSingleton;

@FacesValidator(value = "usersAppSuggestionValidator")
public class UsersAppSuggestionValidator implements Validator {

	private PfIdentityDeviceHome pfIdentityDeviceHome;

	protected Log log = LogFactory.getLog(this.getClass());

	public UsersAppSuggestionValidator() {
		try {
			InitialContext ic = new InitialContext();
			pfIdentityDeviceHome = (PfIdentityDeviceHome) ic
					.lookup(ConstantsSingleton.JNDI_BASE + "PfIdentityDeviceHome");
		} catch (NamingException e) {
			log.error("Errore nella lookup jndi: " + e.getMessage());
		}
	}

	public void validate(FacesContext context, UIComponent component, Object arg2) throws ValidatorException {
		String email = (String) arg2;

		if (email != null) {
			// Ottengo la descrizione presente nel campo di testo

			UIComponent view = context.getViewRoot();
			String componentId = component.getClientId();
			String inputTextId = component.getId();
			String customComponentId = componentId.substring(0, componentId.indexOf(inputTextId));
			UIComponent customComponent = (UIComponent) view.findComponent(customComponentId);

			// Recupero della provincia di riferimento
			String codFiltroProvincia = (String) customComponent.getAttributes().get("codFiltroProvincia");

			String message = "Non trovato.";
			FacesMessage msg = new FacesMessage(message);
			msg.setSeverity(FacesMessage.SEVERITY_ERROR);

			String messageTooMany = "Esistono più di un utente con la stessa email, contattare l'amministratore per verificare i dati.";
			FacesMessage msgTooMany = new FacesMessage(messageTooMany);
			msgTooMany.setSeverity(FacesMessage.SEVERITY_ERROR);

			try {
				Long count = pfIdentityDeviceHome.getCountUsersApp(email, codFiltroProvincia);

				if (count == 0) {
					/* non ho trovato niente */
					throw new ValidatorException(msg);
				} else if (count > 1) {
					// ne ho trovato piu' d'uno
					throw new ValidatorException(msgTooMany);
				}

			} catch (EJBException e) {
				/*
				 * se qualcosa è andato storto durante il recupero dei dati mostra un messaggio di errore generico.
				 */
				log.error("Errore durante la validazione dei dati");
				throw new ValidatorException(msg);
			}
		}

		return;
	}

}

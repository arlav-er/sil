package it.eng.myportal.validator;

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

import it.eng.myportal.entity.home.decodifiche.DeContrattoHome;
import it.eng.myportal.utils.ConstantsSingleton;
import it.eng.myportal.utils.Utils;

/**
 * 
 */
@FacesValidator(value = "contrattoValidator")
public class ContrattoValidator implements Validator {

	/**
	 * Logger di classe. Utilizzare questo log per scrivere messaggi di errore,
	 * info e debug.
	 */
	protected Log log = LogFactory.getLog(this.getClass());

	DeContrattoHome deContrattoHome;

	public ContrattoValidator() {
		try {
			InitialContext ic = new InitialContext();
			deContrattoHome = (DeContrattoHome) ic.lookup(ConstantsSingleton.JNDI_BASE + "DeContrattoHome");
		} catch (NamingException e) {
			log.error("Errore nella lookup jndi: " + e.getMessage());
		}
	}

	/**
	 * @param context
	 *            FacesContext
	 * @param component
	 *            UIComponent
	 * @param value
	 *            Object
	 */
	@Override
	public void validate(FacesContext context, UIComponent component, Object value) {
		boolean codificheScadute = false;

		if (value != null) {
			String codContrattoArrayString = value.toString();
			if (codContrattoArrayString != null) {
				List<String> codContrattoArray = Utils.MyPortalStringUtils.toList(codContrattoArrayString);

				for (String codContratto : codContrattoArray) {
					codificheScadute = deContrattoHome.isScaduto(codContratto);
					if (codificheScadute) {
						break;
					}
				}
			}

			if (codificheScadute) {
				String message = "Eliminare tutte le codifiche scadute.";
				FacesMessage msg = new FacesMessage(message);
				msg.setSeverity(FacesMessage.SEVERITY_ERROR);
				throw new ValidatorException(msg);
			}
		}

		return;
	}
}
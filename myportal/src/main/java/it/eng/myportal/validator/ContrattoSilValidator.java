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

import it.eng.myportal.entity.home.decodifiche.DeContrattoSilHome;
import it.eng.myportal.utils.ConstantsSingleton;
import it.eng.myportal.utils.Utils;

@FacesValidator(value = "contrattoValidatorSil")
public class ContrattoSilValidator implements Validator {
	protected Log log = LogFactory.getLog(this.getClass());

	DeContrattoSilHome deContrattoSilHome;

	public ContrattoSilValidator() {
		try {
			InitialContext ic = new InitialContext();
			deContrattoSilHome = (DeContrattoSilHome) ic.lookup(ConstantsSingleton.JNDI_BASE + "DeContrattoSilHome");
		} catch (NamingException e) {
			log.error("Errore nella lookup jndi: " + e.getMessage());
		}
	}

	@Override
	public void validate(FacesContext context, UIComponent component, Object value) throws ValidatorException {
		boolean codificheScadute = false;

		if (value != null) {
			String codContrattoArrayString = value.toString();
			if (codContrattoArrayString != null) {
				List<String> codContrattoArray = Utils.MyPortalStringUtils.toList(codContrattoArrayString);

				for (String codContratto : codContrattoArray) {
					codificheScadute = deContrattoSilHome.isScaduto(codContratto);
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
		else{
			String message = "Attenzione il campo è obbligatorio, scegliere una o più tipologie contrattuali.";
			FacesMessage msg = new FacesMessage(message);
			msg.setSeverity(FacesMessage.SEVERITY_ERROR);
			throw new ValidatorException(msg);
		}
	}

}

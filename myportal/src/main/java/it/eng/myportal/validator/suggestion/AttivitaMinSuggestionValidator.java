package it.eng.myportal.validator.suggestion;

import java.util.ArrayList;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.component.html.HtmlInputHidden;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import it.eng.myportal.dtos.DeAttivitaMinDTO;
import it.eng.myportal.entity.home.decodifiche.DeAttivitaMinHome;
import it.eng.myportal.utils.ConstantsSingleton;

/**
 * 
 * @author Enrico D'Angelo
 * 
 */
@FacesValidator(value = "attivitaMinSuggestionValidator")
public class AttivitaMinSuggestionValidator implements Validator {

	private DeAttivitaMinHome deAttivitaMinHome;

	public AttivitaMinSuggestionValidator() {
		try {
			InitialContext ic = new InitialContext();
			deAttivitaMinHome = (DeAttivitaMinHome) ic
					.lookup(ConstantsSingleton.JNDI_BASE + "DeAttivitaMinHome");
		} catch (NamingException e) {
			log.error("Errore nella lookup jndi: " + e.getMessage());
		}
	}

	protected Log log = LogFactory.getLog(this.getClass());

	/**
	 * Validatore per gli elementi presenti nella tabella de_attivita_min.
	 * L'elemento che viene selezionato da questa tabella e' vincolato
	 * dall'elemento scelto dalla tabella de_attivita. La validazione va a buon
	 * fine se:
	 * 1) l'elemento selezionato e' presente nella tabella
	 *    de_attivita_min
	 * 2) l'elemento selezionato dalla tabella de_attivita viene
	 *    mappato come radice dell'elemento selezionato dalla tabella
	 *    de_attivita_min
	 */
	@Override
	public void validate(FacesContext context, UIComponent component,
			Object value) throws ValidatorException {

		String valueString = (String) value;

		if (valueString != null && !valueString.isEmpty()) {
			String message = "Non trovato.";
			FacesMessage msg = new FacesMessage(message);
			msg.setSeverity(FacesMessage.SEVERITY_ERROR);

			UIComponent view = context.getViewRoot();
			String componentId = component.getClientId();
			String inputTextId = component.getId();
			String customComponentId = componentId.substring(0,
					componentId.indexOf(inputTextId));
			UIComponent customComponent = (UIComponent) view
					.findComponent(customComponentId);
			String codAtecoComponent = (String) customComponent.getAttributes()
					.get("codAteco");

			HtmlInputHidden htmlInputHidden = (HtmlInputHidden) view
					.findComponent(codAtecoComponent);
			String codAteco = (String) htmlInputHidden.getValue();

			String codAttivitaPadre = deAttivitaMinHome
					.getCodAttivitaPadreByCodAteco(codAteco);

			List<DeAttivitaMinDTO> listPadriDeAttivitaMinDTO = deAttivitaMinHome
					.findByCodPadre(codAttivitaPadre);

			List<String> listCodPadre = new ArrayList<String>();
			for (DeAttivitaMinDTO deAttivitaMinDTO : listPadriDeAttivitaMinDTO) {
				listCodPadre.add(deAttivitaMinDTO.getId());
			}

			List<DeAttivitaMinDTO> listDeAttivitaMinDTO = deAttivitaMinHome
					.findByCodPadre(listCodPadre);

			for (DeAttivitaMinDTO deAttivitaMinDTO : listDeAttivitaMinDTO) {
				if (deAttivitaMinDTO.getDescrizione().toUpperCase()
						.equals(valueString)) {
					return;
				}
			}

			/* validazione fallita */
			throw new ValidatorException(msg);
		}
	}

}

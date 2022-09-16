package it.eng.myportal.validator.suggestion;

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

import it.eng.myportal.dtos.DeMansioneMinDTO;
import it.eng.myportal.entity.decodifiche.min.DeMansioneMin;
import it.eng.myportal.entity.home.decodifiche.DeMansioneMinHome;
import it.eng.myportal.utils.ConstantsSingleton;

/**
 * 
 * @author Enrico D'Angelo
 * 
 */
@FacesValidator(value = "mansioneMinSuggestionValidator")
public class MansioneMinSuggestionValidator implements Validator {

	private DeMansioneMinHome deMansioneMinHome;
	
	public MansioneMinSuggestionValidator() {
		try {
			InitialContext ic = new InitialContext();
			deMansioneMinHome = (DeMansioneMinHome) ic
					.lookup(ConstantsSingleton.JNDI_BASE + "DeMansioneMinHome");
		} catch (NamingException e) {
			log.error("Errore nella lookup jndi: " + e.getMessage());
		}
	}

	protected Log log = LogFactory.getLog(this.getClass());

	/**
	 * Validatore per gli elementi presenti nella tabella de_mansione_min.
	 * L'elemento che viene selezionato da questa tabella e' vincolato
	 * dall'elemento scelto dalla tabella de_mansione. La validazione va a buon
	 * fine se:
	 * 1) l'elemento selezionato e' presente nella tabella
	 *    de_mansione_min
	 * 2) l'elemento selezionato dalla tabella de_mansione viene
	 *    mappato come radice dell'elemento selezionato dalla tabella
	 *    de_mansione_min
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
			String hiddenComponentId = customComponentId+"inputHidden";
			UIComponent customComponent = (UIComponent) view
					.findComponent(customComponentId);
			UIComponent hiddenComponent = (UIComponent) view
					.findComponent(hiddenComponentId);
			String codMansioneComponent = (String) customComponent
					.getAttributes().get("codMansione");
			
			String codMansioneMin = (String) ((HtmlInputHidden) hiddenComponent).getSubmittedValue();
			
			HtmlInputHidden htmlInputHidden = (HtmlInputHidden) view
					.findComponent(codMansioneComponent);
			String codMansione = (String) htmlInputHidden.getValue();


			DeMansioneMin deMansioneMin = deMansioneMinHome.findById(codMansioneMin);
			if (deMansioneMin != null) return;
			
			List<String> listCodMansionePadre = deMansioneMinHome
					.getCodMansionePadreByCodMansione(codMansione);

			for (String codMansionePadre : listCodMansionePadre) {
				List<DeMansioneMinDTO> listDeMansioneMinDTO = deMansioneMinHome
						.findByCodPadre(codMansionePadre);

				for (DeMansioneMinDTO deMansioneMinDTO : listDeMansioneMinDTO) {
					if (deMansioneMinDTO.getDescrizione().toUpperCase()
							.equals(valueString)) {
						return;
					}
				}
			}
			/* validazione fallita */
			throw new ValidatorException(msg);
		}
	}

}

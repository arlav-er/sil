package it.eng.myportal.validator;

import it.eng.myportal.dtos.IHasUniqueValue;

import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.component.html.HtmlInputHidden;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 
 * @author rodi
 *
 * Classe che valida l'unicità degli elementi
 * 
 * TODO. Miglorare la parte che recupera il codice e gli attributi. E' uguale al SuggestionValidator!
 *
 */

@FacesValidator(value="uniqueValidator")
public class UniqueValidator implements Validator {

	private static Log log = LogFactory.getLog(UniqueValidator.class);
	
	@Override
	public void validate(FacesContext context, UIComponent component, Object arg2)
			throws ValidatorException {
		
		UIComponent view = FacesContext.getCurrentInstance().getViewRoot();
		// id completo del componente ([form_id]:[id componente]:inputText)
		String componentId = component.getClientId();

		// id dell'inputText a cui e' agganciato il validatore (inputText)
		String inputTextId = component.getId();
		
		String containerId = componentId.substring(0, componentId.indexOf(inputTextId));
		UIComponent container = (UIComponent) view.findComponent(containerId);	
		
		//id del componente inputHidden ([form_id]:[id componente]:inputHidden)
		String customComponentId = componentId.substring(0, componentId.indexOf(inputTextId))+"inputHidden";
		// il componente inputHidden contenente il codice
		HtmlInputHidden customComponent = (HtmlInputHidden) view.findComponent(customComponentId);
		
		String codice = (String) customComponent.getSubmittedValue();
		
		//se è stato scelto un elemento dalla lista
		if (codice != null && !"".equals(codice)) {
			Object currentObject = container.getAttributes().get("current");
			if (!(currentObject instanceof IHasUniqueValue)) {		
				log.error("E' stato agganciato un UniqueValidator ad un DTO" +
						" che non implementa l'interfaccia HasUniqueValue");
				String message = "Errore durante la validazione.";
				FacesMessage msg = new FacesMessage(message);
				msg.setSeverity(FacesMessage.SEVERITY_ERROR);
				throw new ValidatorException(msg);
			}
				
			
			IHasUniqueValue current = (IHasUniqueValue) currentObject;
			List<IHasUniqueValue> lista = (List<IHasUniqueValue>) container.getAttributes().get("list");
			//devono essere stati passati entrambi i parametri
			if (lista == null || current == null) {	
				log.error("E' stato richiamato UniqueValidator senza gli attributi " +
						"necessari alla sua esecuzione.");
				String message = "Errore durante la validazione";
				FacesMessage msg = new FacesMessage(message);
				msg.setSeverity(FacesMessage.SEVERITY_ERROR);
				throw new ValidatorException(msg);
			}
			
			if (!lista.isEmpty()) {
				for (IHasUniqueValue dto : lista) {
					if (codice.equals(dto.getUniqueValue()) && !current.equals(dto)) {
						String message = getErrorMessage();
						FacesMessage msg = new FacesMessage(message);
						msg.setSeverity(FacesMessage.SEVERITY_ERROR);
						throw new ValidatorException(msg);
					}
				}
			}
		}
		
	}
	
	/**
	 * Il messaggio di errore mostrato dal validatore.
	 * Eseguire un'override di questo metodo per ridefinire il messaggio di errore.
	 * @return String
	 */
	private String getErrorMessage() {
		return "Questo elemento è già stato inserito.";
	}	
	
	
	
}

/**
 * For non repeated entries
 */
package it.eng.myportal.validator;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 *
 * @author Hatem Alimam
 * @author
 *
 */
@FacesValidator(value = "nonRepeatedStringValidator")
public class NonRepeatedStringValidator implements Validator {

	/**
	 * Logger di classe. Utilizzare questo log per scrivere messaggi di errore,
	 * info e debug.
	 */
	protected Log log = LogFactory.getLog(this.getClass());

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
		UIComponent view = context.getViewRoot();

		// id completo del componente
		String componentId = component.getClientId();

		// id dell'inputText a cui e' agganciato il validatore (inputText)
		String inputTextId = component.getId();

		// id del componente inputText scritto da noi ([form_id]:[id
		// componente])
		String customComponentId = componentId.substring(0, componentId.indexOf(inputTextId));
		// il componente inputText scritto da noi
		UIComponent customComponent = (UIComponent) view.findComponent(customComponentId);
		// id del componente data_da da validare insieme a data_a
		String firstId = (String) customComponent.getAttributes().get("first_id");
		String custom_message = (String) customComponent.getAttributes().get("validator_custom_message");

		if (firstId == null) {
			log.error("Non e' stato definito l'attributo \"first_id\" " + "per il validatore \"NonRepeatedStringValidator\"");
		}

		/*
		 * Ottengo i due componenti password da validare
		 */
		UIInput firstInput = (UIInput) view.findComponent(firstId);
		String firstValue = null;
		if (firstInput != null) {
			firstValue = (String) firstInput.getSubmittedValue();
			if (firstValue == null)
				firstValue = (String) firstInput.getValue();
		}
		String confirmValue = (String) value;

		if ((firstValue == null) || (confirmValue == null)) { return; }

		/*
		 * Check if the value is repeated from the first_id field
		 */
		if (confirmValue.equals(firstValue)) {
			//check if there's a custom message passed to the validator, if there's no one, default used
			String message = (custom_message != null)?custom_message:"Voci duplicate";
			FacesMessage msg = new FacesMessage(message);
			msg.setSeverity(FacesMessage.SEVERITY_ERROR);
			context.addMessage(component.getClientId(), msg);
			throw new ValidatorException(msg);
		}

		/*
		 * value not repeated - passed
		 */
		String message = "Confermato!";
		FacesMessage msg = new FacesMessage(message);
		msg.setSeverity(FacesMessage.SEVERITY_INFO);
		context.addMessage(component.getClientId(), msg);
		return;
	}
}
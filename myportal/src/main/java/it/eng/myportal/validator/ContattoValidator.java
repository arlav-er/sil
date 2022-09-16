package it.eng.myportal.validator;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.component.html.HtmlInputText;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 
 */
@FacesValidator(value = "contattoValidator")
public class ContattoValidator implements Validator {

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

		// id completo del componente ([form_id]:[id componente]:inputText)
		String componentId = component.getClientId();
		// id dell'inputText a cui e' agganciato il validatore (inputText)
		String inputTextId = component.getId();
		// id del componente inputText scritto da noi ([form_id]:[id
		// componente])
		String customComponentId = componentId.substring(0, componentId.indexOf(inputTextId));
		// il componente inputText scritto da noi
		UIComponent customComponent = (UIComponent) view.findComponent(customComponentId);
		// id del componente data_da da validare insieme a data_a
		String tel_val = (String) customComponent.getAttributes().get("tel_val") + ":inputText";
		String fax_val = (String) customComponent.getAttributes().get("fax_val") + ":inputText";
		String email_val = (String) customComponent.getAttributes().get("email_val") + ":inputText";

		/*
		 * Ottengo i due componenti data da validare
		 */
		HtmlInputText telHtmlInputText = (HtmlInputText) view.findComponent(tel_val);
		HtmlInputText faxHtmlInputText = (HtmlInputText) view.findComponent(fax_val);
		HtmlInputText mailHtmlInputText = (HtmlInputText) view.findComponent(email_val);
		
		String telefono = "";
		if (telHtmlInputText != null && telHtmlInputText.getValue() != null) {
			telefono = (String) telHtmlInputText.getValue();
		}

		String fax = "";
		if (faxHtmlInputText != null && faxHtmlInputText.getValue() != null) {
			fax = (String) faxHtmlInputText.getValue();
		}
		
		String mail = "";
		if (mailHtmlInputText != null && mailHtmlInputText.getSubmittedValue() != null) {
			mail = (String) mailHtmlInputText.getSubmittedValue();
		}
		// se non ho scelto ne mansione ne attivita
		if ((("").equals(telefono) || telefono == null) && (("").equals(fax) || fax == null)  && (("").equals(mail) || mail == null)) {

			String message = "Inserire almeno uno tra Telefono, Fax o E-mail.";
			FacesMessage msg = new FacesMessage(message);
			msg.setSeverity(FacesMessage.SEVERITY_ERROR);
			throw new ValidatorException(msg);
		}


		return;
	}
}
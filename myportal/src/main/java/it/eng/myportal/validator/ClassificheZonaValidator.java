package it.eng.myportal.validator;

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
 */
@FacesValidator(value = "classificheZonaValidator")
public class ClassificheZonaValidator implements Validator {

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
		String mansione_val = (String) customComponent.getAttributes().get("mansione_val") + ":inputHidden";
		String attivita_val = (String) customComponent.getAttributes().get("attivita_val") + ":inputHidden";

		/*
		 * Ottengo i due componenti data da validare
		 */
		HtmlInputHidden mansioneHtmlInputHidden = (HtmlInputHidden) view.findComponent(mansione_val);
		String mansione = "";
		if (mansioneHtmlInputHidden != null && mansioneHtmlInputHidden.getSubmittedValue() != null) {
			mansione = (String) mansioneHtmlInputHidden.getSubmittedValue();
		}

		HtmlInputHidden attivitaHtmlInputHidden = (HtmlInputHidden) view.findComponent(attivita_val);
		String ateco = "";
		if (attivitaHtmlInputHidden != null && attivitaHtmlInputHidden.getSubmittedValue() != null) {
			ateco = (String) attivitaHtmlInputHidden.getSubmittedValue();
		}

		// se non ho scelto ne mansione ne attivita
		if ((("").equals(mansione) || mansione == null) && (("").equals(ateco) || ateco == null)) {

			String message = "Scegliere il Profilo professionale oppure il Settore per effettuare la ricerca.";
			FacesMessage msg = new FacesMessage(message);
			msg.setSeverity(FacesMessage.SEVERITY_ERROR);
			throw new ValidatorException(msg);
		}

		// se ho scelto sia mansione che attivita
		if (!("").equals(mansione) && !("").equals(ateco)) {

			String message = "Scegliere uno solo tra il Profilo professionale o il Settore per effettuare la ricerca.";
			FacesMessage msg = new FacesMessage(message);
			msg.setSeverity(FacesMessage.SEVERITY_ERROR);
			throw new ValidatorException(msg);
		}

		return;
	}
}
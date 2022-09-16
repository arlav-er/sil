/**
 * 
 */
package it.eng.myportal.validator;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.component.html.HtmlSelectBooleanCheckbox;
import javax.faces.component.html.HtmlSelectOneMenu;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 
 * @author Enrico D'Angelo
 * 
 *         Classe per la validazione della lingua. Controlla che almeno uno dei
 *         tre gradi sia inserito. E' scritto per essere agganciato al
 *         componente del grado lingua parlato e si aspetta che gli vengano
 *         passati i valori dei componenti del grado lingua letto e scritto e
 *         della checkbox per madrelingua.
 * @author
 * 
 */
@FacesValidator(value = "gradoLinguaValidator")
public class GradoLinguaValidator implements Validator {

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
	public void validate(FacesContext context, UIComponent component,
			Object value) {
		UIComponent view = context.getViewRoot();
		Boolean madreLingua = null;
		String gradoLetto = null;
		String gradoScritto = null;
		String gradoParlato = null;

		// id completo del componente ([form_id]:[id componente]:combobox)
		String componentId = component.getClientId();
		// id dell'inputText a cui e' agganciato il validatore (combobox)
		String inputTextId = component.getId();
		// id del componente inputText scritto da noi ([form_id]:[id
		// componente]:)
		String customComponentId = componentId.substring(0,
				componentId.indexOf(inputTextId));
		// il componente inputText scritto da noi
		UIComponent customComponent = (UIComponent) view
				.findComponent(customComponentId);
		// id del componente grado_letto
		String madrelinguaId = (String) customComponent.getAttributes().get(
				"madrelingua_id");
		// id del componente grado_letto
		String gradoLettoId = (String) customComponent.getAttributes().get(
				"grado_letto_id");
		// id del componente grado_scritto
		String gradoScrittoId = (String) customComponent.getAttributes().get(
				"grado_scritto_id");

		if (madrelinguaId == null) {
			log.error("Non e' stato definito l'attributo \"madrelingua_id\" "
					+ "per il validatore \"GradoLinguaValidator\"");
			return;
		}
		if (gradoLettoId == null) {
			log.error("Non e' stato definito l'attributo \"grado_letto_id\" "
					+ "per il validatore \"GradoLinguaValidator\"");
			return;
		}
		if (gradoScrittoId == null) {
			log.error("Non e' stato definito l'attributo \"grado_scritto_id\" "
					+ "per il validatore \"GradoLinguaValidator\"");
			return;
		}

		/*
		 * Ottengo i componenti da validare
		 */
		HtmlSelectBooleanCheckbox madreLinguaComponent = (HtmlSelectBooleanCheckbox) view
				.findComponent(madrelinguaId);
		HtmlSelectOneMenu gradoLettoComponent = (HtmlSelectOneMenu) view
				.findComponent(gradoLettoId);
		HtmlSelectOneMenu gradoScrittoComponent = (HtmlSelectOneMenu) view
				.findComponent(gradoScrittoId);

		if (madreLinguaComponent != null) {
			madreLingua = (Boolean) madreLinguaComponent.getValue();
		}
		if (gradoLettoComponent != null) {
			gradoLetto = (String) gradoLettoComponent.getValue();
		}
		if (gradoScrittoComponent != null) {
			gradoScritto = (String) gradoScrittoComponent.getValue();
		}
		gradoParlato = (String) value;

		/*
		 * Controllo di validazione Se e' selezionato "madrelingua" nessun'altro
		 * livello deve essere selezionato, altrimenti se non e' selezionato
		 * "madrelingua" almeno uno deve essere selezionato
		 */
		if (madreLingua != null && !madreLingua && gradoLetto == null && gradoScritto == null && gradoParlato == null) {
			String message = "È necessario specificare almeno un ambito di conscenza linguistica";
			FacesMessage msg = new FacesMessage(message);
			msg.setSeverity(FacesMessage.SEVERITY_ERROR);
			throw new ValidatorException(msg);
		}

		return;
	}
}
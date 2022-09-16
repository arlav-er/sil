package it.eng.myportal.validator.suggestion;

import java.util.List;

import javax.ejb.EJBException;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import it.eng.myportal.dtos.ISuggestible;
import it.eng.myportal.entity.home.local.ISuggestibleHome;
import it.eng.myportal.utils.ConstantsSingleton;

/**
 * 
 * @author enrico
 * 
 *         Classe astratta per la validazione degli elementi con autosuggest.
 *         Controlla che la descrizione presente nel campo di testo corrisponda
 *         a quella presente su DB associata al codice presente nel campo hidden
 *         del componente.
 * 
 */
public abstract class AbstractSuggestionValidator<DTO extends ISuggestible> implements Validator {

	protected Log log = LogFactory.getLog(this.getClass());

	private ISuggestibleHome<DTO> home;

	public AbstractSuggestionValidator(String jndilookupname) {
		super();
		try {
			InitialContext ic = new InitialContext();
			home = (ISuggestibleHome<DTO>) ic.lookup(ConstantsSingleton.JNDI_BASE + jndilookupname);
		} catch (NamingException e) {
			log.error("Errore nella lookup jndi: " + e.getMessage());
		}
	}

	public ISuggestibleHome<DTO> getHome() {
		return home;
	}

	public void setHome(ISuggestibleHome<DTO> home) {
		this.home = home;
	}

	/**
	 * Implementare il metodo che recupera il DTO del suggerimento a partire dal
	 * codice. tutti i metodi concreti fanno la stessa cosa e richiamano lo
	 * stesso metodo sulla Home. Adesso che anche le Home hanno un'interfaccia
	 * bisognerebbe astrarre meglio.
	 * 
	 * @param valueId
	 *            String
	 * @return ISuggestible
	 */
	protected ISuggestible findDTOById(String valueId) {
		return home.findDTOById(valueId);
	}

	/**
	 * Il testo inserito nell'inputText del componente deve corrispondere ad una
	 * entry nella tabella su DB associata al componente (comune, provincia,
	 * ecc)
	 */
	@Override
	public void validate(FacesContext context, UIComponent component, Object value) throws ValidatorException {
		// Ottengo la descrizione presente nel campo di testo
		String valueLabel = (String) value;
		String message = getGenericErrorMessage();
		FacesMessage msg = new FacesMessage(message);
		msg.setSeverity(FacesMessage.SEVERITY_ERROR);

		String messageTooMany = getTooManyErrorMessage();
		FacesMessage msgTooMany = new FacesMessage(messageTooMany);
		msgTooMany.setSeverity(FacesMessage.SEVERITY_ERROR);

		ISuggestible dto = null;

		UIInput hiddenInputUI = getHiddenInput(component, context);
		String idStr = String.valueOf(hiddenInputUI.getSubmittedValue());

		// NON TOGLIERE LA RICERCA PER CODICE ALTRIMENTI I NOMI DUPLICATI DANNO
		// ERRORE DI VALIDAZIONE!!!!!!
		// ora nel composite component al cambio del valore della input text il
		// valore del campo hidden viene sbiancato

		// se c'è il codice hidden cerco per codice
		if (StringUtils.isNotBlank(idStr) && !"null".equalsIgnoreCase(idStr)) {
			dto = home.findDTOById(idStr);
			if (dto == null) {
				/* non ho trovato niente */
				throw new ValidatorException(msg);
			}
		} else {// altrimenti cerco direttamente per descrizione

			if (valueLabel != null) {
				try {
					/* cerco l'elemento per descrizione */
					List<DTO> lDto = home.findByDescription(valueLabel);
					if (lDto == null || lDto.isEmpty()) {
						/* non ho trovato niente */
						throw new ValidatorException(msg);
					} else if (lDto.size() > 1) {
						// ne ho trovato piu' d'uno
						throw new ValidatorException(msgTooMany);
					}
					// se arrivo qui ottengo l'elemento cercato e solo lui
					dto = lDto.get(0);
					insertCodeIntoHiddenComponent(dto, component, context);
				} catch (EJBException e) {
					/*
					 * se qualcosa è andato storto durante il recupero dei dati
					 * mostra un messaggio di errore generico.
					 */
					log.error("Errore durante la validazione dei dati");
					throw new ValidatorException(msg);
				}
			}

		}

		return;
	}

	/**
	 * Torna la descrizione del dto, specializzare se si deve ripulirla da
	 * informazioni aggiuntive
	 * 
	 * @return
	 */
	public String estraiDescrizioneDTO(ISuggestible dto) {
		return dto.getDescrizione();
	}

	public String getTooManyErrorMessage() {
		return "Selezionare dalla lista.";
	}

	public String getGenericErrorMessage() {
		return "Non trovato.";
	}

	protected void insertCodeIntoHiddenComponent(ISuggestible dto, UIComponent component, FacesContext context) {
		UIInput hiddenInputUI = getHiddenInput(component, context);
		hiddenInputUI.setSubmittedValue(dto.getId());
	}

	protected UIInput getHiddenInput(UIComponent component, FacesContext context) {
		String hiddenInputId = getHiddenInputId(context, component);
		UIViewRoot view = context.getViewRoot();
		UIInput hiddenInputUI = (UIInput) view.findComponent(hiddenInputId);
		return hiddenInputUI;
	}

	private String getHiddenInputId(FacesContext context, UIComponent component) {
		// Genero il nome del campo hidden
		// FORMATO DEL NOME: <nome tab>:<id elemento>:inputHidden
		String clientId = component.getClientId(context);
		String[] clientIdParts = clientId.split(":");
		final StringBuilder hiddenIdStringBuilder = new StringBuilder("");
		for (int i = 0; i < clientIdParts.length - 1; i++) {
			hiddenIdStringBuilder.append(clientIdParts[i] + ":");
		}
		hiddenIdStringBuilder.append("inputHidden");
		String hiddenInputId = hiddenIdStringBuilder.toString();
		return hiddenInputId;
	}
}

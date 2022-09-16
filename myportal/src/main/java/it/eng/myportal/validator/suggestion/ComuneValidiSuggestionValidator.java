package it.eng.myportal.validator.suggestion;

import it.eng.myportal.dtos.DeComuneDTO;
import it.eng.myportal.dtos.ISuggestible;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.ejb.EJBException;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.ValidatorException;

import org.apache.commons.lang3.StringUtils;

/**
 * 
 * @author enrico
 * 
 *         Classe concreta che valida l'autosuggestion per la tabella DE_COMUNE
 * 
 */
@FacesValidator(value = "comuneValidiSuggestionValidator")
public class ComuneValidiSuggestionValidator extends AbstractSuggestionValidator<DeComuneDTO> {

	private static final String REG_EXP = "\\([A-Z]*\\)?";

	public ComuneValidiSuggestionValidator() {
		super("DeComuneHome");
	}

	@Override
	public String getGenericErrorMessage() {
		return "Comune non trovato.";
	}

	@Override
	public String getTooManyErrorMessage() {
		return "Esistono più comuni con lo stesso nome, selezionarne uno dalla lista.";
	}

	@Override
	public void validate(FacesContext context, UIComponent component, Object value) throws ValidatorException {
		String valueLabel = (String) value;
		if (StringUtils.isNotBlank(valueLabel)) {
			valueLabel = valueLabel.replaceAll(REG_EXP, "").trim();
		}
		// se non trovo nulla con la descrizione
		validateValidi(context, component, valueLabel);
	}

	@Override
	public String estraiDescrizioneDTO(ISuggestible dto) {
		return dto.getDescrizione().replaceAll(REG_EXP, "").trim();
	}
	
	private void validateValidi(FacesContext context, UIComponent component, Object value) throws ValidatorException {
		// Ottengo la descrizione presente nel campo di testo
		String valueLabel = (String) value;
		String message = getGenericErrorMessage();
		FacesMessage msg = new FacesMessage(message);
		msg.setSeverity(FacesMessage.SEVERITY_ERROR);

		ISuggestible dto = null;

		UIInput hiddenInputUI = getHiddenInput(component, context);
		String idStr = String.valueOf(hiddenInputUI.getSubmittedValue());

		// NON TOGLIERE LA RICERCA PER CODICE ALTRIMENTI I NOMI DUPLICATI DANNO
		// ERRORE DI VALIDAZIONE!!!!!!
		// ora nel composite component al cambio del valore della input text il
		// valore del campo hidden viene sbiancato

		// se c'è il codice hidden cerco per codice
		if (StringUtils.isNotBlank(idStr) && !"null".equalsIgnoreCase(idStr)) {
			dto = getHome().findDTOById(idStr);
			if (dto == null) {
				/* non ho trovato niente */
				throw new ValidatorException(msg);
			} else {
				Date oggi = Calendar.getInstance().getTime();
				if (oggi.before(dto.getDtInizioVal()) || oggi.after(dto.getDtFineVal())) {
					//codifica non valdia
					String messageNonValido = "Il comune non è valido";
					FacesMessage msgNonValido = new FacesMessage(messageNonValido);
					msgNonValido.setSeverity(FacesMessage.SEVERITY_ERROR);
					throw new ValidatorException(msgNonValido);
				}
			}
		}
		else {// altrimenti cerco direttamente per descrizione

			if (valueLabel != null) {
				try {
					/* cerco l'elemento per descrizione */
					List<DeComuneDTO> lDto = getHome().findByDescription(valueLabel);
					if (lDto == null || lDto.isEmpty()) {
						/* non ho trovato niente */
						throw new ValidatorException(msg);
					} else if (lDto.size() > 1) {
						// ne ho trovato piu' d'uno
						String messageTooMany = getTooManyErrorMessage();
						FacesMessage msgTooMany = new FacesMessage(messageTooMany);
						msgTooMany.setSeverity(FacesMessage.SEVERITY_ERROR);
						throw new ValidatorException(msgTooMany);
					}
					// se arrivo qui ottengo l'elemento cercato e solo lui
					dto = lDto.get(0);
					Date oggi = Calendar.getInstance().getTime();
					if (oggi.before(dto.getDtInizioVal()) || oggi.after(dto.getDtFineVal())) {
						//codifica non valdia
						String messageNonValido = "Il comune non è valido";
						FacesMessage msgNonValido = new FacesMessage(messageNonValido);
						msgNonValido.setSeverity(FacesMessage.SEVERITY_ERROR);
						throw new ValidatorException(msgNonValido);
					}
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
}

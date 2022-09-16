package it.eng.sil.myaccount.controller.validator;

import it.eng.sil.myaccount.helpers.GpGruppoFilter;
import it.eng.sil.myaccount.model.ejb.stateless.gestioneprofilo.GpGruppoMyAccountEJB;

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

@FacesValidator(value = "uniqueGpGruppoValidator")
public class UniqueGpGruppoValidator implements Validator {
	private static final Log log = LogFactory.getLog(UniqueGpGruppoValidator.class);

	private GpGruppoMyAccountEJB gpGruppoEJB;

	/**
	 * Dato che non sono in un bean, devo inizializzare gli EJB nel costruttore.
	 */
	public UniqueGpGruppoValidator() {
		try {
			InitialContext ic = new InitialContext();
			gpGruppoEJB = (GpGruppoMyAccountEJB) ic.lookup("java:module/GpGruppoMyAccountEJB");
		} catch (NamingException e) {
			log.error(e);
		}
	}

	/**
	 * Controllo di unicità : non possono esistere due gruppi con lo stesso padre e la stessa descrizione. Devo validare
	 * due campi, quindi ho bisogno che ci sia un attributo che indica il campo "padre".
	 */
	@Override
	public void validate(FacesContext context, UIComponent component, Object value) throws ValidatorException {
		// Controllo che il valore esista.
		if (value == null)
			return;

		// Estraggo il nome del gruppo da controllare.
		if (!(value instanceof String)) {
			FacesMessage msg = new FacesMessage("Errore di validazione: il valore non è una stringa.");
			msg.setSeverity(FacesMessage.SEVERITY_ERROR);
			throw new ValidatorException(msg);
		}
		String descrizione = (String) value;

		// Estraggo l'idPadre e controllo che sia o null o un integer.
		Object idPadreObj = component.getAttributes().get("idPadre");
		if (idPadreObj != null && !(idPadreObj instanceof Integer)) {
			FacesMessage msg = new FacesMessage("Errore di validazione: idPadre non è un intero.");
			msg.setSeverity(FacesMessage.SEVERITY_ERROR);
			throw new ValidatorException(msg);
		}
		Integer idPadre = (Integer) idPadreObj;

		// Estraggo l'idGruppo di se stesso e controllo che sia null (in caso di creazione nuovo gruppo) o un Integer.
		Object idGpGruppoObj = component.getAttributes().get("idGpGruppo");
		if (idGpGruppoObj != null && !(idGpGruppoObj instanceof Integer)) {
			FacesMessage msg = new FacesMessage("Errore di validazione: idGpGruppo non è un intero.");
			msg.setSeverity(FacesMessage.SEVERITY_ERROR);
			throw new ValidatorException(msg);
		}
		Integer idGpGruppo = (Integer) idGpGruppoObj;

		// Trovo il numero di gruppi con stessa descrizione e stesso padre.
		int duplicati;
		if (idPadre == null) {
			duplicati = gpGruppoEJB.findCountByFilter(
					new GpGruppoFilter().setDescrizione(descrizione).setDescrizioneEsatta(true)
							.setSoloGruppiSenzaPadre(true).setIdDaEscludere(idGpGruppo)).intValue();
		} else {
			duplicati = gpGruppoEJB.findCountByFilter(
					new GpGruppoFilter().setDescrizione(descrizione).setDescrizioneEsatta(true).setIdPadre(idPadre)
							.setIdDaEscludere(idGpGruppo)).intValue();
		}

		// Se esiste già un gruppo con stesso padre e descrizione, la validazione fallisce.
		if (duplicati > 0) {
			FacesMessage msg = new FacesMessage("Esiste già un gruppo con questo padre e questa descrizione.");
			msg.setSeverity(FacesMessage.SEVERITY_ERROR);
			throw new ValidatorException(msg);
		}

	}
}

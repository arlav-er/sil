/**
 *
 */
package it.eng.myportal.beans.vacancies;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ValueChangeEvent;

import org.apache.commons.lang3.time.DateUtils;

import it.eng.myportal.beans.AbstractBaseBean;
import it.eng.myportal.dtos.AziendaInfoDTO;
import it.eng.myportal.dtos.DeAttivitaDTO;
import it.eng.myportal.dtos.DeAttivitaMinDTO;
import it.eng.myportal.dtos.DeMansioneDTO;
import it.eng.myportal.dtos.DeMansioneMinDTO;
import it.eng.myportal.dtos.SvAziendaInfoDTO;
import it.eng.myportal.dtos.VaDatiVacancyDTO;
import it.eng.myportal.entity.VaDatiVacancy;
import it.eng.myportal.entity.enums.CodStatoVacancyEnum;
import it.eng.myportal.entity.home.AziendaInfoHome;
import it.eng.myportal.entity.home.PfPrincipalHome;
import it.eng.myportal.entity.home.PoiHome;
import it.eng.myportal.entity.home.SvAziendaInfoHome;
import it.eng.myportal.entity.home.VaDatiVacancyHome;
import it.eng.myportal.entity.home.decodifiche.DeAttivitaHome;
import it.eng.myportal.entity.home.decodifiche.DeAttivitaMinHome;
import it.eng.myportal.entity.home.decodifiche.DeComuneHome;
import it.eng.myportal.entity.home.decodifiche.DeMansioneHome;
import it.eng.myportal.entity.home.decodifiche.DeMansioneMinHome;
import it.eng.myportal.entity.home.decodifiche.DeProvenienzaHome;
import it.eng.myportal.entity.home.decodifiche.DeProvinciaHome;
import it.eng.myportal.entity.home.decodifiche.DeTipoPoiHome;
import it.eng.myportal.utils.ConstantsSingleton;

/**
 * BB delle vacancies. Controlla il salvataggio degli attributi ottenuti dal Front-end
 * 
 * @author iescone
 */
@ManagedBean
@ViewScoped
public class TestataVacancyBean extends AbstractBaseBean {

	public static final int NUM_GIORNI_VALIDITA_VACANCY_PAT = 30;

	/**
	 * Dati della form.
	 */
	protected VaDatiVacancyDTO data = new VaDatiVacancyDTO();

	/* filtro per la selezione degli elementi dalla tabella de_attivita_min */
	private String filtroAttivita;
	/* filtro per la selezione degli elementi dalla tabella de_mansione_min */
	private List<String> filtroQualifica = new ArrayList<String>();

	private boolean showError;

	private String regione;

	@EJB
	VaDatiVacancyHome vaDatiVacancyHome;

	@EJB
	PoiHome poiHome;

	@EJB
	SvAziendaInfoHome svAziendaInfoHome;

	@EJB
	DeTipoPoiHome deTipoPoiHome;

	@EJB
	DeComuneHome deComuneHome;

	@EJB
	DeProvinciaHome deProvinciaHome;

	@EJB
	PfPrincipalHome pfPrincipalHome;

	@EJB
	DeAttivitaHome deAttivitaHome;

	@EJB
	DeAttivitaMinHome deAttivitaMinHome;

	@EJB
	DeMansioneHome deMansioneHome;

	@EJB
	DeMansioneMinHome deMansioneMinHome;

	@EJB
	DeProvenienzaHome deProvenienzaHome;

	@EJB
	AziendaInfoHome aziendaInfoHome;

	@PostConstruct
	protected void postConstruct() {
		super.postConstruct();

		// Se l'utente non è un'azienda, rimando alla home.
		if (!session.isAzienda()) {
			redirectHome();
		}

		// Solo per la VDA, controllo anche che l'azienda sia valida.
		if (utils.isVDA()) {
			AziendaInfoDTO aziendaInfoDTO = aziendaInfoHome.findDTOById(session.getConnectedAzienda().getId());
			if (!aziendaInfoDTO.getFlagValida()) {
				redirectHome();
			}
		}

		// Inizializzo la nuova vacancy con l'id dell'azienda che la sta inserendo.
		if (getSession().getConnectedAzienda() != null)
			data.setIdPfPrincipalAzienda(getSession().getConnectedAzienda().getId());
	}

	/**
	 * Al momento, il campo 'Qualifica Richiesta' è obbligatorio per Trento e l'Umbria.
	 */
	public boolean isQualificaObbligatoria() {
		return (ConstantsSingleton.COD_REGIONE == ConstantsSingleton.COD_REGIONE_TRENTO)
				|| (ConstantsSingleton.COD_REGIONE == ConstantsSingleton.COD_REGIONE_UMBRIA);
	}

	/**
	 * Istanzia una nuova entity e la rende persistente.
	 * 
	 * @return il path relativo all'edit della vacancy
	 */
	public String save() {
		Date pubblicazioneData = data.getDataPubblicazione();
		try {
			data.setDataPubblicazione(pubblicazioneData);
			data.setIdPfPrincipalAzienda(getSession().getConnectedAzienda().getId());
			if(utils.isPAT()){
				data.setAttivitaPrincipale(data.getAttivitaPrincipale().toUpperCase());
			}
			/*
			 * la VA viene inserita dal portale dal portale e la visibilta' e' true
			 */
			data.setVisibilita(true);

			/*
			 * al momento della creazione la VA non e' sincronizzata con cliclavoro
			 */
			data.setFlagInvioCl(false);

			data.setFlagEliminata(false);

			// al momento della crezione prevalorizzo il campo NullaOsta
			data.setOpzNullaOsta("N");

			/**
			 * set the new created vacancy stote to IN_LAVORAZIONE state
			 */

			data.setCodStatoVacancyEnum(CodStatoVacancyEnum.PUB);

			if (ConstantsSingleton.COD_REGIONE.equals(ConstantsSingleton.COD_REGIONE_TRENTO)) {
				data.setCodStatoVacancyEnum(CodStatoVacancyEnum.LAV);
			}

			// Setto il flag opzTipoDecodifiche in base al tipo di decodifiche usate da questo ambiente MyPortal
			if (ConstantsSingleton.usaDecodificheSilPerVacancy()) {
				data.setOpzTipoDecodifiche(VaDatiVacancy.OpzTipoDecodifiche.SIL);
			} else {
				data.setOpzTipoDecodifiche(VaDatiVacancy.OpzTipoDecodifiche.MINISTERO);
			}

			// Se siamo a Trento, la vacancy scade dopo 30 giorni.
			if (ConstantsSingleton.COD_REGIONE != ConstantsSingleton.COD_REGIONE_TRENTO) {
				Date now = new Date();
				data.setDtScadenza(DateUtils.addDays(now, NUM_GIORNI_VALIDITA_VACANCY_PAT));
			}

			if (pubblicazioneData != null)
				data = vaDatiVacancyHome.persistDTO(data, getSession().getPrincipalId());
		} catch (EJBException e) {
			log.error("Errore durante il salvataggio: " + e.getMessage());
			FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage("Errore durante il salvataggio dai dati"));
			return "";
		}

		if (pubblicazioneData != null) {
			showError = false;

			if (ConstantsSingleton.COD_REGIONE == ConstantsSingleton.COD_REGIONE_TRENTO) {
				String msgText = "<p>La tua offerta di lavoro è quasi completa!</p>"
						+ "<p>Arricchiscila con altre informazioni compilando le sezioni verdi (Rapporto di Lavoro, Istruzione, ecc).</p>"
						+ "<p>Grazie all’attivazione dei bottoni verdi, gli utenti potranno trovare il tuo annuncio con più facilità attraverso la ricerca personalizzata per filtri!</p>"
						+ "<p>Ricordati di cliccare sul tasto Pubblica per rendere visibile l'annuncio.</p>";
				FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, msgText, msgText);
				messages.addMessage(message);
			} else {
				// TODO non so come altro fare
				String msgText = errorsBean.getProperty("vacancy.fill_contacts");
				FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, msgText, msgText);
				addMessage(null, message);
				messages.addMessage(message);

				SvAziendaInfoDTO dto = svAziendaInfoHome.findDTOByIdPfPrincipal(data.getIdPfPrincipalAzienda());

				if (dto != null) {
					String info = "A seguito dell'inserimento della tua offerta, " + ConstantsSingleton.TITLE_APP
							+ " ha creato automaticamente la tua"
							+ " vetrina con i dati minimi a disposizione. Per incrementare la tua visibilita' e rendere piu'"
							+ " completo il tuo profilo visualizzabile dagli utenti, compila le altre sezioni della vetrina"
							+ " cliccando su \"Aggiorna la tua vetrina\" e non dimenticare di inserire il tuo logo dalla"
							+ " sezione \"Profilo\". ";

					FacesMessage message2 = new FacesMessage(FacesMessage.SEVERITY_INFO, info, info);
					addMessage(null, message2);
					messages.addMessage(message2);
				}
				String outcome = "edit?faces-redirect=true&fromNew=true&id=" + data.getId();
				return outcome;
			}
			String outcome = "edit?faces-redirect=true&fromNew=true&id=" + data.getId();
			return outcome;
		} else {
			showError = true;
			return "";
		}
	}

	public String selectRegione() {
		if (ConstantsSingleton.COD_REGIONE.equals(ConstantsSingleton.COD_REGIONE_TRENTO))
			regione = "calendar_22.gif";
		if (ConstantsSingleton.COD_REGIONE.equals(ConstantsSingleton.COD_REGIONE_RER)) {
			regione = "calendar_10.gif";
		}
		else
			regione = "calendar_8.gif";

		return regione;
	}

	public VaDatiVacancyDTO getData() {
		return data;
	}

	public void setData(VaDatiVacancyDTO data) {
		this.data = data;
	}

	public String getFiltroAttivita() {
		return filtroAttivita;
	}

	public void setFiltroAttivita(String filtroAttivita) {
		this.filtroAttivita = filtroAttivita;
	}

	public List<String> getFiltroQualifica() {
		return filtroQualifica;
	}

	public void setFiltroQualifica(List<String> filtroQualifica) {
		this.filtroQualifica = filtroQualifica;
	}

	public void setAttivitaFilter(ValueChangeEvent event) {
		String val = (String) event.getNewValue();

		// TODO sarebbe meglio ottenere l'elemento per id
		List<DeAttivitaDTO> lista = deAttivitaHome.findByDescription(val);

		// se seleziono un elemento valido
		if (lista.size() > 0) {
			String codAteco = lista.get(0).getId();

			String codAttivitaPadre = deAttivitaMinHome.getCodAttivitaPadreByCodAteco(codAteco);

			this.filtroAttivita = codAttivitaPadre;
			this.data.setAttivitaMin(new DeAttivitaMinDTO());
		} else {
			// se non seleziono un elemento valido
			this.filtroAttivita = null;
			this.data.setStrAteco(null);
			this.data.setCodAteco(null);
			this.data.setAttivitaMin(new DeAttivitaMinDTO());
		}

		return;
	}

	public void setQualificaFilter(ValueChangeEvent event) {
		String val = (String) event.getNewValue();

		// TODO sarebbe meglio ottenere l'elemento per id
		List<DeMansioneDTO> lista = deMansioneHome.findByDescription(val);

		// se seleziono un elemento valido
		if (lista.size() > 0) {
			String codMansione = lista.get(0).getId();

			List<String> listCodMansionePadre = deMansioneMinHome.getCodMansionePadreByCodMansione(codMansione);

			this.filtroQualifica = listCodMansionePadre;
			this.data.setMansioneMin(new DeMansioneMinDTO());
		} else {
			// se non seleziono un elemento valido
			this.filtroQualifica = new ArrayList<String>();
			this.data.setStrMansione(null);
			this.data.setCodMansione(null);
			this.data.setMansioneMin(new DeMansioneMinDTO());
		}

		return;
	}

	public boolean isShowError() {
		return showError;
	}

	public void setShowError(boolean showError) {
		this.showError = showError;
	}

	public String getRegione() {
		return regione;
	}

	public void setRegione(String regione) {
		this.regione = regione;
	}

	public String getVacanciesPatMaxScadenza() {
		return ConstantsSingleton.VACANCIES_PAT_MAX_SCADENZA;
	}

	public String getVacanciesMaxScadenza() {
		return ConstantsSingleton.VACANCIES_MAX_SCADENZA;
	}

}

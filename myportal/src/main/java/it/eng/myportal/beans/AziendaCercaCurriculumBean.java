package it.eng.myportal.beans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import it.eng.myportal.dtos.CvFilterDTO;
import it.eng.myportal.dtos.PrimoContattoDTO;
import it.eng.myportal.dtos.RicercaCVaziendaDTO;
import it.eng.myportal.entity.ejb.ClicLavoroPrimoContattoEjb;
import it.eng.myportal.entity.home.CvDatiPersonaliHome;
import it.eng.myportal.entity.home.VaDatiVacancyHome;
import it.eng.myportal.entity.home.decodifiche.DeLinguaHome;
import it.eng.myportal.entity.home.decodifiche.DeTitoloHome;
import it.eng.myportal.utils.ConstantsSingleton;
import it.eng.myportal.utils.PaginationHandler;

@ManagedBean
@ViewScoped
/**
 * Sostituita con PF nel 04/2020 questa classe andrebbe bruciata
 * @author Ale
 *
 */
public class AziendaCercaCurriculumBean extends AbstractBaseBean implements Serializable {

	private static final long serialVersionUID = 5537670668031203617L;

	protected static Log log = LogFactory.getLog(AziendaCercaCurriculumBean.class);

	private static final int ITEMS_PER_PAGE = 10;
	private CvFilterDTO parametriRicerca;
	private List<RicercaCVaziendaDTO> risultato;
	private boolean ricercaEseguita = false;
	private PaginationHandler paginationHandler;

	private List<SelectItem> lingue;
	private String descrizione;

	@EJB
	CvDatiPersonaliHome cvDatiPersonaliHome;

	@EJB
	VaDatiVacancyHome vaDatiVacancyHome;

	@EJB
	DeLinguaHome deLinguaHome;

	@EJB
	DeTitoloHome deTitoloHome;

	@EJB
	ClicLavoroPrimoContattoEjb primoContattoEjb;

	// Contatto da parte dell'azienda che visualizza il CV
	private PrimoContattoDTO primoContatto;
	private static String FILTRO = "filtro";

	/**
	 * Inizializza i parametri del bean.
	 */
	@Override
	@PostConstruct
	public void postConstruct() {
		super.postConstruct();
		secureMeAzienda();
		if (parametriRicerca == null)
			parametriRicerca = new CvFilterDTO();

		if (risultato == null)
			risultato = new ArrayList<RicercaCVaziendaDTO>();

		lingue = deLinguaHome.getListItems(false, "denominazione");
		resetPrimoContatto();

		Map<String, String> map = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
		String idVacancy = map.get("id");

		if (idVacancy != null) {
			Integer vacancyId = Integer.parseInt(idVacancy);
			parametriRicerca = vaDatiVacancyHome.findDatiRicerca(vacancyId);
			search();
		}
	}

	/**
	 * Effettua la ricerca. Calcola il numero totale di elementi, inizializza la paginazione e carica la prima pagina
	 * del risultato.
	 */
	public void search() {
		// Salvo i parametri di ricerca per poter tornare indietro
		putParamsIntoSession();
		// Creo l'handler della paginazione
		parametriRicerca.setMaxResults(ITEMS_PER_PAGE);
		parametriRicerca.setStartResultsFrom(0);
		Long numeroRisultati = cvDatiPersonaliHome.findCountByFilterAzienda(parametriRicerca,
				getSession().getConnectedAzienda().getId(), getSession().getConnectedAzienda().getId());
		paginationHandler = new PaginationHandler(0, ITEMS_PER_PAGE, numeroRisultati);

		// Cerco i risultati da visualizzare nella prima pagina.
		risultato = cvDatiPersonaliHome.ricercaCVazienda(parametriRicerca, getSession().getConnectedAzienda().getId(),
				getSession().getConnectedAzienda().getId());

		// Confermo che la ricerca è stata eseguita.
		ricercaEseguita = true;
	}

	/**
	 * Questo metodo viene chiamato quando l'utente cambia pagina. Fa una query per caricare gli elementi da
	 * visualizzare in quella pagina.
	 */
	public void changePage() {
		int startResultsFrom = paginationHandler.calculateStart(paginationHandler.getCurrentPage());
		parametriRicerca.setStartResultsFrom(startResultsFrom);
		risultato = cvDatiPersonaliHome.ricercaCVazienda(parametriRicerca, getSession().getConnectedAzienda().getId(),
				getSession().getConnectedAzienda().getId());
	}

	/**
	 * Salva i parametri di ricerca, in modo da poterli recuperare in futuro. Viene chiamato da putParamsIntoSession().
	 */
	@Override
	protected RestoreParameters generateRestoreParams() {
		RestoreParameters ret = super.generateRestoreParams();
		ret.put(FILTRO, parametriRicerca);
		return ret;
	}

	/**
	 * Recupera i parametri di ricerca (per ricostruire la pagina se ci si è arrivati premendo il tasto "back"). Viene
	 * chiamato da AbstractBaseBean.postConstruct().
	 */
	@Override
	public void ricreaStatoDaSessione(RestoreParameters restoreParams) {
		super.ricreaStatoDaSessione(restoreParams);
		parametriRicerca = (CvFilterDTO) restoreParams.get(FILTRO);
		search();
	};

	/**
	 * "Pulisce" i parametri del bean riguardo al contattare i lavoratori.
	 */
	private void resetPrimoContatto() {
		primoContatto = new PrimoContattoDTO();
		primoContatto.setIdPfPrincipalAzienda(getSession().getPrincipalId());
	}

	/**
	 * Invia un messaggio di contatto al proprietario di un curriculum.
	 */
	public void inviaPrimoContatto() {
		try {
			primoContattoEjb.sendPrimoContatto(primoContatto);
			resetPrimoContatto();
			addInfoMessage("messaggio.primo_contatto_ok");
			changePage();
		} catch (EJBException e) {
			gestisciErrore(e, "generic.error");
		}
	}

	public boolean chekRER() {
		boolean check = false;
		if (ConstantsSingleton.COD_REGIONE == ConstantsSingleton.COD_REGIONE_RER) {
			check= true;
		}
		return check;
	}

	public void secureMeAzienda() {
		if (!session.isAzienda()) {
			redirectHome();
		}
	}

	/*
	 * DA QUI IN POI: METODI GETTER E SETTER PER I VARI CAMPI DELLA CLASSE.
	 */
	public boolean isRicercaEseguita() {
		return ricercaEseguita;
	}

	public List<RicercaCVaziendaDTO> getRisultato() {
		return risultato;
	}

	public CvDatiPersonaliHome getCvDatiPersonaliHome() {
		return cvDatiPersonaliHome;
	}

	public void setCvDatiPersonaliHome(CvDatiPersonaliHome cvDatiPersonaliHome) {
		this.cvDatiPersonaliHome = cvDatiPersonaliHome;
	}

	public CvFilterDTO getParametriRicerca() {
		return parametriRicerca;
	}

	public void setParametriRicerca(CvFilterDTO parametriRicerca) {
		this.parametriRicerca = parametriRicerca;
	}

	public List<SelectItem> getLingue() {
		return lingue;
	}

	public void setLingue(List<SelectItem> lingue) {
		this.lingue = lingue;
	}

	public PrimoContattoDTO getPrimoContatto() {
		return primoContatto;
	}

	public void setPrimoContatto(PrimoContattoDTO primoContatto) {
		this.primoContatto = primoContatto;
	}

	public String getDescrizione() {
		return descrizione;
	}

	public void setDescrizione(String descrizione) {
		this.descrizione = descrizione;
	}

	public PaginationHandler getPaginationHandler() {
		return paginationHandler;
	}

}

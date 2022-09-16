package it.eng.myportal.beans;

import it.eng.myportal.dtos.CvFilterDTO;
import it.eng.myportal.dtos.RicercaCVprovinciaDTO;
import it.eng.myportal.entity.home.CvDatiPersonaliHome;
import it.eng.myportal.entity.home.VaDatiVacancyHome;
import it.eng.myportal.entity.home.decodifiche.DeLinguaHome;
import it.eng.myportal.entity.home.decodifiche.DeTitoloHome;
import it.eng.myportal.utils.PaginationHandler;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

@ManagedBean
@ViewScoped
public class ProvinciaCercaCurriculumBean extends AbstractBaseBean {
	protected static Log log = LogFactory.getLog(AziendaCercaCurriculumBean.class);

	private CvFilterDTO parametriRicerca;
	private List<RicercaCVprovinciaDTO> risultato;
	private PaginationHandler paginationHandler;
	private boolean ricercaEseguita = false;

	private List<SelectItem> lingue;
	private List<SelectItem> titoli;

	private String cf;
	private String nome;
	private String cognome;
	private String descrizione;

	@EJB
	CvDatiPersonaliHome cvDatiPersonaliHome;

	@EJB
	VaDatiVacancyHome vaDatiVacancyHome;

	@EJB
	DeLinguaHome deLinguaHome;

	@EJB
	DeTitoloHome deTitoloHome;

	private static String FILTRO = "filtro";

	/**
	 * Inizializza i vari campi del bean.
	 */
	@Override
	@PostConstruct
	public void postConstruct() {
		super.postConstruct();

		if (parametriRicerca == null) 
			parametriRicerca = new CvFilterDTO();
		
		if (risultato == null) 
			risultato = new ArrayList<RicercaCVprovinciaDTO>();

		lingue = deLinguaHome.getListItems(false, "denominazione");
		titoli = deTitoloHome.getListItems(false, "descrizione");
	}
	
	/**
	 * Questo metodo viene chiamato quando l'utente preme il pulsante "cerca".
	 * Calcola il numero totale di risultati, inizializza la paginazione e carica
	 * la prima pagina del risultato.
	 */
	public void search() {
		// Salvo i parametri di ricerca nella sessione per poter tornare indietro
		putParamsIntoSession();

		// Creo l'handler della paginazione
		parametriRicerca.setMaxResults(10);
		parametriRicerca.setStartResultsFrom(0);	
		Long numeroRisultati = 
				cvDatiPersonaliHome.findCountByFilterProvincia(parametriRicerca, 
						getSession().getConnectedProvincia().getProvincia(),
						pfPrincipalHome.findById(getSession().getPrincipalId()).isClicLavoro());
		paginationHandler = new PaginationHandler (parametriRicerca.getStartResultsFrom(), 
				parametriRicerca.getMaxResults(), numeroRisultati);
		
		// Cerco i primi 10 risultati da visualizzare nella prima pagina.
		risultato = cvDatiPersonaliHome.ricercaCVprovincia(parametriRicerca, getSession().getConnectedProvincia().getProvincia(),
				pfPrincipalHome.findById(getSession().getPrincipalId()).isClicLavoro());
		
		// Confermo che la ricerca è stata eseguita.
		ricercaEseguita = true;
	}
	
	/**
	 * Questo metodo viene chiamato quando l'utente cambia pagina.
	 * Fa una query per caricare gli elementi da visualizzare in quella pagina.
	 */
	public void changePage() {
		int startResultsFrom = paginationHandler.calculateStart(paginationHandler.getCurrentPage());
		parametriRicerca.setStartResultsFrom(startResultsFrom);
		risultato = cvDatiPersonaliHome.ricercaCVprovincia(parametriRicerca, getSession().getConnectedProvincia().getProvincia(), 
				pfPrincipalHome.findById(getSession().getPrincipalId()).isClicLavoro());
	}
	
	/**
	 * @see it.eng.myportal.beans.AbstractBaseBean#generateRestoreParams()
	 * Metodo per salvare i parametri di ricerca. Viene chiamato da putParamsIntoSession().
	 */
	@Override
	protected RestoreParameters generateRestoreParams() {
		RestoreParameters ret = super.generateRestoreParams();
		ret.put(FILTRO, parametriRicerca);
		return ret;
	}

	/**
	 * @see
	 * it.eng.myportal.beans.AbstractBaseBean#ricreaStatoDaSessione(it.eng.myportal
	 * .beans.RestoreParameters)
	 * 
	 * Metodo per recuperare i parametri di ricerca. Viene chiamato da
	 * AbstractBaseBean.postConstruct()
	 */
	@Override
	public void ricreaStatoDaSessione(RestoreParameters restoreParams) {
		super.ricreaStatoDaSessione(restoreParams);
		parametriRicerca = (CvFilterDTO) restoreParams.get(FILTRO);
		search();
	};
	
	/**
	 * Questo metodo restituisce una stringa con cognome e nome del proprietario di un curriculum.
	 * (Serve per girare attorno alla questione dati normali/dati palesi)
	 */
	public String getVeroCognomeNome(RicercaCVprovinciaDTO dto) {
		if (dto.getNomePalese() != null) 
			return dto.getCognomePalese() + " - " + dto.getNomePalese();
		else
			return dto.getCognome() + " - " + dto.getNome();
	}
	
	/**
	 * Questo metodo restituisce il codice fiscale contenuto in un curriculum.
	 * (Serve per girare attorno alla questione dati normali/dati palesi)
	 */
	public String getVeroCodiceFiscale(RicercaCVprovinciaDTO dto) {
		if (dto.getCodiceFiscalePalese() != null)
			return dto.getCodiceFiscalePalese();
		else
			return dto.getCodiceFiscale();
	}
	
	/*
	 * DA QUI IN POI: METODI GETTER E SETTER PER LE VARIE PROPRIETA' DEL BEAN
	 */

	public String getCf() {
		return cf;
	}

	public void setCf(String cf) {
		this.cf = cf;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getCognome() {
		return cognome;
	}

	public void setCognome(String cognome) {
		this.cognome = cognome;
	}
	
	public List<RicercaCVprovinciaDTO> getRisultato() {
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

	public List<SelectItem> getTitoli() {
		return titoli;
	}

	public void setTitoli(List<SelectItem> titoli) {
		this.titoli = titoli;
	}

	public String getDescrizione() {
		return descrizione;
	}

	public void setDescrizione(String descrizione) {
		this.descrizione = descrizione;
	}
	
	public PaginationHandler getPaginationHandler() {
		return this.paginationHandler;
	}
	
	public boolean isRicercaEseguita() {
		return ricercaEseguita;
	}

}

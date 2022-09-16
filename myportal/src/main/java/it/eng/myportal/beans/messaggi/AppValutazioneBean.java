package it.eng.myportal.beans.messaggi;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;

import it.eng.myportal.beans.AbstractMasterDetailTabBean;
import it.eng.myportal.dtos.AppValutazioneDTO;
import it.eng.myportal.dtos.AppValutazioneRicercaDTO;
import it.eng.myportal.entity.home.AbstractHome;
import it.eng.myportal.entity.home.AppValutazioneHome;
import it.eng.myportal.utils.PaginationHandler;

/**
 * BackingBean della tab "valutazione app" della sezione messaggi.
 * 
 * @author
 *
 */
@ManagedBean
@ViewScoped
public class AppValutazioneBean extends AbstractMasterDetailTabBean<AppValutazioneDTO> {

	private static final int ITEMS_PER_PAGE = 10;

	@EJB
	AppValutazioneHome appValutazioneHome;

	private AppValutazioneRicercaDTO filtriRicerca = new AppValutazioneRicercaDTO();
	private PaginationHandler paginationHandler;

	private List<SelectItem> listaStelle;
	private Boolean ricercaEseguita = false;

	/**
	 * Questo messaggio viene chiamato alla creazione del bean.
	 */
	@Override
	@PostConstruct
	protected void postConstruct() {
		super.postConstruct();

		// Costruzione della lista delle stelle
		listaStelle = findListaStelle();

		if (list != null && !list.isEmpty()) {
			ricercaEseguita = true;
		} else {
			ricercaEseguita = false;
		}

		data = buildNewDataIstance();
	}

	/**
	 * Questo metodo viene chiamato quando l'utente preme il tasto "cerca". Trova il numero totale dei risultati,
	 * inizializza la paginazione e carica la prima pagina del risultato.
	 */
	public void search() {
		// Creo l'handler per la paginazione.
		Long valutazioniCount = appValutazioneHome.getCountValutazione(filtriRicerca.getNumStelle(),
				filtriRicerca.isEscludiAnonime(), filtriRicerca.isSoloAnonime(), filtriRicerca.getDtaDa(),
				filtriRicerca.getDtaA());
		paginationHandler = new PaginationHandler(0, ITEMS_PER_PAGE, valutazioniCount);

		dontedit();
		// Carico la prima pagina del risultato.
		loadPage();
		ricercaEseguita = true;
	}

	/**
	 * Questo metodo viene chiamato quando l'utente cambia pagina.
	 */
	public void changePage() {
		dontedit();
		int startResultsFrom = paginationHandler.calculateStart(paginationHandler.getCurrentPage());
		paginationHandler.setStart(startResultsFrom);
		loadPage();
	}

	/**
	 * Carica i dati relativi ad una pagina dei risultati di ricerca.
	 */
	private void loadPage() {
		list = appValutazioneHome.getValutazioni(filtriRicerca.getNumStelle(), filtriRicerca.isEscludiAnonime(),
				filtriRicerca.isSoloAnonime(), filtriRicerca.getDtaDa(), filtriRicerca.getDtaA(),
				paginationHandler.getStart(), ITEMS_PER_PAGE);
	}

	@Override
	protected void saveData() {
		// Funzionalità non prevista
	}

	@Override
	public AppValutazioneDTO buildNewDataIstance() {
		return new AppValutazioneDTO();
	}

	@Override
	protected List<AppValutazioneDTO> retrieveData() {
		return appValutazioneHome.getValutazioni();
	}

	@Override
	protected AbstractHome<?, AppValutazioneDTO, Integer> getHome() {
		return appValutazioneHome;
	}

	private List<SelectItem> findListaStelle() {
		List<SelectItem> ret = new ArrayList<SelectItem>();

		// Elemento 0
		ret.add(0, new SelectItem(null, ""));
		for (short i = 1; i <= 5; i++) {
			Short obj = new Short(i);
			SelectItem item = new SelectItem(obj, obj.toString());
			ret.add(item);
		}

		return ret;
	}

	public AppValutazioneRicercaDTO getFiltriRicerca() {
		return filtriRicerca;
	}

	public void setFiltriRicerca(AppValutazioneRicercaDTO filtriRicerca) {
		this.filtriRicerca = filtriRicerca;
	}

	public PaginationHandler getPaginationHandler() {
		return paginationHandler;
	}

	public void setPaginationHandler(PaginationHandler paginationHandler) {
		this.paginationHandler = paginationHandler;
	}

	public List<SelectItem> getListaStelle() {
		return listaStelle;
	}

	public void setListaStelle(List<SelectItem> listaStelle) {
		this.listaStelle = listaStelle;
	}

	public Boolean getRicercaEseguita() {
		return ricercaEseguita;
	}

	public void setRicercaEseguita(Boolean ricercaEseguita) {
		this.ricercaEseguita = ricercaEseguita;
	}
}

package it.eng.myportal.beans.messaggi;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;

import it.eng.myportal.beans.AbstractMasterDetailTabBean;
import it.eng.myportal.dtos.AppNotificaDTO;
import it.eng.myportal.dtos.AppNotificaRicercaDTO;
import it.eng.myportal.entity.home.AbstractHome;
import it.eng.myportal.entity.home.AppNotificaHome;
import it.eng.myportal.rest.app.helper.StatoNotifica;
import it.eng.myportal.utils.ConstantsSingleton;
import it.eng.myportal.utils.PaginationHandler;

/**
 * BackingBean della tab "notifiche app" della sezione messaggi.
 * 
 * @author
 *
 */
@ManagedBean
@ViewScoped
public class AppNotificaBean extends AbstractMasterDetailTabBean<AppNotificaDTO> {

	private static final int ITEMS_PER_PAGE = 10;

	@EJB
	AppNotificaHome appNotificaHome;

	private AppNotificaRicercaDTO filtriRicerca = new AppNotificaRicercaDTO();
	private PaginationHandler paginationHandler;

	private String filtroCodProvincia = null;
	private List<SelectItem> listaStatiNotifica;
	private Boolean ricercaEseguita = false;

	/**
	 * Questo messaggio viene chiamato alla creazione del bean.
	 */
	@Override
	@PostConstruct
	protected void postConstruct() {
		super.postConstruct();

		// Recupero della provincia valida dell'utente di riferimento (la gestione è specifica per l'utente provincia)
		filtroCodProvincia = getSession().getConnectedProvincia().getProvincia().getId();

		// Costruzione della lista degli stati delle notifiche
		listaStatiNotifica = findListaStatiNotifica();

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
		Long notificheCount = appNotificaHome.getCountNotificheInviate(filtroCodProvincia,
				filtriRicerca.getEmailDestinatario(), filtriRicerca.getCodStato(),
				filtriRicerca.getEscludiNotificheBatch(), filtriRicerca.getDtaDa(), filtriRicerca.getDtaA());
		paginationHandler = new PaginationHandler(0, ITEMS_PER_PAGE, notificheCount);

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
		list = appNotificaHome.getNotificheInviate(filtroCodProvincia, filtriRicerca.getEmailDestinatario(),
				filtriRicerca.getCodStato(), filtriRicerca.getEscludiNotificheBatch(), filtriRicerca.getDtaDa(),
				filtriRicerca.getDtaA(), paginationHandler.getStart(), ITEMS_PER_PAGE);
	}

	/**
	 * Questo metodo viene chiamato quando l'utente invia una nuova notifica. Salva sul database la notifica inviata.
	 */
	@Override
	protected void saveData() {
		AppNotificaDTO appNotificaDTO = homePersist(appNotificaHome, data);

		if (appNotificaDTO.getStato() == StatoNotifica.L) {
			// Nessun destinatario disponibile, si visualizza un warning ulteriore
			addWarnMessage("appnotifica.invio.logout");
		}
	}

	/**
	 * Metodo chiamato quando l'utente apre il pannello di creazione nuova notifica.
	 */
	public void showInsertPanel() {
		super.showInsertPanel();
		data.setDtmIns(new Date());
	}

	@Override
	public AppNotificaDTO buildNewDataIstance() {
		return new AppNotificaDTO();
	}

	@Override
	protected List<AppNotificaDTO> retrieveData() {
		return appNotificaHome.getNotificheInviate(filtroCodProvincia);
	}

	@Override
	public void save() {
		super.save();

		// Post save reset del filtro
		filtriRicerca = new AppNotificaRicercaDTO();

		search();
	}

	public boolean isCruscottoVisible() {
		return ConstantsSingleton.App.CRUSCOTTO_MYPORTAL_VISIBLE;
	}
	
	@Override
	protected AbstractHome<?, AppNotificaDTO, Integer> getHome() {
		return appNotificaHome;
	}

	private List<SelectItem> findListaStatiNotifica() {
		List<SelectItem> ret = new ArrayList<SelectItem>();

		// Elemento 0
		ret.add(0, new SelectItem(null, ""));
		for (StatoNotifica statoNot : StatoNotifica.values()) {
			SelectItem item = new SelectItem(statoNot, statoNot.getDescrizione());
			ret.add(item);
		}

		return ret;
	}

	public AppNotificaRicercaDTO getFiltriRicerca() {
		return filtriRicerca;
	}

	public void setFiltriRicerca(AppNotificaRicercaDTO filtriRicerca) {
		this.filtriRicerca = filtriRicerca;
	}

	public PaginationHandler getPaginationHandler() {
		return paginationHandler;
	}

	public void setPaginationHandler(PaginationHandler paginationHandler) {
		this.paginationHandler = paginationHandler;
	}

	public String getFiltroCodProvincia() {
		return filtroCodProvincia;
	}

	public void setFiltroCodProvincia(String filtroCodProvincia) {
		this.filtroCodProvincia = filtroCodProvincia;
	}

	public List<SelectItem> getListaStatiNotifica() {
		return listaStatiNotifica;
	}

	public void setListaStatiNotifica(List<SelectItem> listaStatiNotifica) {
		this.listaStatiNotifica = listaStatiNotifica;
	}

	public Boolean getRicercaEseguita() {
		return ricercaEseguita;
	}

	public void setRicercaEseguita(Boolean ricercaEseguita) {
		this.ricercaEseguita = ricercaEseguita;
	}
}

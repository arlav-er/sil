package it.eng.myportal.beans.atipici;

import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import it.eng.myportal.beans.AbstractBaseBean;
import it.eng.myportal.cliclavoro.candidatura.Sessocheck;
import it.eng.myportal.dtos.AbstractUpdatablePkDTO;
import it.eng.myportal.dtos.AziendaInfoDTO;
import it.eng.myportal.dtos.DeTipoConsulenzaDTO;
import it.eng.myportal.dtos.MsgMessaggioAtipicoDTO;
import it.eng.myportal.dtos.MsgMessaggioAtipicoMiniDTO;
import it.eng.myportal.dtos.MsgMessaggioDTO;
import it.eng.myportal.dtos.PfPrincipalDTO;
import it.eng.myportal.dtos.UtenteInfoDTO;
import it.eng.myportal.entity.home.AziendaInfoHome;
import it.eng.myportal.entity.home.MsgMessaggioAtipicoHome;
import it.eng.myportal.entity.home.MsgMessaggioHome;
import it.eng.myportal.entity.home.PfPrincipalHome;
import it.eng.myportal.entity.home.decodifiche.DeAtpAttivitaSvoltaHome;
import it.eng.myportal.entity.home.decodifiche.DeAttivitaHome;
import it.eng.myportal.entity.home.decodifiche.DeContrattoHome;
import it.eng.myportal.entity.home.decodifiche.DeTipoConsulenzaHome;
import it.eng.myportal.entity.home.decodifiche.DeTipoPraticaHome;
import it.eng.myportal.enums.FasciaEta;
import it.eng.myportal.utils.ConstantsSingleton;
import it.eng.myportal.utils.PaginationHandler;

/**
 * Backing Bean della pagina delle pratiche atipici del azienda
 * 
 * @author Maresta A.
 * 
 */
@ManagedBean
@ViewScoped
public class AtipiciAziendaBean extends AbstractBaseBean {
	protected static Log log = LogFactory.getLog(AtipiciAziendaBean.class);

	@EJB
	private DeAttivitaHome deAttivitaHome;

	@EJB
	private DeAtpAttivitaSvoltaHome deAtpAttivitaSvoltaHome;
	
	@EJB
	private DeTipoConsulenzaHome deTipoConsulenzaHome;
	
	@EJB
	private DeContrattoHome deContrattoHome;
		
	@EJB
	private DeTipoPraticaHome deTipoPratica;
	
	@EJB
	private MsgMessaggioAtipicoHome msgMessaggioAtipicoHome;

	@EJB
	private MsgMessaggioHome msgMessaggioHome;
	
	@EJB
	private PfPrincipalHome pfPrincipalHome;	
	
	@EJB
	private AziendaInfoHome aziendaInfoHome;

	private static final int RESULTS_FOR_PAGE = 10;
	private PaginationHandler paginationHandler;
	private List<MsgMessaggioAtipicoMiniDTO> pratiche;
	private MsgMessaggioAtipicoDTO richiesta = new MsgMessaggioAtipicoDTO();
	private MsgMessaggioAtipicoDTO messaggio = new MsgMessaggioAtipicoDTO();
	private String status = "";
	private boolean detail;
	private String tipoQuesito;

	private List<SelectItem> attivita;
	private List<SelectItem> tipiAttivitaSvolte;
	private List<SelectItem> tipiContratto;
	private List<SelectItem> tipiConsulenza;
	private List<SelectItem> tipiPratica;
	private List<SelectItem> tipiPraticaPIva;
	private List<SelectItem> tipiPraticaAtipici;
	private List<SelectItem> fascieEta;

	/**
	 * Metodo chiamato subito dopo la creazione del bean, lo inizializza.
	 */
	@PostConstruct
	public void postConstruct() {
		super.postConstruct();
		
		// Inizializzo gli elementi delle varie liste che dovrò mostrare.
		tipiContratto = deContrattoHome.getListItems(true);
		tipiPraticaPIva = deTipoPratica.getPartitaIvaListItems(true);
		tipiPraticaAtipici = deTipoPratica.getAtipiciListItems(true);
		tipiPratica = tipiPraticaPIva;
		tipiConsulenza = deTipoConsulenzaHome.getListItems(true);
		tipiAttivitaSvolte = deAtpAttivitaSvoltaHome.getListItems(true);
		attivita = deAttivitaHome.getListItems(true);
		fascieEta = FasciaEta.asSelectItems(true);
		
		// Prendo il valore del tipo di quesito che sto trattando dalla sessione.
		Map<String, String> map = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
		tipoQuesito = map.get("tipo_quesito");
		richiesta.getDeTipoQuesito().setId(tipoQuesito);
		richiesta.setTipoConsulenza(deTipoConsulenzaHome.findDTOById(ConstantsSingleton.DeTipoConsulenza.PARTITA_IVA));
		
		// Inizializzo la visualizzazione della lista.
		resetPagination();
	}
	
	/**
	 * Questo metodo inizializza o resetta la visualizzazione della lista di pratiche.
	 */
	private void resetPagination() {
		// Trovo il numero totale di pratiche e creo il PaginationHandler.
		Long praticheCount = msgMessaggioAtipicoHome.findPraticheAtipiciCount
				(session.getConnectedAzienda().getId(), tipoQuesito);
		paginationHandler = new PaginationHandler(0, RESULTS_FOR_PAGE, praticheCount);
		
		// Carico la prima pagina di risultati.
		pratiche = msgMessaggioAtipicoHome.findPraticheAtipici
				(session.getConnectedAzienda().getId(),tipoQuesito, 0, RESULTS_FOR_PAGE);
		
		// Se c'è almeno una pratica, mostro la lista. Altrimenti passo subito all'inserimento.
		setStatus(praticheCount > 0 ? "list" : "insert");
	}
	
	/**
	 * Questo metodo viene chiamato quando l'utente cambia pagina nella lista dei risultati.
	 */
	public void changePage() {
		setStatus("list");
		int startResultsFrom = paginationHandler.calculateStart(paginationHandler.getCurrentPage());
		pratiche = msgMessaggioAtipicoHome.findPraticheAtipici
				(session.getConnectedAzienda().getId(), tipoQuesito, startResultsFrom, RESULTS_FOR_PAGE);	
	}
	
	/**
	 * Metodo chiamato quando l'utente sceglie una pratica dalla lista per visualizzarla.
	 */
	public void view(Integer praticaId) {
		messaggio = msgMessaggioAtipicoHome.findDTOById(praticaId);
		msgMessaggioHome.signAsRead(getSession().getPrincipalId(), praticaId, true);

		if (messaggio.getRisposte() != null) {
			MsgMessaggioDTO msgMessaggioDTO = messaggio.getRisposte().get(0);
			if (msgMessaggioDTO != null)
				msgMessaggioHome.signAsRead(getSession().getPrincipalId(), msgMessaggioDTO.getId(), true);
		}

		for (MsgMessaggioAtipicoMiniDTO m : pratiche)
			if (m.getId() == praticaId) {
				m.setDaLeggere(false);
				break;
			}

		detail = true;
	}
	
	/**
	 * Metodo chiamato quando l'utente decide di inviare una nuova pratica.
	 */
	public void save() {
		try {
			if (tipoQuesito.equals(ConstantsSingleton.TipoQuesito.TEMATICA)) {
				DeTipoConsulenzaDTO tipoConsDTO = deTipoConsulenzaHome.findDTOById(ConstantsSingleton.DeTipoConsulenza.PARTITA_IVA);
				if (tipoConsDTO.getId().equalsIgnoreCase(richiesta.getTipoConsulenza().getId())) {
					if (richiesta.getFlagAssociazioneProfessionale() && richiesta.getAssociazioneProfessionale() == null) {
						addErrorMessage("atipici.assprof_obbligatoria");
						return;
					}
				}
			}
			msgMessaggioAtipicoHome.invioDomanda(richiesta, session.getPrincipalId());
			
			// Svuoto i campi "oggetto" e "corpo" per un'eventuale prossima richiesta.
			richiesta.setCorpo("");
			richiesta.setOggetto("");
			
			// Una volta inserita la richiesta, visualizzo un messaggio e resetto la pagina.
			addInfoMessage("message.sent");
			setStatus("list");
		} catch (Exception e) {
			addErrorMessage("generic.error");
		}
	}
	
	/**
	 * Questo metodo inserisce dei valori di default nei campi del form "inserisci nuova pratica".
	 */
	private void prevalorizzaCampi(MsgMessaggioAtipicoDTO nuovaPratica,PfPrincipalDTO principal, AbstractUpdatablePkDTO info) {
		nuovaPratica.setNome(principal.getNome());
		nuovaPratica.setCognome(principal.getCognome());
		nuovaPratica.setEmail(principal.getEmail());
		List<MsgMessaggioAtipicoDTO> listaPratiche = msgMessaggioAtipicoHome.findDTObyIdPfPrincipal(principal.getId());
		if (listaPratiche != null && !listaPratiche.isEmpty()) 
		{
			MsgMessaggioAtipicoDTO ultima = listaPratiche.get(listaPratiche.size()-1);
			if (ultima.getDeTitolo() != null && ultima.getDeTitolo().getId() != null) {
				nuovaPratica.setDeTitolo(ultima.getDeTitolo());
			}
		}
		if (info instanceof AziendaInfoDTO) {
			nuovaPratica.setCap(((AziendaInfoDTO) info).getSedeOperativa().getComune().getCap());
			nuovaPratica.setFasciaEta(FasciaEta.getFascia(((AziendaInfoDTO) info).getDataNascitaRic()));
			nuovaPratica.setTelefonoFax(((AziendaInfoDTO) info).getTelefonoReferente());
			nuovaPratica.setDeComuneByCodComuneResidenza(((AziendaInfoDTO) info).getSedeOperativa().getComune());
		} else if (info instanceof UtenteInfoDTO) {
			nuovaPratica.setCap(((UtenteInfoDTO) info).getComuneDomicilio().getCap());
			String giornoNascita = ((UtenteInfoDTO) info).getCodiceFiscale().substring(9, 11);
			Sessocheck sesso = (Integer.valueOf(giornoNascita) > 40) ? Sessocheck.F : Sessocheck.M;
			nuovaPratica.setCodSesso(Integer.toString(sesso.ordinal()+1));
			nuovaPratica.setFasciaEta(FasciaEta.getFascia(((UtenteInfoDTO) info).getDataNascita()));
			nuovaPratica.setTelefonoFax(((UtenteInfoDTO) info).getTelCasa());
			nuovaPratica.setDeComuneByCodComuneResidenza(((UtenteInfoDTO) info).getComuneDomicilio());
		}
	}

	/**
	 * Cambia gli oggetti contenuti nelle liste in base al tipo di pratica aperto in questo momento.
	 */
	public void changeTipoLavoro(ValueChangeEvent event) {
		if (event.getNewValue().equals("ATIPICO"))
			tipiPratica = tipiPraticaAtipici;
		else
			tipiPratica = tipiPraticaPIva;	
		return;
	}

	/**
	 * Il setter della proprietà "status" ha alcuni effetti collaterali.
	 * @param status
	 */
	public void setStatus(String status) {
		String oldStatus = this.status;
		this.status = status;
		detail = false;
		messaggio = new MsgMessaggioAtipicoDTO();
		prevalorizzaCampi(richiesta,pfPrincipalHome.findDTOById(session.getConnectedAzienda().getId()),aziendaInfoHome.findDTOById(session.getConnectedAzienda().getId()));
		richiesta.setTipoConsulenza(deTipoConsulenzaHome.findDTOById(ConstantsSingleton.DeTipoConsulenza.PARTITA_IVA));		
		
		// Se sto uscendo dallo status "insert", resetto la lista delle pratiche.
		if (oldStatus.equals("insert") && status.equals("list")) {
			resetPagination();
		}	
	}
	
	/*
	 * ======================== DA QUI IN POI CI SONO GETTER E SETTER SEMPLICI =================================
	 */
	public List<SelectItem> getTipiPratica() {
		return tipiPratica;
	}

	public List<SelectItem> getTipiAttivitaSvolte() {
		return tipiAttivitaSvolte;
	}

	public void setTipiAttivitaSvolte(List<SelectItem> tipiAttivitaSvolte) {
		this.tipiAttivitaSvolte = tipiAttivitaSvolte;
	}

	public void setTipiPratica(List<SelectItem> tipiPratica) {
		this.tipiPratica = tipiPratica;
	}

	public List<SelectItem> getTipiConsulenza() {
		return tipiConsulenza;
	}

	public void setTipiConsulenza(List<SelectItem> tipiConsulenza) {
		this.tipiConsulenza = tipiConsulenza;
	}

	public List<SelectItem> getTipiContratto() {
		return tipiContratto;
	}

	public void setTipiContratto(List<SelectItem> tipiContratto) {
		this.tipiContratto = tipiContratto;
	}

	public List<SelectItem> getAttivita() {
		return attivita;
	}

	public void setAttivita(List<SelectItem> atpAttivita) {
		this.attivita = atpAttivita;
	}

	public MsgMessaggioAtipicoDTO getRichiesta() {
		return richiesta;
	}

	public void setRichiesta(MsgMessaggioAtipicoDTO richiesta) {
		this.richiesta = richiesta;
	}

	public List<MsgMessaggioAtipicoMiniDTO> getPratiche() {
		return pratiche;
	}

	public MsgMessaggioAtipicoDTO getMessaggio() {
		return messaggio;
	}

	public void closeDetail() {
		detail = false;
	}

	public boolean getDetail() {
		return detail;
	}

	public String getStatus() {
		return status;
	}

	public List<SelectItem> getFascieEta() {
		return fascieEta;
	}

	public void setFascieEta(List<SelectItem> fascieEta) {
		this.fascieEta = fascieEta;
	}

	public String getTipoQuesito() {
		return tipoQuesito;
	}

	public void setTipoQuesito(String tipoQuesito) {
		this.tipoQuesito = tipoQuesito;
	}

	public PaginationHandler getPaginationHandler() {
		return paginationHandler;
	}
}

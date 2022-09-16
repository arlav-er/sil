package it.eng.myportal.beans.atipici;

import it.eng.myportal.beans.AbstractBaseBean;
import it.eng.myportal.cliclavoro.candidatura.Sessocheck;
import it.eng.myportal.dtos.AbstractUpdatablePkDTO;
import it.eng.myportal.dtos.AziendaInfoDTO;
import it.eng.myportal.dtos.DeTipoConsulenzaDTO;
import it.eng.myportal.dtos.DeTitoloDTO;
import it.eng.myportal.dtos.MsgMessaggioAtipicoDTO;
import it.eng.myportal.dtos.MsgMessaggioAtipicoMiniDTO;
import it.eng.myportal.dtos.MsgMessaggioDTO;
import it.eng.myportal.dtos.PfPrincipalDTO;
import it.eng.myportal.dtos.UtenteInfoDTO;
import it.eng.myportal.entity.home.MsgMessaggioAtipicoHome;
import it.eng.myportal.entity.home.MsgMessaggioHome;
import it.eng.myportal.entity.home.UtenteInfoHome;
import it.eng.myportal.entity.home.decodifiche.DeAtpAttivitaSvoltaHome;
import it.eng.myportal.entity.home.decodifiche.DeAttivitaHome;
import it.eng.myportal.entity.home.decodifiche.DeContrattoHome;
import it.eng.myportal.entity.home.decodifiche.DeTipoConsulenzaHome;
import it.eng.myportal.entity.home.decodifiche.DeTipoPraticaHome;
import it.eng.myportal.enums.FasciaEta;
import it.eng.myportal.utils.ConstantsSingleton;
import it.eng.myportal.utils.PaginationHandler;

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

/**
 * Backing Bean della pagina delle pratiche atipici del cittadino
 * 
 * @author Maresta A.
 * 
 */
@ManagedBean
@ViewScoped
public class AtipiciCittadinoBean extends AbstractBaseBean {
	protected static Log log = LogFactory.getLog(AtipiciCittadinoBean.class);

	@EJB
	private DeAttivitaHome deAttivitaHome;

	@EJB
	private DeAtpAttivitaSvoltaHome deAtpAttivitaSvoltaHome;

	@EJB
	private DeTipoConsulenzaHome deTipoConsulenzaHome;

	@EJB
	private DeContrattoHome deContrattoHome;

	@EJB
	private DeTipoPraticaHome deTipoPraticaHome;

	@EJB
	private UtenteInfoHome utenteInfoHome;

	@EJB
	private MsgMessaggioAtipicoHome msgMessaggioAtipicoHome;

	@EJB
	private MsgMessaggioHome msgMessaggioHome;

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
	private List<SelectItem> tipiContrattoPIva;
	private List<SelectItem> tipiContrattoAtipici;
	private List<SelectItem> fascieEta;

	/**
	 * Metodo chiamato subito dopo la costruzione del bean.
	 */
	@PostConstruct
	public void postConstruct() {
		super.postConstruct();

		// Inizializzo gli elementi delle varie liste che dovrò mostrare.
		tipiContrattoPIva = deContrattoHome.getPartitaIvaListItems(true);
		tipiContrattoAtipici = deContrattoHome.getAtipiciListItems(true);
		tipiContratto = tipiContrattoAtipici;
		tipiPraticaPIva = deTipoPraticaHome.getPartitaIvaListItems(true);
		tipiPraticaAtipici = deTipoPraticaHome.getAtipiciListItems(true);
		tipiPratica = tipiPraticaPIva;
		tipiConsulenza = deTipoConsulenzaHome.getListItems(false);
		tipiAttivitaSvolte = deAtpAttivitaSvoltaHome.getListItems(true);
		attivita = deAttivitaHome.getListItems(true);
		fascieEta = FasciaEta.asSelectItems(true);

		// Inizializzo i valori di default nei campi.
		Map<String, String> map = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
		prevalorizzaCampi(richiesta, session.getConnectedUtente().getPfPrincipalDTO(),
		        utenteInfoHome.findDTOById(session.getConnectedUtente().getId()));
		tipoQuesito = map.get("tipo_quesito");
		richiesta.getDeTipoQuesito().setId(tipoQuesito);
		
		// Inizializzo la paginazione
		Long praticheCount = msgMessaggioAtipicoHome.findPraticheAtipiciCount
				(session.getConnectedUtente().getId(), tipoQuesito);
		paginationHandler = new PaginationHandler(0, RESULTS_FOR_PAGE, praticheCount);
		
		// Carico la prima pagina di risultati
		pratiche = msgMessaggioAtipicoHome.findPraticheAtipici
				(session.getConnectedUtente().getId(), tipoQuesito, 0, RESULTS_FOR_PAGE);	
		
		// Se c'è almeno una pratica, mostro la lista. Altrimenti passo subito all'inserimento.
		setStatus(pratiche.size() > 0 ? "list" : "insert");
	}
	
	/**
	 * Questo metodo viene chiamato quando l'utente cambia pagina nella lista dei risultati.
	 */
	public void changePage() {
		setStatus("list");
		int startResultsFrom = paginationHandler.calculateStart(paginationHandler.getCurrentPage());
		pratiche = msgMessaggioAtipicoHome.findPraticheAtipici
				(session.getConnectedUtente().getId(), tipoQuesito, startResultsFrom, RESULTS_FOR_PAGE);	
	}
	
	/**
	 * Metodo chiamato quando l'utente sceglie una pratica dala lista per visualizzarla.
	 */
	public void view(Integer pratica) {
		messaggio = msgMessaggioAtipicoHome.findDTOById(pratica);
		msgMessaggioHome.signAsRead(getSession().getPrincipalId(), pratica, true);

		if (messaggio.getRisposte() != null) {
			MsgMessaggioDTO msgMessaggioDTO = messaggio.getRisposte().get(0);
			if (msgMessaggioDTO != null)
				msgMessaggioHome.signAsRead(getSession().getPrincipalId(), msgMessaggioDTO.getId(), true);
		}

		for (MsgMessaggioAtipicoMiniDTO m : pratiche)
			if (m.getId() == pratica) {
				m.setDaLeggere(false);
				break;
			}

		detail = true;
	}
	
	/**
	 * Metodo chiamato quando l'utente conferma che vuole inserire una nuova richiesta
	 * nel database.
	 */
	public void save() {
		try {
			if (tipoQuesito.equals(ConstantsSingleton.TipoQuesito.LAVORO)) {
				if (richiesta.getFlagIscrittoCpi() && richiesta.getComuneIscrizione().getId() == null) {
					addErrorMessage("atipici.valorizzare_comune");
					return;
				}
			}
			else if (tipoQuesito.equals(ConstantsSingleton.TipoQuesito.TEMATICA)) {
				DeTipoConsulenzaDTO tipoConsDTO = deTipoConsulenzaHome.findDTOById(ConstantsSingleton.DeTipoConsulenza.PARTITA_IVA);
				if (tipoConsDTO.getId().equalsIgnoreCase(richiesta.getTipoConsulenza().getId())) {
					if (richiesta.getFlagAssociazioneProfessionale() && richiesta.getAssociazioneProfessionale() == null) {
						addErrorMessage("atipici.assprof_obbligatoria");
						return;
					}
				}
			}
			msgMessaggioAtipicoHome.invioDomanda(richiesta, session.getPrincipalId());
			
			// Una volta inserita la richiesta, visualizzo un messaggio e resetto la pagina.
			addInfoMessage("message.sent");
			status = "list";
			paginationHandler.setCurrentPage(0);
			changePage();
			
			// Svuoto i campi "oggetto" e "corpo" per un'eventuale prossima richiesta.
			richiesta.setCorpo("");
			richiesta.setOggetto("");
		} catch (Exception e) {
			addErrorMessage("generic.error", e);
		}
	}

	/**
	 * Questo metodo inserisce dei valori di default nei campi della form "inserisci nuova richiesta".
	 */
	private void prevalorizzaCampi(MsgMessaggioAtipicoDTO nuovaPratica, PfPrincipalDTO principal,
	        AbstractUpdatablePkDTO info) {
		nuovaPratica.setNome(principal.getNome());
		nuovaPratica.setCognome(principal.getCognome());
		nuovaPratica.setEmail(principal.getEmail());

		DeTitoloDTO deTitoloDTO = msgMessaggioAtipicoHome.findLastTitolo(principal.getId());
		if (deTitoloDTO != null && deTitoloDTO.getId() != null) {
			nuovaPratica.setDeTitolo(deTitoloDTO);
		}
		if (info instanceof AziendaInfoDTO) {
			nuovaPratica.setCap(((AziendaInfoDTO) info).getSedeOperativa().getComune().getCap());
			String giornoNascita = ((AziendaInfoDTO) info).getCodiceFiscale().substring(9, 11);
			Sessocheck sesso = (Integer.valueOf(giornoNascita) > 40) ? Sessocheck.F : Sessocheck.M;
			nuovaPratica.setCodSesso(Integer.toString(sesso.ordinal() + 1));
			nuovaPratica.setFasciaEta(FasciaEta.getFascia(((AziendaInfoDTO) info).getDataNascitaRic()));
			nuovaPratica.setTelefonoFax(((AziendaInfoDTO) info).getTelefonoReferente());
			nuovaPratica.setDeComuneByCodComuneResidenza(((AziendaInfoDTO) info).getSedeOperativa().getComune());
		} else if (info instanceof UtenteInfoDTO) {
			UtenteInfoDTO utenteInfo = (UtenteInfoDTO) info;
			nuovaPratica.setCap(utenteInfo.getComuneDomicilio().getCap());
			if (utenteInfo.getCodiceFiscale() != null) {
				String giornoNascita = utenteInfo.getCodiceFiscale().substring(9, 11);
				Sessocheck sesso = (Integer.valueOf(giornoNascita) > 40) ? Sessocheck.F : Sessocheck.M;
				nuovaPratica.setCodSesso(Integer.toString(sesso.ordinal() + 1));
			}
			nuovaPratica.setFasciaEta(FasciaEta.getFascia(utenteInfo.getDataNascita()));
			nuovaPratica.setTelefonoFax(utenteInfo.getTelCasa());
			nuovaPratica.setDeComuneByCodComuneResidenza(((UtenteInfoDTO) info).getComuneDomicilio());

		}
	}

	/**
	 * Cambia il tipo di oggetti contenuti nelle liste a seconda della tab aperta in questo
	 * momento.
	 */
	public void changeTipoLavoro(ValueChangeEvent event) {
		if (event.getNewValue().equals("ATIPICO")) {
			tipiPratica = tipiPraticaAtipici;
			tipiContratto = tipiContrattoAtipici;
		} else {
			tipiPratica = tipiPraticaPIva;
			tipiContratto = tipiContrattoPIva;
		}
	}

	/**
	 * Il setter della proprietà "status" ha alcuni effetti collaterali.
	 */
	public void setStatus(String status) {
		String oldStatus = this.status;
		this.status = status;
		messaggio = new MsgMessaggioAtipicoDTO();
		detail = false;
		richiesta.setTipoConsulenza(deTipoConsulenzaHome.findDTOById("ATIPICO"));
		
		// Se sto uscendo dallo status "insert", resetto la lista delle pratiche.
		if (oldStatus.equals("insert") && status.equals("list")) {
			paginationHandler.setCurrentPage(0);
			changePage();
		}
	}

	/*
	 * ========= DA QUI IN POI CI SONO GETTER E SETTER ===========================================
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

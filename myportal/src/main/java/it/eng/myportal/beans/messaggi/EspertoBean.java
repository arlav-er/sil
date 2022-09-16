package it.eng.myportal.beans.messaggi;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import it.eng.myportal.beans.AbstractMasterDetailTabBean;
import it.eng.myportal.beans.SessionBean;
import it.eng.myportal.dtos.DeTemaConsulenzaDTO;
import it.eng.myportal.dtos.MsgEspertoDTO;
import it.eng.myportal.dtos.MsgMessaggioDTO;
import it.eng.myportal.entity.home.AbstractHome;
import it.eng.myportal.entity.home.MsgMessaggioHome;
import it.eng.myportal.entity.home.decodifiche.DeTemaConsulenzaHome;
import it.eng.myportal.rest.app.helper.StatoNotifica;
import it.eng.myportal.utils.ConstantsSingleton;
import it.eng.myportal.utils.ConstantsSingleton.MsgMessaggio;
import it.eng.myportal.utils.PaginationHandler;

/**
 * BackingBean della tab "Chidedi una consulenza" nella sezione Messaggi 
 * degli utenti CITTADINO e AZIENDA.
 *
 * @author Rodi A.
 *
 */
@ManagedBean
@ViewScoped
public class EspertoBean extends AbstractMasterDetailTabBean<MsgEspertoDTO> {

	private static final int ITEMS_PER_PAGE = 10;
	private static final String ESPERTO = "esperto";

	private PaginationHandler paginationHandler;
	private List<SelectItem> temi;

	@Deprecated
	private List<SelectItem> settori;
	
	//La risposta dell'esperto.
	//Campo utilizzato nella pagina della provincia per inserire la risposta.
	private MsgMessaggioDTO risposta;

	// Il ticket è ancora aperto...? TODO: Andrà nel ticket.
	private Boolean openTicket = true;
	
	@EJB
	MsgMessaggioHome msgMessaggioHome;

	@EJB
	DeTemaConsulenzaHome deTemaConsulenzaHome;

	/*
	 * UtenteMessagesBean e AziendaMessagesBean sono i bean 'master' in cui vengono salvate 
	 * le informazioni di sessione x la gestione del 'torna indietro'.
	 * NB: Injection manuale perche' controllano l'utente loggato e nel caso sia 'errato' 
	 * ridirezionano verso la home.
	 */
	UtenteMessagesBean utenteMessagesBean;
	AziendaMessagesBean aziendaMessagesBean;

	/**
	 * Questo metodo viene chiamato dopo la creazione del bean.
	 */
	@PostConstruct
	public void postConstruct() {
		super.postConstruct();
		
		// Popolo la lista dei temi, a seconda se l'utente è cittadino o azienda.
		ConstantsSingleton.DeTemaConsulenza.utente utente = null;
		if (session.isUtente()) {
			utente = ConstantsSingleton.DeTemaConsulenza.utente.CITTADINO;
			temi = deTemaConsulenzaHome.getListItems(true, utente, true /*valide*/);
		} else if (session.isAzienda()) {
			utente = ConstantsSingleton.DeTemaConsulenza.utente.AZIENDA;
			temi = deTemaConsulenzaHome.getListItems(true, utente, true /*valide*/);
		} else {
			temi = new ArrayList<SelectItem>();
		}
		
		// Inizializzo il bean.
		popolaMasterMsgBean();
		setTab2open(ESPERTO);
		settori = new ArrayList<SelectItem>();
		
		// Creo la lista paginata dei messaggi.
		try {
			resetPagination();
			data = buildNewDataIstance();
			risposta = new MsgMessaggioDTO();
		} catch (EJBException e) {
			addErrorMessage("data.error_loading", e);
		}
	}
	
	/**
	 * Questo metodo inizializza la paginazione. Carica il numero totale di messaggi e la prima pagina.
	 */
	private void resetPagination() {
		Long messaggiCount = msgMessaggioHome.findMessaggiEspertoCount(getSession().getPrincipalId());
		paginationHandler = new PaginationHandler(0, ITEMS_PER_PAGE, messaggiCount);
		paginationHandler.setCurrentPage(1);
		changePage();
	}
	
	/**
	 * Questo metodo viene chiamato quando l'utente cambia pagina.
	 */
	public void changePage() {
		dontedit();
		int startResultsFrom = paginationHandler.calculateStart(paginationHandler.getCurrentPage());
		list = msgMessaggioHome.findMessaggiEsperto
				(getSession().getPrincipalId(), startResultsFrom, paginationHandler.getChuckSize());
	}
	
	/**
	 * Questo metodo viene chiamato quando l'utente visualizza un ticket.
	 */
	@Override
	public void view() {
		super.view();
		try {
			// Se il messaggio non era mai stato letto, allora lo segno come "letto"
			// e segno come "lette" anche tutte le sue risposte.
			if (!data.getLetto()) {
				msgMessaggioHome.signAsRead(getSession().getPrincipalId(), data.getId(), true);
				for (MsgMessaggioDTO msg : data.getRisposte()) {
					if (!msg.getLetto())
						msgMessaggioHome.signAsRead(getSession().getPrincipalId(), msg.getId(), true);
				}
				data.setLetto(true);
			}
			// Preselezione del check Invio Notifica ad App
			risposta.setSendNotifyToApp(data.getLast().getIsMessageFromApp());

		} catch (EJBException e) {
			log.error("Errore durante la visualizzazione di un messaggio di supporto: " + e.getMessage());
		}
	}
	
	/**
	 * Metodo chiamato quando l'utente accede alla pagina tramite la voce "assistenza tecnica" del menu.
	 * Apre il pannello di inserimento nuova richiesta già settato al tipo "assistenza tecnica".
	 */
	public void richiediAssistenzaTecnica() {
		showPanel = true;
		editing = true;
		saved = false;
		
		String temaId = "6";
		DeTemaConsulenzaDTO deTemaConsulenzaDTO = new DeTemaConsulenzaDTO();
		deTemaConsulenzaDTO.setId(temaId);
		deTemaConsulenzaDTO.setDescrizione(deTemaConsulenzaHome.findDTOById(temaId).getDescrizione());
		
		data.setTemaConsulenza(deTemaConsulenzaDTO);
		data.setDtmIns(new Date());
		
		SessionBean sessionBean = getSession();
		sessionBean.setAssistenzaTecnica(false);
	}

	/**
	 * Salva una nuova richiesta all'esperto.
	 */
	@Override
	protected void saveData() {
		data.setCodTipoMessaggio(MsgMessaggio.ESPERTO);
		data.setDtmMod(data.getDtmIns());
		data.setIdFrom(getSession().getPrincipalId());
		data.setLetto(true); // l'ho scritto io...
		data = msgMessaggioHome.persistMsgEspertoDTO(data, getSession().getPrincipalId());
		resetPagination();
		return;
	}
	
	/**
	 * Invia una risposta.
	 * La risposta è l'ultimo messaggio del ticket aperto.
	 */
	public void send() {
		try {
			risposta.setCodTipoMessaggio(MsgMessaggio.ESPERTO);
			risposta.setIdMsgMessaggioPrecedente(data.getLast().getId());
			risposta.setIdTo(data.getIdFrom());	//invio il messaggio all'utente che ha fatto la richiesta
			risposta.setIdFrom(getSession().getPrincipalId());
			risposta.setLetto(true); // l'ho scritto io...
			risposta.setMittente("Io"); // non importa...
			risposta.setOggetto(data.getOggetto()); // sempre quello
			risposta.setTicket(data.getTicket());
			risposta = homePersist(msgMessaggioHome, risposta);
			data.getRisposte().add(risposta);
			
			addInfoMessage("message.sent");
			if (risposta.getIdNotifyToApp() != null) {
				if (risposta.getStatoNotifyToApp() == StatoNotifica.L) {
					// Non è stao possibile recapitare la notifica, si visualizza un warning ulteriore
					addWarnMessage("appnotifica.invio.logout");	
				}
			}
			
			risposta = new MsgMessaggioDTO();			
			dontedit();
		} catch (EJBException e) {
			addErrorMessage("data.error_saving", e);
		}
	}

	/**
	 * Questo metodo viene chiamato quando l'utente apre il pannello per inserire una nuova domanda.
	 */
	public void showInsertPanel() {
		super.showInsertPanel();
		data.setDtmIns(new Date());
	}
	
	/**
	 * carica il corretto bean in base all'utente loggato
	 */
	private void popolaMasterMsgBean() {
		FacesContext context = FacesContext.getCurrentInstance();
		if (session.isAzienda()) {
			setAziendaMessagesBean(context.getApplication().evaluateExpressionGet(context, "#{aziendaMessagesBean}", AziendaMessagesBean.class));
		} else if (session.isUtente()) {
			setUtenteMessagesBean(context.getApplication().evaluateExpressionGet(context, "#{utenteMessagesBean}", UtenteMessagesBean.class));
		}
	}

	/**
	 * @return l'id dell'elemento da aprire in caso di torna indietro
	 */
	public String getOpenElemId() {
		if (session.isAzienda()) {
			return getAziendaMessagesBean().getOpenElemId();
		} else if (session.isUtente()) {
			return getUtenteMessagesBean().getOpenElemId();
		}
		return null;
	}
	
	/**
	 * @param openElemId - salvo in sessione l'id dell'elemento aperto, cosi' da riaprirlo in caso di torna indietro
	 */
	public void setOpenElemId(String openElemId) {
		if (session.isAzienda()) {
			getAziendaMessagesBean().setOpenElemId(openElemId);
		} else if (session.isUtente()) {
			getUtenteMessagesBean().setOpenElemId(openElemId);
		}
	}

	/**
	 * @param tab2open - salvo in sessione la tab aperta, cosi' da riaprirla in caso di torna indietro
	 */
	public void setTab2open(String tab2open) {
		if (session.isAzienda()) {
			getAziendaMessagesBean().setTab2open(tab2open);
		} else if (session.isUtente()) {
			getUtenteMessagesBean().setTab2open(tab2open);
		}
	}
	
	/*
	 * ================= METODI DI OVERRIDE DELLA CLASSE ASTRATTA ========================
	 */
	
	@Override
	public MsgEspertoDTO buildNewDataIstance() {
		return new MsgEspertoDTO();
	}

	@Override
	protected List<MsgEspertoDTO> retrieveData() {
		return msgMessaggioHome.findMessaggiEsperto(getSession().getPrincipalId(), 0, 0);
	}

	@Override
	protected AbstractHome<?, MsgEspertoDTO, Integer> getHome() {
		throw new UnsupportedOperationException("Impossibile richiamare la Home");
	}

	public Boolean getOpenTicket() {
		return openTicket;
	}
	
	/*
	 * ====================== DA QUI IN POI, CI SONO SEMPLICI GETTER E SETTER =========================
	 */

	public void setOpenTicket(Boolean openTicket) {
		this.openTicket = openTicket;
	}

	public List<SelectItem> getTemi() {
		return temi;
	}

	public void setTemi(List<SelectItem> temi) {
		this.temi = temi;
	}

	public List<SelectItem> getSettori() {
		return settori;
	}

	public void setSettori(List<SelectItem> settori) {
		this.settori = settori;
	}

	public MsgMessaggioDTO getRisposta() {
		return risposta;
	}

	public void setRisposta(MsgMessaggioDTO risposta) {
		this.risposta = risposta;
	}

	public UtenteMessagesBean getUtenteMessagesBean() {
		return utenteMessagesBean;
	}

	public void setUtenteMessagesBean(UtenteMessagesBean utenteMessagesBean) {
		this.utenteMessagesBean = utenteMessagesBean;
	}

	public AziendaMessagesBean getAziendaMessagesBean() {
		return aziendaMessagesBean;
	}

	public void setAziendaMessagesBean(AziendaMessagesBean aziendaMessagesBean) {
		this.aziendaMessagesBean = aziendaMessagesBean;
	}
	
	public PaginationHandler getPaginationHandler() {
		return paginationHandler;
	}
}

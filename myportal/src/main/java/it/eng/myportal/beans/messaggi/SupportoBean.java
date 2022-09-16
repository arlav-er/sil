package it.eng.myportal.beans.messaggi;

import it.eng.myportal.beans.AbstractMasterDetailTabBean;
import it.eng.myportal.dtos.MsgMessaggioDTO;
import it.eng.myportal.dtos.MsgSupportoDTO;
import it.eng.myportal.entity.home.AbstractHome;
import it.eng.myportal.entity.home.MsgMessaggioHome;
import it.eng.myportal.entity.home.PfPrincipalHome;
import it.eng.myportal.entity.home.UtenteInfoHome;
import it.eng.myportal.utils.ConstantsSingleton.MsgMessaggio;
import it.eng.myportal.utils.PaginationHandler;

import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

/**
 * BackingBean della tab NOTIFICHE nella sezione Messaggi dell'Utente
 *
 * @author Rodi A.
 *
 */
@ManagedBean
@ViewScoped
public class SupportoBean extends AbstractMasterDetailTabBean<MsgSupportoDTO> {

	private static final int ITEMS_PER_PAGE = 10;
	private static final String SUPPORTO = "supporto";
	private PaginationHandler paginationHandler;
	private MsgMessaggioDTO risposta;

	@EJB
	MsgMessaggioHome msgMessaggioHome;

	@EJB
	UtenteInfoHome utenteInfoHome;

	@EJB
	PfPrincipalHome pfPrincipalHome;

	/**
	 * UtenteMessagesBean e AziendaMessagesBean sono i bean 'master' in cui vengono salvate le informazioni di sessione
	 * x la gestione del 'torna indietro'. NB: injection manuale perche' controllano l'utente loggato e nel caso sia
	 * 'errato' ridirezionano verso la home.
	 */
	UtenteMessagesBean utenteMessagesBean;
	AziendaMessagesBean aziendaMessagesBean;

	// Elenco di CV
	private List<SelectItem> myCurricula;

	// Elenco di Lettere di presentazione
	private List<SelectItem> myLettere;

	/**
	 * Questo metodo viene chiamato alla creazione del bean.
	 */
	@PostConstruct
	public void postConstruct() {
		super.postConstruct();
		risposta = new MsgMessaggioDTO();
		popolaMasterMsgBean();
		setTab2open(SUPPORTO);
		try {
			resetPagination();
			data = buildNewDataIstance();
			if (getSession().isUtente()) {
				Integer idUtente = getSession().getConnectedUtente().getId();
				myCurricula = utenteInfoHome.getAllCurriculaAsSelectItem(idUtente, false);
				myLettere = utenteInfoHome.getAllLettereAccAsSelectItem(idUtente, false);
			}
		} catch (EJBException e) {
			addErrorMessage("data.error_loading", e);
		}
	}

	/**
	 * Questo metodo inizializza la paginazione: trova il numero totale di messaggi da visualizzare e carica la prima
	 * pagina.
	 */
	private void resetPagination() {
		Long messaggiCount = msgMessaggioHome.findMessaggiSupportoCount(getSession().getPrincipalId());
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
		list = msgMessaggioHome.findMessaggiSupporto(getSession().getPrincipalId(), startResultsFrom, ITEMS_PER_PAGE);
	}

	@Override
	public MsgSupportoDTO buildNewDataIstance() {
		return new MsgSupportoDTO();
	}

	@Override
	protected List<MsgSupportoDTO> retrieveData() {
		return msgMessaggioHome.findMessaggiSupporto(getSession().getPrincipalId(), 0, 0);
	}

	/**
	 * Visualizza un ticket
	 */
	@Override
	public void view() {
		super.view();
		try {
			// Se il messaggio visualizzato non era ancora stato letto, allora
			// lo segno come "letto" e segno come "lette" anche tutte le risposte.
			if (!data.getLetto()) {
				msgMessaggioHome.signAsRead(getSession().getPrincipalId(), data.getId(), true);
				for (MsgMessaggioDTO msg : data.getRisposte()) {
					if (!msg.getLetto())
						msgMessaggioHome.signAsRead(getSession().getPrincipalId(), msg.getId(), true);
				}
				data.setLetto(true);
			}
		} catch (EJBException e) {
			log.error("Errore durante la visualizzazione di un messaggio di supporto: " + e.getMessage());
		}
	}

	/**
	 * Salva un nuovo ticket
	 */
	@Override
	protected void saveData() {
		data.setCodTipoMessaggio(MsgMessaggio.SUPPORTO);
		data.setDtmMod(data.getDtmIns());
		data.setIdFrom(getSession().getPrincipalId());
		data.setLetto(true); // l'ho scritto io...
		data = msgMessaggioHome.persistMsgSupportoDTO(data, getSession().getPrincipalId());
		// Enrico: salva male il messaggio, bisogna prenderlo 2 volte altrimenti non recupera l'allegato!
		data = msgMessaggioHome.findMsgSupportoByTicket(data.getTicket(), getSession().getPrincipalId());
		// Dato che c'è un nuovo messaggio, ricarico la paginazione.
		resetPagination();
	}

	@Override
	protected AbstractHome<?, MsgSupportoDTO, Integer> getHome() {
		throw new UnsupportedOperationException("Impossibile richiamare la Home");
	}

	public MsgMessaggioDTO getRisposta() {
		return risposta;
	}

	public void setRisposta(MsgMessaggioDTO risposta) {
		this.risposta = risposta;
	}

	/**
	 * Invia una risposta. La risposta è l'ultimo messaggio del ticket di supporto aperto.
	 */
	public void send() {
		risposta.setCodTipoMessaggio(MsgMessaggio.SUPPORTO);
		List<MsgMessaggioDTO> risps = data.getRisposte();

		// se chi invia è un cittadino
		if (getSession().isUtente()) {
			// allora il destinatario è la provincia legato al messaggio.
			risposta.setProvinciaTo(data.getProvinciaTo());
		}
		// se invece chi invia è un'azienda
		else {
			// allora il destinatario è l'utente da cui è partito il messaggio
			risposta.setIdTo(data.getIdFrom());
		}

		risposta.setIdMsgMessaggioPrecedente(data.getLast().getId());
		risposta.setIdFrom(getSession().getPrincipalId());
		risposta.setLetto(true); // l'ho scritto io...
		risposta.setMittente("Io"); // non importa...
		risposta.setOggetto(data.getOggetto()); // sempre quello
		risposta.setTicket(data.getTicket());
		risposta = homePersist(msgMessaggioHome, risposta);
		data.getRisposte().add(risposta);
		risposta = new MsgMessaggioDTO();
		addInfoMessage("message.sent");
		dontedit();
	}

	public void showInsertPanel() {
		super.showInsertPanel();
		data.setDtmIns(new Date());
	}

	public List<SelectItem> getMyCurricula() {
		return myCurricula;
	}

	public void setMyCurricula(List<SelectItem> myCurricula) {
		this.myCurricula = myCurricula;
	}

	public List<SelectItem> getMyLettere() {
		return myLettere;
	}

	public void setMyLettere(List<SelectItem> myLettere) {
		this.myLettere = myLettere;
	}

	/**
	 * carica il corretto bean in base all'utente loggato
	 */
	private void popolaMasterMsgBean() {
		FacesContext context = FacesContext.getCurrentInstance();
		if (session.isAzienda()) {
			setAziendaMessagesBean(context.getApplication().evaluateExpressionGet(context, "#{aziendaMessagesBean}",
					AziendaMessagesBean.class));
		} else if (session.isUtente()) {
			setUtenteMessagesBean(context.getApplication().evaluateExpressionGet(context, "#{utenteMessagesBean}",
					UtenteMessagesBean.class));
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
	 * @param openElemId
	 *            - salvo in sessione l'id dell'elemento aperto, cosi' da riaprirlo in caso di torna indietro
	 */
	public void setOpenElemId(String openElemId) {
		if (session.isAzienda()) {
			getAziendaMessagesBean().setOpenElemId(openElemId);
		} else if (session.isUtente()) {
			getUtenteMessagesBean().setOpenElemId(openElemId);
		}
	}

	/**
	 * @param tab2open
	 *            - salvo in sessione la tab aperta, cosi' da riaprirla in caso di torna indietro
	 */
	public void setTab2open(String tab2open) {
		if (session.isAzienda()) {
			getAziendaMessagesBean().setTab2open(tab2open);
		} else if (session.isUtente()) {
			getUtenteMessagesBean().setTab2open(tab2open);
		}
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

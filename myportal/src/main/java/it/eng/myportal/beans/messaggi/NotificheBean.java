package it.eng.myportal.beans.messaggi;

import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import org.apache.commons.lang3.StringUtils;

import it.eng.myportal.beans.AbstractMasterDetailTabBean;
import it.eng.myportal.dtos.MsgMessaggioDTO;
import it.eng.myportal.entity.home.AbstractHome;
import it.eng.myportal.entity.home.MsgMessaggioHome;
import it.eng.myportal.utils.PaginationHandler;

/**
 * BackingBean della tab NOTIFICHE. Viene usato nella sezione Notifiche dell'Utente, 
 * nella sezione Notifiche dell'Azienda e nella sezione Notifiche ricevute della Provincia
 *
 * @author Rodi A.
 */

@ManagedBean
@ViewScoped
public class NotificheBean extends AbstractMasterDetailTabBean<MsgMessaggioDTO> {

	private static final String NOTIFICHE = "notifiche";
	private static final int ITEMS_PER_PAGE = 10;
	private PaginationHandler paginationHandler;

	@EJB
	MsgMessaggioHome msgMessaggioHome;

	/**
	 * UtenteMessagesBean e AziendaMessagesBean sono i bean 'master' in cui vengono salvate le 
	 * informazioni di sessione x la gestione del 'torna indietro'.
	 * NB: injection manuale perche' controllano l'utente loggato e nel caso sia 'errato' 
	 * ridirezionano verso la home.
	 */
	UtenteMessagesBean utenteMessagesBean;
	AziendaMessagesBean aziendaMessagesBean;
	
	/**
	 * Questo metodo viene chiamato subito dopo la creazione del bean.
	 */
	@Override
	@PostConstruct
	protected void postConstruct() {
		super.postConstruct();
		
		// Setto le impostazioni della tab.
		popolaMasterMsgBean();
		setTab2open(NOTIFICHE);
		
		// Carico la lista delle notifiche ricevute.
		try {
			resetPagination();
			if (StringUtils.isBlank(getOpenElemId()) && !isRedoBySess()) {
				data = buildNewDataIstance();
			} else {
				for (MsgMessaggioDTO element : list) {
					if (element.getId() != null
							&& StringUtils.equals(element.getId().toString(),
									getOpenElemId())) {
						data = element;
						showPanel = true;
						editing = false;
						saved = true;
						break;
					}
				}
			}
		} catch (EJBException e) {
			addErrorMessage("data.error_loading", e);
			redirectHome();
		}
	}

	/**
	 * Questo metodo inizializza la paginazione. Conta il numero di notifiche e carica la prima pagina.
	 */
	private void resetPagination() {
		Long notificheCount = msgMessaggioHome.findNotificheRicevuteCount(getSession().getPrincipalId());
		paginationHandler = new PaginationHandler(0, ITEMS_PER_PAGE, notificheCount);
		list = msgMessaggioHome.findNotificheRicevute
				(getSession().getPrincipalId(), 0, ITEMS_PER_PAGE);
	}
	
	/**
	 * Questo metodo viene chiamato quando l'utente cambia pagina.
	 */
	public void changePage() {
		int startResultsFrom = paginationHandler.calculateStart(paginationHandler.getCurrentPage());
		list = msgMessaggioHome.findNotificheRicevute
				(getSession().getPrincipalId(), startResultsFrom, ITEMS_PER_PAGE);	
		showPanel = false;
	}

	/**
	 * Questo metodo viene chiamato quando l'utente apre una notifica per vederne i dettagli.
	 */
	@Override
	public void view() {
		super.view();
		try {
			Map<String, String> map = getRequestParameterMap();
			setOpenElemId(map.get("id"));
			// Se il messaggio non era mai stato visualizzato, lo setto come "letto".
			if (!data.getLetto()) {
				msgMessaggioHome.signAsRead(getSession().getPrincipalId(), data.getId(), true);
				data.setLetto(true);
			}
		} catch (EJBException e) {
			log.error("Errore durante la visualizzazione di un messaggio di notifica: " + e.getMessage());
		}
	}
	
	/**
	 * Carica il super-bean giusto in base all'utente loggato.
	 */
	private void popolaMasterMsgBean() {
		FacesContext context = FacesContext.getCurrentInstance();
		if (session.isAzienda()) {
			setAziendaMessagesBean(context.getApplication().
					evaluateExpressionGet(context, "#{aziendaMessagesBean}", AziendaMessagesBean.class));
		} else if (session.isUtente()) {
			setUtenteMessagesBean(context.getApplication().
					evaluateExpressionGet(context, "#{utenteMessagesBean}", UtenteMessagesBean.class));
		}
	}

	/**
	 * Restituisce l'id dell'elemento da aprire in caso di torna indietro.
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
	 * @param openElemId - salvo in sessione l'id dell'elemento aperto, cosi'  da riaprirlo in caso di torna indietro
	 */
	public void setOpenElemId(String openElemId) {
		if (session.isAzienda()) {
			getAziendaMessagesBean().setOpenElemId(openElemId);
		} else if (session.isUtente()) {
			getUtenteMessagesBean().setOpenElemId(openElemId);
		}
	}

	/**
	 * @param tab2open - salvo in sessione la tab aperta, cosi'  da riaprirla in caso di torna indietro
	 */
	public void setTab2open(String tab2open) {
		if (session.isAzienda()) {
			getAziendaMessagesBean().setTab2open(tab2open);
		} else if (session.isUtente()) {
			getUtenteMessagesBean().setTab2open(tab2open);
		}
	}
	
	/*
	 * ============= METODI OVERRIDE DELLA CLASSE ASTRATTA ==========
	 */
	@Override
	public MsgMessaggioDTO buildNewDataIstance() {
		return new MsgMessaggioDTO();
	}

	@Override
	protected AbstractHome<?, MsgMessaggioDTO, Integer> getHome() {
		return msgMessaggioHome;
	}

	@Override
	protected List<MsgMessaggioDTO> retrieveData() {
		return msgMessaggioHome.findNotificheRicevute(getSession().getPrincipalId(), 0 ,0);
	}

	@Override
	protected void saveData() {
		throw new UnsupportedOperationException("Impossibile salvare una notifica");
	}
	
	/*
	 * ================ DA QUI IN POI, GETTER E SETTER SEMPLICI ====================
	 */

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

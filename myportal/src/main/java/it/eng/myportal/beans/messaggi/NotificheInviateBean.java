package it.eng.myportal.beans.messaggi;

import it.eng.myportal.beans.AbstractMasterDetailTabBean;
import it.eng.myportal.dtos.DeProvinciaDTO;
import it.eng.myportal.dtos.MsgMessaggioDTO;
import it.eng.myportal.entity.home.AbstractHome;
import it.eng.myportal.entity.home.MsgMessaggioHome;
import it.eng.myportal.entity.home.decodifiche.DeProvinciaHome;
import it.eng.myportal.utils.ConstantsSingleton.MsgMessaggio;
import it.eng.myportal.utils.PaginationHandler;

import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;

import org.apache.commons.lang3.time.DateUtils;

/**
 * BackingBean della tab "notifiche inviate" della sezione messaggi per gli utenti PROVINCIA.
 * 
 * @author Rodi A.
 * 
 */
@ManagedBean
@ViewScoped
public class NotificheInviateBean extends AbstractMasterDetailTabBean<MsgMessaggioDTO> {
	private static final int ITEMS_PER_PAGE = 10;
	private static final String BROADCAST = "_BROADCAST_";

	private List<SelectItem> destinatari;
	private PaginationHandler paginationHandler;

	@EJB
	MsgMessaggioHome msgMessaggioHome;

	@EJB
	DeProvinciaHome deProvinciaHome;

	/**
	 * Questo messaggio viene chiamato alla creazione del bean.
	 */
	@Override
	@PostConstruct
	protected void postConstruct() {
		super.postConstruct();

		try {
			resetPagination();
			data = buildNewDataIstance();
			destinatari = deProvinciaHome.getListItemsDestinatariNotifica();
			destinatari.add(0, new SelectItem(BROADCAST, "Broadcast", "Broadcast"));
		} catch (EJBException e) {
			addErrorMessage("data.error_loading", e);
			redirectHome();
		}
	}

	/**
	 * Questo metodo inizializza la paginazione: trova il numero totale di messaggi e visualizza la prima pagina.
	 */
	private void resetPagination() {
		Long notificheCount = msgMessaggioHome.findNotificheInviateCount(getSession().getPrincipalId());
		paginationHandler = new PaginationHandler(0, ITEMS_PER_PAGE, notificheCount);
		list = msgMessaggioHome.findNotificheInviate(getSession().getPrincipalId(), 0, ITEMS_PER_PAGE);
	}

	/**
	 * Questo metodo viene chiamato quando l'utente cambia pagina.
	 */
	public void changePage() {
		dontedit();
		int startResultsFrom = paginationHandler.calculateStart(paginationHandler.getCurrentPage());
		list = msgMessaggioHome.findNotificheRicevute(getSession().getPrincipalId(), startResultsFrom, ITEMS_PER_PAGE);
	}

	/**
	 * Metodo chiamato quando l'utente visualizza i dettagli di una notifica.
	 */
	@Override
	public void view() {
		super.view();
		// Se il messaggio non era mai stato letto, ora viene segnato come "letto".
		try {
			if (!data.getLetto()) {
				msgMessaggioHome.signAsRead(getSession().getPrincipalId(), data.getId(), true);
				data.setLetto(true);
			}
		} catch (EJBException e) {
			log.error("Errore durante la visualizzazione di un messaggio di notifica: " + e.getMessage());
		}
	}

	/**
	 * Questo metodo viene chiamato quando l'utente invia una nuova notifica. Salva sul database la notifica inviata.
	 */
	@Override
	protected void saveData() {
		// setta la descrizione del destinatario, altrimenti non visualizzata nella
		// tabella dopo la pressione del pulsante salva (non si rileggono tutti i dati da database)
		String codProvinciaTo = data.getProvinciaTo().getId();
		DeProvinciaDTO deProvinciaDTO = deProvinciaHome.findDTOById(codProvinciaTo);
		if (deProvinciaDTO != null && deProvinciaDTO.getDescrizione() != null) {
			data.getProvinciaTo().setDescrizione(deProvinciaDTO.getDescrizione());
			data.setDestinatario(deProvinciaDTO.getDescrizione());
		}

		data.setCodTipoMessaggio(MsgMessaggio.NOTIFICA);
		data.setDtmMod(data.getDtmIns());
		data.setScadenza(DateUtils.addDays(data.getDtmIns(), 30));
		data.setIdFrom(getSession().getPrincipalId());
		data.setLetto(true); // l'ho scritto io...
		if (BROADCAST.equals(data.getProvinciaTo().getId())) {
			data.getProvinciaTo().setId(null); // se il gruppo non è impostato allora è in broadcast
		}
		MsgMessaggioDTO persistedMsgMessaggioDTO = homePersist(msgMessaggioHome, data);
		data.setId(persistedMsgMessaggioDTO.getId());
	}

	/**
	 * Metodo chiamato quando l'utente apre il pannello di creazione nuova notifica.
	 */
	public void showInsertPanel() {
		super.showInsertPanel();
		data.setDtmIns(new Date());
	}

	@Override
	public MsgMessaggioDTO buildNewDataIstance() {
		return new MsgMessaggioDTO();
	}

	@Override
	protected List<MsgMessaggioDTO> retrieveData() {
		return msgMessaggioHome.findNotificheInviate(getSession().getPrincipalId());
	}

	@Override
	public void save() {
		super.save();
		resetPagination();
	}

	@Override
	protected AbstractHome<?, MsgMessaggioDTO, Integer> getHome() {
		return msgMessaggioHome;
	}

	public List<SelectItem> getDestinatari() {
		return destinatari;
	}

	public void setDestinatari(List<SelectItem> destinatari) {
		this.destinatari = destinatari;
	}

	public PaginationHandler getPaginationHandler() {
		return paginationHandler;
	}
}

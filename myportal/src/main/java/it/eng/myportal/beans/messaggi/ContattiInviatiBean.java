package it.eng.myportal.beans.messaggi;

import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import it.eng.myportal.dtos.MsgContattoDTO;
import it.eng.myportal.dtos.MsgMessaggioDTO;
import it.eng.myportal.entity.home.AbstractHome;
import it.eng.myportal.entity.home.MsgMessaggioHome;
import it.eng.myportal.exception.MyPortalException;
import it.eng.myportal.utils.ConstantsSingleton;
import it.eng.myportal.utils.PaginationHandler;

/**
 * BackingBean della tab CONTATTI nella sezione Messaggi dell'Utente
 *
 * @author Rodi A.
 *
 */
@ManagedBean
@ViewScoped
public class ContattiInviatiBean extends AbstractContattiBean {

	@EJB
	MsgMessaggioHome msgMessaggioHome;

	private static final int ITEMS_PER_PAGE = 10;

	/**
	 * UtenteMessagesBean e AziendaMessagesBean sono i bean 'master' in cui
	 * vengono salvate le informazioni di sessione per la gestione del 'torna
	 * indietro'<br>
	 * NB injection manuale perche' controllano l'utente loggato e nel caso sia
	 * 'errato' ridirezionano verso la home
	 */
	ContattiBean contattiBean;

	/**
	 * Inizializza la paginazione: cerca il numero di messaggi e carica la prima pagina.
	 */
	protected void resetPagination() {
		Long messaggiCount = msgMessaggioHome.findMessaggiContattiInviatiCount(getSession().getPrincipalId());
		paginationHandler = new PaginationHandler(0, ITEMS_PER_PAGE, messaggiCount);
		paginationHandler.setCurrentPage(1);
		changePage();
	}

	/**
	 * Metodo chiamato quando l'utente cambia pagina.
	 */
	public void changePage() {
		dontedit();
		int start = paginationHandler.calculateStart(paginationHandler.getCurrentPage());
		list = msgMessaggioHome.findMessaggiContattiInviati
				(getSession().getPrincipalId(), start, paginationHandler.getChuckSize());
	}

	@Override
	public String componiBaseLinkRiferimentoMessaggio() {
		String refLink = "/" + ConstantsSingleton.CONTESTO_APP;
		if (getSession().isUtente()) {
			// offerta di lavoro
			if(ConstantsSingleton.COD_REGIONE == ConstantsSingleton.COD_REGIONE_RER){
				refLink =refLink + ConstantsSingleton.LINK_VIEW_CONTATTI_VARER;

			}else{
				refLink = refLink + ConstantsSingleton.LINK_VIEW_CONTATTI_VAOTHER;
			}

			// cv visualizzato dall'azienda
		} else {

			if(ConstantsSingleton.COD_REGIONE == ConstantsSingleton.COD_REGIONE_RER){
				refLink =refLink + ConstantsSingleton.LINK_VIEW_CONTATTI_CVRER;

			}else{
				refLink = refLink + ConstantsSingleton.LINK_VIEW_CONTATTI_OTHER;
			}
		}
		return refLink;
	}








	@Override
	public String getTestoLinkRiferimentoMessaggio() {
		String testo = "";
		if (getSession().isUtente()) {
			// offerta di lavoro
			testo = "Visualizza l'offerta di lavoro";
		} else {
			// cv visualizzato dall'azienda
			testo = "Visualizza il CV";
		}
		return testo;
	}

	@Override
	public MsgContattoDTO buildNewDataIstance() {
		return new MsgContattoDTO();
	}

	@Override
	protected List<MsgContattoDTO> retrieveData() {
		return msgMessaggioHome.findMessaggiContattiInviati(getSession().getPrincipalId(), 0, 0);
	}

	/**
	 * Visualizza un ticket
	 */
	@Override
	public void view() {
		super.view();
		try {
			// Se il messaggio non era stato letto, lo segno come letto e segno
			// anche tutte le risposte.
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

	@Override
	protected AbstractHome<?, MsgContattoDTO, Integer> getHome() {
		throw new UnsupportedOperationException("Impossibile richiamare la Home");
	}

	public void showInsertPanel() {
		super.showInsertPanel();
		data.setDtmIns(new Date());
	}

	@Override
	protected void saveData() {
		throw new MyPortalException("generic.error");
	}

}

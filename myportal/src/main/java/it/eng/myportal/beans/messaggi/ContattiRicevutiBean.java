package it.eng.myportal.beans.messaggi;

import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import it.eng.myportal.dtos.MsgContattoDTO;
import it.eng.myportal.dtos.MsgMessaggioDTO;
import it.eng.myportal.entity.ejb.ClicLavoroCandidaturaEjb;
import it.eng.myportal.entity.ejb.ClicLavoroPrimoContattoEjb;
import it.eng.myportal.entity.home.AbstractHome;
import it.eng.myportal.entity.home.MsgMessaggioHome;
import it.eng.myportal.exception.MyPortalException;
import it.eng.myportal.utils.ConstantsSingleton;
import it.eng.myportal.utils.PaginationHandler;

/**
 * BackingBean della tab CONTATTI RICEVUTI nella sezione Messaggi dell'Utente o
 * dell'azienda
 * 
 * @author Rodi A.
 * 
 */
@ManagedBean
@ViewScoped
public class ContattiRicevutiBean extends AbstractContattiBean {

	private static final int ITEMS_PER_PAGE = 10;
	
	@EJB
	MsgMessaggioHome msgMessaggioHome;

	@EJB
	ClicLavoroPrimoContattoEjb clicLavoroPrimoContattoEjb;

	@EJB
	ClicLavoroCandidaturaEjb clicLavoroCandidaturaEjb;

	private String linkVacancyRiferimentoMessaggio;

	/**
	 * UtenteMessagesBean e AziendaMessagesBean sono i bean 'master' in cui
	 * vengono salvate le informazioni di sessione per la gestione del 'torna
	 * indietro'<br>
	 * NB injection manuale perche' controllano l'utente loggato e nel caso sia
	 * 'errato' ridirezionano verso la home
	 */
	ContattiBean contattiBean;

	/**
	 * Questo metodo viene chiamato alla creazione del bean.
	 */
	@PostConstruct
	public void postConstruct() {
		super.postConstruct();
		// in caso di cittadino o azienda il checkbox dell'esito della risposta non deve essere prevalorizzato
		// rimane obbligatorio con il controllo lato xhtml
		if (getRisposta()!= null && (getSession().isUtente() || getSession().isAzienda())) {
			getRisposta().setEsito(null);
		}
	}
	
	/**
	 * Questo metodo inizializza la paginazione: trova il numero totale di messaggi e carica la prima pagina.
	 */
	protected void resetPagination() {
		Long messaggiCount = msgMessaggioHome.findMessaggiContattiRicevutiCount(getSession().getPrincipalId());
		paginationHandler = new PaginationHandler(0, ITEMS_PER_PAGE, messaggiCount);
		paginationHandler.setCurrentPage(1);
		changePage();
	}
	
	/**
	 * Questo metodo viene chiamato quando l'utente cambia pagina.
	 */
	public void changePage() {
		dontedit();
		int start = paginationHandler.calculateStart(paginationHandler.getCurrentPage());
		list = msgMessaggioHome.findMessaggiContattiRicevuti
				(getSession().getPrincipalId(), start, paginationHandler.getChuckSize());
	}
	
	/**
	 * Visualizza un ticket.
	 */
	@Override
	public void view() {
		super.view();
		try {
			// Se il messaggio non era segnato come letto, lo segno come letto e segno
			// anche tutte le sue risposte.
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
	 * Invia una risposta. La risposta è l'ultimo messaggio del ticket aperto.
	 */
	public void send() {
		try {
			getRisposta().setDtmIns(new Date());
			getRisposta().setIdFrom(getSession().getPrincipalId());
			getRisposta().setIdMsgMessaggioPrecedente(data.getLast().getId());
			if (getSession().isUtente()) {
				clicLavoroPrimoContattoEjb.inviaRispostaPrimoContatto(getRisposta(), data.getId());
			} else {
				clicLavoroCandidaturaEjb.inviaRispostaCandidatura(getRisposta(), data.getId());
			}
			data.getRisposte().add(getRisposta());
			inizializzaNuovaRisposta();
			addInfoMessage("message.sent");
			dontedit();
		} catch (EJBException ex) {
			gestisciErrore(ex, "generic.error");
		}
	}

	/**
	 * Metodo chiamato all'apertura del pannello di inserimento nuovo messaggio.
	 */
	public void showInsertPanel() {
		super.showInsertPanel();
		data.setDtmIns(new Date());
	}
	
	@Override
	public String componiBaseLinkRiferimentoMessaggio() {

		String refLink = "/" + ConstantsSingleton.CONTESTO_APP;
		if(ConstantsSingleton.COD_REGIONE == ConstantsSingleton.COD_REGIONE_RER){
			refLink =refLink + ConstantsSingleton.LINK_VIEW_CONTATTI_CVRER;
			setLinkVacancyRiferimentoMessaggio("/" + ConstantsSingleton.CONTESTO_APP + "/faces/secure/azienda/vacancies/view_pf.xhtml");

		}else{
			setLinkVacancyRiferimentoMessaggio("/" + ConstantsSingleton.CONTESTO_APP + "/faces/secure/azienda/vacancies/visualizza.xhtml");
			refLink = refLink + ConstantsSingleton.LINK_VIEW_CONTATTI_OTHER;
		}

		//setLinkVacancyRiferimentoMessaggio("/" + ConstantsSingleton.CONTESTO_APP + "/faces/secure/azienda/vacancies/visualizza.xhtml");
		return refLink;
	}

	@Override
	public String getTestoLinkRiferimentoMessaggio() {
		String testo = "";
		if (getSession().isUtente()) {
			// offerta di lavoro
			testo = "Visualizza il CV a cui l'azienda è interessata";
		} else {
			// cv visualizzato dall'azienda
			testo = "Visualizza il CV";
		}
		return testo;

	}

	public String getTestoLinkVacancyRiferimentoMessaggio() {
		return "Visualizza l'offerta di lavoro";
	}

	@Override
	public MsgContattoDTO buildNewDataIstance() {
		return new MsgContattoDTO();
	}

	@Override
	protected List<MsgContattoDTO> retrieveData() {
		return msgMessaggioHome.findMessaggiContattiRicevuti(getSession().getPrincipalId(), 0, 0);
	}
	
	@Override
	protected AbstractHome<?, MsgContattoDTO, Integer> getHome() {
		throw new UnsupportedOperationException("Impossibile richiamare la Home");
	}

	@Override
	protected void saveData() {
		throw new MyPortalException("generic.error");
	}

	public String getLinkVacancyRiferimentoMessaggio() {
		return linkVacancyRiferimentoMessaggio;
	}

	public void setLinkVacancyRiferimentoMessaggio(String linkVacancyRiferimentoMessaggio) {
		this.linkVacancyRiferimentoMessaggio = linkVacancyRiferimentoMessaggio;
	}

}

package it.eng.myportal.beans.atipici;

import it.eng.myportal.beans.AbstractBaseBean;
import it.eng.myportal.dtos.MsgMessaggioAtipicoDTO;
import it.eng.myportal.dtos.MsgMessaggioAtipicoMiniDTO;
import it.eng.myportal.dtos.MsgMessaggioDTO;
import it.eng.myportal.entity.home.MsgMessaggioAtipicoHome;
import it.eng.myportal.entity.home.MsgMessaggioHome;
import it.eng.myportal.utils.ConstantsSingleton;
import it.eng.myportal.utils.PaginationHandler;

import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * BackingBean della pagina di gestione Pratiche Atipici del consulente
 * 
 * @author Rodi A.
 * 
 */
@ManagedBean
@ViewScoped
public class AtipiciConsulenteBean extends AbstractBaseBean {
	protected static Log log = LogFactory.getLog(AtipiciConsulenteBean.class);

	@EJB
	MsgMessaggioHome msgMessaggioHome;

	@EJB
	private MsgMessaggioAtipicoHome msgMessaggioAtipicoHome;

	/**
	 * Lista delle pratiche visualizzate
	 */
	private static final int ITEMS_PER_PAGE = 10;
	private PaginationHandler paginationHandler;
	private List<MsgMessaggioAtipicoMiniDTO> pratiche;
	private MsgMessaggioAtipicoDTO risposta;
	private MsgMessaggioAtipicoDTO pratica;
	
	//Determina se si sta mostrando o meno il pannello per l'inserimento dei dati.
	protected boolean showPanel = false;
	private Integer nuoviMsg = 0;

	/**
	 * Questo metodo viene chiamato alla creazione del bean.
	 */
	@PostConstruct
	public void postConstruct() {
		super.postConstruct();
		risposta = new MsgMessaggioAtipicoDTO();
		
		// Inizializzo la lista delle pratiche.
		resetPagination();
		
		// TODO: Questa parte serve...? Conto il numero di messaggi nuovi.
		//for (MsgMessaggioAtipicoMiniDTO pratica : pratiche) {
		//	if (pratica.getDaLeggere()) {
		//		nuoviMsg++;
		//	} 			
		//}
	}

	/**
	 * Metodo da chiamare se cambia il numero totale di pratiche disponibili.
	 * Inizializza la paginazione e carica la prima pagina di risultati.
	 */
	private void resetPagination() {
		// Recupero il numero totale di pratiche ed inizializzo il PaginationHandler.
		Long praticheCount = msgMessaggioAtipicoHome.findPraticheAtipiciCount
				(session.getConnectedConsulente().getId());
		paginationHandler = new PaginationHandler(0, ITEMS_PER_PAGE, praticheCount);
		
		// Carico la prima pagina di risultati.
		pratiche = msgMessaggioAtipicoHome.findPraticheAtipici
				(session.getConnectedConsulente().getId(), 0, ITEMS_PER_PAGE);
		showPanel = false;
	}

	/**
	 * Questo metodo viene chiamato quando l'utente clicca per cambiare la pagina visualizzata.
	 */
	public void changePage() {
		int startResultsFrom = paginationHandler.calculateStart(paginationHandler.getCurrentPage());
		pratiche = msgMessaggioAtipicoHome.findPraticheAtipici
				(session.getConnectedConsulente().getId(), startResultsFrom, ITEMS_PER_PAGE);	
		showPanel = false;
	}
	
	/**
	 * Questo metodo viene chiamato quando l'utente clicca su un elemento nella lista per visualizzare
	 * i dettagli.
	 */
	public void view() {
		// Recupero l'id della pratica da visualizzare.
		Map<String, String> map = getRequestParameterMap();
		String id = map.get("id");
		
		// Recupero la pratica, setto la pagina e preparo una risposta vuota.
		pratica = msgMessaggioAtipicoHome.findDTOById(Integer.valueOf(id));
		risposta = new MsgMessaggioAtipicoDTO();
		showPanel = true;
		
		// Segno il messaggio come già letto, sia nel database che nella lista della pagina.
		if (pratica.getInoltriDaCPIversoConsulente() != null) {
			for (MsgMessaggioDTO inoltro : pratica.getInoltriDaCPIversoConsulente()) {
				msgMessaggioHome.signAsRead(getSession().getPrincipalId(), inoltro.getId(), true);
			}
		}

		for (MsgMessaggioAtipicoMiniDTO m : pratiche) {
			if (m.getId() == pratica.getId().intValue()) {
				m.setDaLeggere(false);
				break;
			}
		}
	}

	/**
	 * Questo metodo viene chiamato quando l'utente conferma di voler rispondere al CPI.
	 */
	public void rispondiAlCPI() {
		
		String corpo = risposta.getCorpo();
		if (corpo == null || corpo.isEmpty()) {
			/* Se il corpo del messaggio e' vuoto stampo un messaggio di errore */			
			addErrorMessage("atipici.consulente.risposta_obbligatoria");			
			return;
		} else {			
			risposta.setId(pratica.getId());
			risposta.setIdPrincipalIns(getSession().getPrincipalId());
			risposta.setIdPrincipalMod(getSession().getPrincipalId());
			risposta.setCodTipoMessaggio(ConstantsSingleton.TipoMessaggio.ATIPICI);
			risposta.setOggetto(pratica.getOggetto());
			msgMessaggioAtipicoHome.rispondiAlCPI(risposta, true, getSession().getPrincipalId());
			addInfoMessage("data.created");
			resetPagination();
		}
	}

	/**
	 * Questo metodo viene chiamato quando l'utente conferma di non voler rispondere al CPI.
	 */
	public void rifiutaAlCPI() {
		
		String note = risposta.getNote();
		if (note == null || note.isEmpty()) {
			/* Se il corpo del messaggio e' vuoto stampo un messaggio di errore */			
			addErrorMessage("atipici.consulente.note_obbligatoria");			
			return;
		}
		else {	
			risposta.setId(pratica.getId());
			risposta.setIdPrincipalIns(getSession().getPrincipalId());
			risposta.setIdPrincipalMod(getSession().getPrincipalId());
			risposta.setCodTipoMessaggio(ConstantsSingleton.TipoMessaggio.ATIPICI);
			risposta.setOggetto(pratica.getOggetto());
			risposta.setCorpo(" ");
			msgMessaggioAtipicoHome.rispondiAlCPI(risposta, false, getSession().getPrincipalId());
			addInfoMessage("data.created");
			resetPagination();
		}
	}

	/*
	 * ================== DA QUI IN POI CI SONO GETTER E SETTER SEMPLICI ============================
	 */
	
	public List<MsgMessaggioAtipicoMiniDTO> getPratiche() {
		return pratiche;
	}

	public void setPratiche(List<MsgMessaggioAtipicoMiniDTO> pratiche) {
		this.pratiche = pratiche;
	}

	public MsgMessaggioAtipicoDTO getRisposta() {
		return risposta;
	}

	public void setRisposta(MsgMessaggioAtipicoDTO risposta) {
		this.risposta = risposta;
	}

	public MsgMessaggioAtipicoDTO getPratica() {
		return pratica;
	}

	public void setPratica(MsgMessaggioAtipicoDTO pratica) {
		this.pratica = pratica;
	}

	public boolean isShowPanel() {
		return showPanel;
	}

	public void setShowPanel(boolean showPanel) {
		this.showPanel = showPanel;
	}

	public void dontedit() {
		showPanel = false;
	}

	public Integer getNuoviMsg() {
		return nuoviMsg;
	}

	public void setNuoviMsg(Integer nuoviMsg) {
		this.nuoviMsg = nuoviMsg;
	}
	
	public PaginationHandler getPaginationHandler() {
		return paginationHandler;
	}

}

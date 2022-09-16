package it.eng.myportal.beans.atipici;

import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import it.eng.myportal.beans.AbstractBaseBean;
import it.eng.myportal.dtos.AziendaInfoDTO;
import it.eng.myportal.dtos.DeComuneDTO;
import it.eng.myportal.dtos.MsgMessaggioAtipicoDTO;
import it.eng.myportal.dtos.MsgMessaggioAtipicoMiniDTO;
import it.eng.myportal.dtos.MsgMessaggioDTO;
import it.eng.myportal.dtos.UtenteInfoDTO;
import it.eng.myportal.entity.home.AziendaInfoHome;
import it.eng.myportal.entity.home.MsgMessaggioAtipicoHome;
import it.eng.myportal.entity.home.MsgMessaggioHome;
import it.eng.myportal.entity.home.PfPrincipalHome;
import it.eng.myportal.entity.home.UtenteInfoHome;
import it.eng.myportal.entity.home.decodifiche.DeComuneHome;
import it.eng.myportal.utils.ConstantsSingleton;
import it.eng.myportal.utils.PaginationHandler;

@ManagedBean
@ViewScoped
public class AtipiciProvinciaBean extends AbstractBaseBean {
	protected static Log log = LogFactory.getLog(AtipiciProvinciaBean.class);

	@EJB
	private MsgMessaggioHome msgMessaggioHome;

	@EJB
	private MsgMessaggioAtipicoHome msgMessaggioAtipicoHome;

	@EJB
	private PfPrincipalHome pfPrincipalHome;

	@EJB
	private AziendaInfoHome aziendaInfoHome;

	@EJB
	private UtenteInfoHome utenteInfoHome;

	@EJB
	private DeComuneHome deComuneHome;

	private static final int ITEMS_PER_PAGE = 10;
	private PaginationHandler paginationHandler;
	private List<MsgMessaggioAtipicoMiniDTO> pratiche;
	private MsgMessaggioAtipicoDTO pratica;
	private MsgMessaggioAtipicoDTO risposta;
	private String tipoQuesito;
	protected boolean showPanel;
	
	// Indica se e' possibile inoltrare la pratica in dettaglio ad un consulente.
	private boolean puoInoltrare;

	/**
	 * Questo metodo viene chiamato al momento della costruzione del bean.
	 */
	@PostConstruct
	public void postConstruct() {
		super.postConstruct();
		
		// Recupero il tipo di questito che mi serve.
		Map<String, String> map = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
		tipoQuesito = map.get("tipo_quesito");
		
		// Inizializzo la lista di pratiche.
		resetPagination();
	}
	
	/**
	 * Questo metodo resetta la paginazione della lista delle pratiche. 
	 */
	private void resetPagination() {
		// Trovo il numero di pratiche ed inizializzo il PaginationHandler.
		Long praticheCount = msgMessaggioAtipicoHome.findPraticheAtipiciCount
				(session.getConnectedProvincia().getIdPfPrincipal(), tipoQuesito);
		paginationHandler = new PaginationHandler(0, ITEMS_PER_PAGE, praticheCount);
		
		// Carico la prima pagina di risultati.
		pratiche = msgMessaggioAtipicoHome.findPraticheAtipici
				(session.getConnectedProvincia().getIdPfPrincipal(), tipoQuesito, 0, ITEMS_PER_PAGE);
		showPanel = false;
	}
	
	/**
	 * Questo metodo viene chiamato quando l'utente cambia pagina.
	 */
	public void changePage() {
		int startResultsFrom = paginationHandler.calculateStart(paginationHandler.getCurrentPage());
		pratiche = msgMessaggioAtipicoHome.findPraticheAtipici
				(session.getConnectedProvincia().getIdPfPrincipal(), tipoQuesito, startResultsFrom, ITEMS_PER_PAGE);
		showPanel = false;
	}
	
	/**
	 * Questo metodo viene chiamato quando l'utente clicca su una pratica per visualizzare il dettaglio.
	 */
	public void view() {
		// Recupero l'id della pratica da visualizzare.
		Map<String, String> map = getRequestParameterMap();
		String id = map.get("id");
		
		// Recupero la pratica dal database ed inizializzo una risposta.
		pratica = msgMessaggioAtipicoHome.findDTOById(Integer.valueOf(id));
		risposta = new MsgMessaggioAtipicoDTO();
		showPanel = true;

		// Controllo se posso inoltrare la pratica ad un consulente.
		Integer idPfPrincipalFrom = pratica.getIdFrom();
		if (pfPrincipalHome.isAzienda(idPfPrincipalFrom)) {
			AziendaInfoDTO aziendaInfoDTO = aziendaInfoHome.findDTOById(idPfPrincipalFrom);
			DeComuneDTO comuneSedeOperativa = aziendaInfoDTO.getSedeOperativa().getComune();
			/*
			 * se si tratta di un'azienda allora la sede operativa deve stare in
			 * regione Emilia-Romagna
			 */
			puoInoltrare = deComuneHome.comuneDomicilioInRER(comuneSedeOperativa.getDescrizione(),
			        comuneSedeOperativa.getId());
		} else if (pfPrincipalHome.isUtente(idPfPrincipalFrom)) {
			DeComuneDTO comuneDomicilio = pratica.getDeComuneByCodComuneResidenza();
			DeComuneDTO comuneLavoro = pratica.getDeComuneByCodComuneLavoro();
			boolean comuneDomicilioInRER = false;
			boolean comuneLavoroInRER = false;

			if (comuneDomicilio.getId() != null && !comuneDomicilio.getId().isEmpty()) {
				comuneDomicilioInRER = deComuneHome.comuneDomicilioInRER(comuneDomicilio.getDescrizione(),
				        comuneDomicilio.getId());
			}
			if (comuneLavoro.getId() != null && !comuneLavoro.getId().isEmpty()) {
				comuneLavoroInRER = deComuneHome.comuneDomicilioInRER(comuneLavoro.getDescrizione(),
				        comuneLavoro.getId());
			}

			/*
			 * se si tratta di un cittadino allora il domicilio (form della
			 * pratica) OPPURE la sede di lavoro (form della pratica) deve stare
			 * in regione Emilia-Romagna
			 */
			puoInoltrare = comuneDomicilioInRER || comuneLavoroInRER;

			/*
			 * se nella form non ho inserito ne il domicilio ne la sede di
			 * lavoro, allora guardo al domicilio inserito in fase di
			 * registrazione.
			 */
			if ((comuneDomicilio.getId() == null || comuneDomicilio.getId().isEmpty())
			        && (comuneLavoro.getId() == null || comuneLavoro.getId().isEmpty())) {
				UtenteInfoDTO utenteInfoDTO = utenteInfoHome.findDTOById(idPfPrincipalFrom);
				DeComuneDTO comuneDomicilioRegistrazione = utenteInfoDTO.getComuneDomicilio();

				puoInoltrare = deComuneHome.comuneDomicilioInRER(comuneDomicilioRegistrazione.getDescrizione(),
				        comuneDomicilioRegistrazione.getId());
			}
		}

		// Segno come letto sempre l'ultimo messaggio inoltrato da coordinatore a CPI.
		if (pratica.getInoltriDaCoordinatoreVersoCPI() != null) {
			for (MsgMessaggioDTO inoltro : pratica.getInoltriDaCoordinatoreVersoCPI()) {
				msgMessaggioHome.signAsRead(getSession().getPrincipalId(), inoltro.getId(), true);
			}
		}
		
		if (pratica.getInoltriDaCPIversoConsulente() != null) {
			for (MsgMessaggioDTO inoltri : pratica.getInoltriDaCPIversoConsulente()) {
				if (inoltri.getRisposte() != null && inoltri.getRisposte().size() > 0) {
					MsgMessaggioDTO risp = inoltri.getRisposte().get(0);

					msgMessaggioHome.signAsRead(getSession().getPrincipalId(), risp.getId(), true);

				}
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
	 * Questa funzione viene chiamata se l'utente invia una risposta ad un cittadino.
	 */
	public void rispondiCittadino() {
		// Se il corpo del messaggio e' vuoto stampo un messaggio di errore.
		String corpo = risposta.getCorpo();
		if (corpo == null || corpo.isEmpty()) {
			if (ConstantsSingleton.TipoQuesito.TEMATICA.equals(pratica.getDeTipoQuesito().getId())) {
				addErrorMessage("atipici.provincia.corpo_obbligatorio");
			} else {
				addErrorMessage("atipici.provincia.risposta_obbligatoria");
			}
			return;
		}

		// Creo la risposta e la invio al cittadino in questione.
		risposta.setId(pratica.getId());
		risposta.setOggetto(pratica.getOggetto());
		risposta.setDeTipoPratica(pratica.getDeTipoPratica());
		MsgMessaggioAtipicoDTO rispostaAlCoordinatore = msgMessaggioAtipicoHome.rispondiAlCittadino(risposta, getSession().getPrincipalId());
		
		
		//Creo la risposta e la invio al coordinatore che mi ha inoltrato il messaggio
		MsgMessaggioDTO messaggio = pratica.getInoltroDaCoordinatoreVersoCPI();
		Integer idCoordinatore = messaggio.getIdFrom();
		rispostaAlCoordinatore.setId(pratica.getId());
		rispostaAlCoordinatore.setOggetto(pratica.getOggetto());
		rispostaAlCoordinatore.setDeTipoPratica(pratica.getDeTipoPratica());
		msgMessaggioAtipicoHome.inviaCopiaAlCoordinatore(idCoordinatore, rispostaAlCoordinatore, getSession().getPrincipalId());
		addInfoMessage("data.created");
				
		// Ricarico la lista delle pratiche.
		resetPagination();
	}

	/**
	 * Questa funzione viene chiamata se l'utente assegna una pratica ad un consulente.
	 */
	public void assignConsulente() {
		// In questo caso il mio commento è facoltativo: se è vuoto, inserisco un singolo spazio.
		// (Serve per bypassare il vincolo NOT NULL sul database).
		String corpo = risposta.getCorpo();
		if (corpo == null || corpo.isEmpty()) {
			risposta.setCorpo(" ");
		}
		
		// Inoltro il messaggio al consulente.
		MsgMessaggioDTO messRicevuto = pratica.getInoltriDaCoordinatoreVersoCPI().iterator().next();
		msgMessaggioAtipicoHome.inoltraConsulente(risposta, messRicevuto.getId(), getSession().getPrincipalId(),
		        pratica.getDeTipoPratica().getId(), false);
		addInfoMessage("data.created");
		
		// Ricarico la lista delle pratiche.
		resetPagination();
	}

	/**
	 * Questa funzione viene chiamata quando l'utente inoltra ad un cittadino la risposta che ha
	 * ricevuto da un consulente.
	 */
	public void inoltraCittadino() {
		// In questo caso il mio commento è facoltativo: se è vuoto, inserisco un singolo spazio.
		// (Serve per bypassare il vincolo NOT NULL sul database).
		String corpo = risposta.getCorpo();
		if (corpo == null || corpo.isEmpty()) {
			risposta.setCorpo(" ");
		}
		
		// Inoltro il messaggio al cittadino.
		msgMessaggioAtipicoHome.inoltraRispostaAlCittadino(Integer.valueOf(pratica.getTicket()), getSession()
		        .getPrincipalId(), risposta);
		addInfoMessage("data.created");
		
		// Ricarico la lista delle pratiche.
		resetPagination();
	}

	/**
	 * Questa funzione viene chiamata se l'utente rifiuta la pratica e la rimanda al coordinatore.
	 */
	public void rifiutaPratica() {
		// Se il corpo del messaggio e' vuoto stampo un messaggio di errore.
		String corpo = risposta.getCorpo();
		if (corpo == null || corpo.isEmpty()) {
			addErrorMessage("atipici.provincia.corpo_obbligatorio");
			return;
		}
		
		// Invio il messaggio di rifiuto al cittadino.
		msgMessaggioAtipicoHome.rifiutaPratica(risposta, Integer.valueOf(pratica.getTicket()), getSession()
		        .getPrincipalId());
		addInfoMessage("data.created");
		
		// Ricarico la lista delle pratiche.
		resetPagination();
	}

	/**
	 * Questa funzione viene chiamata se rifiuto una risposta del consulente.
	 */
	public void rifiutaRisposta() {		
		// In questo caso il mio commento è facoltativo: se è vuoto, inserisco un singolo spazio.
		// (Serve per bypassare il vincolo NOT NULL sul database).		
		String corpo = risposta.getCorpo();
		if (corpo == null || corpo.isEmpty()) {
			risposta.setCorpo(" ");
		}
		
		// Rispondo al consulente con il mio rifiuto?
		MsgMessaggioDTO messRicevuto = pratica.getInoltriDaCoordinatoreVersoCPI().iterator().next();
		msgMessaggioAtipicoHome.inoltraConsulente(risposta, messRicevuto.getId(), getSession().getPrincipalId(),
		        pratica.getDeTipoPratica().getId(), true);
		addInfoMessage("data.created");
		
		// Ricarico la lista delle pratiche.
		resetPagination();
	}
	
	/**
	 * Restituisce true se la provincia ha risposto al cittadino o ha inoltrato
	 * al consulente.
	 */
	public boolean getHasRisposto() {
		MsgMessaggioAtipicoDTO pratica = this.pratica;
		List<MsgMessaggioDTO> list = pratica.getInoltriDaCoordinatoreVersoCPI();
		// scorri la lista degli inotlri effettuati dal CPI ai consulenti
		for (MsgMessaggioDTO msgMessaggioDTO : list) {
			// ho rispsoto se uno dei messaggi inviati dal cpi ai consulenti
			// contiene una risposta da parte mia

			if (msgMessaggioDTO.getInoltri() == null && msgMessaggioDTO.getRisposte() == null)
				return false;
			if (msgMessaggioDTO.getInoltri().isEmpty() && msgMessaggioDTO.getRisposte().isEmpty())
				return false;

			if (msgMessaggioDTO.getInoltri() != null
			        && !msgMessaggioDTO.getInoltri().isEmpty()
			        && msgMessaggioDTO.getInoltri().get(0).getIdFrom().intValue() == getSession().getPrincipalId()
			                .intValue())
				return true;

			if (msgMessaggioDTO.getRisposte() != null
			        && !msgMessaggioDTO.getRisposte().isEmpty()
			        && msgMessaggioDTO.getRisposte().get(0).getIdFrom().intValue() == getSession().getPrincipalId()
			                .intValue())
				return true;
		}
		return false;
	}
	
	/**
	 * Restituisce TRUE se la pratica attualmente visualizzata è in stato "Consulente rifiuta la richiesta".
	 */
	public boolean isRispondiAlCittadino() {
		return this.pratica.getDeStatoPratica().getId().equals(ConstantsSingleton.DeStatoPratica.CON_RIF);
	}

	/**
	 * Restituisce TRUE se la pratica attualmente visualizzata è in stato "Inoltrata al CPI".
	 */
	public boolean isRifiutaPratica() {
		return this.pratica.getDeStatoPratica().getId().equals(ConstantsSingleton.DeStatoPratica.INOL_CPI);
	}

	/**
	 * Restituisce TRUE se la pratica attualmente visualizzata è in stato "Rifiutata dal CPI al consulente".
	 */
	public boolean isRifiutaPraticaCPI() {
		return this.pratica.getDeStatoPratica().getId().equals(ConstantsSingleton.DeStatoPratica.CPI_RIF);
	}
	
	/**
	 * Restituisce TRUE se la pratica attualmente visualizata è in stato "Inoltrata dal coordinatore al CPI"
	 * oppure in stato "Il consulente rifiuta la pratica".
	 */
	public boolean isRifiutaRisposta() {
		String statoPratica = this.pratica.getDeStatoPratica().getId();
		if (ConstantsSingleton.DeStatoPratica.INOL_CPI.equalsIgnoreCase(statoPratica)) {
			return true;
		} else if (ConstantsSingleton.DeStatoPratica.CON_RIF.equalsIgnoreCase(statoPratica)) {
			return true;
		} else {
			return false;
		}
	}

	/*
	 * ========================= DA QUI IN POI CI SONO GETTER E SETTER SEMPLICI ================================
	 */
	public MsgMessaggioAtipicoDTO getRisposta() {
		return risposta;
	}

	public void setRisposta(MsgMessaggioAtipicoDTO risposta) {
		this.risposta = risposta;
	}

	public MsgMessaggioAtipicoDTO getPratica() {
		return pratica;
	}

	public List<MsgMessaggioAtipicoMiniDTO> getPratiche() {
		return pratiche;
	}

	public void setPratiche(List<MsgMessaggioAtipicoMiniDTO> pratiche) {
		this.pratiche = pratiche;
	}

	public boolean isShowPanel() {
		return showPanel;
	}

	public void setShowPanel(boolean showPanel) {
		this.showPanel = showPanel;
	}

	public String getTipoQuesito() {
		return tipoQuesito;
	}

	public void setTipoQuesito(String tipoQuesito) {
		this.tipoQuesito = tipoQuesito;
	}

	public boolean isPuoInoltrare() {
		return puoInoltrare;
	}

	public void setPuoInoltrare(boolean puoInoltrare) {
		this.puoInoltrare = puoInoltrare;
	}

	public boolean isTematica() {
		return session.getConnectedProvincia().getFlagTematica();
	}
	
	public boolean isLavoro() {
		return session.getConnectedProvincia().getFlagLavoro();
	}
	
	public PaginationHandler getPaginationHandler() {
		return paginationHandler;
	}
}

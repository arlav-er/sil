package it.eng.myportal.beans.atipici;

import it.eng.myportal.beans.AbstractBaseBean;
import it.eng.myportal.dtos.AbstractUpdatablePkDTO;
import it.eng.myportal.dtos.AziendaInfoDTO;
import it.eng.myportal.dtos.DeProvinciaDTO;
import it.eng.myportal.dtos.MsgMessaggioAtipicoDTO;
import it.eng.myportal.dtos.MsgMessaggioAtipicoMiniDTO;
import it.eng.myportal.dtos.UtenteInfoDTO;
import it.eng.myportal.entity.home.MsgMessaggioAtipicoHome;
import it.eng.myportal.entity.home.MsgMessaggioHome;
import it.eng.myportal.entity.home.PfPrincipalHome;
import it.eng.myportal.entity.home.decodifiche.DeProvinciaHome;
import it.eng.myportal.utils.ConstantsSingleton;
import it.eng.myportal.utils.PaginationHandler;

import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


@ManagedBean
@ViewScoped
public class AtipiciCoordinatoreBean extends AbstractBaseBean {
	protected static Log log = LogFactory.getLog(AtipiciCoordinatoreBean.class);
	private static final int RESULTS_FOR_PAGE = 10;
	
	private List<MsgMessaggioAtipicoMiniDTO> pratiche;
	private String	state;
	private boolean rejected;
	PaginationHandler paginationHandler;
	
	@EJB
	private MsgMessaggioHome msgMessaggioHome;
	
	@EJB
	private PfPrincipalHome pfPrincipalHome;
	
	@EJB
	private DeProvinciaHome deProvinciaHome;
	
	@EJB
	private MsgMessaggioAtipicoHome msgMessaggioAtipicoHome; 
	private MsgMessaggioAtipicoDTO pratica;
	private MsgMessaggioAtipicoDTO risposta;
	private String tipoQuesito;

	/**
	 * Questo metodo viene chiamato durante la costruzione del bean; fa automaticamente una 
	 * ricerca dei messaggi da visualizzare.
	 */
	@PostConstruct
	public void postConstruct() {
		super.postConstruct();				
		
		// Recupero il tipo di quesito che mi serve.
		Map<String, String> map = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
		tipoQuesito = map.get("tipo_quesito");
		
		// Inizializzo la paginazione.
		Long praticheCount = msgMessaggioAtipicoHome.findPraticheAtipiciCount
				(session.getConnectedCoordinatore().getId(), tipoQuesito);
		paginationHandler = new PaginationHandler(0, RESULTS_FOR_PAGE, praticheCount);
		
		// Carico la prima pagina di risultati.
		pratiche = msgMessaggioAtipicoHome.findPraticheAtipici
				(session.getConnectedCoordinatore().getId(), tipoQuesito, 0, RESULTS_FOR_PAGE);		
		risposta = new MsgMessaggioAtipicoDTO();
		state = "list";
	}
	
	/**
	 * Questo metodo viene chiamato quando l'utente cambia pagina.
	 */
	public void changePage() {
		state = "list";
		int startResultsFrom = paginationHandler.calculateStart(paginationHandler.getCurrentPage());
		pratiche = msgMessaggioAtipicoHome.findPraticheAtipici
				(session.getConnectedCoordinatore().getId(), tipoQuesito, startResultsFrom, RESULTS_FOR_PAGE);	
	}
	
	/**
	 * Questo metodo viene chiamato quando l'utente clicca su una delle pratiche per visualizzarla.
	 */
	public void view(Integer praticaId) {
		// Metto la pagina in modalità "view" sulla pratica in questione.
		pratica = msgMessaggioAtipicoHome.findDTOById(praticaId);
		state = "view";
		
		// Preparo la risposta alla pratica, a seconda del tipo di utente che sta usando la pagina.
		AbstractUpdatablePkDTO infoDto = pfPrincipalHome.getUserInfo(pratica.getIdFrom());
		DeProvinciaDTO provincia = null;
		if (infoDto instanceof AziendaInfoDTO) {
			provincia = ((AziendaInfoDTO) infoDto).getProvinciaRiferimento();
			if (provincia == null || provincia.getId() == null) {
				provincia = deProvinciaHome.findDTOById(((AziendaInfoDTO) infoDto).getSedeOperativa().getComune().getIdProvincia());
			}
		} else if (infoDto instanceof UtenteInfoDTO) {
			provincia = ((UtenteInfoDTO) infoDto).getProvinciaRiferimento();
		}
		risposta.setDeProvincia(provincia);
		
		// Se e' una nuova richiesta segno come letto il primo messaggio
		if (pratica.getInoltri() == null || pratica.getInoltri().isEmpty()) {
			msgMessaggioHome.signAsRead(getSession().getPrincipalId(), praticaId, true);
		}
		// Se invece e' una risposta del CPI devo segnare come letta la risposta dell'ultimo inoltro
		else {
			if (!pratica.getDeStatoPratica().getId().equals(ConstantsSingleton.DeStatoPratica.INOL_CPI)) {
				msgMessaggioHome.signAsRead(getSession().getPrincipalId(), pratica.getInoltri().get(pratica.getInoltri().size()-1).getRisposte().get(0).getId(), true);
			}
		}
		
		// Segno il messaggio come letto nella pagina.
		for (MsgMessaggioAtipicoMiniDTO m : pratiche) {
			if (m.getId() == pratica.getId().intValue()) {
				m.setDaLeggere(false);
				break;
			}
		}
	}

	/**
	 * Questo metodo viene chiamato quando il coordinatore rifiuta ed archivia una pratica.
	 */
	public void archive() {
		msgMessaggioAtipicoHome.archivia(risposta,pratica.getId(), 
				risposta.getCorpo() != null && risposta.getCorpo().length() != 0, 
				getSession().getPrincipalId());		
		addInfoMessage("message.sent");
		risposta.setMotivoRifiuto("");
		risposta.setCorpo("");
		rejected = false;
		changePage();
	}

	/**
	 * Questo metodo viene chiamato quando il coordinatore assegna una pratica ad un CPI.
	 */
	public void assignCPI() {
		try {
			msgMessaggioAtipicoHome.inoltraCPI(risposta, pratica.getId(), getSession().getPrincipalId());
		} catch (Exception e) {
			log.info("Errore");
		}		
		addInfoMessage("message.sent");
		changePage();
	}

	public boolean isEdit() {
		return (pratica.getDeStatoPratica().getId().equals(ConstantsSingleton.DeStatoPratica.NUOVA) 
				|| pratica.getDeStatoPratica().getId().equals(ConstantsSingleton.DeStatoPratica.CPI_RIF));
	}
	
	/*
	 * ==== DA QUI IN POI CI SONO GETTER E SETTER ====
	 */
	
	public MsgMessaggioAtipicoDTO getRisposta() {
		return risposta;
	}

	public void setRisposta(MsgMessaggioAtipicoDTO risposta) {
		this.risposta = risposta;
	}
	
	public boolean isRejected() {
		return rejected;
	}

	public void setRejected(boolean approval) {
		this.rejected = approval;
	}
		
	public String getState() {
		return state;
	}
	
	public MsgMessaggioAtipicoDTO getPratica() {
		return pratica;
	}
	
	public void setState(String state) {
		this.state = state;
	}

	public List<MsgMessaggioAtipicoMiniDTO> getPratiche() {
		return pratiche;
	}

	public void setPratiche(List<MsgMessaggioAtipicoMiniDTO> pratiche) {
		this.pratiche = pratiche;
	}

	public PaginationHandler getPaginationHandler() {
		return paginationHandler;
	}
	
}

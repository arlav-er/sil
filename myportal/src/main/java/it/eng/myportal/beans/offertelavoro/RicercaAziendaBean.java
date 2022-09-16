package it.eng.myportal.beans.offertelavoro;

import it.eng.myportal.beans.AbstractBaseBean;
import it.eng.myportal.beans.RestoreParameters;
import it.eng.myportal.dtos.AziendaMiniDTO;
import it.eng.myportal.entity.home.AziendaInfoHome;
import it.eng.myportal.entity.home.UtenteInfoHome;
import it.eng.myportal.entity.home.WsEndpointHome;
import it.eng.myportal.utils.ConstantsSingleton;
import it.eng.myportal.utils.PaginationHandler;
import it.eng.sil.base.business.GestioneProfiloRemoteClient;
import it.eng.sil.base.pojo.auth.gp.DestinatarioPOJO;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * BackingBean della pagina che permette di cercare tra le Aziende
 * 
 * @author Girotti S.
 * 
 */
@ManagedBean
@ViewScoped
public class RicercaAziendaBean extends AbstractBaseBean implements Serializable {
	private static final long serialVersionUID = 1L;

	private static final String NOME_TKN = "nome";
	private static final String DOVE_COD_TKN = "doveCod";
	private static final String DOVE_TKN = "dove";

	// Logger per registrare informazioni.
	private static Log log = LogFactory.getLog(RicercaAziendaBean.class);

	@EJB
	private UtenteInfoHome utenteInfoHome;

	@EJB
	private AziendaInfoHome aziendaInfoHome;

	@EJB
	private WsEndpointHome wsEndpointHome;

	// Parametri della ricerca.
	private String nome;
	private String dove;
	private String doveCod;
	private String searchType = "C";
	private String entiAccreditati = "TUTTI"; // Valori: 'SI', 'NO', 'TUTTI'
	private int maxResults;
	private int startResultsFrom;

	// Risultato della ricerca.
	private PaginationHandler paginationHandler;
	private List<AziendaMiniDTO> risultato;
	private Boolean ricercaEseguita = false;

	// Lista degli id di tutti gli enti accreditati.
	private List<Integer> idEntiAccreditati = null;

	/**
	 * Recupera l'elenco delle Vacancy e dei Template dal DB ed inizializza il bean.
	 */
	@Override
	@PostConstruct
	public void postConstruct() {
		super.postConstruct();
		if (risultato == null)
			risultato = new ArrayList<AziendaMiniDTO>();

		maxResults = 10;
		startResultsFrom = 0;

		// Chiedo ad Accreditamento la lista di tutti gli enti accreditati, e salvo tutti gli id.
		if (showEntiAccreditatiParam()) {
			try {
				idEntiAccreditati = new ArrayList<Integer>();
				GestioneProfiloRemoteClient gpRemoteClient = new GestioneProfiloRemoteClient();
				List<DestinatarioPOJO> attributeOwners = gpRemoteClient.getAttributeOwners(
						ConstantsSingleton.getAttributeOwnersUrl(), ConstantsSingleton.GpAttributo.A_ENTE_ACCREDITATO,
						session.getIdGruppoAziendaDefault());
				for (DestinatarioPOJO attributeOwner : attributeOwners) {
					idEntiAccreditati.add(attributeOwner.getIdPfPrincipal());
				}
			} catch (Exception e) {
				log.error("Errore durante la chiamata a findEntiAccreditati: " + e.toString());
			}
		}

		log.debug("Costruito il Bean per visualizzare l'elenco delle Vacancy secondo i criteri di ricerca.");
	}

	/**
	 * Carica dei parametri di ricerca salvati in precedenza. (Serve se l'utente è tornato alla pagina tramite il tasto
	 * BACK).
	 */
	@Override
	public void ricreaStatoDaSessione(RestoreParameters restoreParameters) {
		super.ricreaStatoDaSessione(restoreParameters);
		dove = ObjectUtils.toString(restoreParameters.get(DOVE_TKN));
		doveCod = ObjectUtils.toString(restoreParameters.get(DOVE_COD_TKN));
		nome = ObjectUtils.toString(restoreParameters.get(NOME_TKN));
		search();
	};

	/**
	 * Prepara i parametri di ricerca da salvare. (Serviranno se l'utente tornerà alla pagina tramite il tasto BACK").
	 */
	@Override
	protected RestoreParameters generateRestoreParams() {
		RestoreParameters restore = super.generateRestoreParams();
		restore.put(DOVE_TKN, dove);
		restore.put(DOVE_COD_TKN, doveCod);
		restore.put(NOME_TKN, nome);
		return restore;
	}

	/**
	 * Questo metodo viene chiamato quando l'utente preme il tasto "cerca". Trova il numero totale dei risultati,
	 * inizializza la paginazione e carica la prima pagina del risultato.
	 */
	public void search() {
		// Salvo i parametri di ricerca, per poter tornare indietro in futuro.
		putParamsIntoSession();

		Boolean entiAccreditatiCondition = null;
		if ("SI".equals(entiAccreditati)) {
			entiAccreditatiCondition = true;
		} else if ("NO".equals(entiAccreditati)) {
			entiAccreditatiCondition = false;
		}

		// Se il codice comune/provincia è a null, va bene tutto.
		if (doveCod == null) {
			dove = "";
		}

		// Creo l'handler per la paginazione.
		maxResults = 10;
		startResultsFrom = 0;
		Long numeroRisultati = aziendaInfoHome.findCountByFilter(nome, dove, doveCod, idEntiAccreditati,
				entiAccreditatiCondition, searchType.equals("P"));
		paginationHandler = new PaginationHandler(startResultsFrom, maxResults, numeroRisultati);

		// Carico la prima pagina del risultato.
		risultato = aziendaInfoHome.findMiniDTOByFilter(nome, dove, doveCod, idEntiAccreditati,
				entiAccreditatiCondition, searchType.equals("P"), maxResults, startResultsFrom);
		ricercaEseguita = true;
	}

	/**
	 * Questo metodo viene chiamato quando l'utente cambia pagina. Carica i risultati della pagina richiesta.
	 */
	public void changePage() {
		Boolean entiAccreditatiCondition = null;
		if ("SI".equals(entiAccreditati)) {
			entiAccreditatiCondition = true;
		} else if ("NO".equals(entiAccreditati)) {
			entiAccreditatiCondition = false;
		}

		startResultsFrom = paginationHandler.calculateStart(paginationHandler.getCurrentPage());
		risultato = aziendaInfoHome.findMiniDTOByFilter(nome, dove, doveCod, idEntiAccreditati,
				entiAccreditatiCondition, searchType.equals("P"), maxResults, startResultsFrom);
	}

	// Attualmente solo l'Emilia-Romagna ha l'accreditamento, e quindi visualizza questo parametro.
	public boolean showEntiAccreditatiParam() {
		return ConstantsSingleton.COD_REGIONE.equals(ConstantsSingleton.COD_REGIONE_RER);
	}

	/*
	 * DA QUI IN POI: GETTER E SETTER.
	 */
	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		if (nome != null) {
			nome = nome.trim();
		}
		this.nome = nome;
	}

	public String getDove() {
		return dove;
	}

	public void setDove(String dove) {
		if (dove != null) {
			dove = dove.trim();
		}
		this.dove = dove;
	}

	public String getSearchType() {
		return searchType;
	}

	public void setSearchType(String searchType) {
		this.searchType = searchType;
	}

	public String getDoveCod() {
		return doveCod;
	}

	public void setDoveCod(String doveCod) {
		this.doveCod = doveCod;
	}

	public List<AziendaMiniDTO> getRisultato() {
		return risultato;
	}

	public PaginationHandler getPaginationHandler() {
		return this.paginationHandler;
	}

	public boolean isRicercaEseguita() {
		return this.ricercaEseguita;
	}

	public List<Integer> getIdEntiAccreditati() {
		return idEntiAccreditati;
	}

	public void setIdEntiAccreditati(List<Integer> idEntiAccreditati) {
		this.idEntiAccreditati = idEntiAccreditati;
	}

	public String getEntiAccreditati() {
		return entiAccreditati;
	}

	public void setEntiAccreditati(String entiAccreditati) {
		this.entiAccreditati = entiAccreditati;
	}
}
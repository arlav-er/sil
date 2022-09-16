package it.eng.myportal.beans;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.security.SecurityContextAssociation;

import it.eng.myportal.auth.AuthUtil;
import it.eng.myportal.dtos.AziendaInfoDTO;
import it.eng.myportal.dtos.AziendaSessionDTO;
import it.eng.myportal.dtos.CertificatoreInfoDTO;
import it.eng.myportal.dtos.ConsulenteInfoDTO;
import it.eng.myportal.dtos.CoordinatoreInfoDTO;
import it.eng.myportal.dtos.CvLetteraAccDTO;
import it.eng.myportal.dtos.CvVisualizzaDTO;
import it.eng.myportal.dtos.MsgMessaggioDTO;
import it.eng.myportal.dtos.PatronatoDTO;
import it.eng.myportal.dtos.PfPrincipalDTO;
import it.eng.myportal.dtos.ProvinciaDTO;
import it.eng.myportal.dtos.RegioneDTO;
import it.eng.myportal.dtos.SoggettoPubblicoDTO;
import it.eng.myportal.dtos.UtenteDTO;
import it.eng.myportal.dtos.VaDatiVacancyDTO;
import it.eng.myportal.entity.ejb.ErrorsSingleton;
import it.eng.myportal.entity.enums.TipoAbilitazione;
import it.eng.myportal.entity.home.AziendaInfoHome;
import it.eng.myportal.entity.home.CertificatoreInfoHome;
import it.eng.myportal.entity.home.ConsulenteInfoHome;
import it.eng.myportal.entity.home.CoordinatoreInfoHome;
import it.eng.myportal.entity.home.MsgMessaggioHome;
import it.eng.myportal.entity.home.PatronatoHome;
import it.eng.myportal.entity.home.PfPrincipalHome;
import it.eng.myportal.entity.home.ProvinciaHome;
import it.eng.myportal.entity.home.RegioneHome;
import it.eng.myportal.entity.home.SoggettoPubblicoHome;
import it.eng.myportal.entity.home.UtenteInfoHome;
import it.eng.myportal.entity.home.WsEndpointHome;
import it.eng.myportal.entity.home.decodifiche.DeRuoloPortaleHome;
import it.eng.myportal.utils.ConstantsSingleton;
import it.eng.myportal.utils.OracleObfuscation;
import it.eng.myportal.utils.Utils;
import it.eng.sil.base.business.GestioneProfiloRemoteClient;
import it.eng.sil.base.enums.DeSistemaEnum;
import it.eng.sil.base.exceptions.ProfilaturaException;
import it.eng.sil.base.pojo.auth.gp.DestinatarioPOJO;
import it.eng.sil.base.pojo.auth.gp.GpGruppoPOJO;
import it.eng.sil.base.pojo.auth.gp.GpRuoloGruppoPOJO;
import it.eng.sil.base.pojo.auth.gp.GpRuoloPOJO;
import it.eng.sil.base.pojo.auth.gp.UserProfiloPOJO;

/**
 * 
 * ManagedBean JSF per contenere i dati dell'utente che ha effettuato l'accesso al sistema. Il Bean viene creato una
 * sola volta per ogni sessione e ricava tutte le informazioni utili sull'utente connesso.
 * 
 * @author Rodi A.
 * 
 */
@ManagedBean
@SessionScoped
public class SessionBean {
	protected static Log log = LogFactory.getLog(SessionBean.class);

	@EJB
	protected ErrorsSingleton errorsBean;

	@EJB
	private UtenteInfoHome utenteInfoHome;

	@EJB
	private ProvinciaHome provinciaHome;

	@EJB
	private RegioneHome regioneHome;

	@EJB
	private MsgMessaggioHome msgMessaggioHome;

	@EJB
	private WsEndpointHome wsEndpointHome;

	@EJB
	private AziendaInfoHome aziendaInfoHome;

	@EJB
	private CoordinatoreInfoHome coordinatoreInfoHome;

	@EJB
	private CertificatoreInfoHome certificatoreInfoHome;

	@EJB
	private SoggettoPubblicoHome soggettoPubblicoHome;

	@EJB
	private ConsulenteInfoHome consulenteInfoHome;

	@EJB
	transient PfPrincipalHome pfPrincipalHome;

	@EJB
	transient DeRuoloPortaleHome deRuoloPortaleHome;

	@EJB
	private PatronatoHome patronatoHome;

	// Nome dell'utente
	private String username;
	private String nuovaPassword;

	// Le informazioni di base dell'utente (solo una è != null, a seconda del tipo di utente)
	private AziendaSessionDTO connectedAzienda;
	private UtenteDTO connectedUtente;
	private ProvinciaDTO connectedProvincia;
	private RegioneDTO connectedRegione;
	private CoordinatoreInfoDTO connectedCoordinatore;
	private ConsulenteInfoDTO connectedConsulente;
	private CertificatoreInfoDTO connectedCertificatore;
	private SoggettoPubblicoDTO connectedSoggetto;
	private PatronatoDTO connectedPatronato;

	// Stile css dell'utente (funzione disabilitata, dipende solo dalla regione)
	private String cssStyle;

	// Il nome che appare nella barra superiore del portale.
	private String titleDescription;

	// Gli Id dei quattro gruppi di default della nuova profilatura (servono a cercare attributi)
	private Integer idGpGruppoAziendaDefault;
	private Integer idGpGruppoCittadinoDefault;
	private Integer idGpGruppoProvinciaDefault;
	private Integer idGpGruppoRegioneDefault;

	private Map<String, RestoreParameters> params = new HashMap<String, RestoreParameters>();

	/*
	 * true se bisogna aprire la pagina dei messaggi con un nuovo messaggio di richiesta di assistenza tecnica
	 */
	private boolean assistenzaTecnica;

	private String tabToOpen;
	private Integer idMsgMessaggio;

	// Alcuni ruoli particolari che le aziende possono avere (li salvo in sessione per chiamare i WS una volta sola)
	private Boolean enteAccreditato = null;
	private Boolean aziendaAbilitataAssister = null;
	private Boolean entePromotoreCalabria = null;
	private Boolean enteOspitanteCalabria = null;

	/*
	 * fornisce la lista di tutta la profilatura per l'utente collegato
	 */
	private HashMap<String, Map<TipoAbilitazione, Boolean>> abilitazioniUtente = new HashMap<String, Map<TipoAbilitazione, Boolean>>();

	private Boolean abilitataKnowage;

	private Boolean isProfilatoNuovoSare;

	/**
	 * Inzializzo le informazioni sull'utente, e l'ID dei quattro gruppi di default.
	 */
	@PostConstruct
	public void postConstruct() {
		refreshSession();
		assistenzaTecnica = false;

		// Inizializzo i quattro gruppi di default (potrebbero servirmi in seguito per operazioni sulla nuova
		// profilatura)
		idGpGruppoCittadinoDefault = findIdGpGruppoByDesc(ConstantsSingleton.GpGruppo.CITTADINO_DEFAULT);
		idGpGruppoAziendaDefault = findIdGpGruppoByDesc(ConstantsSingleton.GpGruppo.AZIENDA_DEFAULT);
		idGpGruppoProvinciaDefault = findIdGpGruppoByDesc(ConstantsSingleton.GpGruppo.PROVINCIA_DEFAULT);
		idGpGruppoRegioneDefault = findIdGpGruppoByDesc(ConstantsSingleton.GpGruppo.REGIONE_DEFAULT);

		log.info("L'utente " + getUsername() + "(" + getPrincipalId() + ") ha effettuato login con successo.");
	}

	/**
	 * Recupera da MyAuthService l'id di un gruppo in base alla descrizione, SOLO se ne esiste solo uno corrispondente.
	 */
	private Integer findIdGpGruppoByDesc(String gpGruppoDesc) {
		try {
			GestioneProfiloRemoteClient gpRemoteClient = new GestioneProfiloRemoteClient();

			List<GpGruppoPOJO> gruppiDefault = gpRemoteClient
					.getGpGruppoByDesc(ConstantsSingleton.getGpGruppoByDescUrl(), gpGruppoDesc);
			if (gruppiDefault != null && !gruppiDefault.isEmpty() && !(gruppiDefault.size() > 1)) {
				return gruppiDefault.get(0).getIdGpGruppo();
			} else {
				return null;
			}
		} catch (ProfilaturaException e) {
			log.error("Errore durante la findIdGpGruppoByDesc, desc = " + gpGruppoDesc + " : " + e.toString());
			return null;
		}
	}

	/**
	 * Recupera da MyAuthService l'id di un ruolo in base alla descrizione, SOLO se ne esiste solo uno corrispondente.
	 */
	private Integer findIdGpRuoloByDesc(String gpRuoloDesc) {
		try {
			GestioneProfiloRemoteClient gpRemoteClient = new GestioneProfiloRemoteClient();

			List<GpRuoloPOJO> ruoliTrovati = gpRemoteClient.getGpRuoloByDesc(ConstantsSingleton.getGpRuoloByDescUrl(),
					gpRuoloDesc.replace(" ", "%20"));
			if (ruoliTrovati != null && !ruoliTrovati.isEmpty() && !(ruoliTrovati.size() > 1)) {
				return ruoliTrovati.get(0).getIdGpRuolo();
			} else {
				return null;
			}
		} catch (ProfilaturaException e) {
			log.error("Errore durante la findIdGpGruppoByDesc, desc = " + gpRuoloDesc + " : " + e.toString());
			return null;
		}
	}

	public boolean isAdmin() {
		if ("amministratore".equals(getUsername())) {
			return true;
		} else if ("ut_reset".equals(getUsername())) {
			return true;
		}

		return false;
	}

	public String logout() {
		ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
		HttpSession session = (HttpSession) ec.getSession(true);
		session.invalidate();
		try {
			if (!Utils.accessFromICAR(username) && !Utils.firstAccessFromICAR(username)) {
				ec.redirect(ec.getRequestContextPath());
			} else {
				String url = "/idm-pat/LogoutInitiatorServlet";
				ec.redirect(url);
			}
		} catch (IOException e) {
			log.error("Errore durante la redirect:" + e.getMessage());
		}
		log.info("L'utente " + getUsername() + "(" + getPrincipalId() + ") ha effettuato logout con successo.");
		return "";
	}

	/**
	 * Logout per utente amministratore.
	 * 
	 * @return
	 */
	public String adminLogout() {

		ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
		HttpSession session = (HttpSession) ec.getSession(true);
		session.invalidate();

		log.info("L'amministratore ha effettuato logout con successo.");
		try {
			ec.redirect(ConstantsSingleton.MYCAS_URL + "/logout");
		} catch (IOException e) {
			log.error("Errore durente la redirect: " + e.getMessage());
		}
		return null;
	}

	/**
	 * Metodo che inizializza la sessione con tutti i dati dell'utente connesso. Va richiamata ogni volta che vengono
	 * modificati i dati dell'utente connesso.
	 */
	public void refreshSession() {
		try {
			FacesContext fc = FacesContext.getCurrentInstance();
			ExternalContext ec = fc.getExternalContext();

			username = AuthUtil.removeSocialPrefix(ec.getUserPrincipal().getName());
			// if (up != null) {
			// username = up.getName();
			// } else {
			// throw new Exception("User principal not initialized correctly.");
			// }

			String stileDB = null;
			String tmpTitleDescription = "";

			/* recupero tutti gli utenti, solo uno sara' != null */
			connectedUtente = utenteInfoHome.findMiniDTOByUsername(username);
			connectedAzienda = aziendaInfoHome.findSessionDTOByUserName(username);
			connectedCoordinatore = coordinatoreInfoHome.findDTOByUserName(username);
			connectedConsulente = consulenteInfoHome.findDTOByUserName(username);
			connectedProvincia = provinciaHome.findDTOByUserName(username);
			connectedRegione = regioneHome.findDTOByUserName(username);
			connectedCertificatore = certificatoreInfoHome.findDTOByUserName(username);
			connectedSoggetto = soggettoPubblicoHome.findDTOByUserName(username);
			connectedPatronato = patronatoHome.findDTOByUserName(username);

			if (connectedUtente != null) { // CITTADINO
				/* Recupero lo stile selezionato dall'utente */
				stileDB = connectedUtente.getStileSelezionato();

				/* Recupero il title description da visualizzare in home page */
				tmpTitleDescription = connectedUtente.getNome() + " " + connectedUtente.getCognome();
			} else if (connectedAzienda != null) { // AZIENDA
				/* Recupero lo stile selezionato dall'azienda */
				stileDB = connectedAzienda.getStileSelezionato();

				/* Recupero il title description da visualizzare in home page */
				tmpTitleDescription = connectedAzienda.getRagioneSociale();
			} else if (connectedCoordinatore != null) { // COORDINATORE
				/* Recupero lo stile selezionato dall'azienda */
				stileDB = connectedCoordinatore.getStileSelezionato();

				/* Recupero il title description da visualizzare in home page */
				tmpTitleDescription = connectedCoordinatore.getNome() + " " + connectedCoordinatore.getCognome();
			} else if (connectedConsulente != null) { // CONSULENTE
				/* Recupero lo stile selezionato dall'azienda */
				stileDB = connectedConsulente.getStileSelezionato();
				/* Recupero il title description da visualizzare in home page */
				PfPrincipalDTO principal = pfPrincipalHome.findDTOById(connectedConsulente.getId());
				tmpTitleDescription = principal.getNome() + " " + principal.getCognome();
			} else if (connectedProvincia != null) { // PROVINCIA
				/* Recupero lo stile selezionato dalla provincia */
				stileDB = connectedProvincia.getStileSelezionato();
				/* Recupero il title description da visualizzare in home page */
				tmpTitleDescription = pfPrincipalHome.findDTOById(connectedProvincia.getIdPfPrincipal()).getNome();

				if ("-".equalsIgnoreCase(tmpTitleDescription)) {
					tmpTitleDescription = "Centro per l'Impiego di "
							+ connectedProvincia.getProvincia().getDescrizione();
				}
			} else if (connectedRegione != null) { // REGIONE
				/* Recupero lo stile selezionato dalla regione */
				stileDB = connectedRegione.getStileSelezionato();

				/* Recupero il title description da visualizzare in home page */
				tmpTitleDescription = connectedRegione.getRegione().getDescrizione();
			} else if (connectedCertificatore != null) { // CERTIFICATORE
				/* Recupero lo stile selezionato dalla regione */
				stileDB = connectedCertificatore.getStileSelezionato();

				/* Recupero il title description da visualizzare in home page */
				tmpTitleDescription = connectedCertificatore.getNome() + " " + connectedCertificatore.getCognome();
			} else if (connectedSoggetto != null) { // SOGGETTO PUBBLICO
				/* Recupero lo stile selezionato dalla regione */
				stileDB = "myportal";

				/* Recupero il title description da visualizzare in home page */
				PfPrincipalDTO princDTO = pfPrincipalHome.findDTOById(connectedSoggetto.getIdPfPrincipal());
				tmpTitleDescription = princDTO.getNome() + " " + princDTO.getCognome();
			} else if (connectedPatronato != null) { // REGIONE
				/* Recupero lo stile selezionato dalla regione */
				stileDB = "myportal";

				/* Recupero il title description da visualizzare in home page */
				PfPrincipalDTO princDTO = pfPrincipalHome.findDTOById(connectedPatronato.getIdPfPrincipal());
				tmpTitleDescription = princDTO.getNome() + " " + princDTO.getCognome();
			} else {
				ec.redirect(ec.getRequestContextPath());
				return;
			}

			/* setto lo stile corretto */
			if (ConstantsSingleton.COD_REGIONE.equals(ConstantsSingleton.COD_REGIONE_VDA)) {
				cssStyle = ConstantsSingleton.STILE_CSS_VDA;
			} else if (ConstantsSingleton.COD_REGIONE.equals(ConstantsSingleton.COD_REGIONE_UMBRIA)) {
				cssStyle = ConstantsSingleton.STILE_CSS_UMBRIA;
			} else if (ConstantsSingleton.COD_REGIONE.equals(ConstantsSingleton.COD_REGIONE_CALABRIA)) {
				cssStyle = ConstantsSingleton.STILE_CSS_CALABRIA;
			} else {
				cssStyle = (stileDB == null || "".equals(stileDB)) ? ConstantsSingleton.STILE_CSS_DEFAULT : stileDB;
			}

			/* formatto e setto il title description */
			titleDescription = formatTitleDescription(tmpTitleDescription);
			abilitazioniUtente = deRuoloPortaleHome.getProfile(getPrincipalId());
		} catch (Exception e) {
			log.error("Errore durante l'inizializzazione del sessionBean: " + e.getMessage());
			logout();
		}
	}

	/**
	 * Come rendere difficili le cose. Funzione di H.Alimam & a.Pegoraro che insieme sono pericolosissimi
	 * 
	 * @param originalBody
	 * @return stringa troncata a ConstantsSingleton.TITLE_DESCRIPTION_MAX_LENGTH
	 */
	public String formatTitleDescription(String originalBody) {
		// stringa nulla o più corta di MAXLEN -> non faccio nulla
		if (originalBody == null || originalBody.trim().isEmpty()
				|| originalBody.length() <= ConstantsSingleton.TITLE_DESCRIPTION_MAX_LENGTH) {
			return originalBody;

		}
		StringBuffer sb = new StringBuffer(originalBody);

		int actualLength = ConstantsSingleton.TITLE_DESCRIPTION_MAX_LENGTH - 3;
		int endIndex = 0;

		try {
			// tronco al primo spazio
			endIndex = sb.indexOf(" ", actualLength);
			// return StringEscapeUtils.escapeHtml4(sb.insert(endIndex, "...").substring(0, endIndex + 3));
			return sb.insert(endIndex, "...").substring(0, endIndex + 3);
		} catch (StringIndexOutOfBoundsException exception) {
			// c'è una parola "a cavallo" di MAXLEN -> tronco + ellipsis
			return originalBody.substring(0, ConstantsSingleton.TITLE_DESCRIPTION_MAX_LENGTH - 3).concat("...");
		}
	}

	/**
	 * Formatta il title description da visualizzare nella home page degli utenti. Se il title description originale e'
	 * piu' lungo del massimo consentito allora viene spezzato ricorsivamente in piu' parti (viene aggiunto uno spazio
	 * nella stringa)
	 * 
	 * 
	 * QUESTA ROBA QUA E' UN DELIRIO ANDREBBE ARRESTATO CHI L'HA SCRITTA LA LASCIO PER DOVERE DI CRONACA --Pegoraro
	 * 
	 * @param titleDescription
	 *            il title description originale
	 * @return titleDescription formattato
	 */
	@Deprecated
	private String formatTitleDescriptionSchifo(String titleDescription) {
		String formattedTitleDescription = "";

		if (titleDescription.length() > ConstantsSingleton.TITLE_DESCRIPTION_MAX_LENGTH) {
			String[] titleDescriptionParts = titleDescription.split(" ");

			for (int i = 0; i < titleDescriptionParts.length; i++) {
				int pivot;

				if (titleDescriptionParts[i].length() < ConstantsSingleton.TITLE_DESCRIPTION_MAX_LENGTH) {
					pivot = titleDescriptionParts[i].length();
				} else {
					pivot = ConstantsSingleton.TITLE_DESCRIPTION_MAX_LENGTH;
				}

				String part1 = formatTitleDescription(titleDescriptionParts[i].substring(0, pivot));
				String part2 = formatTitleDescription(
						titleDescriptionParts[i].substring(pivot, titleDescriptionParts[i].length()));

				titleDescriptionParts[i] = part1 + " " + part2;
			}

			for (int i = 0; i < titleDescriptionParts.length; i++) {
				formattedTitleDescription += titleDescriptionParts[i] + " ";
			}
			formattedTitleDescription = formattedTitleDescription.trim();
		} else {
			formattedTitleDescription = titleDescription;
		}

		return formattedTitleDescription;
	}

	public void setConnectedAzienda(AziendaSessionDTO connectedAzienda) {
		this.connectedAzienda = connectedAzienda;
	}

	public void setConnectedCoordinatore(CoordinatoreInfoDTO connectedCoordinatore) {
		this.connectedCoordinatore = connectedCoordinatore;
	}

	public void setConnectedCertificatore(CertificatoreInfoDTO connectedCertificatore) {
		this.connectedCertificatore = connectedCertificatore;
	}

	public void setConnectedUtente(UtenteDTO connectedUtente) {
		this.connectedUtente = connectedUtente;
	}

	public void setCssStyle(String cssStyle) {
		this.cssStyle = cssStyle;
	}

	public Integer getPrincipalId() {
		if (isAzienda()) {
			return getConnectedAzienda().getId();
		} else if (isUtente()) {
			return getConnectedUtente().getId();
		} else if (isProvincia()) {
			return getConnectedProvincia().getIdPfPrincipal();
		} else if (isCoordinatore()) {
			return getConnectedCoordinatore().getId();
		} else if (isConsulente()) {
			return getConnectedConsulente().getId();
		} else if (isRegione()) {
			return getConnectedRegione().getIdPfPrincipal();
		} else if (isCertificatore()) {
			return getConnectedCertificatore().getId();
		} else if (isSoggettoPubblico()) {
			return getConnectedSoggetto().getIdPfPrincipal();
		} else if (isPatronato()) {
			return getConnectedPatronato().getIdPfPrincipal();
		} else if (isAdmin()) {
			return pfPrincipalHome.findAbilitatoByUsername(getUsername()).getIdPfPrincipal();
		}
		// TODO restituisce l'id dell'amministratore se il tipo di utente non e'
		// uno di quelli controllati. Sarebbe meglio gestire l'amministratore
		// esplicitamente come gli altri tipi di utente.
		return 0;
	}

	/**
	 * Determina se l'oggetto passato come parametro è di proprietà dell'utente corrente. La proprietà di un oggetto può
	 * essere determinata per:<br />
	 * CvVisualizzaDTO:<br/>
	 * cvVisualizza.cvDatiPersonali.idPrincipalIns
	 * 
	 * MsgMessaggioDTO:<br/>
	 * msgMessaggio.idFrom
	 * 
	 * 
	 * 
	 * rendered="#{sessionBean.isProprietary(object)}"
	 * 
	 * @param o
	 * @return
	 */
	public Boolean isProprietary(Object o) {
		if (o instanceof CvVisualizzaDTO) {
			return ((CvVisualizzaDTO) o).isProprietary(getPrincipalId());
		} else if (o instanceof CvLetteraAccDTO) {
			return ((CvLetteraAccDTO) o).isProprietary(getPrincipalId());
		} else if (o instanceof MsgMessaggioDTO) {
			return ((MsgMessaggioDTO) o).isProprietary(getPrincipalId());
		} else if (o instanceof VaDatiVacancyDTO) {
			// ritorno true solo se appartengo all'azienda che ha pubblicato la
			// vacancy.
			if (isAzienda()) {
				int idAziendaInfoVacancy = ((VaDatiVacancyDTO) o).getIdPfPrincipalAzienda().intValue();
				int idAziendaInfoConnected = getConnectedAzienda().getIdAziendaInfo().intValue();
				return idAziendaInfoVacancy == idAziendaInfoConnected;
			} else {
				return false;
			}
		}
		return true;
	}

	/**
	 * Determina se l'oggetto passato come parametro è di proprietà del gruppo a cui appartiene l'utente corrente. La
	 * proprietà del gruppo di un oggetto può essere determinata per:
	 * 
	 * MsgMessaggioDTO
	 * 
	 * rendered="#{sessionBean.isGroupProprietary(object)}"
	 * 
	 * @param o
	 *            oggetto da verificare
	 * @return
	 */
	public Boolean isGroupProprietary(Object o) {
		if (o instanceof MsgMessaggioDTO) {
			return msgMessaggioHome.isGroupProprietary(((MsgMessaggioDTO) o).getId(), getPrincipalId());
		}
		return false;
	}

	public String getPathInAggiornamento() {
		/*
		 * nel caso si sta aggiornando l'applicazione l'utente collegato risulta un'azienda con abilitazione SARE è
		 * attivo solo l'accesso al SARE per tutti gli altri utenti ritorna alla pagina di login con un ERRORE
		 */
		if (isAzienda()) {
			AziendaSessionDTO azienda = getConnectedAzienda();
			String passwordCript = pfPrincipalHome.findById(azienda.getIdPfPrincipal()).getPassWord();
			// token per il collegamento
			// username|password|dd/mm/yyyy
			SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
			String dataFormattata = dateFormat.format(new Date());
			// recupero la password non cryptata passata alla form di login
			Object passwordNoCriptObj = SecurityContextAssociation.getCredential();
			String passwordNoCript = passwordNoCriptObj.toString();

			String token = getUsername() + "|" + passwordCript + "|" + dataFormattata;
			StringBuilder padded = new StringBuilder(token);
			while ((padded.length() % 8) > 0) {
				padded.append(" ");
			}

			// cripto il token da passare al SARE
			OracleObfuscation desObf = new OracleObfuscation("TODOTODO");
			byte[] encrypted = desObf.encrypt((padded.toString()).getBytes());
			// Encrypt
			String userCript = new String(Hex.encodeHex(encrypted));
			AziendaInfoDTO infoDTO = aziendaInfoHome.findDTOById(azienda.getIdAziendaInfo());
			if (azienda.getAbilitatoSare()) {
				if (infoDTO.getProvinciaRiferimento().getId() != null) {
					String endPoint = wsEndpointHome.getSareAddress(infoDTO.getProvinciaRiferimento().getId());
					// 'https://saretest.regione.emilia-romagna.it/WSSareUtenti/SareUtenti.asmx'
					String[] arrs = endPoint.split("/WSSareUtenti");

					return arrs[0] + "/secure/accessi/accessFromTorre38.asp?t=" + userCript;
				}
			}
		}

		logout();
		addErrorMessage("app.nodisponibile");
		return "";
	}

	private void addErrorMessage(String codErrore) {
		String msgText = errorsBean.getProperty(codErrore);
		FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, msgText, msgText);
		log.error(msgText);
		addMessage(null, message);
	}

	private void addMessage(String id, FacesMessage message) {
		FacesContext.getCurrentInstance().addMessage(id, message);
	}

	public String getNuovaPassword() {
		return nuovaPassword;
	}

	public void setNuovaPassword(String nuovaPassword) {
		this.nuovaPassword = nuovaPassword;
	}

	public boolean isAssistenzaTecnica() {
		return assistenzaTecnica;
	}

	public void setAssistenzaTecnica(boolean assistenzaTecnica) {
		this.assistenzaTecnica = assistenzaTecnica;
	}

	public String getTabToOpen() {
		return tabToOpen;
	}

	public void setTabToOpen(String tabToOpen) {
		this.tabToOpen = tabToOpen;
	}

	public Integer getIdMsgMessaggio() {
		return idMsgMessaggio;
	}

	public void setIdMsgMessaggio(Integer idMsgMessaggio) {
		this.idMsgMessaggio = idMsgMessaggio;
	}

	public ConsulenteInfoDTO getConnectedConsulente() {
		return connectedConsulente;
	}

	public HashMap<String, Map<TipoAbilitazione, Boolean>> getAbilitazioniUtenteCollegato() {
		return abilitazioniUtente;
	}

	public Boolean isAbilitato(String codAttivita, TipoAbilitazione tipoAbilitazione) {
		Boolean check = false;
		HashMap<String, Map<TipoAbilitazione, Boolean>> abilitazioni = getAbilitazioniUtenteCollegato();
		Map<TipoAbilitazione, Boolean> mapFlags = abilitazioni.get(codAttivita);
		if (mapFlags != null) {
			check = mapFlags.get(tipoAbilitazione);
		}

		return check;
	}

	/**
	 * Controllo se l'utente ha un attributo abilitato per un certo tipo di gruppo (nuova profilatura). I tipi di gruppo
	 * che prendo in considerazione sono solo i quattro "DEFAULT", per ora.
	 */
	private boolean isAttributeOwner(String codAttributo, Integer idGpGruppo) {
		try {
			GestioneProfiloRemoteClient gpRemoteClient = new GestioneProfiloRemoteClient();

			List<DestinatarioPOJO> attributeOwners = gpRemoteClient
					.getAttributeOwners(ConstantsSingleton.getAttributeOwnersUrl(), codAttributo, idGpGruppo);
			for (DestinatarioPOJO destinatario : attributeOwners) {
				if (destinatario.getIdPfPrincipal().equals(this.getPrincipalId())) {
					return true;
				}
			}

			return false;
		} catch (Exception e) {
			log.error("Errore durante la verifica isAttributeOwner() per " + codAttributo + ": " + e.toString());
			return false;
		}
	}

	/**
	 * Dalla versione 1.17.0 in poi, un'azienda è un ente accreditato SOLO se possiede un ruolo apposito nella nuova
	 * profilatura. La prima volta che questo metodo viene chiamato, invoco il WS di MyAuthService per verificarlo.
	 */
	public boolean isEnteAccreditato() {
		if (enteAccreditato == null) {
			if (isAzienda() && idGpGruppoAziendaDefault != null) {
				enteAccreditato = isAttributeOwner(ConstantsSingleton.GpAttributo.A_ENTE_ACCREDITATO,
						idGpGruppoAziendaDefault);
				enteAccreditato = enteAccreditato
						|| isAttributeOwner(ConstantsSingleton.GpAttributo.A_ATTUATORE_L1, idGpGruppoAziendaDefault);
				enteAccreditato = enteAccreditato
						|| isAttributeOwner(ConstantsSingleton.GpAttributo.A_ATTUATORE_L2, idGpGruppoAziendaDefault);
				enteAccreditato = enteAccreditato
						|| isAttributeOwner(ConstantsSingleton.GpAttributo.A_ATTUATORE_L3, idGpGruppoAziendaDefault);
				enteAccreditato = enteAccreditato
						|| isAttributeOwner(ConstantsSingleton.GpAttributo.A_ATTUATORE_L4, idGpGruppoAziendaDefault);
			} else {
				enteAccreditato = false;
			}
		}

		return enteAccreditato;
	}

	public boolean isAbilitatoKnowage() {
		if (abilitataKnowage == null) {
			abilitataKnowage = false;

			GestioneProfiloRemoteClient gpRemoteClient = new GestioneProfiloRemoteClient();

			UserProfiloPOJO pojo;
			try {
				pojo = gpRemoteClient.getProfilo(ConstantsSingleton.getProfiloUrl(), getPrincipalId());
				for (GpRuoloGruppoPOJO grp : pojo.getUserGruppoPOJOList()) {
					// semplicemente se esiste, ho qualcosa da fare lì
					if (grp.getCodSistema().equals(DeSistemaEnum.KNOWAGE.toString())) {
						abilitataKnowage = true;
						break;
					}
				}
			} catch (ProfilaturaException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

		return abilitataKnowage;
	}
	
	public boolean isProfilatoSare() {
		if(isProfilatoNuovoSare == null) {
			boolean abilitataSare = false;
			GestioneProfiloRemoteClient gpRemoteClient = new GestioneProfiloRemoteClient();
			UserProfiloPOJO pojo;
			try {
				pojo = gpRemoteClient.getProfilo(ConstantsSingleton.getProfiloUrl(), getPrincipalId());
				for (GpRuoloGruppoPOJO grp : pojo.getUserGruppoPOJOList()) {
					// le profilature del nuovo sono associate al sistema vecchio ?
					if (grp.getCodSistema().equals(DeSistemaEnum.MYSARE.toString())||
							grp.getCodSistema().equals(DeSistemaEnum.SARE.toString())) {
						abilitataSare = true;
						break;
					}
				}
			} catch (ProfilaturaException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			isProfilatoNuovoSare = abilitataSare;
		}
		return isProfilatoNuovoSare;
	}

	public boolean isEnteAccreditatoAssister() {
		if (aziendaAbilitataAssister == null) {
			aziendaAbilitataAssister = false;

			if (isAzienda() && connectedAzienda.getId() != null) {

				GestioneProfiloRemoteClient gpRemoteClient = new GestioneProfiloRemoteClient();

				UserProfiloPOJO pojo;
				try {
					pojo = gpRemoteClient.getProfilo(ConstantsSingleton.getProfiloUrl(), connectedAzienda.getId());
					for (GpRuoloGruppoPOJO grp : pojo.getUserGruppoPOJOList()) {
						// semplicemente se esiste, ho qualcosa da fare lì
						if (grp.getCodSistema().equals(DeSistemaEnum.ASSISTER.toString())) {
							aziendaAbilitataAssister = true;
							break;
						}
					}
				} catch (ProfilaturaException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			/*
			 * }
			 * 
			 * 
			 * if (isAzienda() && idGpGruppoAziendaDefault != null) { enteAccreditatoAssister =
			 * isAttributeOwner(ConstantsSingleton.GpAttributo.A_ATTUATORE_ASSISTER, idGpGruppoAziendaDefault); } else {
			 * enteAccreditatoAssister = false; }
			 */
		}

		return aziendaAbilitataAssister;
	}

	public boolean isEntePromotoreCalabria() {
		if (entePromotoreCalabria == null) {
			if (isAzienda() && idGpGruppoAziendaDefault != null) {
				entePromotoreCalabria = isAttributeOwner(ConstantsSingleton.GpAttributo.A_FB_CONVENZIONI_QUADRO,
						idGpGruppoAziendaDefault);
			} else {
				entePromotoreCalabria = false;
			}
		}

		return entePromotoreCalabria;
	}

	public boolean isEnteOspitanteCalabria() {
		if (enteOspitanteCalabria == null) {
			if (isAzienda() && idGpGruppoAziendaDefault != null) {
				enteOspitanteCalabria = isAttributeOwner(ConstantsSingleton.GpAttributo.A_FABB_SCHEDA,
						idGpGruppoAziendaDefault);
			} else {
				enteOspitanteCalabria = false;
			}
		}

		return enteOspitanteCalabria;
	}

	/**
	 * Chiama il metodo putProfilatura di MyAuth per inserire una profilatura a un utente, usando i due findByDesc per
	 * recuperare gli ID di gruppo e ruolo.
	 */
	public void putProfilaturaByDesc(Integer idPfPrincipal, String descGruppo, String descRuolo) {
		Integer idGpGruppo = findIdGpGruppoByDesc(descGruppo);
		Integer idGpRuolo = findIdGpRuoloByDesc(descRuolo);
		GestioneProfiloRemoteClient client = new GestioneProfiloRemoteClient();
		try {
			client.putProfilatura(ConstantsSingleton.putProfilaturaUrl(), idGpRuolo, idGpGruppo, idPfPrincipal);
		} catch (ProfilaturaException e) {
			log.error("Errore durante la putProfilaturaByDesc per utente " + idPfPrincipal + ", gruppo " + descGruppo
					+ " e ruolo " + descRuolo + " : " + e.toString());
		}
	}

	public Integer getIdGruppoAziendaDefault() {
		return idGpGruppoAziendaDefault;
	}

	/**
	 * verifica se per l'attività passata l'utente è abilitato a visualizzare la sezione o i pulsanti
	 */
	public boolean checkAbilitazioneVisibile(String codAttivitaPf) {
		return isAbilitato(codAttivitaPf, TipoAbilitazione.VISIBILE);
	}

	public boolean checkAbilitazioneInserimento(String codAttivitaPf) {
		return isAbilitato(codAttivitaPf, TipoAbilitazione.INSERIMENTO);
	}

	public boolean checkAbilitazioneLettura(String codAttivitaPf) {
		return isAbilitato(codAttivitaPf, TipoAbilitazione.LETTURA);
	}

	public boolean checkAbilitazioneModifica(String codAttivitaPf) {
		return isAbilitato(codAttivitaPf, TipoAbilitazione.MODIFICA);
	}

	public boolean checkAbilitazioneCancellazione(String codAttivitaPf) {
		return isAbilitato(codAttivitaPf, TipoAbilitazione.CANCELLAZIONE);
	}

	public boolean checkAbilitazioneAmministrazione(String codAttivitaPf) {
		return isAbilitato(codAttivitaPf, TipoAbilitazione.AMMINISTRAZIONE);
	}

	public String getUrlMyAccount() {
		return ConstantsSingleton.MYACCOUNT_URL;
	}

	public String getUrlMyAgenda() {
		return ConstantsSingleton.MYAGENDA_URL;
	}

	public String getUrlAssister() {
		return ConstantsSingleton.ASSISTER_URL;
	}

	public String getUrlMyStage() {
		return ConstantsSingleton.MYSTAGE_URL;
	}

	public String getUrlKnowage() {
		return ConstantsSingleton.KNOWAGE_URL;
	}

	public String getUrlAccreditamento() {
		return ConstantsSingleton.ACCREDITAMENTO_URL;
	}

	public String getUrlRES() {
		return ConstantsSingleton.RES_URL;
	}

	public String getUrlO2l() {
		return ConstantsSingleton.O2L_URL;
	}

	public AziendaSessionDTO getConnectedAzienda() {
		return connectedAzienda;
	}

	public CoordinatoreInfoDTO getConnectedCoordinatore() {
		return connectedCoordinatore;
	}

	public CertificatoreInfoDTO getConnectedCertificatore() {
		return connectedCertificatore;
	}

	public UtenteDTO getConnectedUtente() {
		return connectedUtente;
	}

	public ProvinciaDTO getConnectedProvincia() {
		return connectedProvincia;
	}

	public RegioneDTO getConnectedRegione() {
		return connectedRegione;
	}

	public PatronatoDTO getConnectedPatronato() {
		return connectedPatronato;
	}

	public String getCssStyle() {
		return cssStyle;
	}

	public String getUsername() {
		return username;
	}

	public boolean isAzienda() {
		return connectedAzienda != null;
	}

	public boolean isCoordinatore() {
		return connectedCoordinatore != null;
	}

	public boolean isConsulente() {
		return connectedConsulente != null;
	}

	public boolean isCertificatore() {
		return connectedCertificatore != null;
	}

	public boolean isSoggettoPubblico() {
		return connectedSoggetto != null;
	}

	public boolean isUtente() {
		return connectedUtente != null;
	}

	public boolean isProvincia() {
		return connectedProvincia != null;
	}

	public boolean isRegione() {
		return connectedRegione != null;
	}

	public boolean isPatronato() {
		return connectedPatronato != null;
	}

	public SoggettoPubblicoDTO getConnectedSoggetto() {
		return connectedSoggetto;
	}

	public void setConnectedSoggetto(SoggettoPubblicoDTO connectedSoggetto) {
		this.connectedSoggetto = connectedSoggetto;
	}

	public Map<String, RestoreParameters> getParams() {
		return params;
	}

	public void setParams(Map<String, RestoreParameters> params) {
		this.params = params;
	}

	public String getTitleDescription() {
		return titleDescription;
	}

	public void setTitleDescription(String titleDescription) {
		this.titleDescription = titleDescription;
	}
}

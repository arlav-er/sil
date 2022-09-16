package it.eng.myportal.beans;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedProperty;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.ComponentSystemEvent;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import it.eng.myportal.dtos.IDTO;
import it.eng.myportal.entity.decodifiche.DeSistemaMyCas;
import it.eng.myportal.entity.ejb.ErrorsSingleton;
import it.eng.myportal.entity.enums.TipoAbilitazione;
import it.eng.myportal.entity.home.PfPrincipalHome;
import it.eng.myportal.entity.home.decodifiche.DeSistemaMyCasHome;
import it.eng.myportal.entity.home.local.IDTOHome;
import it.eng.myportal.exception.MyPortalException;
import it.eng.myportal.utils.ConstantsSingleton;

/**
 * Classe astratta che deve essere estesa da tutti i BackingBean del progetto che si occupano di gestire una pagina.
 * Questa classe permette di avere a disposizione, all'interno del proprio backing bean:
 * <ul>
 * <li>un log su cui scrivere</li>
 * <li>sessionBean con le informazioni di sessione</li>
 * <li>metodi per fare la redirect su un altra pagina, sulla pagina pubblica o sulla propria home</li>
 * <li>più metodi per mostrare i messaggi a schermo</li>
 * </ul>
 *
 * La classe si occupa di inizializzare tutto ciò che è può essere necessario nella pagina. Vedere la documentazione
 * dettagliata.
 *
 * @author Rodi A.
 *
 */
/**
 * @author girotti
 * 
 */
public abstract class AbstractBaseBean {

	private static final String REDO_BY_SESSION = "redo_by_session";
	protected static final String BACK_TO = "backTo";
	protected final static String SESSION_TOKEN_HISTORY = "TOKEN_";

	@EJB
	DeSistemaMyCasHome deSistemaMyCasHome;

	/**
	 * Logger di classe. Utilizzare questo log per scrivere messaggi di errore, info e debug.
	 */
	protected Log log = LogFactory.getLog(this.getClass());

	/**
	 * Collegamento al SessionBean che contiene informazioni sulla sessione dell'utente che ha eseguito login.
	 */
	@ManagedProperty(value = "#{sessionBean}")
	protected SessionBean session;

	/**
	 * Collegamento al UtilsBean che contiene informazioni sull'intera applicazione
	 */
	@ManagedProperty(value = "#{utilsBean}")
	protected UtilsBean utils;

	/**
	 * Collegamento al FlashMessagesBean che contiene i messaggi da mostrare all'utente. Questa classe non dovrebbe
	 * essere mai gestita liberamente: è l'AbstractBaseBean a farne uso e si occupa di inserirvi e rimuovervi i messaggi
	 * all'occorrenza.
	 * 
	 * @see FlashMessagesBean
	 */
	@ManagedProperty(value = "#{flashMessagesBean}")
	protected FlashMessagesBean messages;

	/**
	 * Singleton con gli errori
	 */
	@EJB
	protected ErrorsSingleton errorsBean;

	@EJB
	transient PfPrincipalHome pfPrincipalHome;

	/**
	 * Nome del token per memorizzare le informazioni del BB nella Sessione per il Torna Indietro.
	 */
	private String token = SESSION_TOKEN_HISTORY + this.getClass().getSimpleName();

	/**
	 * Il referrer
	 */
	private String backTo;

	private boolean redoBySess;

	/**
	 * Parametri per costruire il BackingBean. Vengono recuperati dalla sessione in fase di postConstruct
	 */
	protected RestoreParameters beanParamsSess;

	/**
	 * E' a tutti gli effetti il costruttore della classe. Gli EJB e le risorse sono già state iniettate e quindi
	 * utilizzabili. Il costruttore dell'AbstractBaseBean non fa altro che verificare se ci sono messaggi da mostrare
	 * all'interno del flashMessagesBean e mostrarli all'utente.
	 * 
	 */
	@PostConstruct
	protected void postConstruct() {
		// cerchi eventuali parametri in request che indichino che mi devo
		// costruire dalla sessione
		popolaRedoBySess();
		beanParamsSess = session.getParams().get(getToken());

		Map<String, String> map = getExternalContext().getRequestParameterMap();
		String backToDaReq = map.get(BACK_TO);
		// recupero il referrer
		// Pulisco il parametro x evitare che si moltiplichi
		String backToDaReferer = StringUtils.defaultString(getExternalContext().getRequestHeaderMap().get("referer"),
				"");
		backTo = StringUtils.defaultString(backToDaReq, backToDaReferer).replace("?redo_by_session=true", "")
				.replace("redo_by_session=true", "");

		popolaBackToDaSess();

		if (redoBySess && beanParamsSess != null) {
			ricreaStatoDaSessione(beanParamsSess);
		}

		// al caricamento di tutte le pagine verifico se ci sono messaggi di
		// errore da mostrare
		// che provengono da un'altra pagina. L'altra pagina li avrà salvati
		// dentro a FlashMessagesBean
		// li recupero, li mostro e li cancello.
		for (FacesMessage message : messages.getList()) {
			addMessage(null, message);
		}
		messages.clear(); // cancellali!

		log.info("Azione eseguita dall'utente '" + session.getUsername() + "' (" + session.getPrincipalId() + ")");
	}

	/**
	 * Viene eseguito alla costruzione di tutti i BB. Recupera il parametro REDO_BY_SESSION dalla request ed indica se
	 * devo costruire il Bean partendo dalla Session o meno.
	 */
	public void popolaRedoBySess() {
		Map<String, String> map = getExternalContext().getRequestParameterMap();
		redoBySess = Boolean.TRUE.toString().equalsIgnoreCase(map.get(REDO_BY_SESSION));
		// Object req = getExternalContext().getRequest();
	}

	/**
	 * Redirige la navigazione corrente verso il path indicato. Salva eventuali messaggi.
	 * 
	 * @param path
	 *            String
	 */
	protected void redirect(String path) {
		List<FacesMessage> list = FacesContext.getCurrentInstance().getMessageList(null);
		messages.addMessages(list);// salva tutti i messaggi globali in caso di
									// redirect.

		try {
			String completePath = getExternalContext().getRequestContextPath() + path;
			log.warn("Redirecting to " + completePath);
			getExternalContext().redirect(completePath);

		} catch (IOException e) {
			log.error("Errore durante la redirect: " + e.getMessage());
		}
	}

	/**
	 * Metodo per il redirect alla home pubblica
	 */
	protected void redirectPublicIndex() {
		redirect(ConstantsSingleton.BASE_URL);
	}

	/**
	 * Metodo per il redirect alla home. Se si e' loggati come azienda redirige alla home dell'azienda, se si e' loggati
	 * come utente redirige alla home dell'utente, altrimenti redirige alla home pubblica segnalando l'errore nel log.
	 */
	protected void redirectHome() {
		String home = getHomeBySession();
		if (StringUtils.isBlank(home)) {
			log.error("Errore durante la costruzione del bean " + getClass().getSimpleName()
					+ ". Nessun utente o azienda collegato");
			redirectPublicIndex();
		}
		redirect(home);
	}

	/**
	 * @param session2
	 * @return la home relativa all'account loggato; altrimenti null
	 */
	public static String getHomeBySession(SessionBean session) {
		if (session == null) {
			return null;
		}
		if (session.isAdmin()) {
			return ConstantsSingleton.HOME_ADMIN;
		}

		if (session.isAzienda()) {
			return ConstantsSingleton.HOME_AZIENDA;
		} else if (session.isCoordinatore() || session.isConsulente()) {
			return ConstantsSingleton.HOME_CONDIVISA;
		} else if (session.isUtente()) {
			return ConstantsSingleton.HOME_UTENTE;
		} else if (session.isProvincia()) {
			return ConstantsSingleton.HOME_PROVINCIA;
		} else if (session.isRegione()) {
			return ConstantsSingleton.HOME_REGIONE;
		} else if (session.isCertificatore()) {
			return ConstantsSingleton.HOME_CERTIFICATORE;
		} else if (session.isPatronato()) {
			return ConstantsSingleton.HOME_PATRONATO;
		} else if (session.isSoggettoPubblico()) {
			return ConstantsSingleton.HOME_SOGGETTO_PUBBLICO;
		} else {
			return null;
		}

	}

	public String getHomeBySession() {
		String home = "";

		home = getHomeBySession(session);
		if (StringUtils.isBlank(home)) {
			log.error("Errore durante la costruzione del bean " + getClass().getSimpleName()
					+ ". Nessun utente o azienda collegato");
		}
		return home;

	}

	/**
	 * Esegue una redirect verso la pagina di errore.<br/>
	 * La redirect verso tale pagine è sintomo di un grave errore dovuto, molto probabilmente, ad una manipolazione dei
	 * parametri da parte dell'utente per cercare di aggirare le limitazioni impostegli dall'applicazione.<br/>
	 * La chiamata a questo metodo comporta quindi la visualizzazione a schermo degli errori ma anche la chiusura della
	 * sessione dell'utente.<br/>
	 * <b>Attenzione! I codici che vengono passati a questo metodo DEVONO essere all'interno del file errors.properties.
	 * </b>
	 * 
	 * @param codiciErrore
	 *            codici degli errori che si vogliono mostrare. <br/>
	 *            I codici sono elencati all'interno del file messages/errors.properties.
	 * 
	 * 
	 */
	protected void redirectGrave(String... codiciErrore) {
		StringBuilder path = new StringBuilder("/faces/public/errors.xhtml");
		if (codiciErrore != null && codiciErrore.length > 0) {
			path.append("?errors=");
			for (String codiceErrore : codiciErrore) {
				path.append(codiceErrore).append(",");
			}
			path.deleteCharAt(path.length() - 1);
		}
		redirect(path.toString());

	}

	/**
	 * Aggiunge un messaggio da mostrare all'utente.
	 * 
	 * @param id
	 *            clientId del componente per il quale si vuole visualizzare il messaggio. null se il messaggio è
	 *            globale.
	 * @param message
	 *            messaggio da mostrare.
	 */
	protected void addMessage(String id, FacesMessage message) {
		FacesContext.getCurrentInstance().addMessage(id, message);
	}

	/**
	 * Aggiunge un errore da mostrare all'utente e stampa sul log lo stacktrace dell'eccezione gestita.
	 * 
	 * @param codErrore
	 *            codice del messaggioche si trova in errors.properties
	 */
	protected void addErrorMessage(String codErrore) {
		String msgText = errorsBean.getProperty(codErrore);
		FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, msgText, msgText);
		log.error(msgText);
		addMessage(null, message);
	}

	/**
	 * Aggiunge un errore da mostrare all'utente, senza bisogno di prenderlo da error.properties
	 */
	protected void addCustomErrorMessage(String msgErrore) {
		FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, msgErrore, msgErrore);
		addMessage(null, message);
	}

	protected void addWarnMessage(String codErrore) {
		String msgText = errorsBean.getProperty(codErrore);

		/*
		 * se non trovo la stringa nel file di properties significa che e' gia' un messaggio
		 */
		if (msgText == null || msgText.isEmpty()) {
			msgText = codErrore;
		}

		FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_WARN, msgText, msgText);
		addMessage(null, message);
	}

	/**
	 * Aggiunge un messaggio da mostrare all'utente come informazione
	 * 
	 * @param codErrore
	 *            codice del messaggio che si trova in errors.properties
	 */
	protected void addInfoMessage(String codMessaggio) {
		String msgText = errorsBean.getProperty(codMessaggio);
		FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, msgText, msgText);
		addMessage(null, message);
	}

	protected void addInfoMessageWithParam(String codMessaggio, String param) {
		String msgText = errorsBean.getProperty(codMessaggio);
		msgText = msgText.concat((param != null && !param.equalsIgnoreCase("") ? " " + param : ""));
		FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, msgText, msgText);
		addMessage(null, message);
	}

	/**
	 * Aggiunge un errore da mostrare all'utente e stampa sul log lo stacktrace dell'eccezione gestita.
	 * 
	 * @param codErrore
	 *            codice del messaggioche si trova in errors.properties
	 */
	protected void addErrorMessage(String codErrore, Exception e) {
		String msgText = errorsBean.getProperty(codErrore);
		FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, msgText, msgText);
		log.error(msgText, e);
		addMessage(null, message);
	}

	/**
	 * Restituisce la sessione con tutte le informazioni ivi contenute.
	 * 
	 * @return SessionBean
	 */
	public SessionBean getSession() {
		return session;
	}

	public void setSession(SessionBean session) {
		this.session = session;
	}

	/**
	 * Restituisce il bean application scoped con tutte info di livello applicativo
	 * 
	 * @return UtilsBean
	 */
	public UtilsBean getUtils() {
		return utils;
	}

	public void setUtils(UtilsBean utils) {
		this.utils = utils;
	}

	/**
	 * <b>Non usare questo metodo!</b>
	 * 
	 * @return FlashMessagesBean
	 * 
	 */
	public FlashMessagesBean getMessages() {
		return messages;
	}

	/**
	 * <b>Non usare questo metodo!</b>
	 */
	public void setMessages(FlashMessagesBean messages) {
		this.messages = messages;
	}

	/**
	 * Metodo per prendere i parametri dalla request
	 * 
	 * @param name
	 * @return
	 */
	public String getRequestParameter(String name) {
		return getRequestParameterMap().get(name);
	}

	/**
	 * Ritorna il parametro che finisce con la stringa passata. Se ci sono più parametri che soddisfano il requisito
	 * lancia una eccezione.
	 * 
	 * @param name
	 *            trova il parametro con key = "%name"
	 * @return il valore del parametro
	 * @throws MyPortalException
	 */
	public String getRequestParameterEndsWith(String name) throws MyPortalException {
		Map<String, String> map = getRequestParameterMap();
		List<String> lista = new ArrayList<String>();
		for (Entry<String, String> entry : map.entrySet()) {
			if (entry.getKey().endsWith(name)) {
				lista.add(entry.getValue());
			}
		}
		if (lista.size() == 0)
			return null;
		else if (lista.size() == 1)
			return lista.get(0);
		else
			throw new MyPortalException(null);
	}

	/**
	 * Metodo per accedere alla mappa dei parametri della request.
	 * 
	 * @return
	 */
	protected Map<String, String> getRequestParameterMap() {
		return getFacesContext().getExternalContext().getRequestParameterMap();
	}

	/**
	 * Metodo per accedere all'externalContext. L'externalContext non può essere memorizzato in una variabile.
	 * 
	 * @return
	 */
	protected ExternalContext getExternalContext() {
		return getFacesContext().getExternalContext();
	}

	/**
	 * Metodo per accedere al facesContext. Il facesContext non può essere memorizzato in una variabile.
	 * 
	 * @return
	 */
	protected FacesContext getFacesContext() {
		return FacesContext.getCurrentInstance();
	}

	protected <DTO extends IDTO> DTO homePersist(IDTOHome<DTO> home, DTO dto) {
		return home.persistDTO(dto, getSession().getPrincipalId());
	}

	protected <DTO extends IDTO> DTO homeMerge(IDTOHome<DTO> home, DTO dto) {
		return home.mergeDTO(dto, getSession().getPrincipalId());
	}

	/**
	 * @return the redoBySess
	 */
	public boolean isRedoBySess() {
		return redoBySess;
	}

	/**
	 * a che pagina tornare? <br>
	 * -priorità da request<br>
	 * -poi da sessione<br>
	 * -poi da header refer<br>
	 * -altrimenti vuota
	 * 
	 */
	public String getBackTo() {
		return backTo;
	}

	/**
	 * Inserisce il proprio stato in sessione. Di default inserisce solo il backTo.
	 * 
	 * Da richiamare ogniqualvota cambia lo stato del BackingBean
	 * 
	 * @param customParam
	 * 
	 */
	protected void putParamsIntoSession() {
		RestoreParameters restoreParams = generateRestoreParams();
		session.getParams().put(getToken(), restoreParams);
	}

	/**
	 * Genera la mappa dei parametri da salvare il sessione per recuperare i dati.
	 * 
	 * I BackingBean devono fare l'override di questo metodo per salvare i propri parametri.
	 * 
	 * protected RestoreParameters generateRestoreParams() { RestoreParameters restore = super.generateRestoreParams();
	 * restore.put... restore.put... return restore; }
	 * 
	 * @return
	 */
	protected RestoreParameters generateRestoreParams() {
		RestoreParameters restore = new RestoreParameters();
		Map<String, String> map = getExternalContext().getRequestHeaderMap();
		restore.put(BACK_TO, map.get("referer"));
		restore.put("token", getToken());
		return restore;
	}

	/**
	 * Questo metodo permette di creare il BackingBean a partire dai dati presenti in sessione.
	 */
	protected void ricreaStatoDaSessione(RestoreParameters restoreParams) {
		popolaBackToDaSess();
	}

	/**
	 * Recupera il parametro di backTo tra quelli da cui mi sto costruendo
	 */
	private void popolaBackToDaSess() {
		// se mi sto costruendo a partire dai parametri in sessione
		if (beanParamsSess != null) {
			Object object = beanParamsSess.get(BACK_TO);
			if (object != null) {
				backTo = object.toString();
			}
		}
	}

	public void setRedoBySess(boolean redoBySess) {
		this.redoBySess = redoBySess;
	}

	/**
	 * @return the token
	 */
	public String getToken() {
		return token;
	}

	/**
	 * Gestisce un'eccezione lanciata da un EJB.
	 * 
	 * @param e
	 *            eccezione ricevuta
	 * @param codErrore
	 *            codice di errore da utilizzare come default se non è presente un messaggio nell'eccezione
	 */
	protected void gestisciErrore(EJBException e, String codErrore) {
		if (e instanceof MyPortalException) {
			gestisciErroreMyPortal((MyPortalException) e);
		} else if (e.getCause() != null && e.getCause() instanceof MyPortalException) {
			gestisciErroreMyPortal((MyPortalException) e.getCause());
		} else {
			addErrorMessage(codErrore, e);
		}
	}

	private void gestisciErroreMyPortal(MyPortalException e) {

		if (e.isMessage()) {
			String msgText = e.getStrMessaggio();
			FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, msgText, msgText);
			log.error(msgText + " - " + e.getMessage());
			addMessage(null, message);
		} else {
			addErrorMessage(e.getCodErrore(), e);
		}
	}

	public void preRenderListener(ComponentSystemEvent event) throws AbortProcessingException {
		// Map<String, String> map = getRequestParameterMap();
		Map<String, Object> smap = FacesContext.getCurrentInstance().getExternalContext().getSessionMap();
		if (log.isDebugEnabled()) {
			log.debug("BackingBean in sessione:");
			for (String key : smap.keySet()) {
				if (smap.get(key) instanceof AbstractBaseBean)
					log.debug(key + ": " + smap.get(key));
			}
		}

		return;
	}

	public Boolean isAbilitato(String codAttivita, TipoAbilitazione tipoAbilitazione) {
		return getSession().isAbilitato(codAttivita, tipoAbilitazione);
	}

	/**
	 * verifica se per l'attività passata l'utente è abilitato a visualizzare la sezione o i pulsanti
	 * 
	 * VECCHIA PROFILATURA, preferibile NON USARE
	 * 
	 * @param servizio
	 * @return
	 */
	public boolean checkAbilitazioneVisibile(String codAttivitaPf) {
		return getSession().checkAbilitazioneVisibile(codAttivitaPf);
	}

	public boolean checkAbilitazioneInserimento(String codAttivitaPf) {
		return getSession().checkAbilitazioneInserimento(codAttivitaPf);
	}

	public boolean checkAbilitazioneLettura(String codAttivitaPf) {
		return getSession().checkAbilitazioneLettura(codAttivitaPf);
	}

	public boolean checkAbilitazioneModifica(String codAttivitaPf) {
		return getSession().checkAbilitazioneModifica(codAttivitaPf);
	}

	public boolean checkAbilitazioneCancellazione(String codAttivitaPf) {
		return getSession().checkAbilitazioneCancellazione(codAttivitaPf);
	}

	public boolean checkAbilitazioneAmministrazione(String codAttivitaPf) {
		return getSession().checkAbilitazioneAmministrazione(codAttivitaPf);
	}

	public boolean checkAbilitazioneSistemaVisibile(String codSistema) {
		DeSistemaMyCas deSistemaSare = deSistemaMyCasHome.findById(codSistema);
		return deSistemaSare.getFlgInstallato();
	}

}
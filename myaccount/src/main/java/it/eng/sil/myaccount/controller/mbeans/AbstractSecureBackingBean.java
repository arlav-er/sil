package it.eng.sil.myaccount.controller.mbeans;

import it.eng.sil.myaccount.controller.mbeans.session.AccountInfoBean;
import it.eng.sil.myaccount.model.exceptions.MyAccountException;
import it.eng.sil.myaccount.model.utils.ConstantsSingleton;
import it.eng.sil.myaccount.utils.StringUtils;
import it.eng.sil.mycas.exceptions.MyCasNoResultException;
import it.eng.sil.mycas.model.entity.decodifiche.DeSistema;
import it.eng.sil.mycas.model.manager.decodifiche.DeSistemaEJB;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedProperty;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.validator.ValidatorException;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.primefaces.context.RequestContext;

/**
 * Classe astratta che deve essere estesa da tutti i BackingBean del progetto che si occupano di gestire una pagina.
 * Questa classe permette di avere a disposizione, all'interno del proprio backing bean:
 * <ul>
 * <li>un log su cui scrivere</li>
 * <li>sessionBean con le informazioni di sessione</li>
 * <li>metodi per fare la redirect su un altra pagina, sulla pagina pubblica o sulla propria home</li>
 * <li>piu metodi per mostrare i messaggi a schermo</li>
 * </ul>
 *
 * La classe si occupa di inizializzare tutto quello che essere necessario nella pagina
 *
 */
public abstract class AbstractSecureBackingBean {

	/**
	 * Logger di classe. Utilizzare questo log per scrivere messaggi di errore, info e debug.
	 */
	protected Log log = LogFactory.getLog(this.getClass());

	public String urlUnauthorizedPage = "/error/403";

	/**
	 * Collegamento al UtilsBean che contiene informazioni sull'intera applicazione
	 */
	@ManagedProperty(value = "#{utils}")
	protected UtilsBean utils;

	/**
	 * Collegamento al ConstantsSingleton che contiene tutte le costanto presenti nel file const.properties
	 */
	@EJB
	protected ConstantsSingleton constantsSingleton;

	@EJB
	protected DeSistemaEJB deSistemaEJB;

	@ManagedProperty(value = "#{accountInfoBean}")
	protected AccountInfoBean accountInfoBean;

	public AbstractSecureBackingBean() {

	}

	/**
	 * E' a tutti gli effetti il costruttore della classe. Gli EJB e le risorse sono gi� state iniettate e quindi
	 * utilizzabili. Il costruttore dell'AbstractBaseBean non fa altro che verificare se ci sono messaggi da mostrare
	 * all'interno del flashMessagesBean e mostrarli all'utente.
	 *
	 */
	@PostConstruct
	protected void postConstruct() {
		log.debug("Abstract Bean postConstruct");

		if (getExternalContext().getSessionMap().containsKey("accountInfoBean")) {
			accountInfoBean = (AccountInfoBean) getExternalContext().getSessionMap().get("accountInfoBean");
		}

		initPostConstruct();
	}

	protected abstract void initPostConstruct();

	@PreDestroy
	public void preDestroy() {
		log.debug("destroying myself");
	}

	/**
	 * Metodo per accedere al facesContext. Il facesContext non può essere memorizzato in una variabile.
	 *
	 * @return
	 */
	protected FacesContext getFacesContext() {
		return FacesContext.getCurrentInstance();
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
	 * Metodo per accedere alla mappa dei parametri della request.
	 *
	 * @return
	 */
	protected Map<String, String> getRequestParameterMap() {
		return getFacesContext().getExternalContext().getRequestParameterMap();
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
	public String getRequestParameterEndsWith(String name) throws MyAccountException {
		Map<String, String> map = getRequestParameterMap();
		List<String> lista = new ArrayList<String>();
		for (Entry<String, String> entry : map.entrySet()) {
			if (entry.getKey().endsWith(name)) {
				lista.add(entry.getValue());
			}
		}
		if (lista.size() == 0) {
			return null;
		} else if (lista.size() == 1) {
			return lista.get(0);
		} else {
			throw new MyAccountException();
		}
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
	private void addMessage(String id, FacesMessage message) {
		/* se inserisco un messaggio di errore elimino tutti i messaggi di info */
		if (message.getSeverity() == FacesMessage.SEVERITY_ERROR
				|| message.getSeverity() == FacesMessage.SEVERITY_FATAL) {
			Iterator<FacesMessage> iterator = FacesContext.getCurrentInstance().getMessages();
			while (iterator.hasNext()) {
				FacesMessage oldMessage = iterator.next();
				if (oldMessage.getSeverity() == FacesMessage.SEVERITY_INFO) {
					iterator.remove();
				}
			}
		}
		FacesContext.getCurrentInstance().addMessage(id, message);
	}

	/**
	 * Aggiunge un errore da mostrare all'utente e stampa sul log lo stacktrace dell'eccezione gestita.
	 *
	 * @param codErrore
	 *            codice del messaggioche si trova in errors.properties
	 */
	protected void addErrorMessage(String codErrore, boolean addGrowl) {
		String msgText = utils.getErrorProperty(codErrore);
		FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, msgText, "");
		log.error(msgText);
		addMessage(null, message);
		if (addGrowl) {
			addMessage("growlBox", message);
		}
	}

	protected void addErrorMessageToComponent(String codErrore, String componentId) {
		String msgText = utils.getErrorProperty(codErrore);
		FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, msgText, msgText);
		log.error(msgText);
		addMessage(componentId, message);
		throw new ValidatorException(message);
	}

	protected void addErrorMessageNoCode(String msgText, boolean addGrowl) {
		FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, msgText, "");
		log.error(msgText);
		addMessage(null, message);
		if (addGrowl) {
			addMessage("growlBox", message);
		}
	}

	protected void addWarnMessage(String codErrore, boolean addGrowl) {
		String msgText = utils.getErrorProperty(codErrore);

		/*
		 * se non trovo la stringa nel file di properties significa che e' gia' un messaggio
		 */
		if (StringUtils.isEmptyNoBlank(msgText)) {
			msgText = codErrore;
		}

		FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_WARN, "", msgText);
		addMessage(null, message);
		if (addGrowl) {
			addMessage("growlBox", message);
		}
	}

	/**
	 * Aggiunge un messaggio da mostrare all'utente come informazione
	 *
	 * @param codErrore
	 *            codice del messaggio che si trova in errors.properties addGrowl se aggiungere una pop-up informativa
	 */
	protected void addInfoMessage(String codErrore, boolean addGrowl) {
		String msgText = utils.getErrorProperty(codErrore);
		/*
		 * se non trovo la stringa nel file di properties significa che e' gia' un messaggio
		 */
		if (StringUtils.isEmptyNoBlank(msgText)) {
			msgText = codErrore;
		}
		FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, msgText, "");
		addMessage(null, message);
		if (addGrowl) {
			addMessage("growlBox", message);
		}
	}

	/**
	 * Aggiunge un errore da mostrare all'utente e stampa sul log lo stacktrace dell'eccezione gestita.
	 *
	 * @param codErrore
	 *            codice del messaggioche si trova in errors.properties
	 */
	protected void addErrorMessage(String codErrore, Exception e) {
		String msgText = utils.getErrorProperty(codErrore);
		FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, msgText, msgText);
		log.error(msgText, e);
		addMessage(null, message);
	}

	protected void addInfoMessage(String clientId, String message) {
		FacesMessage facesMessage = new FacesMessage(FacesMessage.SEVERITY_INFO, message, message);
		FacesContext.getCurrentInstance().addMessage(clientId, facesMessage);
	}

	protected void addWarnMessage(String clientId, String message) {
		FacesMessage facesMessage = new FacesMessage(FacesMessage.SEVERITY_WARN, message, message);
		FacesContext.getCurrentInstance().addMessage(clientId, facesMessage);
	}

	/**
	 * Aggiunge alla pagina una lista di errori contenuta in MySareValidationException
	 *
	 * @param exs
	 */
	protected void addErrorMessages(HashMap<String, List<String>> warnings) {
		for (String warnKey : warnings.keySet()) {
			for (String finalWarn : warnings.get(warnKey)) {
				FacesContext.getCurrentInstance().addMessage(warnKey,
						new FacesMessage(FacesMessage.SEVERITY_ERROR, "", finalWarn));
			}
		}

	}

	/**
	 * Aggiunge alla pagina una lista di warnings
	 *
	 * @param exs
	 */
	protected void addWarningMessages(HashMap<String, List<String>> warnings) {
		for (String warnKey : warnings.keySet()) {
			for (String finalWarn : warnings.get(warnKey)) {
				FacesContext.getCurrentInstance().addMessage(warnKey,
						new FacesMessage(FacesMessage.SEVERITY_WARN, "", finalWarn));
			}
		}

	}

	protected void addInfoMessages(HashMap<String, List<String>> infos) {
		for (String infoKey : infos.keySet()) {
			for (String finalWarn : infos.get(infoKey)) {
				FacesContext.getCurrentInstance().addMessage(infoKey,
						new FacesMessage(FacesMessage.SEVERITY_WARN, "", finalWarn));
			}
		}
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

	public ConstantsSingleton getConstantsSingleton() {
		return constantsSingleton;
	}

	public void setConstantsSingleton(ConstantsSingleton constantsSingleton) {
		this.constantsSingleton = constantsSingleton;
	}

	/**
	 *
	 * @return FeedbackBean
	 *
	 *
	 *         public FeedbackBean getMessages() { return messages; }
	 *
	 *         /**
	 *
	 *
	 *         public void setMessages(FeedbackBean messages) { this.messages = messages; }
	 */
	/**
	 * Metodo per prendere i parametri dalla request
	 *
	 * @param name
	 * @return
	 */
	public String getRequestParameter(String name) {
		return getRequestParameterMap().get(name);
	}

	protected void redirect(String path) {
		try {
			String completePath = getExternalContext().getRequestContextPath() + path;
			log.warn("Redirecting to " + completePath);
			getExternalContext().redirect(completePath);
			return;
		} catch (IOException e) {
			log.error("Errore durante la redirect", e);
		}
	}

	protected void redirect500() {
		redirect("/error/500");
	}

	protected void redirect404() {
		redirect("/error/404");
	}

	protected void redirect403() {
		redirect("/error/403");
	}

	public void unexpectedError() {
		FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "errore inatteso",
				"Si prega di riprovare!");
		RequestContext.getCurrentInstance().showMessageInDialog(message);
	}

	public boolean checkAbilitazioneSistemaVisibile(String codSistema) throws MyCasNoResultException {
		DeSistema deSistemaSare = deSistemaEJB.findById(codSistema);
		return deSistemaSare.getFlgInstallato();
	}

	public AccountInfoBean getAccountInfoBean() {
		return accountInfoBean;
	}

	public void setAccountInfoBean(AccountInfoBean accountInfoBean) {
		this.accountInfoBean = accountInfoBean;
	}

	public void addJSInfoMessage(String message) {
		message = StringEscapeUtils.escapeEcmaScript(message);
		RequestContext.getCurrentInstance().execute("infoMesssage('" + message + "')");
	}

	public void addJSSuccessMessage(String message) {
		message = StringEscapeUtils.escapeEcmaScript(message);
		RequestContext.getCurrentInstance().execute("sucessMessage('" + message + "')");
	}

	public void addJSWarnMessage(String message) {
		message = StringEscapeUtils.escapeEcmaScript(message);
		RequestContext.getCurrentInstance().execute("warnMesssage('" + message + "')");
	}

	public void addJSDangerMessage(String message) {
		message = StringEscapeUtils.escapeEcmaScript(message);
		RequestContext.getCurrentInstance().execute("dangerMesssage('" + message + "')");
	}

	public boolean checkSezioneSareAbilitata() {
		return constantsSingleton.isAbilitatoSare();
	}

	public boolean checkSezioneServiziAmministrativiAbilitato() {
		return constantsSingleton.isAbilitatoServAmministrativi();
	}

}

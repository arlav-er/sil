package it.eng.myportal.beans;

import it.eng.myportal.dtos.DeCittadinanzaDTO;
import it.eng.myportal.dtos.DeComuneDTO;
import it.eng.myportal.dtos.DeCpiDTO;
import it.eng.myportal.dtos.DeProvinciaDTO;
import it.eng.myportal.dtos.EmailDTO;
import it.eng.myportal.dtos.RegisterAziendaDTO;
import it.eng.myportal.dtos.RegisterDTO;
import it.eng.myportal.dtos.RegisterUtenteDTO;
import it.eng.myportal.dtos.UtenteCompletoDTO;
import it.eng.myportal.entity.ejb.ErrorsSingleton;
import it.eng.myportal.entity.enums.TipoAbilitazione;
import it.eng.myportal.entity.home.AziendaInfoHome;
import it.eng.myportal.entity.home.PfPrincipalHome;
import it.eng.myportal.entity.home.UtenteInfoHome;
import it.eng.myportal.entity.home.decodifiche.DeCittadinanzaHome;
import it.eng.myportal.entity.home.decodifiche.DeComuneHome;
import it.eng.myportal.entity.home.decodifiche.DeCpiHome;
import it.eng.myportal.entity.home.decodifiche.DeProvinciaHome;
import it.eng.myportal.entity.home.decodifiche.DeRuoloPortaleHome;
import it.eng.myportal.entity.home.decodifiche.DeTipoAbilitatoHome;
import it.eng.myportal.entity.home.decodifiche.DeTipoDelegatoHome;
import it.eng.myportal.enums.SoftwareSAREUtilizzato;
import it.eng.myportal.exception.ComuneFuoriRegioneException;
import it.eng.myportal.exception.ServiziLavoratoreException;
import it.eng.myportal.utils.ConstantsSingleton;
import it.eng.myportal.utils.ConstantsSingleton.DeTipoAbilitato;
import it.eng.myportal.utils.Mailer;
import it.eng.myportal.utils.Utils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.event.ComponentSystemEvent;
import javax.faces.event.PhaseId;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;
import javax.jms.ConnectionFactory;
import javax.jms.Queue;
import javax.validation.Valid;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

@ManagedBean
@ViewScoped
public class RegistrationBean {

	private final static String RETURN_OK = "register_ok?faces-redirect=true";
	private final static String RETURN_KO = "register_ko?faces-redirect=true";

	@EJB
	protected ErrorsSingleton errorsBean;

	@EJB
	UtenteInfoHome utenteInfoHome;

	@EJB
	AziendaInfoHome aziendaInfoHome;

	@EJB
	PfPrincipalHome pfPrincipalHome;

	@EJB
	DeTipoDelegatoHome deTipoDelegatoHome;

	@EJB
	DeTipoAbilitatoHome deTipoAbilitatoHome;

	@EJB
	DeCittadinanzaHome deCittadinanzaHome;

	@EJB
	DeComuneHome deComuneHome;

	@EJB
	DeProvinciaHome deProvinciaHome;

	@EJB
	DeCpiHome deCpiHome;

	@EJB
	DeRuoloPortaleHome deRuoloPortaleHome;

	@Resource(name = "RemoteConnectionFactory", mappedName = "java:/RemoteConnectionFactory")
	private ConnectionFactory connectionFactory;

	@Resource(name = "email_queue", mappedName = "java:/queue/email_queue")
	private Queue emailQueue;

	private static final Log log = LogFactory.getLog(RegistrationBean.class);

	/**
	 * Dati per la registrazione di un utente
	 */
	private RegisterUtenteDTO utenteData;

	/**
	 * Dati per la registrazione di un'azienda
	 */
	@Valid
	private RegisterAziendaDTO aziendaData;

	/**
	 * Dati di conferma della registrazione
	 */
	private RegisterDTO confirmData;

	private Integer activationCode;

	private Boolean voglioServiziAvanzati;

	private String emailServiziOnline;
	/**
	 * Variabile settata quando viene invocato il metodo per ottenere il token di autenticazione forte. True se la
	 * provincia inserita dall'utente in fase di registrazione e' la stessa presente sull'indice regionale, false
	 * altrimenti.
	 */
	private Boolean richiediConfermaPerPEC;
	private Boolean indiceRegionaleInvocato;

	/**
	 * campo utilizzato quando l'utente conferma il proprio account per mostragli il codice a schermo se il valore è
	 * null significa che non ha effettuato la richiesta di accreditamento forte
	 */
	private String codiceRichiestaAutForte;
	private String emailProvincia;

	/**
	 * Tipi possibili di richiedente nella registrazione dell'azienda
	 */
	private List<SelectItem> tipiRichiedente;

	/**
	 * Tipi possibili di delegato nella registrazione dell'azienda
	 */
	private List<SelectItem> tipiDelegato;

	/**
	 * Tipi di software utilizzati per le comunicazioni oblbigatorie
	 */
	private List<SelectItem> softwareSAREUtilizzati;

	/**
	 * Determina se è possibile scegliere il tipo di delegato in fase di registrazione
	 */
	private boolean editTipoDelegato;

	/**
	 * Determina se è possibile scegliere il soggetto abilitato in fase di registrazione
	 */
	private boolean editSoggettoAbilitato;

	/**
	 * Determina se è possibile scegliere l'agenzia di somministrazione in fase di registrazione
	 */
	private boolean editAgenziaSomministrazione;

	/**
	 * Determina se mostrare la sezione 'Dati agenzia di somministrazione' in fase di registrazione
	 */
	private Boolean showAgenziaSomministrazioneSection;

	/**
	 * Determina se mostrare la sezione 'Dati soggetto abilitato' in fase di registrazione
	 */
	private Boolean showDatiSoggettoAbilitatoSection;

	/**
	 * Determina se la provincia di riferimento debba essere scelta dall'utente o se viene impostata in automatico dal
	 * sistema
	 */
	private Boolean editProvinciaRiferimento;

	public RegistrationBean() {
		super();
	}

	@PostConstruct
	public void postConstruct() {
		utenteData = new RegisterUtenteDTO();
		aziendaData = new RegisterAziendaDTO();
		confirmData = new RegisterDTO();
		tipiRichiedente = deTipoAbilitatoHome.getListItems(false);
		tipiDelegato = deTipoDelegatoHome.getListItems(true);
		softwareSAREUtilizzati = SoftwareSAREUtilizzato.asSelectItems();
		editTipoDelegato = false;
		showAgenziaSomministrazioneSection = false;
		showDatiSoggettoAbilitatoSection = false;
		editProvinciaRiferimento = false;
		aziendaData.setRichiestaAccessoSARE(false);
		voglioServiziAvanzati = false;

		richiediConfermaPerPEC = false;
		indiceRegionaleInvocato = false;
		// aziendaData.setTipoDelegato(ConstantsSingleton.DeTipoDelegato.PROMOTORE_TIROCINI);
		FacesContext.getCurrentInstance().getExternalContext().getSession(true); // bugfix
	}

	/**
	 * Quando cambia il tipo di richiedente cambiano anche i campi da compilare.
	 * 
	 * @param event
	 *            evento di cambio valore del tipo richiedente
	 */
	public void changeTipoRichiedente(ValueChangeEvent event) {
		String val = (String) event.getNewValue();
		if (val == null) {
			editTipoDelegato = false;
			editSoggettoAbilitato = false;
			editAgenziaSomministrazione = false;
			aziendaData.setTipoDelegato(null);
			showAgenziaSomministrazioneSection = false;
			showDatiSoggettoAbilitatoSection = false;
		} else if (DeTipoAbilitato.DATORE_PRIVATO.equals(val)) {
			editTipoDelegato = false;
			editSoggettoAbilitato = false;
			editAgenziaSomministrazione = false;
			aziendaData.setTipoDelegato(null);
			showAgenziaSomministrazioneSection = false;
			showDatiSoggettoAbilitatoSection = false;
		} else if (DeTipoAbilitato.DATORE_PUBBLICO.equals(val)) {
			editTipoDelegato = false;
			editSoggettoAbilitato = false;
			editAgenziaSomministrazione = false;
			aziendaData.setTipoDelegato(null);
			showAgenziaSomministrazioneSection = false;
			showDatiSoggettoAbilitatoSection = false;
		} else if (DeTipoAbilitato.AGENZIA_SOMMINISTRAZIONE.equals(val)) {
			editTipoDelegato = false;
			editSoggettoAbilitato = false;
			editAgenziaSomministrazione = true;
			aziendaData.setTipoDelegato(null);
			showAgenziaSomministrazioneSection = true;
			showDatiSoggettoAbilitatoSection = false;
		} else if (DeTipoAbilitato.SOGGETTO_ABILITATO.equals(val)) {
			editTipoDelegato = true;
			editSoggettoAbilitato = true;
			editAgenziaSomministrazione = false;
			aziendaData.setTipoDelegato(null);
			showAgenziaSomministrazioneSection = false;
			showDatiSoggettoAbilitatoSection = true;
		} else if (DeTipoAbilitato.AGENZIA_LAVORO.equals(val)) {
			editTipoDelegato = false;
			editSoggettoAbilitato = false;
			editAgenziaSomministrazione = false;
			aziendaData.setTipoDelegato(ConstantsSingleton.DeTipoDelegato.SOGGETTO_INTERMEDIAZIONE);
			showAgenziaSomministrazioneSection = false;
			showDatiSoggettoAbilitatoSection = false;
		} else if (DeTipoAbilitato.SOGGETTO_TIROCINI.equals(val)) {
			editTipoDelegato = false;
			editSoggettoAbilitato = false;
			editAgenziaSomministrazione = false;
			aziendaData.setTipoDelegato(ConstantsSingleton.DeTipoDelegato.PROMOTORE_TIROCINI);
			showAgenziaSomministrazioneSection = false;
			showDatiSoggettoAbilitatoSection = false;
		} else {
			log.error("Errore nella gestione del tipo delegato. Tipo '" + val + "' sconosciuto.");
			editTipoDelegato = false;
			editSoggettoAbilitato = false;
			editAgenziaSomministrazione = false;
			aziendaData.setTipoDelegato(null);
			showAgenziaSomministrazioneSection = false;
			showDatiSoggettoAbilitatoSection = false;
		}
	}

	/**
	 * utilizzo sempre lo stesso boolean, basta invocare il refresh
	 * 
	 * @param event
	 */
	public void changeListenerAccessoSARE(ValueChangeEvent event) {
	}

	/**
	 * Quando cambia il comune di domicilio è necessario verificare se
	 * 
	 * @param event
	 *            evento di cambio valore del comune di domicilio
	 */
	public void changeComuneDomicilio(AjaxBehaviorEvent event) {
		controlloCambioComuneDomicilio();
		popolaEmailServiziOnline();
	}

	/**
	 * Controlla che il comune di domicilio sia in regione EMILIA-ROMAGNA. Se il controllo fallisce allora bisogna
	 * inserire manualmente la provincia di riferimento (in EMILIA-ROMAGNA)
	 * 
	 * ATTENZIONE: Il metodo e' duplicato in "UserInfoBean"
	 */
	private void controlloCambioComuneDomicilio() {
		try {
			Map<String, String> map = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();

			String comuneDomicilio = map.get("register_cittadino:comune_domicilio:inputText");
			String codComuneDomicilio = map.get("register_cittadino:comune_domicilio:inputHidden");

			if (deComuneHome.comuneDomicilioInRER(comuneDomicilio, codComuneDomicilio)) {
				DeProvinciaDTO provinciaDomicilio = deComuneHome.findProvinciaDTOByComune(comuneDomicilio,
						codComuneDomicilio);
				utenteData.setProvincia(provinciaDomicilio);
				editProvinciaRiferimento = false;
			} else {
				utenteData.setProvincia(new DeProvinciaDTO());
				editProvinciaRiferimento = true;
			}
		} catch (EJBException e) {
			utenteData.setProvincia(new DeProvinciaDTO());
			editProvinciaRiferimento = true;
		}
	}

	/**
	 * Registra un utente al sistema in stato inattivo.
	 * 
	 * @return 'register_ok' se la validazione è andata a buon fine, 'register_ko' altrimenti
	 */
	public String registraUtente() {
		try {
			// Controllo registrazione servizi avanzati
			if (voglioServiziAvanzati) {
				this.checkRegioneAppartenenzaDomicilio();
				generaCodiceRichiestaAutforte();
			}
			/*
			 * impostagli una stringa random per la validazione dell'indirizzo email
			 */
			utenteData.setActivateToken(Utils.randomString(ConstantsSingleton.TOKEN_LENGTH));
			/* imposta lo stato su NON ATTIVO */
			utenteData.setAttivo(false);
			/* richiama il servizio per la registrazione */
			utenteInfoHome.register(utenteData, false);

			EmailDTO registrationEmail = EmailDTO.buildRegistrationEmail(utenteData);
			Mailer.getInstance().putInQueue(connectionFactory, emailQueue, registrationEmail);
		} catch (RuntimeException e) {
			FacesMessage message = new FacesMessage();
			message.setSummary("Errore durante la registrazione dell'account. Spiacenti");
			message.setSeverity(FacesMessage.SEVERITY_ERROR);
			FacesContext.getCurrentInstance().addMessage(null, message);
			return "";
		} catch (ComuneFuoriRegioneException e) {
			this.addErrorMessage(e.getErrorMessageKey());
			return "";
		}
		return RETURN_OK;
	}

	/**
	 * Registra un utente al sistema in stato inattivo.
	 * 
	 * @return 'register_ok' se la validazione è andata a buon fine, 'register_ko' altrimenti
	 */
	public String registraAzienda() {

		try {

			/*
			 * impostagli una stringa random per la validazione dell'indirizzo email
			 */
			aziendaData.setActivateToken(Utils.randomString(ConstantsSingleton.TOKEN_LENGTH));
			/* imposta lo stato su NON ATTIVO */
			aziendaData.setAttivo(false);
			/* richiama il servizio per la registrazione */
			aziendaInfoHome.register(aziendaData, false);

			EmailDTO registrationEmail = EmailDTO.buildRegistrationEmail(aziendaData);
			Mailer.getInstance().putInQueue(connectionFactory, emailQueue, registrationEmail);
		} catch (RuntimeException e) {
			log.debug("register_user FAILED");
			FacesMessage message = new FacesMessage();
			message.setSummary("Errore durante la registrazione dell'account. Spiacenti");
			message.setSeverity(FacesMessage.SEVERITY_ERROR);
			FacesContext.getCurrentInstance().addMessage(null, message);
			return "";
		}
		return RETURN_OK;
	}

	public void conferma(ComponentSystemEvent event) throws AbortProcessingException {
		confirmData.setAttivo(true);

		try {
			activationCode = utenteInfoHome.confirm(confirmData);
		} catch (RuntimeException e) {
			activationCode = ConstantsSingleton.Register.ACTIVATION_ERROR;
		}

		if (ConstantsSingleton.Register.USER_ACTIVATED.equals(activationCode)) {
			if (pfPrincipalHome.isUtente(confirmData.getUsername())) {
				UtenteCompletoDTO utente = utenteInfoHome.findDTOCompletoByUsername(confirmData.getUsername());
				codiceRichiestaAutForte = utente.getCodiceRichiestaAutForte();
				DeCpiDTO deCpi = null;
				DeComuneDTO comDom = utente.getComuneDomicilio();
				DeProvinciaDTO provDom = deProvinciaHome.findDTOById(comDom.getIdProvincia());

				if (ConstantsSingleton.COD_REGIONE.equals(new Integer(provDom.getIdRegione()))) {
					deCpi = deCpiHome.findDTOByCodComune(utente.getComuneDomicilio().getId());
				} else {
					DeProvinciaDTO provRif = utente.getUtenteInfo().getProvinciaRiferimento();
					DeComuneDTO comRif = deComuneHome.findDTOByDenominazione(provRif.getDescrizione());
					deCpi = deCpiHome.findDTOByCodComune(comRif.getId());
				}
				emailServiziOnline = deCpi.getEmailServiziOnline();

				EmailDTO confirmRegistrationEmail = EmailDTO.buildConfirmRegistrazioneForteEmail(confirmData,
						codiceRichiestaAutForte, emailServiziOnline);
				Mailer.getInstance().putInQueue(connectionFactory, emailQueue, confirmRegistrationEmail);
			} else if (pfPrincipalHome.isAzienda(confirmData.getUsername())) {

				EmailDTO confirmRegistrationEmail = EmailDTO.buildConfirmRegistrationeEmail(confirmData);
				Mailer.getInstance().putInQueue(connectionFactory, emailQueue, confirmRegistrationEmail);
			}

		}
		return;
	}

	public RegisterUtenteDTO getUtenteData() {
		return utenteData;
	}

	public void setUtenteData(RegisterUtenteDTO data) {
		this.utenteData = data;
	}

	public RegisterAziendaDTO getAziendaData() {
		return aziendaData;
	}

	public void setAziendaData(RegisterAziendaDTO aziendaData) {
		this.aziendaData = aziendaData;
	}

	public RegisterDTO getConfirmData() {
		return confirmData;
	}

	public void setConfirmData(RegisterDTO confirmData) {
		this.confirmData = confirmData;
	}

	public Integer getActivationCode() {
		return activationCode;
	}

	public void setActivationCode(Integer activation) {
		this.activationCode = activation;
	}

	public List<SelectItem> getTipiRichiedente() {
		return tipiRichiedente;
	}

	public void setTipiRichiedente(List<SelectItem> tipiRichiedente) {
		this.tipiRichiedente = tipiRichiedente;
	}

	public List<SelectItem> getSoftwareSAREUtilizzati() {
		return softwareSAREUtilizzati;
	}

	public void setSoftwareSAREUtilizzati(List<SelectItem> softwareSAREUtilizzati) {
		this.softwareSAREUtilizzati = softwareSAREUtilizzati;
	}

	public Boolean getEditTipoDelegato() {
		return editTipoDelegato;
	}

	public void setEditTipoDelegato(Boolean editTipoDelegato) {
		this.editTipoDelegato = editTipoDelegato;
	}

	public Boolean getEditSoggettoAbilitato() {
		return editSoggettoAbilitato;
	}

	public void setEditSoggettoAbilitato(Boolean editSoggettoAbilitato) {
		this.editSoggettoAbilitato = editSoggettoAbilitato;
	}

	public Boolean getEditAgenziaSomministrazione() {
		return editAgenziaSomministrazione;
	}

	public void setEditAgenziaSomministrazione(Boolean editAgenziaSomministrazione) {
		this.editAgenziaSomministrazione = editAgenziaSomministrazione;
	}

	public Boolean getShowAgenziaSomministrazioneSection() {
		return showAgenziaSomministrazioneSection;
	}

	public void setShowAgenziaSomministrazioneSection(Boolean showAgenziaSomministrazioneSection) {
		this.showAgenziaSomministrazioneSection = showAgenziaSomministrazioneSection;
	}

	public Boolean getShowDatiSoggettoAbilitatoSection() {
		return showDatiSoggettoAbilitatoSection;
	}

	public void setShowDatiSoggettoAbilitatoSection(Boolean showDatiSoggettoAbilitatoSection) {
		this.showDatiSoggettoAbilitatoSection = showDatiSoggettoAbilitatoSection;
	}

	public List<SelectItem> getTipiDelegato() {
		return tipiDelegato;
	}

	public void setTipiDelegato(List<SelectItem> tipiDelegato) {
		this.tipiDelegato = tipiDelegato;
	}

	public Boolean getEditProvinciaRiferimento() {
		return editProvinciaRiferimento;
	}

	public void setEditProvinciaRiferimento(Boolean editProvinciaRiferimento) {
		this.editProvinciaRiferimento = editProvinciaRiferimento;
	}

	public Boolean getRichiediConfermaPerPEC() {
		return richiediConfermaPerPEC;
	}

	public void setRichiediConfermaPerPEC(Boolean richiediConfermaPerPEC) {
		this.richiediConfermaPerPEC = richiediConfermaPerPEC;
	}

	public Boolean getVoglioServiziAvanzati() {
		return voglioServiziAvanzati;
	}

	public void setVoglioServiziAvanzati(Boolean voglioServiziAvanzati) {
		this.voglioServiziAvanzati = voglioServiziAvanzati;
	}

	public Boolean getIndiceRegionaleInvocato() {
		return indiceRegionaleInvocato;
	}

	public void setIndiceRegionaleInvocato(Boolean indiceRegionaleInvocato) {
		this.indiceRegionaleInvocato = indiceRegionaleInvocato;
	}

	/**
	 * Controlla che la provincia di residenza dell'utente che richiede il codice sia la stessa presente nell'archivio
	 * regionale. Questo per individuare il CPI di riferimento. In caso contrario viene visualizzato un alert all'utente
	 * chiedendo se continuare o meno l'operazione.
	 * 
	 * @return
	 */
	public void ottieniCodice() {
		try {
			// controllo provincia di domicilio inserita con provincia cpi di
			// riferimento in archivio
			DeProvinciaDTO provinciaDomicilioDTO = utenteData.getProvincia();

			boolean stessaProvincia = utenteInfoHome.controlloProvinciaDomicilio(utenteData.getNome(),
					utenteData.getCognome(), utenteData.getDataNascita(), utenteData.getCodiceFiscale(),
					provinciaDomicilioDTO.getId());

			DeComuneDTO comuneDomicilio = utenteData.getDomicilio();
			DeCpiDTO deCpi = deCpiHome.findDTOByCodComune(comuneDomicilio.getId());
			emailServiziOnline = deCpi.getEmailServiziOnline();

			if (stessaProvincia) {
				richiediConfermaPerPEC = false;
				generaCodiceRichiestaAutforte();
			} else {
				richiediConfermaPerPEC = true;
			}
			indiceRegionaleInvocato = true;
		} catch (ServiziLavoratoreException e) {
			log.error("Errore nell'invocazione del servizio dell'Indice Regionale: getLavoratoreIR()");
			addErrorMessage("retrieving_pec");
		}
	}

	/**
	 * Metodo che genera il codice di autenticazione forte per l'utente.
	 */
	public void generaCodiceRichiestaAutforte() {
		String codice = utenteInfoHome.generaCodiceRichiestaAutforte();
		utenteData.setCodiceRichiestaAutForte(codice);
		richiediConfermaPerPEC = false;
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

	public void changeCittadinanza(ValueChangeEvent event) {
		String val = (String) event.getNewValue();
		if (val == null)
			return;

		List<DeCittadinanzaDTO> lista = deCittadinanzaHome.findByDescription(val);
		if (lista != null && !lista.isEmpty()) {
			utenteData.setCittadinanza(lista.get(0));
		}

	}

	public void changeCheckServiziAvanzati(ValueChangeEvent event) {
		PhaseId phaseId = event.getPhaseId();
		// voglio che scatti dopo il set del modello altrimenti non riesco a
		// rimettere il vecchio valore al check
		Boolean newValue = (Boolean) event.getNewValue();
		if (phaseId.equals(PhaseId.ANY_PHASE)) {
			event.setPhaseId(PhaseId.UPDATE_MODEL_VALUES);
			event.queue();
		} else if (phaseId.equals(PhaseId.UPDATE_MODEL_VALUES)) {
			if (newValue) {
				// il domicilio deve essere interno alla regione di
				// installazione
				try {
					this.checkRegioneAppartenenzaDomicilio();
				} catch (ComuneFuoriRegioneException e) {
					this.setVoglioServiziAvanzati(false);
					this.addErrorMessage(e.getErrorMessageKey());
					FacesContext.getCurrentInstance().renderResponse();
				}
			}
		}

	}

	private void checkRegioneAppartenenzaDomicilio() throws ComuneFuoriRegioneException {
		DeComuneDTO domicilio = this.getUtenteData().getDomicilio();
		if (domicilio.getId() != null
				&& !deComuneHome.comuneDomicilioInRER(domicilio.getDescrizione(), domicilio.getId())) {
			throw new ComuneFuoriRegioneException();
		}
	}

	public String getEmailProvincia() {
		return emailProvincia;
	}

	public void setEmailProvincia(String emailProvincia) {
		this.emailProvincia = emailProvincia;
	}

	/**
	 * 
	 */
	public void cancelRichiediConfermaPec() {
		richiediConfermaPerPEC = false;
		indiceRegionaleInvocato = false;
	}

	public String getEmailServiziOnline() {
		return emailServiziOnline;
	}

	public void setEmailServiziOnline(String emailServiziOnline) {
		this.emailServiziOnline = emailServiziOnline;
	}

	public String getCodiceRichiestaAutForte() {
		return codiceRichiestaAutForte;
	}

	public void setCodiceRichiestaAutForte(String codiceRichiestaAutForte) {
		this.codiceRichiestaAutForte = codiceRichiestaAutForte;
	}

	public void popolaEmailServiziOnline() {
		DeComuneDTO comuneDomicilio = utenteData.getDomicilio();
		DeCpiDTO deCpi = deCpiHome.findDTOByCodComune(comuneDomicilio.getId());
		emailServiziOnline = deCpi.getEmailServiziOnline();
	}

	public Boolean checkAbilitazioneVisibile(String codAttivita, String tipoUtente) {
		Boolean check = false;
		HashMap<String, Map<TipoAbilitazione, Boolean>> abilitazioni = new HashMap<String, Map<TipoAbilitazione, Boolean>>();

		if ("cittadino".equalsIgnoreCase(tipoUtente)) {
			abilitazioni = deRuoloPortaleHome.getProfileCittadino();
		} else if ("azienda".equalsIgnoreCase(tipoUtente)) {
			abilitazioni = deRuoloPortaleHome.getProfileAzienda();
		}

		Map<TipoAbilitazione, Boolean> mapFlags = abilitazioni.get(codAttivita);
		if (mapFlags != null) {
			check = mapFlags.get(TipoAbilitazione.VISIBILE);
		}

		return check;
	}

	public String getMyPortalBaseUrl() {
		return ConstantsSingleton.BASE_URL;
	}

}

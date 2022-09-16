package it.eng.myportal.beans.amministrazione;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import javax.jms.ConnectionFactory;
import javax.jms.Queue;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import it.eng.myportal.beans.AbstractBaseBean;
import it.eng.myportal.beans.AuthenticateHomeBean;
import it.eng.myportal.beans.amministrazione.pojo.UtentePojo;
import it.eng.myportal.dtos.EmailDTO;
import it.eng.myportal.dtos.PfPrincipalDTO;
import it.eng.myportal.dtos.RegisterAziendaDTO;
import it.eng.myportal.dtos.RegisterDTO;
import it.eng.myportal.dtos.RegisterUtenteDTO;
import it.eng.myportal.entity.home.PfIdentityProviderHome;
import it.eng.myportal.entity.home.PfPrincipalHome;
import it.eng.myportal.entity.home.PtScrivaniaHome;
import it.eng.myportal.utils.Mailer;
import it.eng.myportal.utils.PaginationHandler;
import it.eng.myportal.utils.Utils;

/**
 * @author Enrico D'Angelo
 */
@ManagedBean
@ViewScoped
public class ResetPasswordBean extends AbstractBaseBean {

	protected static Log log = LogFactory.getLog(ResetPasswordBean.class);
	private static final int ITEMS_PER_PAGE = 10;

	private PaginationHandler paginationHandler;
	private List<UtentePojo> listaUtenti = new ArrayList<UtentePojo>();
	private UtentePojo selectedUtentePojo;

	private String altEmailToConfirm;

	private List<SelectItem> tipiRicerca;
	private String tipoRicerca = "TUTTI";
	private String username;
	private String nome;
	private String cognome;
	private String ragioneSocAzienda;
	private String codiceFiscale;
	private String email;

	private final String passwordTemporanea = "Temporanea123";
	private Boolean ricercaEseguita = false;

	@Resource(name = "RemoteConnectionFactory", mappedName = "java:/RemoteConnectionFactory")
	private ConnectionFactory connectionFactory;

	@Resource(name = "email_queue", mappedName = "java:/queue/email_queue")
	private Queue emailQueue;

	@EJB
	PfPrincipalHome pfPrincipalHome;

	@EJB
	PfIdentityProviderHome pfIdentityProviderHome;

	@EJB
	PtScrivaniaHome ptScrivaniaHome;

	/**
	 * Inizializza il bean.
	 */
	@PostConstruct
	public void postConstruct() {
		if ("amministratore".equals(getSession().getUsername()) || "ut_reset".equals(getSession().getUsername())) {
			inizializzaTipiRicerca();
		} else {
			redirectPublicIndex();
		}
	}

	/** Inizializza il selectOneMenu con i vari tipi di ricerca. */
	private void inizializzaTipiRicerca() {
		tipiRicerca = new ArrayList<SelectItem>();
		tipiRicerca.add(new SelectItem("TUTTI", "Tutti"));
		tipiRicerca.add(new SelectItem("CITTADINO", "Cittadino"));
		tipiRicerca.add(new SelectItem("AZIENDA", "Azienda"));
		tipiRicerca.add(new SelectItem("PROVINCIA", "Provincia"));
		tipiRicerca.add(new SelectItem("REGIONE", "Regione"));
		tipiRicerca.add(new SelectItem("CONSULENTE", "Consulente"));
		tipiRicerca.add(new SelectItem("COORDINATORE", "Coordinatore"));
		tipiRicerca.add(new SelectItem("CERTIFICATORE", "Certificatore"));
	}

	/**
	 * Questo metodo viene chiamato quando l'utente esegue la ricerca.
	 */
	public void cercaUtente() {
		Long ricercaCount = pfPrincipalHome.findUtenteCountByFilter(username, nome, cognome, ragioneSocAzienda,
				codiceFiscale, email, tipoRicerca);
		paginationHandler = new PaginationHandler(0, ITEMS_PER_PAGE, ricercaCount);
		paginationHandler.setCurrentPage(1);
		ricercaEseguita = true;
		changePage();
	}

	/** Metodo chiamato quando l'utente cambia pagina. */
	public void changePage() {
		int startResultsFrom = paginationHandler.calculateStart(paginationHandler.getCurrentPage());
		listaUtenti = pfPrincipalHome.findUtenteByFilter(username, nome, cognome, ragioneSocAzienda, codiceFiscale,
				email, tipoRicerca, startResultsFrom, paginationHandler.getChuckSize());
	}

	/** Azzera il contenuto dei campi di ricerca. */
	public void resetSearchParams() {
		username = null;
		nome = null;
		cognome = null;
		ragioneSocAzienda = null;
		codiceFiscale = null;
		email = null;
	}

	/**
	 * Invalida la password dell'utente facendoigliela scadeer
	 */
	public void invalidaPassword(Integer idPfPrincipal) {
		try {
			pfPrincipalHome.invalidaPassword(idPfPrincipal);
			changePage();
			addInfoMessage("data.updated");
		} catch (EJBException e) {
			gestisciErrore(e, "generic.error");
		}
	}

	/**
	 * Ri-setta la password dell'utente al valore di default: "Temporanea123"
	 */
	public void resetPassword(Integer idPfPrincipal) {
		try {
			String passwordTemporaneaEncrypted = Utils.SHA1.encrypt(passwordTemporanea);
			pfPrincipalHome.resetPassword(idPfPrincipal, passwordTemporaneaEncrypted);
			changePage();
			addInfoMessage("data.updated");
		} catch (EJBException e) {
			gestisciErrore(e, "generic.error");
		}
	}

	/**
	 * Abilita un utente.
	 */
	public void abilitaUtente(UtentePojo utente) {
		try {
			pfPrincipalHome.abilitaUtente(utente.getIdPfPrincipal());
			changePage();
			addInfoMessage("data.updated");
		} catch (Exception e) {
			addErrorMessage("generic.error", e);
		}
	}

	/**
	 * Disabilita un utente.
	 */
	public void disabilitaUtente() {
		try {
			pfPrincipalHome.disabilitaUtente(selectedUtentePojo.getIdPfPrincipal());
			changePage();
			addInfoMessage("data.updated");
		} catch (Exception e) {
			addErrorMessage("generic.error", e);
		}
	}

	/**
	 * Cancella tutte le portlet dalla scrivania di un utente.
	 * Verranno re-inserite quelle predefinite al successivo login
	 * @see findByCodRuoloPortale() e {@link AuthenticateHomeBean}
	 */
	public void resetScrivania(UtentePojo utente) {
		try {
			ptScrivaniaHome.deleteAllPtScrivaniaByUtente(utente.getIdPfPrincipal());
			changePage();
			addInfoMessage("data.updated");
		} catch (Exception e) {
			addErrorMessage("generic.error", e);
		}
	}

	/**
	 * Rimanda una mail di attivazione account all'utente.
	 */
	public void sendMail() {
		try {
			EmailDTO mail;
			Integer idPfPrincipal = selectedUtentePojo.getIdPfPrincipal();
			PfPrincipalDTO pfdto = pfPrincipalHome.findDTOById(idPfPrincipal);

			if (pfPrincipalHome.isAzienda(idPfPrincipal)) {
				RegisterAziendaDTO dto = new RegisterAziendaDTO();

				dto.setNome(pfdto.getNome());
				dto.setCognome(pfdto.getCognome());
				dto.setUsername(pfdto.getUsername());
				dto.setEmail(pfdto.getEmail());
				dto.setActivateToken(pfdto.getConfirmationToken());

				mail = EmailDTO.buildRegistrationEmail(dto,
						(altEmailToConfirm != null && !altEmailToConfirm.isEmpty()) ? altEmailToConfirm : null);
			} else if (pfPrincipalHome.isUtente(idPfPrincipal)) {
				RegisterUtenteDTO dto = new RegisterUtenteDTO();

				dto.setNome(pfdto.getNome());
				dto.setCognome(pfdto.getCognome());
				dto.setUsername(pfdto.getUsername());
				dto.setEmail(pfdto.getEmail());
				dto.setActivateToken(pfdto.getConfirmationToken());

				mail = EmailDTO.buildRegistrationEmail(dto,
						(altEmailToConfirm != null && !altEmailToConfirm.isEmpty()) ? altEmailToConfirm : null);
			} else {
				RegisterDTO dto = new RegisterDTO();

				dto.setNome(pfdto.getNome());
				dto.setCognome(pfdto.getCognome());
				dto.setUsername(pfdto.getUsername());
				dto.setEmail(pfdto.getEmail());
				dto.setActivateToken(pfdto.getConfirmationToken());

				mail = EmailDTO.buildRegistrationEmailAtipici(dto, false,
						(altEmailToConfirm != null && !altEmailToConfirm.isEmpty()) ? altEmailToConfirm : null);
			}

			Mailer.getInstance().putInQueue(connectionFactory, emailQueue, mail);
			addMessage(null, new FacesMessage("Email Inviata"));
			changePage();
			selectedUtentePojo = null;
			altEmailToConfirm = null;
		} catch (EJBException e) {
			gestisciErrore(e, "generic.error");
		}
	}

	/*
	 * =================== QUA CI SONO GETTER/SETTER PARTICOLARI ================
	 */

	/** Dice se visualizzare il parametro di ricerca "Codice fiscale" */
	public boolean getRenderCodiceFiscale() {
		return (tipoRicerca.equals("CITTADINO") || tipoRicerca.equals("AZIENDA") || tipoRicerca.equals("CERTIFICATORE") || tipoRicerca
				.equals("TUTTI"));
	}

	/** Dice se visualizzare il parametro di ricerca "Ragione sociale" */
	public boolean getRenderRagioneSociale() {
		return (tipoRicerca.equals("AZIENDA") || tipoRicerca.equals("CERTIFICATORE") || tipoRicerca.equals("TUTTI"));
	}

	public void setTipoRicerca(String tipoRicerca) {
		this.tipoRicerca = tipoRicerca;
		resetSearchParams();
	}

	public List<UtentePojo> getListaUtenti() {
		return listaUtenti;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getCognome() {
		return cognome;
	}

	public String getTipoRicerca() {
		return tipoRicerca;
	}

	public List<SelectItem> getTipiRicerca() {
		return tipiRicerca;
	}

	public Boolean getRicercaEseguita() {
		return ricercaEseguita;
	}

	public void setRicercaEseguita(Boolean ricercaEseguita) {
		this.ricercaEseguita = ricercaEseguita;
	}

	public void setCognome(String cognome) {
		this.cognome = cognome;
	}

	public String getRagioneSocAzienda() {
		return ragioneSocAzienda;
	}

	public void setRagioneSocAzienda(String ragioneSocAzienda) {
		this.ragioneSocAzienda = ragioneSocAzienda;
	}

	public String getCodiceFiscale() {
		return codiceFiscale;
	}

	public void setCodiceFiscale(String codiceFiscale) {
		this.codiceFiscale = codiceFiscale;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}
	
	public String getAltEmailToConfirm() {
		return altEmailToConfirm;
	}

	public void setAltEmailToConfirm(String altEmailToConfirm) {
		this.altEmailToConfirm = altEmailToConfirm;
	}

	public PaginationHandler getPaginationHandler() {
		return paginationHandler;
	}

	public UtentePojo getSelectedUtentePojo() {
		return selectedUtentePojo;
	}

	public void setSelectedUtentePojo(UtentePojo selectedUtentePojo) {
		this.selectedUtentePojo = selectedUtentePojo;
	}

	
}

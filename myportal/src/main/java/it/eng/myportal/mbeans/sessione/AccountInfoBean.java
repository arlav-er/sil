package it.eng.myportal.mbeans.sessione;

import it.eng.myportal.auth.AuthUtil;
import it.eng.myportal.utils.ConstantsSingleton;
import it.eng.sil.base.business.GestioneProfiloRemoteClient;
import it.eng.sil.base.pojo.auth.gp.UserProfiloPOJO;

import java.io.IOException;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.omnifaces.util.Faces;

/**
 * 
 * ManagedBean JSF per contenere i dati dell'utente che ha effettuato l'accesso al sistema. Il Bean viene creato una
 * sola volta per ogni sessione e ricava tutte le informazioni utili sull'utente connesso.
 * 
 * 
 */
@ManagedBean(name = "accountInfoBean")
@SessionScoped
public class AccountInfoBean {
	protected static Log log = LogFactory.getLog(AccountInfoBean.class);

	// Dati dell'utente attualmente collegato al sistema
	private UserProfiloPOJO userInfo;
	private String username;
	private String urlLogout;
	private String outcome;
	private Integer codGruppo;
	private Integer idPfPrincipal;
	private String codFunzione;

	public AccountInfoBean() {
		getUser();
	}

	public AccountInfoBean(String username) {
		this.username = AuthUtil.removeSocialPrefix(username);
		getUser();
	}

	@PostConstruct
	public void postConstruct() {
		refreshSession();
		log.info("L'utente ha effettuato login con successo.");
		retrieveUrlLogout();
		getUser();
	}

	private void getUser() {
		// Recupero lo username dalla sessione.
		if (username == null) {
			FacesContext fc = FacesContext.getCurrentInstance();
			ExternalContext ec = fc.getExternalContext();
			username = AuthUtil.removeSocialPrefix(ec.getUserPrincipal().getName());
		}

		// Chiamo la getProfilo di MyAuthService per recuperare tutte le informazioni dell'utente.
		try {
			GestioneProfiloRemoteClient profiloRemoteClient = new GestioneProfiloRemoteClient();
			userInfo = profiloRemoteClient.getProfilo(ConstantsSingleton.AUTHENTICATE_URL, username);
			idPfPrincipal = userInfo.getIdPfPrincipal();
		} catch (Exception e) {
			log.error("Errore durante la getUserProfilo in AccountInfoBean: " + e.toString());
		}
	}

	public String logout() {
		ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
		HttpSession session = (HttpSession) ec.getSession(true);
		session.invalidate();

		log.info("L'utente ha effettuato logout con successo.");
		try {
			Faces.redirect(getUrlLogout(), null);
		} catch (IOException e) {
			log.error("Redirect failed: " + e.getMessage());
		}
		return null;
	}

	private void refreshSession() {
		try {
			FacesContext fc = FacesContext.getCurrentInstance();
			ExternalContext ec = fc.getExternalContext();

			username = AuthUtil.removeSocialPrefix(ec.getUserPrincipal().getName());

			try {
				GestioneProfiloRemoteClient profiloRemoteClient = new GestioneProfiloRemoteClient();
				userInfo = profiloRemoteClient.getProfilo(ConstantsSingleton.AUTHENTICATE_URL, username);
				idPfPrincipal = userInfo.getIdPfPrincipal();
			} catch (Exception e) {
				log.error("Errore durante la getUserProfilo in AccountInfoBean: " + e.toString());
			}
		} catch (Exception e) {
			log.error("Errore durante l'inizializzazione del sessionBean");
			logout();
		}
	}

	public String getUsername() {
		return username;
	}

	public String getUrlLogout() {
		retrieveUrlLogout();
		return urlLogout;

	}

	public void retrieveUrlLogout() {

		FacesContext fc = FacesContext.getCurrentInstance();
		ExternalContext ec = fc.getExternalContext();
		String casServerUrlPrefix = ec.getInitParameter("casServerUrlPrefix");

		log.debug("URL del CAS: " + casServerUrlPrefix);
		urlLogout = casServerUrlPrefix + "/logout";
	}

	public UserProfiloPOJO getUserInfo() {
		return userInfo;
	}

	public void setUserInfo(UserProfiloPOJO userInfo) {
		this.userInfo = userInfo;
	}

	public String getOutcome() {
		return outcome;
	}

	public void setOutcome(String outcome) {
		this.outcome = outcome;
	}

	public Integer getCodGruppo() {
		return codGruppo;
	}

	public void setCodGruppo(Integer codGruppo) {
		this.codGruppo = codGruppo;
	}

	public Integer getIdPfPrincipal() {
		return idPfPrincipal;
	}

	public void setIdPfPrincipal(Integer idPfPrincipal) {
		this.idPfPrincipal = idPfPrincipal;
	}

	public String getCodFunzione() {
		return codFunzione;
	}

	public void setCodFunzione(String codFunzione) {
		this.codFunzione = codFunzione;
	}

}

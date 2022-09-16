package it.eng.sil.myaccount.controller.mbeans.session;

import it.eng.sil.base.exceptions.ProfilaturaException;
import it.eng.sil.base.pojo.auth.gp.UserProfiloPOJO;
import it.eng.sil.myaccount.model.utils.ConstantsSingleton;
import it.eng.sil.mycas.model.business.GestioneProfiloClientEJB;

import java.io.IOException;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.omnifaces.util.Faces;

/**
 * ManagedBean JSF per contenere i dati dell'utente che ha effettuato l'accesso al sistema. Il Bean viene creato una
 * sola volta per ogni sessione e ricava tutte le informazioni utili sull'utente connesso.
 * 
 */
@ManagedBean(name = "accountInfoBean")
@SessionScoped
public class AccountInfoBean {
	protected static Log log = LogFactory.getLog(AccountInfoBean.class);

	@EJB
	GestioneProfiloClientEJB gestioneProfiloClientEJB;

	@EJB
	ConstantsSingleton constantsSingleton;

	// POJO contenente le informazioni sull'utente collegato.
	private UserProfiloPOJO userInfo;

	// Alcune informazioni sull'utente collegato vengono mantenute a parte.
	private String username;

	private String outcome;

	private Integer codGruppo;

	private Integer idPfPrincipal;

	private String codFunzione;

	private String currentTheme;

	public AccountInfoBean() {
		getUser();
	}

	public AccountInfoBean(String username, GestioneProfiloClientEJB gestioneProfiloClientEJB,
			ConstantsSingleton constantsSingleton) {
		this.username = username;
		this.gestioneProfiloClientEJB = gestioneProfiloClientEJB;
		this.constantsSingleton = constantsSingleton;
		getUser();
		currentTheme = "yellow";
	}

	@PostConstruct
	public void postConstruct() {
		refreshSession();
		log.info("L'utente ha effettuato login con successo.");
		// retrieveUrlLogout();
		getUser();
	}

	private void getUser() {
		if (username == null) {
			FacesContext fc = FacesContext.getCurrentInstance();
			ExternalContext ec = fc.getExternalContext();
			username = ec.getUserPrincipal().getName();
		}

		try {
			String ep = constantsSingleton.isMyAuthLocalCallMode() ? null : constantsSingleton.getMyAuthGetInfoURL();
			userInfo = gestioneProfiloClientEJB.getProfilo(ep, username);
			idPfPrincipal = userInfo.getIdPfPrincipal();
		} catch (ProfilaturaException e) {
			log.error("Errore durante la getUser di AccountInfoBean: " + e.toString());
			// Non metto gestione dell'errore perchè neanche prima la faceva. :(
		}
	}

	public String logout() {
		ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
		HttpSession session = (HttpSession) ec.getSession(true);
		session.invalidate();

		log.info("L'utente ha effettuato logout con successo.");
		try {
			Faces.redirect(getUrlLogout());
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	private void refreshSession() {
		try {
			FacesContext fc = FacesContext.getCurrentInstance();
			ExternalContext ec = fc.getExternalContext();
			username = ec.getUserPrincipal().getName();

			String ep = constantsSingleton.isMyAuthLocalCallMode() ? null : constantsSingleton.getMyAuthGetInfoURL();
			setUserInfo(gestioneProfiloClientEJB.getProfilo(ep, username));

		} catch (Exception e) {
			log.error("Errore durante l'inizializzazione del sessionBean");
			logout();
		}
	}

	public String getUsername() {
		return username;
	}

	public String getUrlLogout() {
		return constantsSingleton.getCasLogoutURL();
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

	public String getCurrentTheme() {
		return currentTheme;
	}

	public void setCurrentTheme(String currentTheme) {
		this.currentTheme = currentTheme;
	}

	public String getUrlHomePortale() {
		return constantsSingleton.getPortaleURL();
	}

}

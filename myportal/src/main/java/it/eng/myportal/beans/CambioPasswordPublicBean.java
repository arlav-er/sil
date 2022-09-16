package it.eng.myportal.beans;

import it.eng.myportal.dtos.PfPrincipalDTO;
import it.eng.myportal.entity.ejb.ErrorsSingleton;
import it.eng.myportal.entity.home.AziendaInfoHome;
import it.eng.myportal.entity.home.PfPrincipalHome;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * BackingBean per il cambio password
 * 
 * 
 */
@ManagedBean
@ViewScoped
public class CambioPasswordPublicBean {

	protected static Log log = LogFactory.getLog(CambioPasswordPublicBean.class);

	@EJB
	private PfPrincipalHome pfPrincipalHome;

	@EJB
	private AziendaInfoHome aziendaInfoHome;

	@EJB
	protected ErrorsSingleton errorsBean;

	private String username;
	private String email;
	private String passwordNew;
	private String passwordConfirm;
	// TODO sarebbe carino un commento su cosa significano gli stati 1 e 2...
	private Integer stato;
	private PfPrincipalDTO data;
	private String messaggio;

	@PostConstruct
	public void postConstruct() {

		stato = 1;

		HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext()
				.getRequest();
		if (request.getParameter("username") != null) {
			username = request.getParameter("username");
			if (username == null) {
				stato = 2;
				messaggio = "Il link su cui hai cliccato risulta non piu' valido. Riesegui la procedura.";
				return;
			}
		}
		if (request.getParameter("mail") != null) {
			email = request.getParameter("mail");
		}
		String token = null;
		if (request.getParameter("t") != null) {
			token = request.getParameter("t");
			PfPrincipalDTO principal = pfPrincipalHome.findDTOByUsername(username);
			if (principal == null) {
				stato = 2;
				messaggio = "Questo username non risulta registrato presso questo portale. Ti preghiamo di contattare l'assistenza.";
				return;
			}
			String dbToken = principal.getRecuperoPasswordToken();
			if (dbToken == null) {
				stato = 2;
				messaggio = "Il link su cui hai cliccato risulta non piu' valido. Riesegui la procedura.";
			} else {
				if (!dbToken.equals(token)) {
					stato = 2;
					messaggio = "Il link su cui hai cliccato risulta non piu' valido. Riesegui la procedura.";
				}
			}
		} else {
			stato = 2;
			messaggio = "Il link su cui hai cliccato risulta non piu' valido. Riesegui la procedura.";
		}
	}

	public void cambiaPassword() {
		try {
			pfPrincipalHome.cambiaPassword(username, passwordNew);
			messaggio = errorsBean.getProperty("recuperopassword.passwordmodificata");
		} catch (EJBException e) {
			messaggio = errorsBean.getProperty("recuperopassword.password_non_modificata");
		}
		stato = 2;
	}

	/**
	 * @return the passwordNew
	 */
	public String getPasswordNew() {
		return passwordNew;
	}

	/**
	 * @return the passwordConfirm
	 */
	public String getPasswordConfirm() {
		return passwordConfirm;
	}

	/**
	 * @param passwordNew
	 *            the passwordNew to set
	 */
	public void setPasswordNew(String passwordNew) {
		this.passwordNew = passwordNew;
	}

	/**
	 * @param passwordConfirm
	 *            the passwordConfirm to set
	 */
	public void setPasswordConfirm(String passwordConfirm) {
		this.passwordConfirm = passwordConfirm;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public Integer getStato() {
		return stato;
	}

	public void setStato(Integer stato) {
		this.stato = stato;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getMessaggio() {
		return messaggio;
	}

	public void setMessaggio(String messaggio) {
		this.messaggio = messaggio;
	}

}

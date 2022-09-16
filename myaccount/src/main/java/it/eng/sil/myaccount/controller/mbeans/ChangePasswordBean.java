package it.eng.sil.myaccount.controller.mbeans;

import java.io.Serializable;

import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import it.eng.sil.myaccount.model.ejb.stateless.profile.PfPrincipalMyAccountEJB;
import it.eng.sil.myaccount.model.exceptions.MyAccountException;
import it.eng.sil.mycas.exceptions.MyCasException;
import it.eng.sil.mycas.model.entity.profilo.PfPrincipal;

@ManagedBean(name = "changePasswordBean")
@ViewScoped
public class ChangePasswordBean extends AbstractBackingBean implements Serializable {

	private static final long serialVersionUID = -8789866200209438566L;

	@EJB
	PfPrincipalMyAccountEJB pfPrincipalMyAccountEJB;

	private String newPassword;

	/**
	 * Request params
	 */
	private String username;
	private String email;
	private String token;

	private Boolean passwordChanged;
	private PfPrincipal utente;

	@Override
	protected void initPostConstruct() {
		username = getRequestParameter("username");
		email = getRequestParameter("email");
		token = getRequestParameter("token");

		utente = pfPrincipalMyAccountEJB.findByResetPassword(username, email, token);
		if (utente == null) {
			redirect404();
		}
	}

	public void changePassword() {
		log.debug("change password for username: " + username + " email: " + email + " token: " + token);

		try {
			pfPrincipalMyAccountEJB.resetPassword(utente, newPassword);
			passwordChanged = true;
		} catch ( MyCasException e) {
			log.error("Errore durante il cambio password: " + e);
			passwordChanged = false;
			String msgko = utils.getUiProperty("msg.updated.ko");
			addJSDangerMessage(msgko);
			throw new EJBException(e);
		}catch (MyAccountException oldSareErr) {
			log.error("Errore durante il cambio password sul vecchio SARE: " + oldSareErr);
			passwordChanged = true;//la cambia comunque
			String msgko = utils.getUiProperty("msg.account.oldSare.ko");
			addWarningMessage(msgko, oldSareErr);
			//throw new EJBException(e);
		}

	}

	public String getNewPassword() {
		return newPassword;
	}

	public void setNewPassword(String newPassword) {
		this.newPassword = newPassword;
	}

	public Boolean getPasswordChanged() {
		return passwordChanged;
	}

	public void setPasswordChanged(Boolean passwordChanged) {
		this.passwordChanged = passwordChanged;
	}

	public String getLoginURL() {
		return constantsSingleton.getPortaleURL();
	}

	public String getLoginSocialeURL() {
		return constantsSingleton.getResURL();
	}

}

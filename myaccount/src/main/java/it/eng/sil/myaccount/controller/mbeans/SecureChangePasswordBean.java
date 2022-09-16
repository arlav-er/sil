package it.eng.sil.myaccount.controller.mbeans;

import java.io.Serializable;

import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;

import it.eng.sil.myaccount.model.ejb.stateless.profile.PfPrincipalMyAccountEJB;
import it.eng.sil.myaccount.model.exceptions.MyAccountException;
import it.eng.sil.myaccount.utils.Utils;
import it.eng.sil.mycas.exceptions.MyCasException;
import it.eng.sil.mycas.exceptions.MyCasNoResultException;
import it.eng.sil.mycas.model.entity.profilo.PfPrincipal;

@ManagedBean(name = "secureChangePasswordBean")
@ViewScoped
public class SecureChangePasswordBean extends AbstractSecureBackingBean implements Serializable {
	private static final long serialVersionUID = -8789866200209438566L;

	@EJB
	PfPrincipalMyAccountEJB pfPrincipalMyAccountEJB;

	private String newPassword;

	private Boolean passwordChanged;
	private PfPrincipal pfPrincipal;

	@Override
	protected void initPostConstruct() {
		try {
			pfPrincipal = pfPrincipalMyAccountEJB.findById(accountInfoBean.getUserInfo().getIdPfPrincipal());
		} catch (MyCasNoResultException e) {
			e.printStackTrace();
		}
		if (pfPrincipal == null) {
			redirect404();
		}
	}

	public void changePassword() {
		try {
			pfPrincipalMyAccountEJB.changePassword(accountInfoBean.getUserInfo().getIdPfPrincipal(), newPassword);
			passwordChanged = true;
		} catch ( MyCasException e) {
			log.error("Errore durante il cambio password: " + e);
			passwordChanged = false;
			String msgko = utils.getUiProperty("msg.updated.ko");
			addJSDangerMessage(msgko);
			throw new EJBException(e);
		}catch ( MyAccountException e) {
			log.error("Errore durante il cambio password: " + e);
			passwordChanged = true;
			String msgko = utils.getUiProperty("msg.account.oldSare.ko");
			addJSWarnMessage(msgko);
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

	public void checkOldPasswordValidator(FacesContext facesContext, UIComponent uiComponent, Object newValue) {
		String oldPassword = newValue.toString();
		String oldPasswordEncrypted = Utils.SHA1.encrypt(oldPassword);

		String dbUserPasswordEncrypted = pfPrincipal.getPassWord();

		if (!dbUserPasswordEncrypted.equals(oldPasswordEncrypted)) {
			addErrorMessageToComponent("forgetP.oldPasswordNotCorrect", "changePasswordForm:oldPassword");
		}
	}

}

package it.eng.sil.myaccount.controller.mbeans.profile;

import java.io.Serializable;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import it.eng.sil.base.pojo.auth.gp.UserProfiloPOJO;
import it.eng.sil.myaccount.controller.mbeans.AbstractSecureBackingBean;

@ViewScoped
@ManagedBean
public class MasterProfileBean extends AbstractSecureBackingBean implements Serializable {

	private static final long serialVersionUID = -6414719102850501036L;

	private UserProfiloPOJO userInfo;

	@Override
	protected void initPostConstruct() {
		userInfo = accountInfoBean.getUserInfo();
	}

	public UserProfiloPOJO getUserInfo() {
		return userInfo;
	}

	public void setUserInfo(UserProfiloPOJO userInfo) {
		this.userInfo = userInfo;
	}

	@Deprecated
	public String getType() {
		String type = "utente";
		if (userInfo.isAdmin())
			type = "amministratore";
		else if (userInfo.isAzienda())
			type = "azienda";
		else if (userInfo.isProvincia())
			type = "provincia";
		else if (userInfo.isConsulente())
			type = "consulente";
		else if (userInfo.isCoordinatore())
			type = "coordinatore";
		else if (userInfo.isCertificatore())
			type = "certificatore";
		else if (userInfo.isRegione())
			type = "regione";
		else if (userInfo.isPatronato())
			type = "patronato";
		else if (userInfo.isSoggettoPubblico())
			type = "soggettoPubblico";

		return type;
	}

	/**
	 * Metodo chiamato quando l'amministratore prova ad accedere al proprio profilo: dato che l'amministratore non ha un
	 * profilo, viene mandato al pannello di gestione profilatura.
	 */
	public void redirectAmministratore() {
		redirect("/secure/gestioneProfilatura");
	}
}

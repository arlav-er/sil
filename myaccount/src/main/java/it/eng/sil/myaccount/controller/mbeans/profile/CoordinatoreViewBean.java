package it.eng.sil.myaccount.controller.mbeans.profile;

import it.eng.sil.myaccount.controller.mbeans.AbstractSecureBackingBean;
import it.eng.sil.myaccount.model.ejb.stateless.profile.CoordinatoreInfoEJB;
import it.eng.sil.mycas.exceptions.MyCasNoResultException;
import it.eng.sil.mycas.model.entity.profilo.CoordinatoreInfo;

import java.io.Serializable;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

@ViewScoped
@ManagedBean
public class CoordinatoreViewBean extends AbstractSecureBackingBean implements Serializable {

	private static final long serialVersionUID = 8389069240154466138L;

	private CoordinatoreInfo coordinatore;

	@EJB
	CoordinatoreInfoEJB coordinatoreInfoEJB;

	@Override
	protected void initPostConstruct() {
		loadUser();
	}

	public void loadUser() {
		try {
			coordinatore = coordinatoreInfoEJB.findByPfPrincipalId(accountInfoBean.getUserInfo().getIdPfPrincipal());
		} catch (MyCasNoResultException e) {
			// probabili dati sporchi
			throw new RuntimeException("Errore nel caricamento di UTENTE coordinatore", e);
		}
	}

	public String profilePic() {
		if (constantsSingleton.isTrento()) {
			return "trento";
		} else if (constantsSingleton.isEmiliaRomagna()) {
			return "emiliaRomagna";
		} else if (constantsSingleton.isUmbria()) {
			return "umbria";
		} else {
			return "commons";
		}
	}

	public CoordinatoreInfo getCoordinatore() {
		return coordinatore;
	}

	public void setCoordinatore(CoordinatoreInfo coordinatore) {
		this.coordinatore = coordinatore;
	}

}

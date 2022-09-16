package it.eng.sil.myaccount.controller.mbeans.profile;

import it.eng.sil.base.enums.GpDeMacroTipoEnum;
import it.eng.sil.myaccount.controller.mbeans.AbstractSecureBackingBean;
import it.eng.sil.myaccount.model.ejb.stateless.gestioneprofilo.GpProfilaturaMyAccountEJB;
import it.eng.sil.myaccount.model.ejb.stateless.profile.RegioneEJB;
import it.eng.sil.mycas.exceptions.MyCasException;
import it.eng.sil.mycas.exceptions.MyCasNoResultException;
import it.eng.sil.mycas.model.entity.profilo.Regione;

import java.io.Serializable;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

@ViewScoped
@ManagedBean
public class RegioneViewBean extends AbstractSecureBackingBean implements Serializable {

	private static final long serialVersionUID = 8389069240154466138L;

	private Regione regione;

	@EJB
	RegioneEJB regioneEJB;

	@EJB
	GpProfilaturaMyAccountEJB gpProfilaturaEJB;

	@Override
	protected void initPostConstruct() {
		loadUser();

		// Controllo se l'utente ha bisogno delle profilature di default
		try {
			gpProfilaturaEJB.checkProfilatureDefault(regione.getIdPfPrincipal(), GpDeMacroTipoEnum.REG);
		} catch (MyCasException e) {
			log.error("ERRORE durante l'inserimento delle profilatura di default: " + e.toString());
		}
	}

	public void loadUser() {
		try {
			regione = regioneEJB.findByPfPrincipalId(accountInfoBean.getUserInfo().getIdPfPrincipal());

		} catch (MyCasNoResultException e) {
			// probabili dati sporchi
			throw new RuntimeException("Errore nel caricamento di UTENTE Regione", e);
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

	public Regione getRegione() {
		return regione;
	}

	public void setRegione(Regione regione) {
		this.regione = regione;
	}
}

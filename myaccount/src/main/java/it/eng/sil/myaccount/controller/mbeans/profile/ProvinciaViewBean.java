package it.eng.sil.myaccount.controller.mbeans.profile;

import it.eng.sil.base.enums.GpDeMacroTipoEnum;
import it.eng.sil.myaccount.controller.mbeans.AbstractSecureBackingBean;
import it.eng.sil.myaccount.model.ejb.stateless.gestioneprofilo.GpProfilaturaMyAccountEJB;
import it.eng.sil.myaccount.model.ejb.stateless.profile.ProvinciaEJB;
import it.eng.sil.mycas.exceptions.MyCasException;
import it.eng.sil.mycas.exceptions.MyCasNoResultException;
import it.eng.sil.mycas.model.entity.decodifiche.DeProvincia;
import it.eng.sil.mycas.model.entity.profilo.Provincia;
import it.eng.sil.mycas.model.manager.decodifiche.DeProvinciaEJB;

import java.io.Serializable;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

@ViewScoped
@ManagedBean
public class ProvinciaViewBean extends AbstractSecureBackingBean implements Serializable {

	private static final long serialVersionUID = 8389069240154466138L;

	private Provincia provincia;

	@EJB
	private ProvinciaEJB provinciaEJB;

	@EJB
	private DeProvinciaEJB deProvinciaEJB;

	@EJB
	private GpProfilaturaMyAccountEJB gpProfilaturaEJB;

	@Override
	protected void initPostConstruct() {
		loadUser();

		// Controllo se l'utente ha bisogno delle profilature di default
		try {
			gpProfilaturaEJB.checkProfilatureDefault(provincia.getIdPfPrincipal(), GpDeMacroTipoEnum.PRV);
		} catch (MyCasException e) {
			log.error("ERRORE durante l'inserimento delle profilatura di default: " + e.toString());
		}
	}

	public Provincia getProvincia() {
		return provincia;
	}

	public void setProvincia(Provincia provincia) {
		this.provincia = provincia;
	}

	public void loadUser() {
		try {
			provincia = provinciaEJB.findByPfPrincipalId(accountInfoBean.getUserInfo().getIdPfPrincipal());

			if (provincia.getDeProvincia() != null) {
				String codProv = provincia.getDeProvincia().getCodProvincia();
				DeProvincia provComplete = deProvinciaEJB.findById(codProv);
				provincia.setDeProvincia(provComplete);
			}
		} catch (MyCasNoResultException e) {
			// probabili dati sporchi
			throw new RuntimeException("Errore nel caricamento della Provincia", e);
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
}

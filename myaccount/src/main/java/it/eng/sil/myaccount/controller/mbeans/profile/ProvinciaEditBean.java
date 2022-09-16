package it.eng.sil.myaccount.controller.mbeans.profile;

import it.eng.sil.myaccount.controller.mbeans.AbstractSecureBackingBean;
import it.eng.sil.myaccount.model.ejb.stateless.profile.ProvinciaEJB;
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
public class ProvinciaEditBean extends AbstractSecureBackingBean implements Serializable {

	private static final long serialVersionUID = 7209285126634880068L;

	private Provincia provincia;

	@EJB
	private ProvinciaEJB provinciaEJB;

	@EJB
	private DeProvinciaEJB deProvinciaEJB;

	@Override
	protected void initPostConstruct() {
		loadUser();
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

	public String sync() {
		try {
			provinciaEJB.updateProfile(provincia);
			return "/secure/profile/viewMain.xhtml?faces-redirect=true&saveSucess=yes";

		} catch (Exception e) {
			String msgko = utils.getUiProperty("msg.updated.ko");
			log.error(msgko, e);
			addJSDangerMessage(msgko);

		}

		return null;
	}
}

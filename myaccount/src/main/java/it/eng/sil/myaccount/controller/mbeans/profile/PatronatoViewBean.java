package it.eng.sil.myaccount.controller.mbeans.profile;

import it.eng.sil.myaccount.controller.mbeans.AbstractSecureBackingBean;
import it.eng.sil.myaccount.model.ejb.stateless.profile.PatrAbiProvinciaEJB;
import it.eng.sil.myaccount.model.ejb.stateless.profile.PatronatoEJB;
import it.eng.sil.mycas.exceptions.MyCasNoResultException;
import it.eng.sil.mycas.model.entity.profilo.PatrAbiProvincia;
import it.eng.sil.mycas.model.entity.profilo.Patronato;
import it.eng.sil.mycas.model.manager.decodifiche.DeProvinciaEJB;

import java.io.Serializable;
import java.util.List;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

@ViewScoped
@ManagedBean
public class PatronatoViewBean extends AbstractSecureBackingBean implements Serializable {

	private static final long serialVersionUID = 8389069240154466138L;

	private Patronato patronato;
	List<PatrAbiProvincia> provinceAbilitate;

	@EJB
	PatronatoEJB patronatoEJB;

	@EJB
	PatrAbiProvinciaEJB patrAbiProvinciaEJB;

	@EJB
	DeProvinciaEJB deProvinciaEJB;

	@Override
	protected void initPostConstruct() {
		loadUser();
	}

	public void loadUser() {
		try {
			patronato = patronatoEJB.findByPfPrincipalId(accountInfoBean.getUserInfo().getIdPfPrincipal());
			provinceAbilitate = patrAbiProvinciaEJB.findByPatronatoId(patronato.getIdPatronato());
			for (PatrAbiProvincia provinciaAbilitata : provinceAbilitate) {
				String codProvinciaAbilitata = provinciaAbilitata.getDeProvincia().getCodProvincia();
				provinciaAbilitata.setDeProvincia(deProvinciaEJB.findById(codProvinciaAbilitata));
			}

			String codProv = patronato.getDeProvinciaRif().getCodProvincia();
			patronato.setDeProvinciaRif(deProvinciaEJB.findById(codProv));
		} catch (MyCasNoResultException e) {
			// probabili dati sporchi
			throw new RuntimeException("Errore nel caricamento di UTENTE Patronato", e);
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

	public Patronato getPatronato() {
		return patronato;
	}

	public void setPatronato(Patronato patronato) {
		this.patronato = patronato;
	}

	public List<PatrAbiProvincia> getProvinceAbilitate() {
		return provinceAbilitate;
	}

	public void setProvinceAbilitate(List<PatrAbiProvincia> provinceAbilitate) {
		this.provinceAbilitate = provinceAbilitate;
	}
}

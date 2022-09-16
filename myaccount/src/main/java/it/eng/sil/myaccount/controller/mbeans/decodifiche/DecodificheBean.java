package it.eng.sil.myaccount.controller.mbeans.decodifiche;

import it.eng.sil.myaccount.controller.mbeans.AbstractBackingBean;
import it.eng.sil.myaccount.model.ejb.stateless.decodifiche.DeAutorizzazioneSareEJB;
import it.eng.sil.myaccount.model.ejb.stateless.decodifiche.DeTipoUtenteSareEJB;
import it.eng.sil.mycas.model.entity.decodifiche.DeProvincia;
import it.eng.sil.mycas.model.entity.decodifiche.DeTipoDelegato;
import it.eng.sil.mycas.model.entity.decodifiche.DeTitoloSoggiorno;
import it.eng.sil.mycas.model.entity.decodifiche.profilatura.DeAutorizzazioneSare;
import it.eng.sil.mycas.model.entity.decodifiche.profilatura.DeTipoAbilitato;
import it.eng.sil.mycas.model.entity.decodifiche.profilatura.DeTipoUtenteSare;
import it.eng.sil.mycas.model.manager.decodifiche.DeProvinciaEJB;
import it.eng.sil.mycas.model.manager.decodifiche.DeTipoAbilitatoEJB;
import it.eng.sil.mycas.model.manager.decodifiche.DeTipoDelegatoEJB;
import it.eng.sil.mycas.model.manager.decodifiche.DeTitoloSoggiornoEJB;

import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.enterprise.context.ApplicationScoped;
import javax.faces.bean.ManagedBean;

@ManagedBean(name = "decodificheBean")
@ApplicationScoped
public class DecodificheBean extends AbstractBackingBean {

	private Date currentDate;

	@EJB
	private DeProvinciaEJB deProvinciaEJB;

	@EJB
	private DeTitoloSoggiornoEJB deTitoloSoggiornoEJB;

	@EJB
	private DeTipoAbilitatoEJB deTipoAbilitatoEJB;

	@EJB
	private DeTipoDelegatoEJB deTipoDelegatoEJB;

	@EJB
	private DeAutorizzazioneSareEJB autorizzazioneSareEJB;

	@EJB
	private DeTipoUtenteSareEJB deTipoUtenteSareEJB;

	public List<DeProvincia> getProvinceByCurrentRegione() {
		return deProvinciaEJB.findByRegione(constantsSingleton.getCodRegione());
	}

	public List<DeTitoloSoggiorno> getDeTitoloSoggiorni() {
		return deTitoloSoggiornoEJB.findAllValide(currentDate);
	}

	public List<DeTipoAbilitato> getDeTipiAbilitato() {
		return deTipoAbilitatoEJB.findAllValide(currentDate);
	}

	public List<DeTipoDelegato> getDeTipiDelegato() {
		return deTipoDelegatoEJB.findAllValide(currentDate);
	}

	public List<DeAutorizzazioneSare> getDeAutorizzazioneSare() {
		return autorizzazioneSareEJB.findAllValide(currentDate);
	}

	public List<DeTipoUtenteSare> getDeTipoUtenteSare() {
		return deTipoUtenteSareEJB.findAllValide(currentDate);
	}

	@Override
	protected void initPostConstruct() {
		currentDate = new Date();
	}
}

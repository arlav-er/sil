package it.eng.sil.myaccount.model.ejb.stateless.gestioneprofilo;

import it.eng.sil.mycas.exceptions.MyCasException;
import it.eng.sil.mycas.model.entity.gestioneprofilo.GpAbiAttributo;
import it.eng.sil.mycas.model.entity.gestioneprofilo.GpAbiComponente;
import it.eng.sil.mycas.model.entity.gestioneprofilo.GpAttributo;
import it.eng.sil.mycas.model.entity.gestioneprofilo.GpComponente;
import it.eng.sil.mycas.model.entity.gestioneprofilo.GpRuolo;
import it.eng.sil.mycas.model.manager.gestioneprofilo.GpAttributoEJB;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.TypedQuery;

@Stateless
public class GpAttributoMyAccountEJB extends GpAttributoEJB {

	@EJB
	GpRuoloMyAccountEJB gpRuoloEJB;

	@EJB
	GpComponenteMyAccountEJB gpComponenteEJB;

	public List<GpAttributo> findByComponente(Integer idGpComponente) {
		TypedQuery<GpAttributo> quiry = entityMan // "quIry" in omaggio a Domenico
				.createNamedQuery("findGpAttributoByIdGpComponente", GpAttributo.class);
		quiry.setParameter(":idGpComponente", idGpComponente);
		quiry.setHint("org.hibernate.cacheable", true);
		return quiry.getResultList();
	}

	public List<GpAttributo> findByComponente(GpComponente componente) {
		return findByComponente(componente.getIdGpComponente());
	}

	public List<GpAttributo> findAttributiAbilitatiByRuolo(Integer idGpRuolo) throws MyCasException {
		List<GpAttributo> ret = new ArrayList<GpAttributo>();
		GpRuolo ruolo = gpRuoloEJB.findById(idGpRuolo);
		List<GpAbiComponente> abiComponenti = ruolo.getComponentiAbilitati();
		for (GpAbiComponente abiComponente : abiComponenti) {
			for (GpAbiAttributo abiAttributo : abiComponente.getAttributiAbilitati()) {
				ret.add(abiAttributo.getGpAttributo());
			}
		}

		return ret;
	}

	public List<GpAttributo> findAttributiAbilitatiByRuolo(GpRuolo ruolo) throws MyCasException {
		return findAttributiAbilitatiByRuolo(ruolo.getIdGpRuolo());
	}
}

package it.eng.sil.myaccount.model.ejb.stateless.gestioneprofilo;

import it.eng.sil.mycas.exceptions.MyCasException;
import it.eng.sil.mycas.model.entity.decodifiche.DeSistema;
import it.eng.sil.mycas.model.entity.gestioneprofilo.GpAbiComponente;
import it.eng.sil.mycas.model.entity.gestioneprofilo.GpAttributo;
import it.eng.sil.mycas.model.entity.gestioneprofilo.GpComponente;
import it.eng.sil.mycas.model.entity.gestioneprofilo.GpFunzione;
import it.eng.sil.mycas.model.entity.gestioneprofilo.GpRuolo;
import it.eng.sil.mycas.model.manager.gestioneprofilo.GpComponenteEJB;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.TypedQuery;

@Stateless
public class GpComponenteMyAccountEJB extends GpComponenteEJB {

	@EJB
	GpRuoloMyAccountEJB gpRuoloEJB;

	public List<GpComponente> findByCodSistema(String codSistema) {
		TypedQuery<GpComponente> query = entityMan.createNamedQuery("findGpComponenteByCodSistema", GpComponente.class);
		query.setParameter("codSistema", codSistema);
		query.setHint("org.hibernate.cacheable", true);
		List<GpComponente> result = query.getResultList();

		// Evito la LazyLoadingException sugli attributi e sulle funzioni.
		for (GpComponente componente : result) {
			for (GpAttributo attributo : componente.getAttributi()) {
				attributo.getCodiceToken();
			}
			for (GpFunzione funzione : componente.getFunzioniComponente()) {
				funzione.getCodiceToken();
			}
		}

		return result;
	}

	public List<GpComponente> findByCodSistema(DeSistema sistema) {
		return findByCodSistema(sistema.getCodSistema());
	}

	public List<GpComponente> findComponentiAbilitatiByRuolo(Integer idGpRuolo) throws MyCasException {
		GpRuolo ruoloFind = gpRuoloEJB.findById(idGpRuolo);
		List<GpComponente> ret = new ArrayList<GpComponente>();

		List<GpAbiComponente> abiComponenti = ruoloFind.getComponentiAbilitati();
		for (GpAbiComponente abiComponente : abiComponenti) {
			GpComponente componente = abiComponente.getGpComponente();
			componente.getCodiceToken();

			for (GpFunzione funzione : componente.getFunzioniComponente()) {
				funzione.getCodiceToken();
			}

			ret.add(componente);
		}

		return ret;
	}

	public List<GpComponente> findComponentiAbilitatiByRuolo(GpRuolo ruolo) throws MyCasException {
		return findComponentiAbilitatiByRuolo(ruolo.getIdGpRuolo());
	}
}

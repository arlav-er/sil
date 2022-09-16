package it.eng.sil.myaccount.model.ejb.stateless.gestioneprofilo;

import it.eng.sil.mycas.model.entity.decodifiche.DeSistema;
import it.eng.sil.mycas.model.entity.gestioneprofilo.GpFunzione;
import it.eng.sil.mycas.model.manager.gestioneprofilo.GpFunzioneEJB;

import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.TypedQuery;

@Stateless
public class GpFunzioneMyAccountEJB extends GpFunzioneEJB {

	public List<GpFunzione> findBySistema(String codSistema) {
		TypedQuery<GpFunzione> query = entityMan.createNamedQuery("findGpFunzioneByCodSistema", GpFunzione.class);
		query.setParameter("codSistema", codSistema);
		return query.getResultList();
	}

	public List<GpFunzione> findBySistema(DeSistema sistema) {
		return findBySistema(sistema.getCodSistema());
	}

}

package it.eng.sil.myaccount.model.ejb.stateless.gestioneprofilo;

import it.eng.sil.mycas.model.entity.decodifiche.DeSistema;
import it.eng.sil.mycas.model.manager.decodifiche.DeSistemaEJB;

import java.util.Date;
import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.TypedQuery;

@Stateless
public class DeSistemaMyAccountEJB extends DeSistemaEJB {

	/**
	 * Questo metodo restituisce la lista di tutti i sistemi a cui è associato almeno un ruolo.
	 */
	public List<DeSistema> findAllValideWithRuoli(Date now) {
		TypedQuery<DeSistema> query = entityMan.createNamedQuery("findAllDeSistemaValidiWithRuoli", DeSistema.class);
		query.setParameter("now", now);
		return query.getResultList();
	}

}

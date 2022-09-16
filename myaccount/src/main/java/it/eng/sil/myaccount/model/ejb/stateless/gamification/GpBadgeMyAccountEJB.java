package it.eng.sil.myaccount.model.ejb.stateless.gamification;

import it.eng.sil.mycas.exceptions.MyCasException;
import it.eng.sil.mycas.exceptions.MyCasNoResultException;
import it.eng.sil.mycas.model.entity.gamification.DeBadge;
import it.eng.sil.mycas.model.entity.gamification.GpBadge;
import it.eng.sil.mycas.model.manager.stateless.gamification.GpBadgeEJB;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.TypedQuery;

@Stateless
public class GpBadgeMyAccountEJB extends GpBadgeEJB {

	@EJB
	GpBadgeEJB gpBadgeEJB;

	public void removeBadgeFromUser(Integer idBadge) throws MyCasException {
		GpBadge badge;
		try {
			badge = gpBadgeEJB.findById(idBadge);
			gpBadgeEJB.remove(badge);
		} catch (MyCasNoResultException e) {
			log.warn("Id GpBadge da rimuovere non trovato");
		}
	}

	public DeBadge findLastAchievedBadge(Integer idPfPrincipal) {
		TypedQuery<GpBadge> query = entityMan.createNamedQuery("findAchievedGpBadgesByIdPrincipal", GpBadge.class);
		query.setParameter("idPfPrincipal", idPfPrincipal);
		List<GpBadge> achievedGpBadges = query.getResultList();
		if (achievedGpBadges != null && achievedGpBadges.size() > 0) {
			return achievedGpBadges.get(0).getDeBadge();
		} else {
			return null;
		}
	}
}

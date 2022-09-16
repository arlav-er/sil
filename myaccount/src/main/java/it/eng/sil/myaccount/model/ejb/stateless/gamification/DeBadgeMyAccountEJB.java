package it.eng.sil.myaccount.model.ejb.stateless.gamification;

import it.eng.sil.myaccount.model.utils.ConstantsSingleton;
import it.eng.sil.mycas.model.entity.gamification.DeBadge;
import it.eng.sil.mycas.model.entity.gamification.GpBadge;
import it.eng.sil.mycas.model.manager.stateless.gamification.DeBadgeEJB;
import it.eng.sil.mycas.model.manager.stateless.gamification.GpBadgeEJB;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.ejb.EJB;
import javax.ejb.Stateless;

@Stateless
public class DeBadgeMyAccountEJB extends DeBadgeEJB {

	@EJB
	GpBadgeEJB gpBadgeEJB;

	public List<GpBadge> findVisibleBadgesByUser(Integer idPfPrincipal) {
		List<GpBadge> retSet = new ArrayList<>();
		retSet.addAll(gpBadgeEJB.findByUserId(idPfPrincipal));
		return findVisibleBadgesByUser(idPfPrincipal, retSet);

	}

	/**
	 * Restituisce la lista di tutti i DeBadge visibili all'utente. Questa lista include: tutti i DeBadge per cui esiste
	 * un record in GpBadge legato all'utente, e tutti gli altri DeBadge con flgHidden a FALSE.
	 */
	public List<GpBadge> findVisibleBadgesByUser(Integer idPfPrincipal, List<GpBadge> ownedGpBadges) {
		List<GpBadge> visibleGpBadges = ownedGpBadges;

		// Creo una lista di tutti i DeBadge già compresi nella lista.
		Set<DeBadge> ownedDeBadges = new HashSet<DeBadge>();
		for (GpBadge ownedBadge : ownedGpBadges) {
			if (ownedBadge.getValue() > ConstantsSingleton.Gamification.BADGE_ABSENT)
				ownedDeBadges.add(ownedBadge.getDeBadge());
		}

		// Aggiungo tutti i badge "pubblici" non inclusi nella lista, costruendo
		// dei GpBadge farlocchi con value = 0
		List<DeBadge> publicBadges = findAllValide(new Date());
		for (DeBadge publicBadge : publicBadges) {
			if (!publicBadge.getHidden() && !ownedDeBadges.contains(publicBadge)) {
				GpBadge notOwnedBadge = new GpBadge();
				notOwnedBadge.setDeBadge(publicBadge);
				notOwnedBadge.setValue(ConstantsSingleton.Gamification.BADGE_ABSENT);
				visibleGpBadges.add(notOwnedBadge);
			}
		}

		// Ordino la lista per value (in modo che i badge posseduti appaiano per primi)
		Collections.sort(visibleGpBadges, Collections.reverseOrder(new Comparator<GpBadge>() {
			@Override
			public int compare(GpBadge badge1, GpBadge badge2) {
				Double value1 = new Double(badge1.getValue());
				Double value2 = new Double(badge2.getValue());
				return value1.compareTo(value2);
			}
		}));

		return visibleGpBadges;
	}

}

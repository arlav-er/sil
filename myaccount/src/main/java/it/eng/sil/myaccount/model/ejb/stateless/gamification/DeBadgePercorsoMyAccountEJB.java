package it.eng.sil.myaccount.model.ejb.stateless.gamification;

import it.eng.sil.base.exceptions.GamificationException;
import it.eng.sil.mycas.exceptions.MyCasNoResultException;
import it.eng.sil.mycas.model.entity.gamification.DeBadge;
import it.eng.sil.mycas.model.entity.gamification.DeBadgePercorso;
import it.eng.sil.mycas.model.entity.gamification.DeBadgeSuggerimento;
import it.eng.sil.mycas.model.entity.gamification.GpBadge;
import it.eng.sil.mycas.model.manager.stateless.gamification.DeBadgePercorsoEJB;
import it.eng.sil.mycas.model.manager.stateless.gamification.GpBadgeEJB;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.TypedQuery;

@Stateless
@LocalBean
/**
 * Questa classe può esistere perchè MyAccount ha accesso diretto alle tabelle
 * della gamification
 * 
 * @author pegoraro
 *
 */
public class DeBadgePercorsoMyAccountEJB extends DeBadgePercorsoEJB {

	@EJB
	GpBadgeEJB gpBadgeEJB;

	/**
	 * Ritorna i percorsi in forma di catena di DeBadge
	 * 
	 * @param codPercorso
	 * @return
	 * @throws MyCasGamificationException
	 */
	public List<DeBadge> getPercorsoPojoChain(String codPercorso) throws GamificationException {
		List<DeBadge> chain = new ArrayList<>();
		boolean found = false;
		DeBadgePercorso perc;
		try {
			// attached
			perc = findById(codPercorso);

		} catch (MyCasNoResultException e) {
			throw new GamificationException("Percorso non trovato: " + codPercorso);
		}

		Set<DeBadgeSuggerimento> suggs = new HashSet<DeBadgeSuggerimento>(perc.getSuggerimentiList());
		DeBadgeSuggerimento chainFirst = null;
		for (DeBadgeSuggerimento deBadgeSuggerimento : suggs) {
			if (deBadgeSuggerimento.getDeBadge() == null) {
				chainFirst = deBadgeSuggerimento;
				break;
			}
		}

		if (chainFirst == null)
			throw new GamificationException(
					"Errore configurazione percorso: step iniziale non trovato per codpercorso= " + codPercorso);

		// primo della catena
		// chain.add(DeBadgePojo.getInstance(chainFirst.getDeBadgeDaSvolgere()));
		// suggs.remove(chainFirst);
		// codice di merda, rivedere a mente fresca
		while (!suggs.isEmpty()) {
			if (chain.contains(chainFirst))
				throw new GamificationException("Errore configurazione percorso: LOOP trovato! "
						+ chainFirst.getDeBadgeDaSvolgere().getDescrizione());

			// chain.add(DeBadgePojo.getInstance(
			// chainFirst.getDeBadgeDaSvolgere()));
			chain.add(chainFirst.getDeBadgeDaSvolgere());
			suggs.remove(chainFirst);

			for (DeBadgeSuggerimento deBadgeSuggerimento : suggs) {
				if (deBadgeSuggerimento.getDeBadge().equals(chainFirst.getDeBadgeDaSvolgere())) {
					chainFirst = deBadgeSuggerimento;
					found = true;
					break;
				}
			}
			// errore: non trovo il badge della catena
			if (!found)
				throw new GamificationException("Errore configurazione percorso: step non trovato: "
						+ chainFirst.getDeBadgeDaSvolgere().getDescrizione());

		}

		return chain;
	}

	public boolean isCompleted(String codPercorso, Integer idPfPrincipal) {
		List<GpBadge> allOwnedBadges = gpBadgeEJB.findByUserId(idPfPrincipal);
		List<DeBadge> achievedBadges = new ArrayList<DeBadge>();
		for (GpBadge ownedBadge : allOwnedBadges) {
			if (ownedBadge.getValueAsBool())
				achievedBadges.add(ownedBadge.getDeBadge());
		}
		return isCompleted(codPercorso, idPfPrincipal, achievedBadges);
	}

	public boolean isCompleted(String codPercorso, Integer idPfPrincipal, List<DeBadge> achievedBadges) {
		try {
			DeBadgePercorso perc = findById(codPercorso);
			HashSet<DeBadge> badgeSet = new HashSet<>();

			for (DeBadgeSuggerimento sugg : perc.getSuggerimentiList()) {
				if (sugg.getDeBadge() != null)
					badgeSet.add(sugg.getDeBadge());
				// questo c'è sempre
				badgeSet.add(sugg.getDeBadgeDaSvolgere());
			}

			for (DeBadge achievedBadge : achievedBadges) {
				badgeSet.remove(achievedBadge);
			}
			return badgeSet.isEmpty();

		} catch (MyCasNoResultException e) {
			log.error("Errore calcolo percorso badge complete: " + e.getMessage());
			return false;
		}
	}

	/**
	 * Trova la data di completamento percorso come MAX(dtmins) dei badge facenti parte del percorso
	 */
	public Date completionDate(String codPercorso, Integer idPfPrincipal) {
		if (!isCompleted(codPercorso, idPfPrincipal))
			return null;// makes sense, not completed at all

		Date maxDtPercUsr = null;
		TypedQuery<Date> query = entityMan.createNamedQuery("findMaxDataPercorsoByUser", Date.class);
		query.setParameter("idPfPrincipal", idPfPrincipal).setParameter("codPercorso", codPercorso);

		if (query.getSingleResult() != null) {
			maxDtPercUsr = query.getSingleResult();
		}
		return maxDtPercUsr;
	}

	/**
	 * Torna SOLO i percorsi per cui non ci sono badge HIDDEN oppure l'utente ce li ha già (anche con val=0)
	 * 
	 * @param idPfPrincipal
	 * @return
	 */
	public List<DeBadgePercorso> findCandidatePercorsi(List<GpBadge> userBadges, Integer idPfPrincipal) {
		boolean isToAdd;
		List<DeBadgePercorso> ret = new ArrayList<>();
		// I somehow hope these won't be 1k rows
		List<DeBadgePercorso> wk = findAll();
		List<DeBadge> userDeBadges = new ArrayList<>();

		for (GpBadge gpBadge : userBadges) {
			userDeBadges.add(gpBadge.getDeBadge());
		}

		for (DeBadgePercorso deBadgePercorso : wk) {
			isToAdd = true;
			List<DeBadgeSuggerimento> suggerimenti = deBadgePercorso.getSuggerimentiList();
			for (DeBadgeSuggerimento deBadgeSuggerimento : suggerimenti) {
				// se l'utente non ce l'ha (a 0) ED è nascosto
				if (deBadgeSuggerimento.getDeBadgeDaSvolgere().getHidden()
						&& !userDeBadges.contains(deBadgeSuggerimento.getDeBadgeDaSvolgere()))
					isToAdd = false;
			}
			if (isToAdd)
				ret.add(deBadgePercorso);
		}

		return ret;
	}

}
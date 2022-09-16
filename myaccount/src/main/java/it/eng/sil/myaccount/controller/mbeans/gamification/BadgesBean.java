package it.eng.sil.myaccount.controller.mbeans.gamification;

import it.eng.sil.base.exceptions.GamificationException;
import it.eng.sil.myaccount.controller.mbeans.AbstractSecureBackingBean;
import it.eng.sil.myaccount.model.ejb.stateless.gamification.DeBadgeMyAccountEJB;
import it.eng.sil.myaccount.model.ejb.stateless.gamification.DeBadgePercorsoMyAccountEJB;
import it.eng.sil.myaccount.model.utils.ConstantsSingleton;
import it.eng.sil.mycas.model.business.gamification.GamificationClientEJB;
import it.eng.sil.mycas.model.entity.gamification.DeBadge;
import it.eng.sil.mycas.model.entity.gamification.DeBadgeLivello;
import it.eng.sil.mycas.model.entity.gamification.DeBadgePercorso;
import it.eng.sil.mycas.model.entity.gamification.DeBadgeSuggerimento;
import it.eng.sil.mycas.model.entity.gamification.GpBadge;
import it.eng.sil.mycas.model.manager.stateless.gamification.DeBadgeLivelloEJB;
import it.eng.sil.mycas.model.manager.stateless.gamification.GpBadgeEJB;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

@ViewScoped
@ManagedBean
public class BadgesBean extends AbstractSecureBackingBean implements Serializable {
	private static final long serialVersionUID = 353676295346840088L;

	@EJB
	DeBadgeMyAccountEJB deBadgeEJB;

	@EJB
	DeBadgeLivelloEJB deBadgeLivelloEJB;

	@EJB
	GamificationClientEJB gamificationEJB;

	@EJB
	GpBadgeEJB gpBadgeEJB;

	@EJB
	DeBadgePercorsoMyAccountEJB deBadgePercorsoMyAccountEJB;

	private List<GpBadge> userGpBadges = new ArrayList<>();
	private List<GpBadge> visibleGpBadges = new ArrayList<>();
	private Set<DeBadge> achievedDeBadges = new HashSet<DeBadge>();
	private Set<DeBadge> incompleteDeBadges = new HashSet<DeBadge>();
	private Set<DeBadge> suggestedDeBadges = new HashSet<DeBadge>();

	private Map<DeBadgePercorso, List<DeBadge>> percorsi = new HashMap<>();

	private int completePercentage;

	private GpBadge selectedBadge;

	@Override
	protected void initPostConstruct() {
		if (!constantsSingleton.isGamificationEnabled())
			redirect404();
	}

	public void prepareBadges() {
		userGpBadges.clear();
		visibleGpBadges.clear();
		achievedDeBadges.clear();
		incompleteDeBadges.clear();
		suggestedDeBadges.clear();

		// Recupero le liste di GpBadge "owned" (anche con value = 0) e "available" (tutti quelli visibili)
		userGpBadges.addAll(gpBadgeEJB.findByUserId(accountInfoBean.getIdPfPrincipal()));
		visibleGpBadges = deBadgeEJB.findVisibleBadgesByUser(accountInfoBean.getIdPfPrincipal(), userGpBadges);

		// Preparo gli insiemi di DeBadge ottenuti e incompleti.
		for (GpBadge gpBadge : visibleGpBadges) {
			if (gpBadge.getValue() >= ConstantsSingleton.Gamification.BADGE_COMPLETE) {
				achievedDeBadges.add(gpBadge.getDeBadge());
			} else if (gpBadge.getValue() > ConstantsSingleton.Gamification.BADGE_ABSENT) {
				incompleteDeBadges.add(gpBadge.getDeBadge());
			}
		}

		// Preparo il set di DeBadge suggeriti (escludendo quelli già completati e quelli non validi).
		for (DeBadge ownedBadge : achievedDeBadges) {
			List<DeBadgeSuggerimento> suggestions = ownedBadge.getDeBadgeSuggerimentiList();
			for (DeBadgeSuggerimento suggestion : suggestions) {
				if (!achievedDeBadges.contains(suggestion.getDeBadgeDaSvolgere())
						&& suggestion.getDeBadgeDaSvolgere().isValidoAt(new Date())) {
					suggestedDeBadges.add(suggestion.getDeBadgeDaSvolgere());
				}
			}
		}

		// Calcolo la percentuale di completamento.
		prepareCompletePercentage();
	}

	/**
	 * I percorsi sono organizzati in suggerimenti, ovvero c'è la necessità di ricostruire la "catena" DeBadge a partire
	 * dai suggerimenti
	 */
	public void preparePercorsi() {
		List<DeBadgePercorso> workSet = new ArrayList<>();
		// trovo i percorsi "applicabili"
		workSet = deBadgePercorsoMyAccountEJB.findCandidatePercorsi(userGpBadges, accountInfoBean.getIdPfPrincipal());
		log.info("Badge totali: " + workSet.size());
		// from here on should be OK
		try {
			for (DeBadgePercorso deBadgePojo : workSet) {
				log.info("Ricostruzione catena badge x percorso: " + deBadgePojo.getCodPercorso());
				percorsi.put(deBadgePojo,
						deBadgePercorsoMyAccountEJB.getPercorsoPojoChain(deBadgePojo.getCodPercorso()));
			}
		} catch (GamificationException e) {
			e.printStackTrace();
		}
	}

	public DeBadgeLivello getUserLivello() {
		return deBadgeLivelloEJB.getLivelloUtente(accountInfoBean.getIdPfPrincipal());
	}

	public Long getUserPunteggio() {
		try {
			return gpBadgeEJB.getPuntiUtente(accountInfoBean.getIdPfPrincipal());
		} catch (GamificationException e) {
			log.error("ERRORE durante il calcolo del punteggio: " + e);
			return 0l;
		}
	}

	public Long getPunteggioForNextLevel() {
		try {
			return gpBadgeEJB.getPuntiUtenteNext(accountInfoBean.getIdPfPrincipal());
		} catch (GamificationException e) {
			log.error("ERRORE durante il calcolo dei punti mancanti al prossimo livello: " + e);
			return 0l;
		}
	}

	public boolean isPercorsoCompleted(String codPercorso) {
		return deBadgePercorsoMyAccountEJB.isCompleted(codPercorso, accountInfoBean.getIdPfPrincipal(),
				new ArrayList<DeBadge>(achievedDeBadges));
	}

	public Date completionPercorsoDate(String codPercorso) {
		return deBadgePercorsoMyAccountEJB.completionDate(codPercorso, accountInfoBean.getIdPfPrincipal());
	}

	public List<GpBadge> getAbsentBadges() {
		List<GpBadge> appoggio = new ArrayList<>(visibleGpBadges);
		Collections.copy(appoggio, visibleGpBadges);
		appoggio.removeAll(userGpBadges);

		return appoggio;
	}

	public List<DeBadge> getSuggestedBadgesList() {
		List<DeBadge> suggestedBadgesList = new ArrayList<DeBadge>();
		suggestedBadgesList.addAll(suggestedDeBadges);
		return suggestedBadgesList;
	}

	public boolean isBadgeAbsent(DeBadge badge) {
		for (GpBadge bb : getAbsentBadges()) {
			if (bb.getDeBadge().equals(badge))
				return true;
		}
		return false;
	}

	public GpBadge getGpBadge(DeBadge input) {
		for (GpBadge gpBadge : userGpBadges) {
			if (gpBadge.getDeBadge().equals(input))
				return gpBadge;
		}
		return null;
	}

	/**
	 * La percentuale completamento non dipende esclusivamente dai badge completati, c'è anche un apporto di quelli
	 * incompleti
	 */
	public void prepareCompletePercentage() {

		int tot = getTuttiBadgesCount();
		int ach = getAchievedBadgesCount();
		int inc = getIncompleteBadgesCount();

		// avoid 0 divide
		if (tot == 0) {
			// matematicamente è così, anche se paradosso
			completePercentage = 100;
			return;
		}

		float incompletedAccumul = 0;
		// progresso totale incompleti
		for (GpBadge deBadge : visibleGpBadges) {
			if (deBadge.getValue() < ConstantsSingleton.Gamification.BADGE_COMPLETE
					&& deBadge.getValue() > ConstantsSingleton.Gamification.BADGE_ABSENT)
				incompletedAccumul += deBadge.getValue();
		}
		// apporto completi
		float completeperc = (ach * 100) / tot;
		// apporto incompleti
		float incompleteperc = (inc * 100) / tot;

		// apporto incompleti riportato a inc
		float incompletecontribute = 0;
		if (inc > 0)
			incompletecontribute = (incompletedAccumul * incompleteperc) / inc;

		// il risultato è la somma completi + incompleti riportato a 100
		completePercentage = (int) (completeperc + incompletecontribute);

		// tappo di sicurezza
		if (completePercentage > 100)
			completePercentage = 100;
	}

	/**
	 * Badge completi, ovvero value >= 1
	 */
	public int getAchievedBadgesCount() {
		return achievedDeBadges.size();
	}

	/**
	 * Badge incompleti, ovvero 0 < value < 1
	 */
	public int getIncompleteBadgesCount() {
		return incompleteDeBadges.size();
	}

	public int getAbsentBadgesCount() {
		return getAbsentBadges().size();
	}

	public int getSuggestedBadgesCount() {
		return suggestedDeBadges.size();
	}

	public int getTuttiBadgesCount() {
		// tutti i badge disponibili per questo utente in qualunque stato
		return visibleGpBadges.size();
	}

	public boolean isBadgeAchieved(DeBadge badge) {
		return achievedDeBadges.contains(badge);
	}

	public boolean isBadgeIncomplete(DeBadge badge) {
		return incompleteDeBadges.contains(badge);
	}

	public boolean isBadgeSuggested(DeBadge badge) {
		return suggestedDeBadges.contains(badge);
	}

	public int getCompletePercentage() {
		return completePercentage;
	}

	public void setCompletePercentage(int completePercentage) {
		this.completePercentage = completePercentage;
	}

	public GpBadge getSelectedBadge() {
		return selectedBadge;
	}

	public void setSelectedBadge(GpBadge selectedBadge) {
		this.selectedBadge = selectedBadge;
	}

	public List<GpBadge> getVisibleGpBadges() {
		return visibleGpBadges;
	}

	public void setVisibleGpBadges(List<GpBadge> visibleGpBadges) {
		this.visibleGpBadges = visibleGpBadges;
	}

	public Map<DeBadgePercorso, List<DeBadge>> getPercorsi() {
		return percorsi;
	}

	public List<DeBadgePercorso> getPercorsiList() {
		return new ArrayList<>(percorsi.keySet());
	}

	public void setPercorsi(Map<DeBadgePercorso, List<DeBadge>> percorsi) {
		this.percorsi = percorsi;
	}

}

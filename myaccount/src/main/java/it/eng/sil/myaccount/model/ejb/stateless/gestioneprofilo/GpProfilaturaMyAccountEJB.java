package it.eng.sil.myaccount.model.ejb.stateless.gestioneprofilo;

import it.eng.sil.base.enums.GpDeMacroTipoEnum;
import it.eng.sil.myaccount.model.ejb.stateless.profile.PfPrincipalMyAccountEJB;
import it.eng.sil.mycas.exceptions.MyCasException;
import it.eng.sil.mycas.model.entity.decodifiche.DeSistema;
import it.eng.sil.mycas.model.entity.decodifiche.DeSistema_;
import it.eng.sil.mycas.model.entity.gestioneprofilo.GpGruppo;
import it.eng.sil.mycas.model.entity.gestioneprofilo.GpProfilatura;
import it.eng.sil.mycas.model.entity.gestioneprofilo.GpProfilatura_;
import it.eng.sil.mycas.model.entity.gestioneprofilo.GpRuolo;
import it.eng.sil.mycas.model.entity.gestioneprofilo.GpRuoloGruppo;
import it.eng.sil.mycas.model.entity.gestioneprofilo.GpRuoloGruppo_;
import it.eng.sil.mycas.model.entity.gestioneprofilo.GpRuolo_;
import it.eng.sil.mycas.model.entity.profilo.PfPrincipal;
import it.eng.sil.mycas.model.manager.gestioneprofilo.GpProfilaturaEJB;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

@Stateless
public class GpProfilaturaMyAccountEJB extends GpProfilaturaEJB {

	@EJB
	PfPrincipalMyAccountEJB pfPrincipalEJB;

	@EJB
	GpRuoloGruppoMyAccountEJB gpRuoloGruppoEJB;

	/**
	 * Recupera la lista di tutte le profilature di un utente.
	 */
	public List<GpProfilatura> findForIdPfPrincipal(Integer idPfPrincipal) {
		TypedQuery<GpProfilatura> query = entityMan.createNamedQuery("findGpProfilaturaByIdPfPrincipal",
				GpProfilatura.class);
		query.setParameter("idPfPrincipal", idPfPrincipal);
		return query.getResultList();
	}

	/**
	 * Recupera una lista di profilature in base a vari parametri.
	 */
	public List<GpProfilatura> findByFilter(PfPrincipal pfPrincipal, GpRuolo ruolo, GpGruppo gruppo,
			Boolean flagDefault, DeSistema sistema) {
		CriteriaBuilder cb = entityMan.getCriteriaBuilder();
		CriteriaQuery<GpProfilatura> criteria = cb.createQuery(GpProfilatura.class);
		Root<GpProfilatura> gpProfilaturaRoot = criteria.from(GpProfilatura.class);
		criteria.select(gpProfilaturaRoot);

		List<Predicate> predicati = createWhereCondition(gpProfilaturaRoot, pfPrincipal, ruolo, gruppo, flagDefault,
				sistema);
		if (predicati.size() > 0) {
			criteria.where(cb.and(predicati.toArray(new Predicate[predicati.size()])));
		}

		TypedQuery<GpProfilatura> query = entityMan.createQuery(criteria);
		return query.getResultList();
	}

	/**
	 * Recupera il numero di profilature esistenti in base a vari parametri.
	 */
	public Long findCountByFilter(PfPrincipal pfPrincipal, GpRuolo ruolo, GpGruppo gruppo, Boolean flagDefault) {
		CriteriaBuilder cb = entityMan.getCriteriaBuilder();
		CriteriaQuery<Long> criteria = cb.createQuery(Long.class);
		Root<GpProfilatura> gpProfilaturaRoot = criteria.from(GpProfilatura.class);
		criteria.select(cb.count(gpProfilaturaRoot));

		List<Predicate> predicati = createWhereCondition(gpProfilaturaRoot, pfPrincipal, ruolo, gruppo, flagDefault,
				null);
		if (predicati.size() > 0) {
			criteria.where(cb.and(predicati.toArray(new Predicate[predicati.size()])));
		}

		TypedQuery<Long> query = entityMan.createQuery(criteria);
		return query.getSingleResult();
	}

	/**
	 * Crea i predicati per la findByFilter e la findCountByFilter.
	 */
	private List<Predicate> createWhereCondition(Root<GpProfilatura> gpProfilaturaRoot, PfPrincipal pfPrincipal,
			GpRuolo ruolo, GpGruppo gruppo, Boolean flagDefault, DeSistema sistema) {
		Join<GpProfilatura, GpRuoloGruppo> gpRuoloGruppoJoin = gpProfilaturaRoot.join(GpProfilatura_.gpRuoloGruppo);
		Join<GpRuoloGruppo, GpRuolo> gpRuoloJoin = gpRuoloGruppoJoin.join(GpRuoloGruppo_.gpRuolo);
		Join<GpRuolo, DeSistema> deSistemaJoin = gpRuoloJoin.join(GpRuolo_.deSistema);
		List<Predicate> predicati = new ArrayList<Predicate>();
		CriteriaBuilder cb = entityMan.getCriteriaBuilder();

		if (pfPrincipal != null) {
			predicati.add(cb.equal(gpProfilaturaRoot.get(GpProfilatura_.pfPrincipal), pfPrincipal));
		}

		if (ruolo != null) {
			predicati.add(cb.equal(gpRuoloGruppoJoin.get(GpRuoloGruppo_.gpRuolo), ruolo));
		}

		if (gruppo != null) {
			predicati.add(cb.equal(gpRuoloGruppoJoin.get(GpRuoloGruppo_.gpGruppo), gruppo));
		}
		if (sistema != null) {
			predicati.add(cb.equal(deSistemaJoin.get(DeSistema_.codSistema), sistema));
		}
		if (flagDefault != null) {
			predicati.add(cb.equal(gpRuoloGruppoJoin.get(GpRuoloGruppo_.flgDefault), flagDefault));
		}

		return predicati;
	}

	/**
	 * Aggiunge una profilatura ad un utente. I parametri sono obbligatori.
	 */
	public GpProfilatura add(Integer idUtente, GpRuoloGruppo gpRuoloGruppo, Integer idUtenteInsert)
			throws MyCasException {
		if (idUtente != null && gpRuoloGruppo != null && idUtenteInsert != null) {
			GpProfilatura newProfilatura = new GpProfilatura();
			newProfilatura.setPfPrincipal(pfPrincipalEJB.findById(idUtente));
			newProfilatura.setGpRuoloGruppo(gpRuoloGruppoEJB.findById(gpRuoloGruppo.getIdGpRuoloGruppo()));
			return persist(idUtenteInsert, newProfilatura);
		} else {
			throw new MyCasException("Parametro null nella GpProfilaturaEJB.addProfilatura");
		}
	}

	/**
	 * Rimuove una profilatura.
	 */
	public void remove(Integer idGpProfilatura) throws MyCasException {
		super.remove(findById(idGpProfilatura));
	}

	/**
	 * Funzione utilizzata per gli utenti che provengono da un social network o che sono stati creati da WS del portale:
	 * se è la prima volta che accedono al profilo, e quindi non hanno nessuna profilatura di default, vengono loro
	 * assegnate le profilature di default.
	 */
	public void checkProfilatureDefault(Integer idPfPrincipal, GpDeMacroTipoEnum macroTipo) throws MyCasException {
		// Controllo se l'utente ha profilature di default.
		TypedQuery<GpRuoloGruppo> checkProfDefaultQuery = entityMan.createNamedQuery(
				"findGpRuoloGruppoDefaultByPfPrincipal", GpRuoloGruppo.class);
		checkProfDefaultQuery.setParameter("idPfPrincipal", idPfPrincipal);
		List<GpRuoloGruppo> profDefaultList = checkProfDefaultQuery.getResultList();

		// Se l'utente non ha nessuna profilatura di default, gli inserisco quelle disponibili per i CITTADINI.
		if (profDefaultList == null || profDefaultList.isEmpty()) {
			PfPrincipal principalUtente = pfPrincipalEJB.findById(idPfPrincipal);
			List<GpRuoloGruppo> ruoliGruppiDefault = gpRuoloGruppoEJB.findDefaultByMacroTipo(macroTipo);
			for (GpRuoloGruppo ruoloGruppo : ruoliGruppiDefault) {
				GpProfilatura newProfilatura = new GpProfilatura();
				newProfilatura.setGpRuoloGruppo(ruoloGruppo);
				newProfilatura.setPfPrincipal(principalUtente);
				persist(0, newProfilatura);
			}
		}
	}
}

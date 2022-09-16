package it.eng.sil.myaccount.model.ejb.stateless.gestioneprofilo;

import it.eng.sil.base.enums.GpDeMacroTipoEnum;
import it.eng.sil.myaccount.model.utils.ConstantsSingleton;
import it.eng.sil.mycas.exceptions.MyCasException;
import it.eng.sil.mycas.model.entity.decodifiche.DeSistema;
import it.eng.sil.mycas.model.entity.decodifiche.DeSistema_;
import it.eng.sil.mycas.model.entity.gestioneprofilo.GpDeMacroTipo;
import it.eng.sil.mycas.model.entity.gestioneprofilo.GpDeMacroTipo_;
import it.eng.sil.mycas.model.entity.gestioneprofilo.GpDeTipoGruppo;
import it.eng.sil.mycas.model.entity.gestioneprofilo.GpDeTipoGruppo_;
import it.eng.sil.mycas.model.entity.gestioneprofilo.GpGruppo;
import it.eng.sil.mycas.model.entity.gestioneprofilo.GpGruppo_;
import it.eng.sil.mycas.model.entity.gestioneprofilo.GpProfilatura;
import it.eng.sil.mycas.model.entity.gestioneprofilo.GpProfilatura_;
import it.eng.sil.mycas.model.entity.gestioneprofilo.GpRuolo;
import it.eng.sil.mycas.model.entity.gestioneprofilo.GpRuoloGruppo;
import it.eng.sil.mycas.model.entity.gestioneprofilo.GpRuoloGruppo_;
import it.eng.sil.mycas.model.entity.gestioneprofilo.GpRuolo_;
import it.eng.sil.mycas.model.entity.profilo.AziendaInfo;
import it.eng.sil.mycas.model.entity.profilo.AziendaInfo_;
import it.eng.sil.mycas.model.entity.profilo.PfPrincipal;
import it.eng.sil.mycas.model.entity.profilo.PfPrincipal_;
import it.eng.sil.mycas.model.manager.gestioneprofilo.GpRuoloGruppoEJB;

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
import javax.persistence.criteria.Subquery;

@Stateless
public class GpRuoloGruppoMyAccountEJB extends GpRuoloGruppoEJB {

	@EJB
	GpGruppoMyAccountEJB gpGruppoEJB;

	@EJB
	GpRuoloMyAccountEJB gpRuoloEJB;

	public List<GpRuoloGruppo> findByFilter(Integer idPfPrincipal, GpRuolo ruolo, DeSistema sistema, GpGruppo gruppo,
			GpDeTipoGruppo tipoGruppo, GpDeMacroTipo macroTipo) {
		return findByFilter(idPfPrincipal, ruolo, sistema, gruppo, tipoGruppo, macroTipo, null);
	}

	public List<GpRuoloGruppo> findByFilter(Integer idPfPrincipal, GpRuolo ruolo, DeSistema sistema, GpGruppo gruppo,
			GpDeTipoGruppo tipoGruppo, GpDeMacroTipo macroTipo, Integer maxResults) {
		List<Predicate> predicati = new ArrayList<Predicate>();
		CriteriaBuilder qb = entityMan.getCriteriaBuilder();
		CriteriaQuery<GpRuoloGruppo> cq = qb.createQuery(GpRuoloGruppo.class);
		Root<GpRuoloGruppo> root = cq.from(GpRuoloGruppo.class);
		Join<GpRuoloGruppo, GpRuolo> gpRuolo = root.join(GpRuoloGruppo_.gpRuolo);
		Join<GpRuoloGruppo, GpGruppo> gpGruppo = root.join(GpRuoloGruppo_.gpGruppo);
		cq.select(root);
		cq.orderBy(qb.asc(gpRuolo.get(GpRuolo_.descrizione)), qb.asc(gpGruppo.get(GpGruppo_.descrizione)));

		if (ruolo != null) {
			if (ruolo.getIdGpRuolo() != null) {
				Predicate ruoloPred = qb.equal(gpRuolo.get(GpRuolo_.idGpRuolo), ruolo.getIdGpRuolo());
				predicati.add(ruoloPred);
			}
		}

		Join<GpRuolo, DeSistema> deSistema = gpRuolo.join(GpRuolo_.deSistema);
		if (sistema != null) {
			if (sistema.getCodSistema() != null && !sistema.getCodSistema().isEmpty()) {
				Predicate sistemaPred = qb.equal(qb.upper(deSistema.get(DeSistema_.codSistema)), sistema
						.getCodSistema().toUpperCase());
				predicati.add(sistemaPred);
			}
		}

		if (gruppo != null) {
			if (gruppo.getIdGpGruppo() != null) {
				Predicate gruppoPred = qb.equal(gpGruppo.get(GpGruppo_.idGpGruppo), gruppo.getIdGpGruppo());
				predicati.add(gruppoPred);
			}
		}

		Join<GpGruppo, GpDeTipoGruppo> gpDeTipoGruppo = gpGruppo.join(GpGruppo_.gpDeTipoGruppo);
		if (tipoGruppo != null) {
			if (tipoGruppo.getCodTipoGruppo() != null) {
				Predicate tipoGruppoPred = qb.equal(gpDeTipoGruppo.get(GpDeTipoGruppo_.codTipoGruppo),
						tipoGruppo.getCodTipoGruppo());
				predicati.add(tipoGruppoPred);
			}
		}

		Join<GpDeTipoGruppo, GpDeMacroTipo> gpDeMacroTipo = gpDeTipoGruppo.join(GpDeTipoGruppo_.gpDeMacroTipo);
		if (macroTipo != null) {
			Predicate macroTipoPred = qb.equal(gpDeMacroTipo.get(GpDeMacroTipo_.codMacroTipo),
					macroTipo.getCodMacroTipo());
			predicati.add(macroTipoPred);
		}

		if (idPfPrincipal != null) {
			Subquery<Integer> sq = cq.subquery(Integer.class);
			Root<GpProfilatura> gpProfilatura = sq.from(GpProfilatura.class);
			Join<GpProfilatura, PfPrincipal> pfPrincipal = gpProfilatura.join(GpProfilatura_.pfPrincipal);
			Join<GpProfilatura, GpRuoloGruppo> gpRuoloGruppo = gpProfilatura.join(GpProfilatura_.gpRuoloGruppo);
			sq.select(gpRuoloGruppo.get(GpRuoloGruppo_.idGpRuoloGruppo)).where(
					qb.and(qb.equal(pfPrincipal.get(PfPrincipal_.idPfPrincipal), idPfPrincipal)));
			Predicate sqPred = root.get(GpRuoloGruppo_.idGpRuoloGruppo).in(sq).not();
			predicati.add(sqPred);
		}

		if (predicati.size() > 0) {
			Predicate qsPred = qb.and(predicati.toArray(new Predicate[predicati.size()]));
			cq.where(qsPred);
		}

		TypedQuery<GpRuoloGruppo> query = entityMan.createQuery(cq);

		if (maxResults != null && maxResults > 0) {
			query.setMaxResults(maxResults);
		}

		List<GpRuoloGruppo> resultList = query.getResultList();
		return resultList;
	}

	/**
	 * Recupera tutte le profilature di default per un certo macro-tipo.
	 */
	public List<GpRuoloGruppo> findDefaultByMacroTipo(GpDeMacroTipoEnum macroTipo) {
		TypedQuery<GpRuoloGruppo> query = entityMan.createNamedQuery("findGpRuoloGruppoDefaultByMacroTipo",
				GpRuoloGruppo.class);
		query.setParameter("codMacroTipo", macroTipo);
		return query.getResultList();
	}

	/**
	 * Recupera le profilature di tutti gli utenti che abbiano un ruolo di tipo 'Gestione CO', filtrando per username
	 * e/o codice fiscale.
	 */
	public Integer findMittentiSareCount(String username, String codiceFiscale) {
		CriteriaBuilder cb = entityMan.getCriteriaBuilder();
		CriteriaQuery<Long> criteria = cb.createQuery(Long.class);
		Root<GpProfilatura> gpProfilaturaRoot = criteria.from(GpProfilatura.class);
		Join<GpProfilatura, GpRuoloGruppo> gpRuoloGruppoJoin = gpProfilaturaRoot.join(GpProfilatura_.gpRuoloGruppo);
		Join<GpRuoloGruppo, GpRuolo> gpRuoloJoin = gpRuoloGruppoJoin.join(GpRuoloGruppo_.gpRuolo);
		Join<GpProfilatura, PfPrincipal> pfPrincipalJoin = gpProfilaturaRoot.join(GpProfilatura_.pfPrincipal);
		Join<PfPrincipal, AziendaInfo> aziendaInfoJoin = pfPrincipalJoin.join(PfPrincipal_.aziendaInfo);
		criteria.select(cb.count(gpRuoloGruppoJoin));
		List<Predicate> whereConditions = findMittentiSareWhereConditions(gpProfilaturaRoot, gpRuoloGruppoJoin,
				gpRuoloJoin, pfPrincipalJoin, aziendaInfoJoin, username, codiceFiscale);
		criteria.where(whereConditions.toArray(new Predicate[whereConditions.size()]));
		TypedQuery<Long> query = entityMan.createQuery(criteria);
		return query.getSingleResult().intValue();
	}

	/**
	 * Recupera le profilature di tutti gli utenti che abbiano un ruolo di tipo 'Gestione CO', filtrando per username
	 * e/o codice fiscale (versione non paginata).
	 */
	public List<Object[]> findMittentiSare(String username, String codiceFiscale) {
		return findMittentiSare(username, codiceFiscale, null, null);
	}

	/**
	 * Recupera le profilature di tutti gli utenti che abbiano un ruolo di tipo 'Gestione CO', filtrando per username
	 * e/o codice fiscale (versione paginata).
	 * 
	 * Restituisce una lista di Object[3] dove Object[0] = username, Object[1] = idGruppo, Object[2] = nome gruppo.
	 */
	public List<Object[]> findMittentiSare(String username, String codiceFiscale, Integer firstResult,
			Integer maxResults) {
		CriteriaBuilder cb = entityMan.getCriteriaBuilder();
		CriteriaQuery<Object[]> criteria = cb.createQuery(Object[].class);
		Root<GpProfilatura> gpProfilaturaRoot = criteria.from(GpProfilatura.class);
		Join<GpProfilatura, GpRuoloGruppo> gpRuoloGruppoJoin = gpProfilaturaRoot.join(GpProfilatura_.gpRuoloGruppo);
		Join<GpRuoloGruppo, GpRuolo> gpRuoloJoin = gpRuoloGruppoJoin.join(GpRuoloGruppo_.gpRuolo);
		Join<GpRuoloGruppo, GpGruppo> gpGruppoJoin = gpRuoloGruppoJoin.join(GpRuoloGruppo_.gpGruppo);
		Join<GpProfilatura, PfPrincipal> pfPrincipalJoin = gpProfilaturaRoot.join(GpProfilatura_.pfPrincipal);
		Join<PfPrincipal, AziendaInfo> aziendaInfoJoin = pfPrincipalJoin.join(PfPrincipal_.aziendaInfo);
		criteria.select(cb.array(pfPrincipalJoin.get(PfPrincipal_.username), gpGruppoJoin.get(GpGruppo_.idGpGruppo),
				gpGruppoJoin.get(GpGruppo_.descrizione)));

		List<Predicate> whereConditions = findMittentiSareWhereConditions(gpProfilaturaRoot, gpRuoloGruppoJoin,
				gpRuoloJoin, pfPrincipalJoin, aziendaInfoJoin, username, codiceFiscale);

		criteria.where(whereConditions.toArray(new Predicate[whereConditions.size()]));
		TypedQuery<Object[]> query = entityMan.createQuery(criteria);

		if (firstResult != null)
			query.setFirstResult(firstResult);
		if (maxResults != null)
			query.setMaxResults(maxResults);

		return query.getResultList();
	}

	private List<Predicate> findMittentiSareWhereConditions(Root<GpProfilatura> gpProfilaturaRoot,
			Join<GpProfilatura, GpRuoloGruppo> gpRuoloGruppoJoin, Join<GpRuoloGruppo, GpRuolo> gpRuoloJoin,
			Join<GpProfilatura, PfPrincipal> pfPrincipalJoin, Join<PfPrincipal, AziendaInfo> aziendaInfoJoin,
			String username, String codiceFiscale) {
		CriteriaBuilder cb = entityMan.getCriteriaBuilder();
		List<Predicate> whereConditions = new ArrayList<Predicate>();

		// Profilatura con ruolo 'gestione CO'
		whereConditions.add(cb.equal(gpRuoloJoin.get(GpRuolo_.descrizione),
				ConstantsSingleton.GpRuoloConstants.GESTIONE_CO));

		// Condizione su username
		if (username != null && !username.isEmpty()) {
			username = username.toUpperCase();
			whereConditions.add(cb.or(cb.like(cb.upper(pfPrincipalJoin.get(PfPrincipal_.username)), username + "%"),
					cb.like(cb.upper(pfPrincipalJoin.get(PfPrincipal_.username)), "% " + username + "%")));
		}

		// Condizione su CF
		if (codiceFiscale != null && !codiceFiscale.isEmpty()) {
			codiceFiscale = codiceFiscale.toUpperCase();
			whereConditions.add(cb.or(
					cb.like(cb.upper(aziendaInfoJoin.get(AziendaInfo_.codiceFiscale)), codiceFiscale + "%"),
					cb.like(cb.upper(aziendaInfoJoin.get(AziendaInfo_.codiceFiscale)), "% " + codiceFiscale + "%")));
		}

		return whereConditions;
	}

	/**
	 * Crea un nuovo RuoloGruppo.
	 */
	public GpRuoloGruppo add(GpRuolo ruolo, GpGruppo gruppo, Boolean flagDefault, Integer idPrincipalIns)
			throws MyCasException {
		GpRuoloGruppo newRuoloGruppo = new GpRuoloGruppo();
		newRuoloGruppo.setGpGruppo(gpGruppoEJB.findById(gruppo.getIdGpGruppo()));
		newRuoloGruppo.setGpRuolo(gpRuoloEJB.findById(ruolo.getIdGpRuolo()));
		newRuoloGruppo.setFlgDefault(flagDefault);
		return persist(idPrincipalIns, newRuoloGruppo);
	}

	/**
	 * Se esiste già un RuoloGruppo con il ruolo e il gruppo specificati, lo restituisce. Altrimenti, lo crea.
	 */
	public GpRuoloGruppo findOrCreate(Integer idRuolo, Integer idGruppo, Integer idPrincipalIns) throws MyCasException {
		GpRuolo ruolo = gpRuoloEJB.findById(idRuolo);
		GpGruppo gruppo = gpGruppoEJB.findById(idGruppo);
		List<GpRuoloGruppo> esistenti = findByFilter(null, ruolo, null, gruppo, null, null);
		if (esistenti != null && !esistenti.isEmpty()) {
			return esistenti.get(0);
		} else {
			GpRuoloGruppo newRuoloGruppo = new GpRuoloGruppo();
			newRuoloGruppo.setGpGruppo(gruppo);
			newRuoloGruppo.setGpRuolo(ruolo);
			newRuoloGruppo.setFlgDefault(false);
			return persist(idPrincipalIns, newRuoloGruppo);
		}
	}

	/**
	 * Cancella una RuoloGruppo.
	 */
	public void remove(Integer idGpRuoloGruppo) throws MyCasException {
		remove(findById(idGpRuoloGruppo));
	}
}

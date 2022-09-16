package it.eng.sil.myaccount.model.ejb.stateless.gestioneprofilo;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;

import it.eng.sil.base.enums.GpDeTipoGruppoEnum;
import it.eng.sil.myaccount.helpers.GpGruppoFilter;
import it.eng.sil.mycas.exceptions.MyCasException;
import it.eng.sil.mycas.exceptions.MyCasNoResultException;
import it.eng.sil.mycas.exceptions.MyCasTooManyResultsException;
import it.eng.sil.mycas.model.entity.decodifiche.DeComune;
import it.eng.sil.mycas.model.entity.decodifiche.DeComune_;
import it.eng.sil.mycas.model.entity.gestioneprofilo.GpDeMacroTipo;
import it.eng.sil.mycas.model.entity.gestioneprofilo.GpDeMacroTipo_;
import it.eng.sil.mycas.model.entity.gestioneprofilo.GpDeTipoGruppo;
import it.eng.sil.mycas.model.entity.gestioneprofilo.GpDeTipoGruppo_;
import it.eng.sil.mycas.model.entity.gestioneprofilo.GpGruppo;
import it.eng.sil.mycas.model.entity.gestioneprofilo.GpGruppo_;
import it.eng.sil.mycas.model.entity.gestioneprofilo.GpRuolo;
import it.eng.sil.mycas.model.entity.gestioneprofilo.GpRuoloGruppo;
import it.eng.sil.mycas.model.entity.gestioneprofilo.GpRuoloGruppo_;
import it.eng.sil.mycas.model.entity.gestioneprofilo.GpRuolo_;
import it.eng.sil.mycas.model.manager.gestioneprofilo.GpDeTipoGruppoEJB;
import it.eng.sil.mycas.model.manager.gestioneprofilo.GpGruppoEJB;

@Stateless
public class GpGruppoMyAccountEJB extends GpGruppoEJB {

	@EJB
	GpDeTipoGruppoEJB gpDeTipoGruppoEJB;

	/**
	 * Recupera un gruppo con la findById e inoltre inizializza tutte le sue lazy collections.
	 */
	public GpGruppo fullLookup(Integer idGruppo) throws MyCasNoResultException {
		GpGruppo gruppo = findById(idGruppo);

		for (DeComune comuneAssociato : gruppo.getComuniAssociatiPerDistretto()) {
			comuneAssociato.getStrDescrizioneConTarga();
		}
		for (DeComune comuneAssociato : gruppo.getComuniAssociatiPerProvincie()) {
			comuneAssociato.getStrDescrizioneConTarga();
		}

		return gruppo;
	}

	/**
	 * Crea un nuovo gruppo. Il nuovo gruppo può avere un padre, ma non è obbligatorio.
	 */
	public void add(GpDeTipoGruppo tipoGruppo, GpGruppo gruppoPadre, String descrizione, DeComune comuneRiferimento,
			Integer idPrincipalIns) throws MyCasException {
		GpGruppo newGruppo = new GpGruppo();
		newGruppo.setDescrizione(descrizione);

		if (gruppoPadre != null) {
			newGruppo.setPadre(findById(gruppoPadre.getIdGpGruppo()));
		}

		if (tipoGruppo != null) {
			newGruppo.setGpDeTipoGruppo(gpDeTipoGruppoEJB.findById(tipoGruppo.getCodTipoGruppo()));
		}

		if (comuneRiferimento != null) {
			newGruppo.getComuniAssociatiPerDistretto().add(comuneRiferimento);
		}

		persist(idPrincipalIns, newGruppo);
	}

	/**
	 * Recupera una lista di gruppi in base ad una serie di parametri.
	 */
	public List<GpGruppo> findByFilter(GpGruppoFilter filter, Integer maxResults) {
		CriteriaBuilder cb = entityMan.getCriteriaBuilder();
		CriteriaQuery<GpGruppo> criteria = cb.createQuery(GpGruppo.class);
		Root<GpGruppo> gpGruppoRoot = criteria.from(GpGruppo.class);
		criteria.select(gpGruppoRoot);

		List<Predicate> predicati = creaFindByFilterWhereConditions(gpGruppoRoot, filter);
		Predicate qsPred = null;
		if (predicati.size() > 0) {
			qsPred = cb.and(predicati.toArray(new Predicate[predicati.size()]));
			criteria.where(qsPred);
		}
		// condizione per estrarre SOLO i padri
		if (filter.getSoloGruppiPadre()) {

			Subquery<GpGruppo> figliSubQ = criteria.subquery(GpGruppo.class);
			Root<GpGruppo> gpGruppoRootFigli = figliSubQ.from(GpGruppo.class);
			figliSubQ.select(gpGruppoRootFigli)// subquery selection
					.where(cb.equal(gpGruppoRootFigli.get(GpGruppo_.padre), gpGruppoRoot.get(GpGruppo_.idGpGruppo)));// subquery
			// restriction
			/*
			 * Join<GpGruppo, GpGruppo> figli = gpGruppoRoot.join(GpGruppo_.figli, JoinType.INNER); Predicate padrePred
			 * = cb.or(cb.isNotNull(figli.get(GpGruppo_.idGpGruppo)), cb.isNull(gpGruppoRoot.get(GpGruppo_.padre)));
			 * 
			 * predicati.add(padrePred);
			 */
			criteria.select(gpGruppoRoot).where(cb.and(cb.exists(figliSubQ), (qsPred == null ? cb.and() : qsPred)));
		}

		// Ordine di default: alfabetico sulla descrizione.
		criteria.orderBy(cb.asc(gpGruppoRoot.get(GpGruppo_.descrizione)));

		// Recupero la lista di gruppi e la scorro per evitare lazyException.
		TypedQuery<GpGruppo> query = entityMan.createQuery(criteria);

		if (maxResults != null && maxResults > 0) {
			query.setMaxResults(maxResults);
		}

		return query.getResultList();
	}

	/**
	 * Recupera il numero di gruppi che corrispondono ad una serie di parametri.
	 */
	public Long findCountByFilter(GpGruppoFilter filter) {
		CriteriaBuilder cb = entityMan.getCriteriaBuilder();
		CriteriaQuery<Long> criteria = cb.createQuery(Long.class);
		Root<GpGruppo> gpGruppoRoot = criteria.from(GpGruppo.class);
		criteria.select(cb.count(gpGruppoRoot));

		List<Predicate> predicati = creaFindByFilterWhereConditions(gpGruppoRoot, filter);
		if (predicati.size() > 0) {
			Predicate qsPred = cb.and(predicati.toArray(new Predicate[predicati.size()]));
			criteria.where(qsPred);
		}

		TypedQuery<Long> query = entityMan.createQuery(criteria);
		return query.getSingleResult();
	}

	/**
	 * Crea la clausola WHERE delle query findByFilter e findCountByFilter.
	 */
	private List<Predicate> creaFindByFilterWhereConditions(Root<GpGruppo> gpGruppoRoot, GpGruppoFilter filter) {
		List<Predicate> predicati = new ArrayList<Predicate>();
		CriteriaBuilder cb = entityMan.getCriteriaBuilder();

		// Cambio il tipo di join con la GpDeTipoGruppo a seconda se voglio o no anche i gruppi astratti.
		Join<GpGruppo, GpDeTipoGruppo> gpDeTipoGruppo;
		if (filter.getIncludiGruppiAstratti() && filter.getIncludiGruppiConcreti()) {
			// Includo sia gruppi astratti che gruppi concreti
			gpDeTipoGruppo = gpGruppoRoot.join(GpGruppo_.gpDeTipoGruppo, JoinType.LEFT);
		} else if (filter.getIncludiGruppiAstratti()) {
			// Includo solo i gruppi astratti
			gpDeTipoGruppo = gpGruppoRoot.join(GpGruppo_.gpDeTipoGruppo, JoinType.LEFT);
			Predicate soloAstrattiPred = cb.isNull(gpGruppoRoot.get(GpGruppo_.gpDeTipoGruppo));
			predicati.add(soloAstrattiPred);
		} else if (filter.getIncludiGruppiConcreti()) {
			// Includo solo i gruppi concreti
			gpDeTipoGruppo = gpGruppoRoot.join(GpGruppo_.gpDeTipoGruppo, JoinType.INNER);
		} else {
			// ATTENZIONE : sto facendo una query senza voler includere nè gruppi concreti nè gruppi astratti.
			log.error("Errore : findByFilter sulla GpGruppo senza includere nè gruppi concreti, nè gruppi astratti.");
			gpDeTipoGruppo = gpGruppoRoot.join(GpGruppo_.gpDeTipoGruppo, JoinType.INNER);
			Predicate escludiTuttiPredicate = cb.isNull(gpGruppoRoot.get(GpGruppo_.idGpGruppo));
			predicati.add(escludiTuttiPredicate);
		}

		// Condizione sulla descrizione
		if (filter.getDescrizione() != null && !filter.getDescrizione().isEmpty()) {
			if (filter.getDescrizioneEsatta()) {
				Predicate descrizioneEsattaPred = cb.equal(cb.upper(gpGruppoRoot.get(GpGruppo_.descrizione)),
						filter.getDescrizione().toUpperCase());
				predicati.add(descrizioneEsattaPred);
			} else {
				// Se non voglio corrispondenza esatta, cerco il parametro all'inizio della descrizione O di una parola.
				Predicate descrInizioFrasePred = cb.like(cb.upper(gpGruppoRoot.get(GpGruppo_.descrizione)),
						filter.getDescrizione().toUpperCase() + "%");
				Predicate descrInizioParolaPred = cb.like(cb.upper(gpGruppoRoot.get(GpGruppo_.descrizione)),
						"% " + filter.getDescrizione().toUpperCase() + "%");
				predicati.add(cb.or(descrInizioFrasePred, descrInizioParolaPred));
			}
		}

		// Condizione sul tipo di gruppo.
		// Se sto includendo i gruppi astratti (che non hanno tipo), aggiungo un caso speciale.
		if (filter.getCodTipoGruppo() != null) {
			Predicate codTipoGruppoPred;
			if (filter.getIncludiGruppiAstratti()) {
				codTipoGruppoPred = cb.or(
						cb.equal(gpDeTipoGruppo.get(GpDeTipoGruppo_.codTipoGruppo), filter.getCodTipoGruppo()),
						cb.isNull(gpGruppoRoot.get(GpGruppo_.gpDeTipoGruppo)));
			} else {
				codTipoGruppoPred = cb.equal(gpDeTipoGruppo.get(GpDeTipoGruppo_.codTipoGruppo),
						filter.getCodTipoGruppo());
			}
			predicati.add(codTipoGruppoPred);
		}

		// Condizione sul macro-tipo.
		// Se sto includendo i gruppi astratti (che non hanno tipo e quindi macro-tipo), aggiungo un caso speciale.
		if (filter.getCodMacroTipo() != null) {
			Join<GpDeTipoGruppo, GpDeMacroTipo> gpDeMacroTipo;
			Predicate codMacroTipoPred;
			if (filter.getIncludiGruppiAstratti()) {
				gpDeMacroTipo = gpDeTipoGruppo.join(GpDeTipoGruppo_.gpDeMacroTipo, JoinType.LEFT);
				codMacroTipoPred = cb.or(
						cb.equal(gpDeMacroTipo.get(GpDeMacroTipo_.codMacroTipo), filter.getCodMacroTipo()),
						cb.isNull(gpGruppoRoot.get(GpGruppo_.gpDeTipoGruppo)));
			} else {
				gpDeMacroTipo = gpDeTipoGruppo.join(GpDeTipoGruppo_.gpDeMacroTipo, JoinType.INNER);
				codMacroTipoPred = cb.equal(gpDeMacroTipo.get(GpDeMacroTipo_.codMacroTipo), filter.getCodMacroTipo());
			}
			predicati.add(codMacroTipoPred);
		}

		// Condizione sul gruppo padre.
		if (filter.getIdPadre() != null) {
			// main query selection
			Predicate padrePred = cb.equal(gpGruppoRoot.get(GpGruppo_.padre), filter.getIdPadre());
			predicati.add(padrePred);
		}

		// Condizione che seleziona solo gruppi senza un padre.
		if (filter.getSoloGruppiSenzaPadre()) {
			Predicate senzaPadrePred = cb.isNull(gpGruppoRoot.get(GpGruppo_.padre));
			predicati.add(senzaPadrePred);
		}

		// Condizione se c'è un id da escludere (utile, per esempio, se si cercano duplicati).
		if (filter.getIdDaEscludere() != null) {
			Predicate escludiPred = cb.not(cb.equal(gpGruppoRoot.get(GpGruppo_.idGpGruppo), filter.getIdDaEscludere()));
			predicati.add(escludiPred);
		}

		// Condizione sul comune collegato al gruppo.
		if (filter.getCodComune() != null) {
			Join<GpGruppo, DeComune> deComuneJoin = gpGruppoRoot.join(GpGruppo_.comuniAssociatiPerDistretto);
			Predicate comunePred = cb.equal(deComuneJoin.get(DeComune_.codCom), filter.getCodComune());
			predicati.add(comunePred);
		}

		// Condizione sulla provincia collegata al gruppo.

		// FIXME wonde
		/*
		 * if (filter.getCodProvincia() != null) { Join<GpGruppo, DeProvincia> deProvinciaJoin =
		 * gpGruppoRoot.join(GpGruppo_.provinceAssociate); Predicate provinciaPred =
		 * cb.equal(deProvinciaJoin.get(DeProvincia_.codProvincia), filter.getCodProvincia());
		 * predicati.add(provinciaPred); }
		 */

		return predicati;
	}

	/**
	 * Recupera la lista di gruppi che potrebbero fare da padre ad un nuovo gruppo che si sta creando (se il parametro
	 * "gruppo" è null) oppure ad un gruppo già esistente.
	 */
	public List<GpGruppo> findPossibiliPadri(String par, GpDeTipoGruppo tipoGruppo, GpGruppo figlio,
			Integer maxResults) {
		CriteriaBuilder cb = entityMan.getCriteriaBuilder();
		CriteriaQuery<GpGruppo> criteria = cb.createQuery(GpGruppo.class);
		Root<GpGruppo> gpGruppoRoot = criteria.from(GpGruppo.class);
		criteria.select(gpGruppoRoot);
		criteria.orderBy(cb.asc(gpGruppoRoot.get(GpGruppo_.descrizione)));

		// Nel filtrare i possibili padri, devo tener conto se il figlio ha un tipoGruppo o meno.
		List<Predicate> predicati;
		GpGruppoFilter filter = new GpGruppoFilter().setDescrizione(par);
		if (tipoGruppo == null) {
			// Se il figlio è astratto, anche il padre può essere solo astratto.
			predicati = creaFindByFilterWhereConditions(gpGruppoRoot, filter.setIncludiGruppiConcreti(false));
		} else {
			// Se il figlio è concreto, il padre può essere sia concreto (con lo stesso MacroTipo) che astratto.
			predicati = creaFindByFilterWhereConditions(gpGruppoRoot,
					filter.setCodMacroTipo(tipoGruppo.getGpDeMacroTipo().getCodMacroTipo()));
		}

		// Se sto modificando il padre di un gruppo già esistente, non posso scegliere il gruppo stesso.
		if (figlio != null) {
			predicati.add(cb.not(cb.equal(gpGruppoRoot, figlio)));
		}

		if (predicati.size() > 0) {
			Predicate qsPred = cb.and(predicati.toArray(new Predicate[predicati.size()]));
			criteria.where(qsPred);
		}

		TypedQuery<GpGruppo> query = entityMan.createQuery(criteria);
		if (maxResults != null && maxResults > 0) {
			query.setMaxResults(maxResults);
		}

		return query.getResultList();
	}

	/**
	 * Restituisce una lista di gruppi per cui è possibile creare un RuoloGruppo insieme ad un certo ruolo.
	 */
	public List<GpGruppo> findGruppiAssegnabiliByRuolo(String par, GpRuolo ruolo, GpDeTipoGruppo tipoGruppo,
			Integer maxResults) {
		CriteriaBuilder cb = entityMan.getCriteriaBuilder();
		CriteriaQuery<GpGruppo> criteria = cb.createQuery(GpGruppo.class);
		Root<GpGruppo> gpGruppoRoot = criteria.from(GpGruppo.class);
		criteria.select(gpGruppoRoot);
		criteria.orderBy(cb.asc(gpGruppoRoot.get(GpGruppo_.descrizione)));
		List<Predicate> predicati = new ArrayList<Predicate>();
		par = par.trim().toUpperCase();

		// Condizione opzionale sul parametro stringa (inizio frase o parola)
		if (par != null && !par.isEmpty()) {
			predicati.add(cb.or(cb.like(cb.upper(gpGruppoRoot.get(GpGruppo_.descrizione)), par + "%"),
					cb.like(cb.upper(gpGruppoRoot.get(GpGruppo_.descrizione)), "% " + par + "%")));
		}

		// Condizione opzionale sul tipo di gruppo.
		if (tipoGruppo != null) {
			Join<GpGruppo, GpDeTipoGruppo> tipoGruppoJoin = gpGruppoRoot.join(GpGruppo_.gpDeTipoGruppo);
			predicati.add(cb.equal(tipoGruppoJoin.get(GpDeTipoGruppo_.codTipoGruppo), tipoGruppo.getCodTipoGruppo()));
		}

		// Condizione : voglio solo gruppi per cui non esiste già un RuoloGruppo con questo ruolo.
		Subquery<Integer> subquery = criteria.subquery(Integer.class);
		Root<GpRuoloGruppo> subRoot = subquery.from(GpRuoloGruppo.class);
		Join<GpRuoloGruppo, GpGruppo> subGruppoJoin = subRoot.join(GpRuoloGruppo_.gpGruppo);
		Join<GpRuoloGruppo, GpRuolo> subRuoloJoin = subRoot.join(GpRuoloGruppo_.gpRuolo);
		subquery.select(subGruppoJoin.get(GpGruppo_.idGpGruppo));
		List<Predicate> subqueryPredicati = new ArrayList<Predicate>();
		subqueryPredicati
				.add(cb.equal(subGruppoJoin.get(GpGruppo_.idGpGruppo), gpGruppoRoot.get(GpGruppo_.idGpGruppo)));
		subqueryPredicati.add(cb.equal(subRuoloJoin.get(GpRuolo_.idGpRuolo), ruolo.getIdGpRuolo()));
		subquery.where(subqueryPredicati.toArray(new Predicate[predicati.size()]));
		predicati.add(gpGruppoRoot.get(GpGruppo_.idGpGruppo).in(subquery).not());

		// Lancio la query e restituisco la lista di risultati
		criteria.where(predicati.toArray(new Predicate[predicati.size()]));
		TypedQuery<GpGruppo> query = entityMan.createQuery(criteria);
		if (maxResults != null && maxResults > 0) {
			query.setMaxResults(maxResults);
		}
		return query.getResultList();
	}

	/**
	 * Cerco un gruppo creato per il SARE (da porting, oppure durante l'abilitazione SARE per l'azienda). Se non esiste,
	 * non lancio eccezioni ma restituisco null.
	 */
	public GpGruppo findGruppoAziendaSare(Integer idPfPrincipal) throws MyCasTooManyResultsException {
		TypedQuery<GpGruppo> query = entityMan.createNamedQuery("findGpGruppoAziendaSare", GpGruppo.class);
		String noteParam = "SARE: " + idPfPrincipal;

		query.setParameter("codTipoGruppoEnum", GpDeTipoGruppoEnum.AZI);
		query.setParameter("noteParam", noteParam);
		List<GpGruppo> results = query.getResultList();

		if (results == null || results.isEmpty()) {
			return null;
		} else if (results.size() > 1) {
			throw new MyCasTooManyResultsException("Trovato più di un gruppo SARE per utente " + idPfPrincipal);
		} else {
			return results.get(0);
		}
	}
}

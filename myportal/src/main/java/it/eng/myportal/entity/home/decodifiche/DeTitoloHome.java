package it.eng.myportal.entity.home.decodifiche;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.Stateless;
import javax.faces.model.SelectItem;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang3.StringUtils;

import it.eng.myportal.dtos.DeTitoloDTO;
import it.eng.myportal.entity.decodifiche.DeTitolo;
import it.eng.myportal.entity.decodifiche.DeTitolo_;

/**
 * 
 * @see it.eng.myportal.dtos.it.eng.myportal.dtos.decodifiche.DeTitoloDTO
 * @author Rodi A.
 */
@Stateless
public class DeTitoloHome extends AbstractTreeableHome<DeTitolo, DeTitoloDTO> {

	private static final String QUERY_RICERCA = "select c from DeTitolo c where current_date between c.dtInizioVal and c.dtFineVal and c.padre is null order by c.descrizione";

	private static final String TS_QUERY = " select e.* from de_titolo e where to_tsvector(lower(e.descrizione)) @@ to_tsquery(lower(?)) ORDER BY descrizione LIMIT 10";

	private static final String TS_QUERY_FIGLI = " select e.* from de_titolo e where to_tsvector(lower(e.descrizione)) @@ to_tsquery(lower(?)) and (cod_padre is not null or cod_titolo in ('00000000', '10000000', '20000000')) ORDER BY descrizione LIMIT 10";

	public DeTitolo findById(String id) {
		return findById(DeTitolo.class, id);
	}

	@Override
	public DeTitolo fromDTO(DeTitoloDTO titolo) {
		if (titolo == null)
			return null;
		DeTitolo ret = super.fromDTO(titolo);
		String codPadre = titolo.getCodPadre();
		if (StringUtils.isNotBlank(codPadre)) {
			ret.setPadre(findById(codPadre));
		}
		ret.setCodTitolo(titolo.getId());
		ret.setDescrizione(titolo.getDescrizione());
		ret.setDescrizioneParlante(titolo.getDescrizioneParlante());
		ret.setFlagLaurea(titolo.getFlgLaurea());
		ret.setFlgConferimentoDid(titolo.getFlgConferimentoDid());
		return ret;
	}

	@Override
	public DeTitoloDTO toDTO(DeTitolo titolo) {
		if (titolo == null)
			return null;
		DeTitoloDTO ret = super.toDTO(titolo);
		String codTipoTitolo;
		if (titolo.getPadre() != null) {
			ret.setCodPadre(titolo.getPadre().getCodTitolo());

			// ricavo il codice tipo titolo dal codice padre
			codTipoTitolo = getCodTipoTitolo(titolo.getPadre().getCodTitolo());
		} else {
			// se non ho padre vuol dire che sono la radice, ricavo iol codice
			// tipo titolo dal mio codice
			codTipoTitolo = getCodTipoTitolo(titolo.getCodTitolo());
		}
		if (!codTipoTitolo.isEmpty()) {
			DeTitolo tipoTitolo = findById(codTipoTitolo);
			ret.setDescrizioneTipoTitolo(tipoTitolo.getDescrizioneParlante());
		}

		ret.setId(titolo.getCodTitolo());
		ret.setDescrizione(titolo.getDescrizione());
		ret.setDescrizioneParlante(titolo.getDescrizioneParlante());
		ret.setFlgLaurea(titolo.getFlagLaurea());
		ret.setNumeroFigli(titolo.getFigli().size());
		ret.setFlgConferimentoDid(titolo.getFlgConferimentoDid());
		return ret;
	}
	
	public List<DeTitolo> findPadriValidiAt(Date thold){
		TypedQuery<DeTitolo> query = entityManager.createNamedQuery("findDeTitoloPadri",
				DeTitolo.class);
		query.setParameter("data", thold);
		return query.getResultList();
	}
	
	//TODO refactor w/ findByCodPadre
	public List<DeTitolo> findByCodPadreNoDTO(String codTitolo) {
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<DeTitolo> c = cb.createQuery(DeTitolo.class);
		Root<DeTitolo> root = c.from(DeTitolo.class);
		Join<DeTitolo, DeTitolo> deTitoloPadre = root.join(DeTitolo_.padre, JoinType.LEFT);
		Predicate codPadrePred = null;
		if (!"0".equalsIgnoreCase(codTitolo)) {
			codPadrePred = cb.equal(deTitoloPadre.get(DeTitolo_.codTitolo), codTitolo);
		} else {
			codPadrePred = cb.isNull(root.get(DeTitolo_.padre));
		}
		c = c.where(codPadrePred);
		TypedQuery<DeTitolo> query = entityManager.createQuery(c).setHint("org.hibernate.cacheable", true);
		return query.getResultList();
	}

	@Override
	public List<DeTitoloDTO> findByCodPadre(String codPadre) {
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<DeTitolo> c = cb.createQuery(DeTitolo.class);
		Root<DeTitolo> root = c.from(DeTitolo.class);
		Join<DeTitolo, DeTitolo> deTitoloPadre = root.join(DeTitolo_.padre, JoinType.LEFT);
		Predicate codPadrePred = null;
		if (!"0".equalsIgnoreCase(codPadre)) {
			codPadrePred = cb.equal(deTitoloPadre.get(DeTitolo_.codTitolo), codPadre);
		} else {
			codPadrePred = cb.isNull(root.get(DeTitolo_.padre));
		}
		c = c.where(codPadrePred);
		TypedQuery<DeTitolo> query = entityManager.createQuery(c).setHint("org.hibernate.cacheable", true);
		return findDTOByQuery(query);
	}
	
	// Ritorna la lista delle mansioni a 3 oppure 4 livelli
	public List<DeTitolo> findDeTitoloForTree(boolean conPotatura) {
		TypedQuery<DeTitolo> query = entityManager.createNamedQuery("findDeTitoloForTree", DeTitolo.class)
				.setParameter("parDate", new Date()).setParameter("potatura", conPotatura).setHint("org.hibernate.cacheable", true);
		return query.getResultList();
	}

	// Ritorna la mansione padre della mansione cercata secondo il codice passato
	public DeTitolo findDeTitoloPadreById(String codMansione) {
		TypedQuery<DeTitolo> query = entityManager.createNamedQuery("findDeTitoloPadreById", DeTitolo.class)
				.setParameter("parDate", new Date()).setParameter("parCodMans", codMansione)
				.setHint("org.hibernate.cacheable", true);
		return query.getSingleResult();
	}

	// Ritorna la lista delle mansioni figlio
	public List<DeTitolo> findDeTitoloFigliById(String codMansione) {
		TypedQuery<DeTitolo> query = entityManager.createNamedQuery("findDeTitoloFigliById", DeTitolo.class)
				.setParameter("parDate", new Date()).setParameter("parCodMans", codMansione)
				.setHint("org.hibernate.cacheable", true);
		return query.getResultList();
	}

	/**
	 * Genera il codice tipo titolo del titolo con il codice padre passato come input. Il tipo titolo si ottiene
	 * aggiungendo 5 zeri '00000' alle prime 3 cifre del codice padre del titolo
	 * 
	 * @param codPadre
	 *            codice padre del titolo di cui si vuole conosce il codice tipo titolo
	 * @return codice tipo titolo
	 */
	public String getCodTipoTitolo(String codPadre) {
		String codTipoTitolo = "";
		if (codPadre != null && codPadre.length() >= 3) {
			codTipoTitolo = codPadre.substring(0, 3) + "00000";
		}

		return codTipoTitolo;
	}

	/**
	 * restituisce il titolo di studio a partire dal codice titolo di studio da cliclkvoro
	 * 
	 */
	public DeTitolo getCodTitoloClicLavoro(String codTitoloClicLavoro) {
		return findById(codTitoloClicLavoro + "000000");
	}

	/**
	 * @param par
	 *            String termine per la ricerca dei suggerimenti
	 * @return List<DTO> lista suggerimenti per elemento autocomplete
	 * @see it.eng.myportal.entity.home.local.IDecodeHome#findBySuggestion(java.lang.String)
	 */
	@Override
	public List<DeTitoloDTO> findBySuggestion(String par) {

		List<DeTitolo> resultList = null;
		;
		try {
			resultList = findBySuggest(par);
		} catch (Exception e) {
			// FV 28-11-2014
			// semplificazione del messagio di errore quando la query composta
			// presenta errori di "GRAMMATICA" (presenza ad es. del carattere '&' )

			log.error(e.getMessage());
		}
		List<DeTitoloDTO> resultListDTO = new ArrayList<DeTitoloDTO>();
		if (resultList != null) {
			for (DeTitolo titolo : resultList) {
				resultListDTO.add(toDTO(titolo));
			}
		}

		return resultListDTO;
	}

	/**
	 * Restituisco tutti gli elementi validi con flg_conferimento_did = true.
	 */
	public List<DeTitoloDTO> findConferimentoDidBySuggestion(String par) {
		TypedQuery<DeTitolo> query = entityManager.createNamedQuery("findDeTitoloConferimentoDidBySuggestion",
				DeTitolo.class);
		query.setParameter("data", new Date());
		query.setParameter("par", "%" + par.trim().toUpperCase() + "%");
		List<DeTitolo> entityList = query.getResultList();
		List<DeTitoloDTO> result = new ArrayList<DeTitoloDTO>(entityList.size());
		for (DeTitolo entity : entityList) {
			result.add(toDTO(entity));
		}

		return result;
	}

	/**
	 * Find by suggestion senza DTO
	 * 
	 * @param par
	 * @return
	 */
	public List<DeTitolo> findBySuggest(String par) {
		String queryString = TS_QUERY;
		par = this.checkSqlParameter(par);

		String queryPar = splitStringForTSQuery(par);
		Query query = entityManager.createNativeQuery(queryString, DeTitolo.class);
		query.setParameter(1, queryPar);

		return query.getResultList();
	}

	public List<DeTitolo> findBySuggestFigli(String par) {
		String queryString = TS_QUERY_FIGLI;
		par = this.checkSqlParameter(par);

		String queryPar = splitStringForTSQuery(par);
		Query query = entityManager.createNativeQuery(queryString, DeTitolo.class);
		query.setParameter(1, queryPar);

		return query.getResultList();
	}

	@Override
	public List<SelectItem> getListItems(boolean addBlank, String order) {
		List<SelectItem> selectItems = new ArrayList<SelectItem>();
		try {
			CriteriaBuilder cb = entityManager.getCriteriaBuilder();
			CriteriaQuery<SelectItem> query = cb.createQuery(SelectItem.class);
			Root<DeTitolo> f = query.from(DeTitolo.class);
			query.select(cb.construct(SelectItem.class, f.get(DeTitolo_.codTitolo), f.get(DeTitolo_.descrizione)));
			List<Predicate> whereConditions = new ArrayList<Predicate>();
			Predicate p = cb.greaterThanOrEqualTo(f.get(DeTitolo_.dtFineVal), new Date());
			whereConditions.add(p);
			p = cb.lessThanOrEqualTo(f.get(DeTitolo_.dtInizioVal), new Date());
			whereConditions.add(p);
			query.where(whereConditions.toArray(new Predicate[whereConditions.size()]));
			if (order != null && order.length() > 0) {
				query.orderBy(cb.asc(f.get(DeTitolo_.descrizione)));
			}
			List<SelectItem> data = entityManager.createQuery(query).setHint("org.hibernate.cacheable", true)
					.getResultList();
			if (addBlank) {
				selectItems.add(0, new SelectItem(null, ""));
			}
			selectItems.addAll(data);

		} catch (NoResultException nre) {
			log.error("No result found for type SelectItem using this statement");

		} catch (RuntimeException re) {
			log.error("Cannot find results for type SelectItem using statement: ");
		}
		return selectItems;
	}

	public List<DeTitolo> getAll() {
		TypedQuery<DeTitolo> query = entityManager.createQuery(QUERY_RICERCA, DeTitolo.class).setHint(
				"org.hibernate.cacheable", true);
		List<DeTitolo> resultList = query.getResultList();
		return resultList;
	}

	
}

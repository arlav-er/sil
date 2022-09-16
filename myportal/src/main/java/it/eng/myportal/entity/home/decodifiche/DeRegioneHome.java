package it.eng.myportal.entity.home.decodifiche;

import it.eng.myportal.dtos.DeRegioneDTO;
import it.eng.myportal.entity.decodifiche.DeProvincia;
import it.eng.myportal.entity.decodifiche.DeProvincia_;
import it.eng.myportal.entity.decodifiche.DeRegione;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;
import javax.faces.model.SelectItem;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang3.StringUtils;

/**
 * Home object for domain model class DeRegione.
 * 
 * @see it.eng.myportal.entity.decodifiche.DeRegione
 * @author Rodi A.
 */
@Stateless
public class DeRegioneHome extends AbstractSuggestibleHome<DeRegione, DeRegioneDTO> {

	private final static String SQL_FIND_REGIONE_MIN = "SELECT t1 "
			+ " FROM DeRegione t1  WHERE t1.codMin = :codRegioneMin ";

	public DeRegione findById(String id) {
		return findById(DeRegione.class, id);
	}

	@Override
	public DeRegioneDTO toDTO(DeRegione entity) {
		if (entity == null)
			return null;
		DeRegioneDTO dto = super.toDTO(entity);
		dto.setId(entity.getCodRegione());
		dto.setDenominazione(entity.getDenominazione());
		dto.setCodMin(entity.getCodMin());
		return dto;
	}

	@Override
	public DeRegione fromDTO(DeRegioneDTO dto) {
		if (dto == null)
			return null;
		DeRegione entity = super.fromDTO(dto);
		entity.setCodRegione(dto.getId());
		entity.setDenominazione(dto.getDenominazione());
		entity.setCodMin(dto.getCodMin());
		return entity;
	}

	@Override
	public String getFieldName() {
		return "denominazione";
	}

	private static final String SELECT_PROVINCE_ITEM_QUERY = "select new javax.faces.model.SelectItem(p.codProvincia,p.denominazione) from DeProvincia p where p.deRegione.codRegione = :codRegione ";

	public List<SelectItem> getProvinceListItems(String codRegione, boolean addBlank) {
		TypedQuery<SelectItem> typedQuery = entityManager.createQuery(SELECT_PROVINCE_ITEM_QUERY, SelectItem.class)
				.setHint("org.hibernate.cacheable", true);
		typedQuery.setParameter("codRegione", codRegione);
		return getListItems(typedQuery, addBlank);
	}

	/**
	 * Restituisce il capoluogo di provincia di una regione
	 * 
	 * @param deRegione
	 * @return il capoluogo
	 */
	public DeProvincia getCapoluogo(DeRegione deRegione) {
		CriteriaBuilder qb = entityManager.getCriteriaBuilder();
		CriteriaQuery<DeProvincia> query = qb.createQuery(DeProvincia.class);
		Root<DeProvincia> provincia = query.from(DeProvincia.class);

		List<Predicate> whereConditions = new ArrayList<Predicate>();

		whereConditions.add(qb.and(qb.equal(provincia.get(DeProvincia_.deRegione), deRegione),
				qb.equal(provincia.get(DeProvincia_.flagCapoluogo), true)));

		query.where(whereConditions.toArray(new Predicate[whereConditions.size()]));

		TypedQuery<DeProvincia> q = entityManager.createQuery(query).setHint("org.hibernate.cacheable", true);

		DeProvincia capoluogo = q.getSingleResult();

		return capoluogo;
	}

	private static final String SELECT_REGIONI_INITCAP_QUERY = "select  new it.eng.myportal.entity.decodifiche.DeRegione(e.codRegione, initcap(e.denominazione), e.dtInizioVal, e.dtFineVal) from DeRegione e where current_date between e.dtInizioVal and e.dtFineVal and lower(trim(e.denominazione)) like :query ";

	public List<DeRegioneDTO> findBySuggestionInitCap(String par) {
		String trimmedSuggestion = StringUtils.trimToEmpty(par);
		TypedQuery<DeRegione> query = entityManager.createQuery(SELECT_REGIONI_INITCAP_QUERY, DeRegione.class).setHint(
				"org.hibernate.cacheable", true);
		String lowerCase = StringUtils.trimToEmpty(trimmedSuggestion).toLowerCase();
		query.setParameter("query", "%" + lowerCase + "%");
		query.setMaxResults(10);

		return findDTOByQuery(query);
	}

	public DeRegioneDTO findDTOByCodRegioneMin(String codRegioneMin) {
		TypedQuery<DeRegione> query;

		query = entityManager.createQuery(SQL_FIND_REGIONE_MIN, DeRegione.class);
		query.setParameter("codRegioneMin", codRegioneMin).setHint("org.hibernate.cacheable", true);

		List<DeRegione> results = query.getResultList();
		if (results == null || results.isEmpty())
			return null;
		return toDTO(results.get(0));

	}

	public DeRegione findByCodRegioneMin(String codRegioneMin) {
		TypedQuery<DeRegione> query;

		query = entityManager.createQuery(SQL_FIND_REGIONE_MIN, DeRegione.class);
		query.setHint("org.hibernate.cacheable", true).setHint("org.hibernate.cacheable", true)
				.setParameter("codRegioneMin", codRegioneMin);

		List<DeRegione> results = query.getResultList();
		if (results == null || results.isEmpty()) {
			return null;
		}
		return results.get(0);
	}
}

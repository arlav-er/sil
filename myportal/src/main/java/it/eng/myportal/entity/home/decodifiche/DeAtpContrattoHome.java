package it.eng.myportal.entity.home.decodifiche;

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

// Generated 1-set-2011 12.32.46 by Hibernate Tools 3.4.0.CR1





import it.eng.myportal.dtos.DeAtpContrattoDTO;
import it.eng.myportal.entity.decodifiche.DeAtpContratto;
import it.eng.myportal.entity.decodifiche.DeAtpContratto_;

/**
 * Home object for domain model class DeAtpContratto.
 * 
 * @see it.eng.myportal.entity.decodifiche.DeAtpContratto
 * @author enrico
 */
@Stateless
public class DeAtpContrattoHome extends AbstractTreeableHome<DeAtpContratto, DeAtpContrattoDTO> {

	private static final String QUERY_SELECT_ITEMS = "select new javax.faces.model.SelectItem(c.codAtpContratto,c.descrizione ) from DeAtpContratto c";
	private final static String SQL_SUGGEST_COD_PADRE = "  " + " SELECT t " + " FROM DeAtpContratto t "
			+ " WHERE t.padre.codAtpContratto = :query " + " ORDER BY  t.descrizione ";

	public DeAtpContratto findById(String id) {
		return findById(DeAtpContratto.class, id);
	}

	/**
	 * Override per visualizzare "descrizione padre - descrizione nodo"
	 */
	@Override
	public List<DeAtpContrattoDTO> findBySuggestion(String desc) {

		CriteriaBuilder qb = entityManager.getCriteriaBuilder();
		CriteriaQuery<DeAtpContrattoDTO> query = qb.createQuery(DeAtpContrattoDTO.class);
		Root<DeAtpContratto> deAtpContratto = query.from(DeAtpContratto.class);

		List<Predicate> whereConditions = new ArrayList<Predicate>();
		Predicate suggestion = qb.like(qb.lower(deAtpContratto.get(DeAtpContratto_.descrizione)),
				"%" + desc.toLowerCase() + "%");
		Predicate nonScaduto = qb.between(qb.currentDate(), deAtpContratto.get(DeAtpContratto_.dtInizioVal),
				deAtpContratto.get(DeAtpContratto_.dtFineVal));
		Predicate nonPadri = qb.notEqual(deAtpContratto.get(DeAtpContratto_.padre).get(DeAtpContratto_.codAtpContratto),
				"0");
		whereConditions.add(suggestion);
		whereConditions.add(nonScaduto);
		whereConditions.add(nonPadri);
		query.where(whereConditions.toArray(new Predicate[whereConditions.size()]));

		query.select(qb.construct(DeAtpContrattoDTO.class, deAtpContratto.get(DeAtpContratto_.codAtpContratto),
				deAtpContratto.get(DeAtpContratto_.descrizione),
				deAtpContratto.get(DeAtpContratto_.padre).get(DeAtpContratto_.codAtpContratto),
				deAtpContratto.get(DeAtpContratto_.padre).get(DeAtpContratto_.descrizione)));

		TypedQuery<DeAtpContrattoDTO> q = entityManager.createQuery(query).setHint("org.hibernate.cacheable", true);
		List<DeAtpContrattoDTO> list = q.getResultList();

		return list;
	}

	@Override
	public DeAtpContrattoDTO toDTO(DeAtpContratto entity) {
		if (entity == null) {
			return null;
		}
		DeAtpContrattoDTO dto = super.toDTO(entity);
		dto.setId(entity.getCodAtpContratto());
		dto.setDescrizione(entity.getDescrizione());
		if (entity.getPadre() != null) {
			dto.setCodPadre(entity.getPadre().getCodAtpContratto());
		}
		dto.setNumeroFigli(entity.getFigli().size());
		return dto;
	}

	@Override
	public DeAtpContratto fromDTO(DeAtpContrattoDTO dto) {
		if (dto == null) {
			return null;
		}
		DeAtpContratto entity = super.fromDTO(dto);
		entity.setCodAtpContratto(dto.getId());
		entity.setDescrizione(dto.getDescrizione());
		if (StringUtils.isNotBlank(dto.getCodPadre())) {
			entity.setPadre(findById(dto.getCodPadre()));
		}
		return entity;
	}

	public List<SelectItem> getListaSelectItem(boolean rigaBianca) {
		TypedQuery<SelectItem> typedQuery = entityManager.createQuery(QUERY_SELECT_ITEMS, SelectItem.class)
				.setHint("org.hibernate.cacheable", true);
		return getListItems(typedQuery, rigaBianca);
	}

	@Override
	public List<DeAtpContrattoDTO> findByCodPadre(String par) {
		TypedQuery<DeAtpContratto> query = entityManager.createQuery(SQL_SUGGEST_COD_PADRE, DeAtpContratto.class)
				.setHint("org.hibernate.cacheable", true);
		query.setParameter("query", par.toUpperCase());
		return findDTOByQuery(query);
	}
}

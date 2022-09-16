package it.eng.myportal.entity.home.decodifiche;

// Generated 1-set-2011 12.32.46 by Hibernate Tools 3.4.0.CR1
import it.eng.myportal.dtos.DeAttivitaMinDTO;
import it.eng.myportal.entity.decodifiche.DeAttivita;
import it.eng.myportal.entity.decodifiche.DeAttivita_;
import it.eng.myportal.entity.decodifiche.min.DeAttivitaMin;
import it.eng.myportal.entity.decodifiche.min.DeAttivitaMin_;
import it.eng.myportal.entity.decodifiche.sil.DeAttivitaSil;
import it.eng.myportal.entity.decodifiche.sil.DeAttivitaSil_;

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

import org.apache.commons.lang3.StringUtils;

/**
 * Home object for domain model class DeAttivitaMin.
 * 
 * @see it.eng.myportal.entity.decodifiche.min.DeAttivitaMin
 * @author Turrini
 */
@Stateless
public class DeAttivitaMinHome extends AbstractTreeableHome<DeAttivitaMin, DeAttivitaMinDTO> {

	private final static String SQL_FIND_DOT = "SELECT t1 " + " FROM DeAttivitaMin t1 "
			+ " WHERE t1.codAttivitaDot = :codAttivitaDot ";

	@EJB
	DeAttivitaSilHome deAttivitaSilHome;

	public DeAttivitaMin findById(String id) {
		return findById(DeAttivitaMin.class, id);
	}

	@Override
	public DeAttivitaMinDTO toDTO(DeAttivitaMin entity) {
		if (entity == null)
			return null;
		DeAttivitaMinDTO dto = super.toDTO(entity);
		dto.setId(entity.getCodAttivitaMin());
		dto.setDescrizione(entity.getDescrizione().toUpperCase());
		dto.setCodAttivitaDot(entity.getCodAttivitaDot());
		if (entity.getPadre() != null) {
			dto.setCodAttivitaPadre(entity.getPadre().getCodAttivitaMin());
		} else {
			dto.setCodAttivitaPadre("0");
		}
		dto.setNumeroFigli((long) entity.getFigli().size());
		return dto;
	}

	@Override
	public DeAttivitaMin fromDTO(DeAttivitaMinDTO dto) {
		if (dto == null)
			return null;
		DeAttivitaMin entity = super.fromDTO(dto);
		entity.setCodAttivitaMin(dto.getId());
		entity.setDescrizione(dto.getDescrizione());
		entity.setCodAttivitaDot(dto.getCodAttivitaDot());
		if (StringUtils.isNotBlank(dto.getCodAttivitaPadre())) {
			entity.setPadre(findById(dto.getCodAttivitaPadre()));
		}
		return entity;
	}

	@Override
	public String getFieldName() {
		return "descrizione";
	}

	@Override
	public String getExtraQuery() {
		return "ORDER BY e.descrizione";
	}

	@Override
	public List<DeAttivitaMinDTO> findByCodPadre(String codAttivitaPadre) {
		List<String> list = new ArrayList<String>();
		list.add(codAttivitaPadre);
		return findByCodPadre("", list);
	}

	public List<DeAttivitaMinDTO> findByCodPadre(List<String> listCodAttivitaPadre) {
		return findByCodPadre("", listCodAttivitaPadre);
	}

	public List<DeAttivitaMinDTO> findByCodPadre(String desc, List<String> listCodAttivitaPadre) {
		List<DeAttivitaMinDTO> returnList = new ArrayList<DeAttivitaMinDTO>();
		List<Predicate> predicates = new ArrayList<Predicate>();
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<DeAttivitaMin> criteriaQuery = criteriaBuilder.createQuery(DeAttivitaMin.class);

		Root<DeAttivitaMin> deAttivitaMin = criteriaQuery.from(DeAttivitaMin.class);
		Join<DeAttivitaMin, DeAttivitaMin> join = deAttivitaMin.join(DeAttivitaMin_.padre);

		Predicate p1 = join.get(DeAttivitaMin_.codAttivitaMin).in(listCodAttivitaPadre);
		predicates.add(p1);

		if (desc != null && !desc.isEmpty()) {
			desc = desc.trim();
			Predicate p2 = criteriaBuilder.like(deAttivitaMin.get(DeAttivitaMin_.descrizione), "%" + desc + "%");
			predicates.add(p2);
		}

		criteriaQuery.where(predicates.toArray(new Predicate[0]));

		/* ordino per descrizione */
		criteriaQuery.orderBy(criteriaBuilder.asc(deAttivitaMin.get(DeAttivitaMin_.descrizione)));

		TypedQuery<DeAttivitaMin> typedQuery = entityManager.createQuery(criteriaQuery).setHint(
				"org.hibernate.cacheable", true);

		List<DeAttivitaMin> results = typedQuery.getResultList();

		for (DeAttivitaMin result : results) {
			DeAttivitaMinDTO newDTO = this.toDTO(result);

			newDTO.setNumeroFigli((long) result.getFigli().size());
			returnList.add(newDTO);
		}

		return returnList;
	}

	@Override
	public String getSuggestionQuery() {
		return "select m from DeAttivitaMin m where lower(m.descrizione) like :query and m.padre is not null";
	}

	/**
	 * Restituisce il cod_attivita_padre della tabella de_attivita_min corrispondente al cod_ateco della tabella
	 * de_attivita tramite la mappatura della tabella de_attivita_sil.
	 * 
	 * @param codAteco
	 * @return
	 */
	public String getCodAttivitaPadreByCodAteco(String codAteco) {
		String result;

		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<String> criteriaQuery = criteriaBuilder.createQuery(String.class);

		criteriaQuery.distinct(true);

		Root<DeAttivita> deAttivita = criteriaQuery.from(DeAttivita.class);
		Root<DeAttivitaSil> deAttivitaSil = criteriaQuery.from(DeAttivitaSil.class);
		Root<DeAttivitaMin> deAttivitaMin = criteriaQuery.from(DeAttivitaMin.class);

		Predicate p1 = criteriaBuilder.equal(deAttivita.get(DeAttivita_.codAteco),
				deAttivitaSil.get(DeAttivitaSil_.deAttivita));
		Predicate p2 = criteriaBuilder.equal(deAttivitaSil.get(DeAttivitaSil_.codAttivitaSil),
				deAttivitaMin.get(DeAttivitaMin_.codAttivitaMin));
		Predicate p3 = criteriaBuilder.isNull(deAttivitaMin.get(DeAttivitaMin_.padre));
		Predicate p4 = criteriaBuilder.equal(deAttivita.get(DeAttivita_.codAteco), codAteco);

		criteriaQuery.where(p1, p2, p3, p4);

		criteriaQuery.select(deAttivitaMin.get(DeAttivitaMin_.codAttivitaMin));

		TypedQuery<String> typedQuery = entityManager.createQuery(criteriaQuery);

		result = typedQuery.getResultList().get(0);

		return result;
	}

	public DeAttivitaMin findByDot(String codAttivitaDot) {
		// List<DeAttivitaMinDTO> returnList = new ArrayList<DeAttivitaMinDTO>();
		TypedQuery<DeAttivitaMin> query;

		query = entityManager.createQuery(SQL_FIND_DOT, DeAttivitaMin.class);
		query.setHint("org.hibernate.cacheable", true).setParameter("codAttivitaDot", codAttivitaDot);

		List<DeAttivitaMin> results = query.getResultList();
		if (results == null || results.isEmpty())
			return null;
		return results.get(0);
	}

	public List<DeAttivitaMinDTO> findDTOBySuggestionAndCodAttivita(String par, String codAttivita) {
		List<DeAttivitaMin> entityList = null;

		if (codAttivita == null || codAttivita.trim().isEmpty()) {
			return findBySuggestion(par);
		} else if (par == null || par.trim().isEmpty()) {
			TypedQuery<DeAttivitaMin> query = entityManager.createNamedQuery("findDeAttivitaMinByCodAttivita",
					DeAttivitaMin.class);
			query.setParameter("codAttivita", codAttivita);
			entityList = query.getResultList();
		} else {
			TypedQuery<DeAttivitaMin> query = entityManager.createNamedQuery(
					"findDeAttivitaMinByCodAttivitaAndSuggestion", DeAttivitaMin.class);
			query.setParameter("codAttivita", codAttivita);
			query.setParameter("par", "%" + par.trim().toUpperCase() + "%");
			entityList = query.getResultList();
		}

		// Converto la lista in lista di DTO
		List<DeAttivitaMinDTO> dtoList = new ArrayList<DeAttivitaMinDTO>(entityList.size());
		for (DeAttivitaMin entity : entityList) {
			dtoList.add(toDTO(entity));
		}
		return dtoList;
	}

	public List<DeAttivitaMinDTO> findDTOBySuggestionAndPadre(String par, String codPadre) {
		List<DeAttivitaMin> entityList = null;

		if (codPadre == null || codPadre.trim().isEmpty()) {
			return findBySuggestion(par);
		} else if (par == null || par.trim().isEmpty()) {
			TypedQuery<DeAttivitaMin> query = entityManager.createNamedQuery("findDeAttivitaMinByCodPadre",
					DeAttivitaMin.class);
			query.setParameter("codAttivitaMin", codPadre);
			entityList = query.getResultList();
		} else {
			TypedQuery<DeAttivitaMin> query = entityManager.createNamedQuery(
					"findDeAttivitaMinByCodPadreAndSuggestion", DeAttivitaMin.class);
			query.setParameter("codAttivitaMin", codPadre);
			query.setParameter("par", "%" + par.trim().toUpperCase() + "%");
			entityList = query.getResultList();
		}

		// Converto la lista in lista di DTO
		List<DeAttivitaMinDTO> dtoList = new ArrayList<DeAttivitaMinDTO>(entityList.size());
		for (DeAttivitaMin entity : entityList) {
			dtoList.add(toDTO(entity));
		}
		return dtoList;
	}
}

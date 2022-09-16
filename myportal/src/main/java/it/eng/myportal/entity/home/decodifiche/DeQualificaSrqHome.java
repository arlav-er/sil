package it.eng.myportal.entity.home.decodifiche;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;

import it.eng.myportal.dtos.DeQualificaSrqDTO;
import it.eng.myportal.entity.decodifiche.DeQualificaSrq;

/**
 * Home object for domain model class DeQualificaSrq.
 * 
 * @see it.eng.myportal.entity.decodifiche.DeQualificaSrq
 * @author Turrini
 */
@Stateless
public class DeQualificaSrqHome extends
		AbstractSuggestibleHome<DeQualificaSrq, DeQualificaSrqDTO> {

	private static final String QUERY_SEL_BY_DESCR = " select c from DeQualificaSrq c where upper(c.descrizione) like upper(:denominazione)";

	public DeQualificaSrq findById(final String id) {
		return findById(DeQualificaSrq.class, id);
	}

	public DeQualificaSrq findByDenominazione(String denominazione) {
		log.debug("getting DeQualificaSrq instance with name: " + denominazione);
		DeQualificaSrq instance = null;
		try {
			TypedQuery<DeQualificaSrq> query = entityManager.createQuery(
					QUERY_SEL_BY_DESCR, DeQualificaSrq.class);

			query.setParameter("denominazione",
					denominazione.replace("'", "''"));
			instance = findById(((DeQualificaSrq) query.getSingleResult())
					.getCodQualificaSrq());
			log.debug("get DeQualificaSrq successful");

		} catch (NoResultException nre) {
			log.error("Cannot find DeQualificaSrq result for query: "
					+ denominazione, nre);

		} catch (RuntimeException re) {
			log.error("Errore durante il tentativo di ricerca dell'istanza DeQualificaSrq: " + re.getMessage());
		}
		return instance;
	}

	@Override
	public DeQualificaSrqDTO toDTO(DeQualificaSrq entity) {
		if (entity == null)
			return null;
		DeQualificaSrqDTO dto = super.toDTO(entity);
		dto.setId(entity.getCodQualificaSrq());
		dto.setDescrizione(entity.getDescrizione());
		return dto;
	}

	@Override
	public DeQualificaSrq fromDTO(DeQualificaSrqDTO dto) {
		if (dto == null)
			return null;
		DeQualificaSrq entity = super.fromDTO(dto);
		entity.setCodQualificaSrq(dto.getId());
		entity.setDescrizione(dto.getDescrizione());
		return entity;
	}

	@Override
	public String getFieldName() {
		return "denominazione";
	}

	public List<DeQualificaSrqDTO> findSuggestion(String query) {
		List<DeQualificaSrq> deQualificaSrqList = findBySuggestionNoDto(query);
		List<DeQualificaSrqDTO> deQualificaSrqDTOList = new ArrayList<DeQualificaSrqDTO>();
		
		if (deQualificaSrqList != null && !deQualificaSrqList.isEmpty()) {
			for (DeQualificaSrq deQualificaSrq : deQualificaSrqList)
				deQualificaSrqDTOList.add(toDTO(deQualificaSrq));
		}
		return deQualificaSrqDTOList;
	}
	
	public List<DeQualificaSrq> findBySuggestionNoDto(String par) {

		List<DeQualificaSrq> deQualificaSrqList = entityManager
				.createNamedQuery("DeQualificaSrqFindSuggestionFigli", DeQualificaSrq.class)
				.setHint("org.hibernate.cacheable", true).setParameter("query", "%"+par+"%").getResultList();
		
		return deQualificaSrqList;
	}

}

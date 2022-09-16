package it.eng.myportal.entity.home.decodifiche;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.TypedQuery;

import org.apache.commons.lang3.StringUtils;

// Generated 1-set-2011 12.32.46 by Hibernate Tools 3.4.0.CR1

import it.eng.myportal.dtos.DeMansioneDTO;
import it.eng.myportal.entity.decodifiche.DeMansione;

/**
 * Home object for domain model class DeMansione.
 * 
 * @see it.eng.myportal.entity.decodifiche.DeMansione
 * @author Rodi A.
 */
@Stateless
public class DeMansioneHome extends AbstractTreeableHome<DeMansione, DeMansioneDTO> {

	private final static String SQL_SUGGEST_COD_PADRE = " SELECT t1, count(t2) AS numeroFigli" + " FROM DeMansione t1 "
	        + " LEFT JOIN t1.figli t2 " + " WHERE t1.padre.codMansione = :codMansione " 
	        + " and current_date between t1.dtInizioVal and t1.dtFineVal"
	        + " GROUP BY t1 "
	        + " ORDER BY  t1.descrizione, t1.codMansione ";

	private final static String SQL_SUGGEST_PADRI = " SELECT t1, count(t2) AS numeroFigli" + " FROM DeMansione t1 "
	        + " LEFT JOIN t1.figli t2 " + " WHERE t1.padre is null "
			+ " and current_date between t1.dtInizioVal and t1.dtFineVal"
			+ " GROUP BY t1 "
	        + " ORDER BY  t1.descrizione, t1.codMansione ";

	private static final String QUERY_RICERCA = "select c from DeMansione c where current_date between c.dtInizioVal and c.dtFineVal order by c.descrizione";
	
	public DeMansione findById(String id) {
		return findById(DeMansione.class, id);
	}

	@Override
	public DeMansioneDTO toDTO(DeMansione entity) {
		if (entity == null)
			return null;
		DeMansioneDTO dto = super.toDTO(entity);
		dto.setId(entity.getCodMansione());
		dto.setDescrizione(entity.getDescrizione());
		if (entity.getPadre() != null) {
			dto.setCodPadre(entity.getPadre().getCodMansione());
		}
		return dto;
	}

	@Override
	public DeMansione fromDTO(DeMansioneDTO dto) {
		if (dto == null)
			return null;
		DeMansione entity = super.fromDTO(dto);
		entity.setCodMansione(dto.getId());
		entity.setDescrizione(dto.getDescrizione());
		if (StringUtils.isNotBlank(dto.getCodPadre())) {
			entity.setPadre(findById(dto.getCodPadre()));
		}
		return entity;
	}

	@Override
	public String getExtraQuery() {
		return "and padre is not null" + super.getExtraQuery();
	}

	@Override
	public List<DeMansioneDTO> findByCodPadre(String par) {
		List<DeMansioneDTO> returnList = new ArrayList<DeMansioneDTO>();

		TypedQuery<Object[]> query;
		if (!"0".equalsIgnoreCase(par)) {
			query = entityManager.createQuery(SQL_SUGGEST_COD_PADRE, Object[].class);
			query.setHint("org.hibernate.cacheable", true).setParameter("codMansione", par.toUpperCase());
		} else {
			query = entityManager.createQuery(SQL_SUGGEST_PADRI, Object[].class);
		}

		List<Object[]> results = query.getResultList();
		for (Object[] result : results) {
			DeMansioneDTO newDTO = this.toDTO((DeMansione) result[0]);
			newDTO.setNumeroFigli((Long) result[1]);
			returnList.add(newDTO);
		}

		return returnList;
	}
	
	public List<DeMansione> getAll() {		
		TypedQuery<DeMansione> query = entityManager.createQuery(QUERY_RICERCA, DeMansione.class);
		List<DeMansione> resultList = query.getResultList();		
		return resultList;
	}
	
	public List<DeMansione> findMansioniStartingWith(String startsWith) {
		return entityManager.createNamedQuery("findMansioniLikeFigli", DeMansione.class)
				.setParameter("codLike", startsWith + "%").setHint("org.hibernate.cacheable", true).getResultList();
	}
	
	public List<DeMansione> findByDescrizione(String query) {
		return entityManager.createNamedQuery("findMansioniLike", DeMansione.class)
				.setParameter("codLike", "%" + query + "%").setHint("org.hibernate.cacheable", true).getResultList();
	}
	
	public List<DeMansioneDTO> findDTOByDescrizione(String query) {
		List<DeMansione> deMansiones = findByDescrizione(query);
		List<DeMansioneDTO> deMansioneDtos = new ArrayList<DeMansioneDTO>();
		for (DeMansione current : deMansiones) {
			deMansioneDtos.add(toDTO(current));
		}
		return deMansioneDtos;
	}

	public List<DeMansione> findBySuggestionNoDTO(String startsWith) {
		return findMansioniStartingWith(startsWith);
	}
	
}

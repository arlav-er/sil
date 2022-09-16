package it.eng.myportal.entity.home.decodifiche;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.TypedQuery;

import org.apache.commons.lang3.StringUtils;

// Generated 1-set-2011 12.32.46 by Hibernate Tools 3.4.0.CR1

import it.eng.myportal.dtos.DeMansioneDTO;
import it.eng.myportal.entity.decodifiche.DeBpMansione;

/**
 * Home object for domain model class DeMansione.
 * 
 * @see it.eng.myportal.entity.decodifiche.DeMansione
 * @author Rodi A.
 */
@Stateless
public class DeBpMansioneHome extends AbstractTreeableHome<DeBpMansione, DeMansioneDTO> {

	public DeBpMansione findById(String id) {
		return findById(DeBpMansione.class, id);
	}

	@Override
	public DeMansioneDTO toDTO(DeBpMansione entity) {
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
	public DeBpMansione fromDTO(DeMansioneDTO dto) {
		if (dto == null)
			return null;
		DeBpMansione entity = super.fromDTO(dto);
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
	@Deprecated // schifo, non usare
	public List<DeMansioneDTO> findByCodPadre(String par) {
		List<DeMansioneDTO> returnList = new ArrayList<DeMansioneDTO>();

		List<DeBpMansione> tiro = findDeBpMansioneFigliById(par);

		for (DeBpMansione result : tiro) {
			DeMansioneDTO newDTO = this.toDTO(result);
			// newDTO.setNumeroFigli((Long) result.);
			returnList.add(newDTO);
		}

		return returnList;
	}

	// Ritorna la lista delle mansioni
	public List<DeBpMansione> findDeBpMansioneForTree() {
		TypedQuery<DeBpMansione> query = entityManager.createNamedQuery("findDeBpMansioneForTree", DeBpMansione.class)
				.setParameter("parDate", new Date()).setHint("org.hibernate.cacheable", true);
		return query.getResultList();
	}
	
	public List<DeBpMansione> findDeBpMansioneAgricoliForTree() {
		TypedQuery<DeBpMansione> query = entityManager.createNamedQuery("findDeBpMansioneAgricoliForTree", DeBpMansione.class)
				.setParameter("parDate", new Date()).setHint("org.hibernate.cacheable", true);
		return query.getResultList();
	}

	// Ritorna la mansione padre della mansione cercata secondo il codice passato
	public DeBpMansione findDeBpMansionePadreById(String codMansione) {
		TypedQuery<DeBpMansione> query = entityManager.createNamedQuery("findDeBpMansionePadreById", DeBpMansione.class)
				.setParameter("parDate", new Date()).setParameter("parCodMans", codMansione)
				.setHint("org.hibernate.cacheable", true);
		return query.getSingleResult();
	}

	// Ritorna la lista delle mansioni figlio
	public List<DeBpMansione> findDeBpMansioneFigliById(String codMansione) {
		TypedQuery<DeBpMansione> query = entityManager.createNamedQuery("findDeBpMansioneFigliById", DeBpMansione.class)
				.setParameter("parDate", new Date()).setParameter("parCodMans", codMansione)
				.setHint("org.hibernate.cacheable", true);
		return query.getResultList();
	}

	public List<DeBpMansione> findBySuggestionNoDTO(String startsWith) {
		return entityManager.createNamedQuery("findBpMansioniLike", DeBpMansione.class)
				.setParameter("codLike", "%" + startsWith + "%").getResultList();
	}
	
	

}

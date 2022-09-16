package it.eng.myportal.entity.home;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.TypedQuery;

import it.eng.myportal.dtos.PatrAbiProvinciaDTO;
import it.eng.myportal.entity.PatrAbiProvincia;
import it.eng.myportal.entity.home.decodifiche.DeProvinciaHome;

/**
 * Home per la entity PatrAbiProvincia.
 * @author Cozza G.
 */
@Stateless
public class PatrAbiProvinciaHome extends AbstractUpdatableHome<PatrAbiProvincia, PatrAbiProvinciaDTO> {
	
	@EJB
	PatronatoHome patronatoHome;
	
	@EJB
	DeProvinciaHome deProvinciaHome;

	@Override
	public PatrAbiProvincia findById(Integer id) {
		return findById(PatrAbiProvincia.class, id);
	}
	
	public List<PatrAbiProvincia> findByPatronatoId(Integer patronatoId) {
		TypedQuery<PatrAbiProvincia> query = 
				entityManager.createNamedQuery("findPatrAbiProvinciaByPatronatoId", PatrAbiProvincia.class);
		query.setParameter("patronatoId", patronatoId);
		return query.getResultList();
	}
	
	public List<PatrAbiProvinciaDTO> findDTOByPatronatoId(Integer patronatoId) {
		List<PatrAbiProvinciaDTO> result = new ArrayList<PatrAbiProvinciaDTO>();
		List<PatrAbiProvincia> entities = findByPatronatoId(patronatoId);
		for (PatrAbiProvincia entity : entities) {
			result.add(toDTO(entity));
		}
		return result;
	}
	
	/**
	 * Crea un DTO a partire dalla entity.
	 */
	public PatrAbiProvinciaDTO toDTO(PatrAbiProvincia entity) {
		if (entity == null)
			return null;
		PatrAbiProvinciaDTO dto = super.toDTO(entity);
		dto.setId(entity.getIdPatrAbiProvincia());
		dto.setPatronato(patronatoHome.toDTO(entity.getPatronato()));
		dto.setProvincia(deProvinciaHome.toDTO(entity.getDeProvincia()));
		return dto;
	}
	
	/**
	 * Crea una entity a partire dal DTO.
	 */
	public PatrAbiProvincia fromDTO(PatrAbiProvinciaDTO dto) {
		if (dto == null)
			return null;
		PatrAbiProvincia entity = super.fromDTO(dto);
		entity.setIdPatrAbiProvincia(dto.getId());
		entity.setPatronato(patronatoHome.fromDTO(dto.getPatronato()));
		entity.setDeProvincia(deProvinciaHome.fromDTO(dto.getProvincia()));
		return entity;
	}

}

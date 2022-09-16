package it.eng.myportal.entity.home.decodifiche;

import it.eng.myportal.dtos.DeStatoAdesioneDTO;
import it.eng.myportal.entity.decodifiche.DeStatoAdesione;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.TypedQuery;

@Stateless
public class DeStatoAdesioneHome extends AbstractDecodeHome<DeStatoAdesione, DeStatoAdesioneDTO> {

	private static final String QUERY_RICERCA = "select c from DeStatoAdesione c where current_date between c.dtInizioVal and c.dtFineVal order by c.descrizione";
	
	public DeStatoAdesione findById(final String id) {
		return findById(DeStatoAdesione.class, id);
	}

	@Override
	public DeStatoAdesioneDTO toDTO(final DeStatoAdesione entity) {
		if (entity == null) return null;
		DeStatoAdesioneDTO dto = super.toDTO(entity);
		dto.setId(entity.getCodStatoAdesione());
		dto.setDescrizione(entity.getDescrizione());
		dto.setCodMonoClasse(entity.getCodMonoClasse());
		return dto;
	}
	
	@Override
	public DeStatoAdesione fromDTO(final DeStatoAdesioneDTO dto) {
		if (dto == null) return null;
		DeStatoAdesione entity = super.fromDTO(dto);   
		entity.setCodStatoAdesione(dto.getId());
		entity.setDescrizione(dto.getDescrizione());
		entity.setCodMonoClasse(dto.getCodMonoClasse());
		return entity;
	}
	
	public List<DeStatoAdesione> getAll() {		
		TypedQuery<DeStatoAdesione> query = entityManager.createQuery(QUERY_RICERCA, DeStatoAdesione.class);
		List<DeStatoAdesione> resultList = query.getResultList();		
		return resultList;
	}
	
	public List<DeStatoAdesioneDTO> getAllDTO() {		
		List<DeStatoAdesioneDTO> deStatoAdesioneDTOList = new ArrayList<DeStatoAdesioneDTO>();
		List<DeStatoAdesione> deStatoAdesioneList = getAll();
		for (DeStatoAdesione deStatoAdesione : deStatoAdesioneList) {
			deStatoAdesioneDTOList.add(toDTO(deStatoAdesione));
		}
		return deStatoAdesioneDTOList;
	}
	
}

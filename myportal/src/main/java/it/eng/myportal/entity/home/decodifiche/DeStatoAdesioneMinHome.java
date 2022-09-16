package it.eng.myportal.entity.home.decodifiche;

import it.eng.myportal.dtos.DeStatoAdesioneMinDTO;
import it.eng.myportal.entity.decodifiche.min.DeStatoAdesioneMin;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.TypedQuery;

@Stateless
public class DeStatoAdesioneMinHome extends AbstractDecodeHome<DeStatoAdesioneMin, DeStatoAdesioneMinDTO> {

	private static final String QUERY_RICERCA = "select c from DeStatoAdesioneMin c where current_date between c.dtInizioVal and c.dtFineVal order by c.descrizione";

	public DeStatoAdesioneMin findById(final String id) {
		return findById(DeStatoAdesioneMin.class, id);
	}

	@Override
	public DeStatoAdesioneMinDTO toDTO(final DeStatoAdesioneMin entity) {
		if (entity == null) {
			return null;
		}
		
		DeStatoAdesioneMinDTO dto = super.toDTO(entity);
		dto.setId(entity.getCodStatoAdesioneMin());
		dto.setDescrizione(entity.getDescrizione());
		dto.setCodMonoAttiva(entity.getCodMonoAttiva());
		return dto;
	}

	@Override
	public DeStatoAdesioneMin fromDTO(final DeStatoAdesioneMinDTO dto) {
		if (dto == null)
			return null;
		DeStatoAdesioneMin entity = super.fromDTO(dto);
		entity.setCodStatoAdesioneMin(dto.getId());
		entity.setDescrizione(dto.getDescrizione());
		entity.setCodMonoAttiva(dto.getCodMonoAttiva());
		return entity;
	}

	public List<DeStatoAdesioneMin> getAll() {
		TypedQuery<DeStatoAdesioneMin> query = entityManager.createQuery(QUERY_RICERCA, DeStatoAdesioneMin.class);
		List<DeStatoAdesioneMin> resultList = query.getResultList();
		return resultList;
	}

	public List<DeStatoAdesioneMinDTO> getAllDTO() {
		List<DeStatoAdesioneMinDTO> deStatoAdesioneMinDTOList = new ArrayList<DeStatoAdesioneMinDTO>();
		List<DeStatoAdesioneMin> deStatoAdesioneMinList = getAll();
		for (DeStatoAdesioneMin deStatoAdesioneMin : deStatoAdesioneMinList) {
			deStatoAdesioneMinDTOList.add(toDTO(deStatoAdesioneMin));
		}
		return deStatoAdesioneMinDTOList;
	}

}

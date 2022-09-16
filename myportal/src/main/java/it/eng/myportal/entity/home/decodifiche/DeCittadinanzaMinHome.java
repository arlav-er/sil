package it.eng.myportal.entity.home.decodifiche;

import it.eng.myportal.dtos.DeCittadinanzaMinDTO;
import it.eng.myportal.entity.decodifiche.min.DeCittadinanzaMin;

import javax.ejb.Stateless;

@Stateless
public class DeCittadinanzaMinHome extends AbstractSuggestibleHome<DeCittadinanzaMin, DeCittadinanzaMinDTO> {

	@Override
	public DeCittadinanzaMin findById(String id) {
		return findById(DeCittadinanzaMin.class, id);
	}

	@Override
	public DeCittadinanzaMinDTO toDTO(DeCittadinanzaMin entity) {
		if (entity == null)
			return null;
		DeCittadinanzaMinDTO dto = super.toDTO(entity);
		dto.setId(entity.getCodCittadinanzaMin());
		dto.setDescrizione(entity.getDescrizione());
		return dto;
	}

	@Override
	public DeCittadinanzaMin fromDTO(DeCittadinanzaMinDTO dto) {
		if (dto == null)
			return null;
		DeCittadinanzaMin entity = super.fromDTO(dto);
		entity.setCodCittadinanzaMin(dto.getId());
		entity.setDescrizione(dto.getDescrizione());
		return entity;
	}
}

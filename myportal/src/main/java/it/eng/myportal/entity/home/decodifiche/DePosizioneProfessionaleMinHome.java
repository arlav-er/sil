package it.eng.myportal.entity.home.decodifiche;

import it.eng.myportal.dtos.DePosizioneProfessionaleMinDTO;
import it.eng.myportal.entity.decodifiche.min.DePosizioneProfessionaleMin;

import javax.ejb.Stateless;

@Stateless
public class DePosizioneProfessionaleMinHome extends
		AbstractDecodeHome<DePosizioneProfessionaleMin, DePosizioneProfessionaleMinDTO> {

	@Override
	public DePosizioneProfessionaleMin findById(String id) {
		return findById(DePosizioneProfessionaleMin.class, id);
	}

	@Override
	public DePosizioneProfessionaleMinDTO toDTO(DePosizioneProfessionaleMin entity) {
		if (entity == null)
			return null;
		DePosizioneProfessionaleMinDTO dto = super.toDTO(entity);
		dto.setId(entity.getCodPosizioneProfessionaleMin());
		dto.setDescrizione(entity.getDescrizione());
		return dto;
	}

	@Override
	public DePosizioneProfessionaleMin fromDTO(DePosizioneProfessionaleMinDTO dto) {
		if (dto == null)
			return null;
		DePosizioneProfessionaleMin entity = super.fromDTO(dto);
		entity.setCodPosizioneProfessionaleMin(dto.getId());
		entity.setDescrizione(dto.getDescrizione());
		return entity;
	}
}

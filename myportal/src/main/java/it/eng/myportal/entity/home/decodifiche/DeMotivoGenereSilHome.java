package it.eng.myportal.entity.home.decodifiche;

import it.eng.myportal.dtos.DeMotivoGenereSilDTO;
import it.eng.myportal.entity.decodifiche.DeMotivoGenereSil;

import javax.ejb.Stateless;

@Stateless
public class DeMotivoGenereSilHome extends AbstractDecodeHome<DeMotivoGenereSil, DeMotivoGenereSilDTO> {

	@Override
	public DeMotivoGenereSil findById(String id) {
		return findById(DeMotivoGenereSil.class, id);
	}

	@Override
	public DeMotivoGenereSilDTO toDTO(final DeMotivoGenereSil entity) {
		if (entity == null)
			return null;
		DeMotivoGenereSilDTO dto = super.toDTO(entity);
		dto.setId(entity.getCodMotivoGenereSil());
		dto.setDescrizione(entity.getDescrizione());
		return dto;
	}

	@Override
	public DeMotivoGenereSil fromDTO(final DeMotivoGenereSilDTO dto) {
		if (dto == null)
			return null;
		DeMotivoGenereSil entity = super.fromDTO(dto);
		entity.setCodMotivoGenereSil(dto.getId());
		entity.setDescrizione(dto.getDescrizione());
		return entity;
	}
}

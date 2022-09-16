package it.eng.myportal.entity.home.decodifiche;

import it.eng.myportal.dtos.DeMotivoEtaSilDTO;
import it.eng.myportal.entity.decodifiche.sil.DeMotivoEtaSil;

import javax.ejb.Stateless;

@Stateless
public class DeMotivoEtaSilHome extends AbstractDecodeHome<DeMotivoEtaSil, DeMotivoEtaSilDTO> {

	@Override
	public DeMotivoEtaSil findById(String id) {
		return findById(DeMotivoEtaSil.class, id);
	}

	@Override
	public DeMotivoEtaSilDTO toDTO(final DeMotivoEtaSil entity) {
		if (entity == null)
			return null;
		DeMotivoEtaSilDTO dto = super.toDTO(entity);
		dto.setId(entity.getCodMotivoEtaSil());
		dto.setDescrizione(entity.getDescrizione());
		return dto;
	}

	@Override
	public DeMotivoEtaSil fromDTO(final DeMotivoEtaSilDTO dto) {
		if (dto == null)
			return null;
		DeMotivoEtaSil entity = super.fromDTO(dto);
		entity.setCodMotivoEtaSil(dto.getId());
		entity.setDescrizione(dto.getDescrizione());
		return entity;
	}

}

package it.eng.myportal.entity.home.decodifiche;

import it.eng.myportal.dtos.DeAreaSilDTO;
import it.eng.myportal.entity.decodifiche.sil.DeAreaSil;

import javax.ejb.Stateless;

@Stateless
public class DeAreaSilHome extends AbstractSuggestibleHome<DeAreaSil, DeAreaSilDTO> {

	@Override
	public DeAreaSil findById(String id) {
		return findById(DeAreaSil.class, id);
	}

	@Override
	public DeAreaSilDTO toDTO(final DeAreaSil entity) {
		if (entity == null)
			return null;
		DeAreaSilDTO dto = super.toDTO(entity);
		dto.setId(entity.getCodAreaSil());
		dto.setDescrizione(entity.getDescrizione());
		return dto;
	}

	@Override
	public DeAreaSil fromDTO(final DeAreaSilDTO dto) {
		if (dto == null)
			return null;
		DeAreaSil entity = super.fromDTO(dto);
		entity.setCodAreaSil(dto.getId());
		entity.setDescrizione(dto.getDescrizione());
		return entity;
	}

}

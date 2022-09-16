package it.eng.myportal.entity.home.decodifiche;

import it.eng.myportal.dtos.DeModalitaLinguaDTO;
import it.eng.myportal.entity.decodifiche.DeModalitaLingua;

import javax.ejb.Stateless;

@Stateless
public class DeModalitaLinguaHome extends AbstractSuggestibleHome<DeModalitaLingua, DeModalitaLinguaDTO> {

	@Override
	public DeModalitaLingua findById(String id) {
		return findById(DeModalitaLingua.class, id);
	}

	@Override
	public DeModalitaLinguaDTO toDTO(DeModalitaLingua entity) {
		if (entity == null) {
			return null;
		}

		DeModalitaLinguaDTO dto = super.toDTO(entity);
		dto.setId(entity.getCodModalitaLingua());
		dto.setDescrizione(entity.getDescrizione());
		return dto;
	}

	@Override
	public DeModalitaLingua fromDTO(DeModalitaLinguaDTO dto) {
		if (dto == null) {
			return null;
		}

		DeModalitaLingua entity = super.fromDTO(dto);
		entity.setCodModalitaLingua(dto.getId());
		entity.setDescrizione(dto.getDescrizione());
		return entity;
	}

}

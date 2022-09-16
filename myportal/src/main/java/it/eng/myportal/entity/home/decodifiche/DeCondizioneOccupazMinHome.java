package it.eng.myportal.entity.home.decodifiche;

import it.eng.myportal.dtos.DeCondizioneOccupazMinDTO;
import it.eng.myportal.entity.decodifiche.min.DeCondizioneOccupazMin;

import javax.ejb.Stateless;

@Stateless
public class DeCondizioneOccupazMinHome extends AbstractDecodeHome<DeCondizioneOccupazMin, DeCondizioneOccupazMinDTO> {

	@Override
	public DeCondizioneOccupazMin findById(String id) {
		return findById(DeCondizioneOccupazMin.class, id);
	}

	@Override
	public DeCondizioneOccupazMinDTO toDTO(DeCondizioneOccupazMin entity) {
		if (entity == null)
			return null;
		DeCondizioneOccupazMinDTO dto = super.toDTO(entity);
		dto.setId(entity.getCodCondizioneOccupazMin());
		dto.setDescrizione(entity.getDescrizione());
		return dto;
	}

	@Override
	public DeCondizioneOccupazMin fromDTO(DeCondizioneOccupazMinDTO dto) {
		if (dto == null)
			return null;
		DeCondizioneOccupazMin entity = super.fromDTO(dto);
		entity.setCodCondizioneOccupazMin(dto.getId());
		entity.setDescrizione(dto.getDescrizione());
		return entity;
	}
}

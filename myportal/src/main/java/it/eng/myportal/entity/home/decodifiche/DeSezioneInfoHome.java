package it.eng.myportal.entity.home.decodifiche;

import it.eng.myportal.dtos.DeSezioneInfoDTO;
import it.eng.myportal.entity.decodifiche.DeSezioneInfo;

import javax.ejb.Stateless;

@Stateless
public class DeSezioneInfoHome extends AbstractDecodeHome<DeSezioneInfo, DeSezioneInfoDTO> {
	
	public DeSezioneInfo findById(String id) {
		return findById(DeSezioneInfo.class, id);
	}
	
	public DeSezioneInfoDTO findDTOById(String id) {
		return toDTO(findById(id));
	}
	
	@Override
	public DeSezioneInfoDTO toDTO(final DeSezioneInfo entity) {
		if (entity == null)
			return null;
		final DeSezioneInfoDTO dto = super.toDTO(entity);
		dto.setId(entity.getCodSezioneInfo());
		dto.setDescrizione(entity.getDescrizione());
		dto.setTesto(entity.getTesto());
		return dto;
	}

}

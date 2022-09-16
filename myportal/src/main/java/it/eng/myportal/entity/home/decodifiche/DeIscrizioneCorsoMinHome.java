package it.eng.myportal.entity.home.decodifiche;

import it.eng.myportal.dtos.DeIscrizioneCorsoMinDTO;
import it.eng.myportal.entity.decodifiche.min.DeIscrizioneCorsoMin;

import javax.ejb.Stateless;

@Stateless
public class DeIscrizioneCorsoMinHome extends AbstractDecodeHome<DeIscrizioneCorsoMin, DeIscrizioneCorsoMinDTO> {

	@Override
	public DeIscrizioneCorsoMin findById(String id) {
		return findById(DeIscrizioneCorsoMin.class, id);
	}

	@Override
	public DeIscrizioneCorsoMinDTO toDTO(DeIscrizioneCorsoMin entity) {
		if (entity == null)
			return null;
		DeIscrizioneCorsoMinDTO dto = super.toDTO(entity);
		dto.setId(entity.getCodIscrizioneCorsoMin());
		dto.setDescrizione(entity.getDescrizione());
		return dto;
	}

	@Override
	public DeIscrizioneCorsoMin fromDTO(DeIscrizioneCorsoMinDTO dto) {
		if (dto == null)
			return null;
		DeIscrizioneCorsoMin entity = super.fromDTO(dto);
		entity.setCodIscrizioneCorsoMin(dto.getId());
		entity.setDescrizione(dto.getDescrizione());
		return entity;
	}

}

package it.eng.myportal.entity.home.decodifiche;

import it.eng.myportal.dtos.DeMotivoCessazioneDTO;
import it.eng.myportal.entity.decodifiche.DeMotivoCessazione;

import javax.ejb.Stateless;

@Stateless
public class DeMotivoCessazioneHome extends AbstractDecodeHome<DeMotivoCessazione, DeMotivoCessazioneDTO> {

	@Override
	public DeMotivoCessazione findById(String id) {
		return findById(DeMotivoCessazione.class, id);
	}

	@Override
	public DeMotivoCessazioneDTO toDTO(final DeMotivoCessazione entity) {
		if (entity == null)
			return null;
		DeMotivoCessazioneDTO dto = super.toDTO(entity);
		dto.setId(entity.getCodMotivoCessazione());
		dto.setDescrizione(entity.getDescrizione());
		return dto;
	}

	@Override
	public DeMotivoCessazione fromDTO(final DeMotivoCessazioneDTO dto) {
		if (dto == null)
			return null;
		DeMotivoCessazione entity = super.fromDTO(dto);
		entity.setCodMotivoCessazione(dto.getId());
		entity.setDescrizione(dto.getDescrizione());
		return entity;
	}
}

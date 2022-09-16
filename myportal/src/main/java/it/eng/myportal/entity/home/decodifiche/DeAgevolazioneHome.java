package it.eng.myportal.entity.home.decodifiche;

import it.eng.myportal.dtos.DeAgevolazioneDTO;
import it.eng.myportal.entity.decodifiche.DeAgevolazione;

import javax.ejb.Stateless;

/**
 * Home object for domain model class DeAgevolazione.
 * 
 * @see it.eng.myportal.entity.decodifiche.DeAgevolazione
 * @author Rodi A.
 */
@Stateless
public class DeAgevolazioneHome extends AbstractSuggestibleHome<DeAgevolazione, DeAgevolazioneDTO> {
	
	public DeAgevolazione findById(final String id) {
		return findById(DeAgevolazione.class, id);
	}

	@Override
	public DeAgevolazioneDTO toDTO(final DeAgevolazione entity) {
		if (entity == null)
			return null;
		DeAgevolazioneDTO dto = super.toDTO(entity);
		dto.setId(entity.getCodAgevolazione());
		dto.setDescrizione(entity.getDescrizione());
		return dto;
	}

	@Override
	public DeAgevolazione fromDTO(DeAgevolazioneDTO dto) {
		if (dto == null)
			return null;
		final DeAgevolazione entity = super.fromDTO(dto);
		entity.setCodAgevolazione(dto.getId());
		entity.setDescrizione(dto.getDescrizione());
		return entity;
	}

}

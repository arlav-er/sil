package it.eng.myportal.entity.home.decodifiche;

import it.eng.myportal.dtos.DeAtpContrattoInfoDTO;
import it.eng.myportal.entity.decodifiche.DeAtpContrattoInfo;

import javax.ejb.Stateless;

/**
 *
 *
 * @author enrico
 */
@Stateless
public class DeAtpContrattoInfoHome extends AbstractSuggestibleHome<DeAtpContrattoInfo, DeAtpContrattoInfoDTO> {

	public DeAtpContrattoInfo findById(final String id) {
		return findById(DeAtpContrattoInfo.class, id);
	}

	@Override
	public DeAtpContrattoInfoDTO toDTO(final DeAtpContrattoInfo entity) {
		if (entity == null) {
			return null;
		}
		final DeAtpContrattoInfoDTO dto = super.toDTO(entity);
		dto.setId(entity.getCodAtpContratto());
		dto.setTitoloInfo(entity.getTitoloInfo());
		dto.setDescrizione(entity.getDescrizioneInfo());
		return dto;
	}

	@Override
	public DeAtpContrattoInfo fromDTO(final DeAtpContrattoInfoDTO dto) {
		if (dto == null) {
			return null;
		}
		final DeAtpContrattoInfo entity =super.fromDTO(dto);
		entity.setCodAtpContratto(dto.getId());
		entity.setTitoloInfo(dto.getTitoloInfo());
		entity.setDescrizioneInfo(dto.getDescrizione());		
		return entity;
	}

	@Override
	public String getFieldName() {
		return "descrizioneInfo";
	}
}

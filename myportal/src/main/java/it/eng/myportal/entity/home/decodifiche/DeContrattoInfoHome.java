package it.eng.myportal.entity.home.decodifiche;

import it.eng.myportal.dtos.DeContrattoInfoDTO;
import it.eng.myportal.entity.decodifiche.DeContrattoInfo;

import javax.ejb.Stateless;

/**
 *
 *
 * @author Girotti
 */
@Stateless
public class DeContrattoInfoHome extends AbstractSuggestibleHome<DeContrattoInfo, DeContrattoInfoDTO> {

	public DeContrattoInfo findById(final String id) {
		return findById(DeContrattoInfo.class, id);
	}

	@Override
	public DeContrattoInfoDTO toDTO(final DeContrattoInfo entity) {
		if (entity == null) {
			return null;
		}
		final DeContrattoInfoDTO dto = super.toDTO(entity);
		dto.setId(entity.getCodContratto());
		dto.setTitoloInfo(entity.getTitoloInfo());
		dto.setDescrizione(entity.getDescrizioneInfo());
		return dto;
	}

	@Override
	public DeContrattoInfo fromDTO(final DeContrattoInfoDTO dto) {
		if (dto == null) {
			return null;
		}
		final DeContrattoInfo entity =super.fromDTO(dto);
		entity.setCodContratto(dto.getId());
		entity.setTitoloInfo(dto.getTitoloInfo());
		entity.setDescrizioneInfo(dto.getDescrizione());		
		return entity;
	}

	@Override
	public String getFieldName() {
		return "descrizioneInfo";
	}
}

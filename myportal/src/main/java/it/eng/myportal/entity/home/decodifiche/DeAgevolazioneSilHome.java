package it.eng.myportal.entity.home.decodifiche;

import it.eng.myportal.dtos.DeAgevolazioneDTO;
import it.eng.myportal.entity.decodifiche.sil.DeAgevolazioneSil;

import javax.ejb.Stateless;

@Stateless
public class DeAgevolazioneSilHome extends AbstractSuggestibleHome<DeAgevolazioneSil, DeAgevolazioneDTO> {

	@Override
	public DeAgevolazioneSil findById(String id) {
		return findById(DeAgevolazioneSil.class, id);
	}

	@Override
	public DeAgevolazioneDTO toDTO(final DeAgevolazioneSil entity) {
		if (entity == null)
			return null;
		DeAgevolazioneDTO dto = new DeAgevolazioneDTO();
		dto.setDtInizioVal(entity.getDtInizioVal());
		dto.setDtFineVal(entity.getDtFineVal());
		dto.setId(entity.getCodAgevolazioneSil());
		dto.setDescrizione(entity.getDescrizione());
		return dto;
	}

	@Override
	public DeAgevolazioneSil fromDTO(DeAgevolazioneDTO dto) {
		if (dto == null)
			return null;
		final DeAgevolazioneSil entity = super.fromDTO(dto);
		entity.setCodAgevolazioneSil(dto.getId());
		entity.setDescrizione(dto.getDescrizione());
		return entity;
	}
}

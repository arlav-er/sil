package it.eng.myportal.entity.home.decodifiche;

import it.eng.myportal.dtos.DeStatoFbChecklistDTO;
import it.eng.myportal.entity.decodifiche.DeStatoFbChecklist;

import javax.ejb.Stateless;

@Stateless
public class DeStatoFbChecklistHome extends AbstractSuggestibleHome<DeStatoFbChecklist, DeStatoFbChecklistDTO> {

	@Override
	public DeStatoFbChecklist findById(String id) {
		return findById(DeStatoFbChecklist.class, id);
	}

	public DeStatoFbChecklistDTO toDTO(DeStatoFbChecklist entity) {
		DeStatoFbChecklistDTO dto = super.toDTO(entity);
		dto.setId(entity.getCodStatoFbChecklist());
		dto.setDescrizione(entity.getDescrizione());
		return dto;
	}

	public DeStatoFbChecklist fromDTO(DeStatoFbChecklistDTO dto) {
		DeStatoFbChecklist entity = super.fromDTO(dto);
		entity.setCodStatoFbChecklist(dto.getId());
		entity.setDescrizione(dto.getDescrizione());
		return entity;
	}
}

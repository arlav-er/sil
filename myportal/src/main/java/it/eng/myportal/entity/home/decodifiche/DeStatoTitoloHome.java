package it.eng.myportal.entity.home.decodifiche;

import it.eng.myportal.dtos.DeStatoTitoloDTO;
import it.eng.myportal.entity.decodifiche.DeStatoTitolo;

import javax.ejb.Stateless;

@Stateless
public class DeStatoTitoloHome extends AbstractSuggestibleHome<DeStatoTitolo, DeStatoTitoloDTO> {

	@Override
	public DeStatoTitolo findById(String id) {
		return findById(DeStatoTitolo.class, id);
	}

	@Override
	public DeStatoTitoloDTO toDTO(DeStatoTitolo entity) {
		if (entity == null) {
			return null;
		}

		DeStatoTitoloDTO dto = super.toDTO(entity);
		dto.setId(entity.getCodStatoTitolo());
		dto.setDescrizione(entity.getDescrizione());
		return dto;
	}

	@Override
	public DeStatoTitolo fromDTO(DeStatoTitoloDTO dto) {
		if (dto == null) {
			return null;
		}

		DeStatoTitolo entity = super.fromDTO(dto);
		entity.setCodStatoTitolo(dto.getId());
		entity.setDescrizione(dto.getDescrizione());
		return entity;
	}

}

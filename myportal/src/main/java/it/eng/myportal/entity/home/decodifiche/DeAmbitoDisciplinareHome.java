package it.eng.myportal.entity.home.decodifiche;

import it.eng.myportal.dtos.DeAmbitoDisciplinareDTO;
import it.eng.myportal.entity.decodifiche.DeAmbitoDisciplinare;

import javax.ejb.Stateless;

@Stateless
public class DeAmbitoDisciplinareHome extends AbstractSuggestibleHome<DeAmbitoDisciplinare, DeAmbitoDisciplinareDTO> {

	@Override
	public DeAmbitoDisciplinare findById(String id) {
		return findById(DeAmbitoDisciplinare.class, id);
	}

	@Override
	public DeAmbitoDisciplinareDTO toDTO(DeAmbitoDisciplinare entity) {
		if (entity == null) {
			return null;
		}

		DeAmbitoDisciplinareDTO dto = super.toDTO(entity);
		dto.setId(entity.getCodAmbitoDisciplinare());
		dto.setDescrizione(entity.getDescrizione());
		return dto;
	}

	@Override
	public DeAmbitoDisciplinare fromDTO(DeAmbitoDisciplinareDTO dto) {
		if (dto == null) {
			return null;
		}

		DeAmbitoDisciplinare entity = super.fromDTO(dto);
		entity.setCodAmbitoDisciplinare(dto.getId());
		entity.setDescrizione(dto.getDescrizione());
		return entity;
	}
}

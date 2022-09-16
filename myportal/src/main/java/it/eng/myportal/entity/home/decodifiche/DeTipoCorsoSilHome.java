package it.eng.myportal.entity.home.decodifiche;

import it.eng.myportal.dtos.DeTipoCorsoSilDTO;
import it.eng.myportal.entity.decodifiche.DeTipoCorsoSil;

import javax.ejb.Stateless;

@Stateless
public class DeTipoCorsoSilHome extends AbstractSuggestibleHome<DeTipoCorsoSil, DeTipoCorsoSilDTO> {

	@Override
	public DeTipoCorsoSil findById(String id) {
		return findById(DeTipoCorsoSil.class, id);
	}

	@Override
	public DeTipoCorsoSilDTO toDTO(DeTipoCorsoSil entity) {
		if (entity == null) {
			return null;
		}

		DeTipoCorsoSilDTO dto = super.toDTO(entity);
		dto.setId(entity.getCodTipoCorsoSil());
		dto.setDescrizione(entity.getDescrizione());
		dto.setCodTipoCorsoMin(entity.getCodTipoCorsoMin());
		return dto;
	}

	@Override
	public DeTipoCorsoSil fromDTO(DeTipoCorsoSilDTO dto) {
		if (dto == null) {
			return null;
		}

		DeTipoCorsoSil entity = super.fromDTO(dto);
		entity.setCodTipoCorsoSil(dto.getId());
		entity.setDescrizione(dto.getDescrizione());
		entity.setCodTipoCorsoMin(dto.getCodTipoCorsoMin());
		return entity;
	}
}

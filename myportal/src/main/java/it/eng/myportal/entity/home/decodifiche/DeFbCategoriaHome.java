package it.eng.myportal.entity.home.decodifiche;

import it.eng.myportal.dtos.DeFbCategoriaDTO;
import it.eng.myportal.entity.decodifiche.DeFbCategoria;

import javax.ejb.Stateless;

@Stateless
public class DeFbCategoriaHome extends AbstractSuggestibleHome<DeFbCategoria, DeFbCategoriaDTO> {

	@Override
	public DeFbCategoria findById(String id) {
		return findById(DeFbCategoria.class, id);
	}

	public DeFbCategoriaDTO toDTO(DeFbCategoria entity) {
		DeFbCategoriaDTO dto = super.toDTO(entity);
		dto.setId(entity.getCodFbCategoria());
		dto.setDescrizione(entity.getDescrizione());
		return dto;
	}

	public DeFbCategoria fromDTO(DeFbCategoriaDTO dto) {
		DeFbCategoria entity = super.fromDTO(dto);
		entity.setCodFbCategoria(dto.getId());
		entity.setDescrizione(dto.getDescrizione());
		return entity;
	}

}

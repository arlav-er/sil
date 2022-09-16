package it.eng.myportal.entity.home.decodifiche;

import it.eng.myportal.dtos.DeFbTipoTirocinioDTO;
import it.eng.myportal.entity.decodifiche.DeFbTipoTirocinio;

import javax.ejb.Stateless;

@Stateless
public class DeFbTipoTirocinioHome extends AbstractSuggestibleHome<DeFbTipoTirocinio, DeFbTipoTirocinioDTO> {

	@Override
	public DeFbTipoTirocinio findById(String id) {
		return findById(DeFbTipoTirocinio.class, id);
	}

	public DeFbTipoTirocinioDTO toDTO(DeFbTipoTirocinio entity) {
		DeFbTipoTirocinioDTO dto = super.toDTO(entity);
		dto.setId(entity.getCodFbTipoTirocinio());
		dto.setDescrizione(entity.getDescrizione());
		return dto;
	}

	public DeFbTipoTirocinio fromDTO(DeFbTipoTirocinioDTO dto) {
		DeFbTipoTirocinio entity = super.fromDTO(dto);
		entity.setCodFbTipoTirocinio(dto.getId());
		entity.setDescrizione(dto.getDescrizione());
		return entity;
	}

}

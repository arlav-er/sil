package it.eng.myportal.entity.home.decodifiche;

import javax.ejb.Stateless;

import it.eng.myportal.dtos.DeNaturaGiuridicaDTO;
import it.eng.myportal.entity.decodifiche.DeNaturaGiuridica;

@Stateless
public class DeNaturaGiuridicaHome extends AbstractSuggestibleHome<DeNaturaGiuridica, DeNaturaGiuridicaDTO> {

	@Override
	public DeNaturaGiuridica findById(String id) {

		return findById(DeNaturaGiuridica.class, id);
	}

	public DeNaturaGiuridicaDTO toDTO(DeNaturaGiuridica entity) {
		DeNaturaGiuridicaDTO dto = super.toDTO(entity);
		dto.setId(entity.getCodNaturaGiuridica());
		dto.setDescrizione(entity.getDescrizione());
		return dto;
	}

	public DeNaturaGiuridica fromDTO(DeNaturaGiuridicaDTO dto) {
		DeNaturaGiuridica entity = super.fromDTO(dto);
		entity.setCodNaturaGiuridica(dto.getId());
		entity.setDescrizione(dto.getDescrizione());
		return entity;
	}
}

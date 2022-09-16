package it.eng.myportal.entity.home.decodifiche;

import java.util.List;

import javax.ejb.Stateless;

import it.eng.myportal.dtos.DeGradoLinDTO;
import it.eng.myportal.entity.decodifiche.sil.DeGradoLinSil;

@Stateless
public class DeGradoLinSilHome extends AbstractSuggestibleHome<DeGradoLinSil, DeGradoLinDTO> {

	@Override
	public DeGradoLinSil findById(String id) {
		return findById(DeGradoLinSil.class, id);
	}

	@Override
	public DeGradoLinDTO toDTO(DeGradoLinSil entity) {
		if (entity == null)
			return null;
		DeGradoLinDTO dto = new DeGradoLinDTO();
		dto.setDtInizioVal(entity.getDtInizioVal());
		dto.setDtFineVal(entity.getDtFineVal());
		dto.setId(entity.getCodGradoLinSil());
		dto.setDescrizione(entity.getDescrizione());
		return dto;
	}

	@Override
	public DeGradoLinSil fromDTO(DeGradoLinDTO dto) {
		if (dto == null)
			return null;
		final DeGradoLinSil entity = super.fromDTO(dto);
		entity.setCodGradoLinSil(dto.getId());
		entity.setDescrizione(dto.getDescrizione());
		return entity;
	}

	public List<DeGradoLinSil> findByCodIdoNotNull(){
		List<DeGradoLinSil> deGradoLinSil = entityManager
				.createNamedQuery("deGradoLinSilValideByCodIdoNotNull", DeGradoLinSil.class)
				.getResultList();
		return deGradoLinSil;
	}
}

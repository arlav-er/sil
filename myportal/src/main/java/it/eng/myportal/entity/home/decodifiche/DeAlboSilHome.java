package it.eng.myportal.entity.home.decodifiche;

import java.util.List;

import javax.ejb.Stateless;

import it.eng.myportal.dtos.DeAlboDTO;
import it.eng.myportal.entity.decodifiche.sil.DeAlboSil;

@Stateless
public class DeAlboSilHome extends AbstractSuggestibleHome<DeAlboSil, DeAlboDTO> {

	@Override
	public DeAlboSil findById(String id) {
		return findById(DeAlboSil.class, id);
	}

	@Override
	public DeAlboDTO toDTO(final DeAlboSil entity) {
		if (entity == null)
			return null;
		final DeAlboDTO dto = new DeAlboDTO();
		dto.setDtInizioVal(entity.getDtInizioVal());
		dto.setDtFineVal(entity.getDtFineVal());
		dto.setId(entity.getCodAlboSil());
		dto.setDescrizione(entity.getDescrizione());
		return dto;
	}

	@Override
	public DeAlboSil fromDTO(final DeAlboDTO dto) {
		if (dto == null)
			return null;
		DeAlboSil entity = super.fromDTO(dto);
		entity.setCodAlboSil(dto.getId());
		entity.setDescrizione(dto.getDescrizione());

		return entity;
	}
 
	
	public List<DeAlboSil> findBySuggestionNoDto(String word) {
		List<DeAlboSil> deList = entityManager
				.createNamedQuery("DeAlboSilFindSuggestionValide", DeAlboSil.class)
				.setHint("org.hibernate.cacheable", true).setParameter("query", "%"+word+"%").getResultList();
		
		return deList;
	}
}

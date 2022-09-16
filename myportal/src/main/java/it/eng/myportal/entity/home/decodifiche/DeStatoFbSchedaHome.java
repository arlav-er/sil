package it.eng.myportal.entity.home.decodifiche;

import it.eng.myportal.dtos.DeStatoFbSchedaDTO;
import it.eng.myportal.entity.decodifiche.DeStatoFbScheda;

import javax.ejb.Stateless;

@Stateless
public class DeStatoFbSchedaHome extends AbstractSuggestibleHome<DeStatoFbScheda, DeStatoFbSchedaDTO> {

	@Override
	public DeStatoFbScheda findById(String id) {
		return findById(DeStatoFbScheda.class, id);
	}

	public DeStatoFbSchedaDTO toDTO(DeStatoFbScheda entity) {
		DeStatoFbSchedaDTO dto = super.toDTO(entity);
		dto.setId(entity.getCodStatoFbScheda());
		dto.setDescrizione(entity.getDescrizione());
		return dto;
	}

	public DeStatoFbScheda fromDTO(DeStatoFbSchedaDTO dto) {
		DeStatoFbScheda entity = super.fromDTO(dto);
		entity.setCodStatoFbScheda(dto.getId());
		entity.setDescrizione(dto.getDescrizione());
		return entity;
	}

}

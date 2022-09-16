package it.eng.myportal.entity.home.decodifiche;

import it.eng.myportal.dtos.DePresenzaItaliaMinDTO;
import it.eng.myportal.entity.decodifiche.min.DePresenzaItaliaMin;

import javax.ejb.Stateless;

@Stateless
public class DePresenzaItaliaMinHome extends AbstractDecodeHome<DePresenzaItaliaMin, DePresenzaItaliaMinDTO> {

	@Override
	public DePresenzaItaliaMin findById(String id) {
		return findById(DePresenzaItaliaMin.class, id);
	}

	@Override
	public DePresenzaItaliaMinDTO toDTO(DePresenzaItaliaMin entity) {
		if (entity == null)
			return null;
		DePresenzaItaliaMinDTO dto = super.toDTO(entity);
		dto.setId(entity.getCodPresenzaItaliaMin());
		dto.setDescrizione(entity.getDescrizione());
		return dto;
	}

	@Override
	public DePresenzaItaliaMin fromDTO(DePresenzaItaliaMinDTO dto) {
		if (dto == null)
			return null;
		DePresenzaItaliaMin entity = super.fromDTO(dto);
		entity.setCodPresenzaItaliaMin(dto.getId());
		entity.setDescrizione(dto.getDescrizione());
		return entity;
	}
}

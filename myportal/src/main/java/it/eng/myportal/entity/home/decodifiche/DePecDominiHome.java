package it.eng.myportal.entity.home.decodifiche;

import it.eng.myportal.dtos.DePecDominiDTO;
import it.eng.myportal.entity.decodifiche.DePecDomini;

import javax.ejb.Stateless;

@Stateless
public class DePecDominiHome extends AbstractSuggestibleHome<DePecDomini, DePecDominiDTO> {

	@Override
	public DePecDomini findById(String id) {
		return findById(DePecDomini.class, id);
	}

	@Override
	public DePecDominiDTO toDTO(DePecDomini entity) {
		if (entity == null)
			return null;
		DePecDominiDTO ret = super.toDTO(entity);
		ret.setId(entity.getCodPecDomini());
		ret.setDescrizione(entity.getDescrizione());
		return ret;
	}

	@Override
	public DePecDomini fromDTO(DePecDominiDTO dto) {
		if (dto == null)
			return null;
		DePecDomini ret = super.fromDTO(dto);
		ret.setCodPecDomini(dto.getId());
		ret.setDescrizione(dto.getDescrizione());
		return ret;
	}

}

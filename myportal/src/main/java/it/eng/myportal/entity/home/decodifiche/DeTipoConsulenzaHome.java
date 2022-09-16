package it.eng.myportal.entity.home.decodifiche;

// Generated 1-set-2011 12.32.46 by Hibernate Tools 3.4.0.CR1

import it.eng.myportal.dtos.DeTipoConsulenzaDTO;
import it.eng.myportal.entity.decodifiche.DeTipoConsulenza;

import javax.ejb.Stateless;

/**
 * Home object for domain model class DeTipoConsulenza.
 * 
 * @see it.eng.myportal.entity.decodifiche.DeTipoConsulenza
 * @author Rodi A.
 */
@Stateless
public class DeTipoConsulenzaHome extends AbstractSuggestibleHome<DeTipoConsulenza, DeTipoConsulenzaDTO> {

	public DeTipoConsulenza findById(String id) {
		return findById(DeTipoConsulenza.class, id);
	}

	@Override
	public DeTipoConsulenzaDTO toDTO(DeTipoConsulenza entity) {
		if (entity == null)
			return null;
		DeTipoConsulenzaDTO ret = super.toDTO(entity);
		ret.setId(entity.getCodTipoConsulente());
		ret.setDescrizione(entity.getDescrizione());
		return ret;
	}

	@Override
	public DeTipoConsulenza fromDTO(DeTipoConsulenzaDTO dto) {
		if (dto == null)
			return null;
		DeTipoConsulenza ret = super.fromDTO(dto);
		ret.setCodTipoConsulente(dto.getId());
		ret.setDescrizione(dto.getDescrizione());
		return ret;
	}

}

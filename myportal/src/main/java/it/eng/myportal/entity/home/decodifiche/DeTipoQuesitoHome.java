package it.eng.myportal.entity.home.decodifiche;

// Generated 1-set-2011 12.32.46 by Hibernate Tools 3.4.0.CR1

import it.eng.myportal.dtos.DeTipoQuesitoDTO;
import it.eng.myportal.entity.decodifiche.DeTipoQuesito;

import javax.ejb.Stateless;

/**
 * Home object for domain model class DeTipoQuesito.
 * 
 * @see it.eng.myportal.entity.decodifiche.DeTipoQuesito
 * @author Turro
 */
@Stateless
public class DeTipoQuesitoHome extends AbstractSuggestibleHome<DeTipoQuesito, DeTipoQuesitoDTO> {

	
	
	public DeTipoQuesito findById(String id) {
		return findById(DeTipoQuesito.class, id);
	}

		
	public DeTipoQuesitoDTO findDTOById(String codStatoPratica) {
		return toDTO(findById(codStatoPratica));
	}
	
	
	@Override
	public DeTipoQuesitoDTO toDTO(DeTipoQuesito entity) {
		if (entity == null)
			return null;
		DeTipoQuesitoDTO dto = super.toDTO(entity);

		dto.setId(entity.getCodTipoQuesito());
		dto.setDescrizione(entity.getDescrizione());
		
		return dto;
	}

	@Override
	public DeTipoQuesito fromDTO(DeTipoQuesitoDTO dto) {
		if (dto == null)
			return null;
		DeTipoQuesito entity = super.fromDTO(dto);

		entity.setCodTipoQuesito(dto.getId());
		entity.setDescrizione(dto.getDescrizione());
		
		return entity;
	}

	

}

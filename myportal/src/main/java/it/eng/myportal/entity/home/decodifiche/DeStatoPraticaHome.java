package it.eng.myportal.entity.home.decodifiche;

// Generated 1-set-2011 12.32.46 by Hibernate Tools 3.4.0.CR1

import it.eng.myportal.dtos.DeStatoPraticaDTO;
import it.eng.myportal.entity.decodifiche.DeStatoPratica;

import javax.ejb.Stateless;

/**
 * Home object for domain model class DeStatoPratica.
 * 
 * @see it.eng.myportal.entity.decodifiche.DeStatoPratica
 * @author Turro
 */
@Stateless
public class DeStatoPraticaHome extends AbstractSuggestibleHome<DeStatoPratica, DeStatoPraticaDTO> {

	// private static final String QUERY_SEL_BY_DESCR = " select c from DeStatoPratica c where upper(c.descrizione) like upper(:descrizione)";	

	
	public DeStatoPratica findById(String id) {
		return findById(DeStatoPratica.class, id);
	}

		
	public DeStatoPraticaDTO findDTOById(String codStatoPratica) {
		return toDTO(findById(codStatoPratica));
	}
	
	
	@Override
	public DeStatoPraticaDTO toDTO(DeStatoPratica entity) {
		if (entity == null)
			return null;
		DeStatoPraticaDTO dto = super.toDTO(entity);

		dto.setId(entity.getCodStatoPratica());
		dto.setDescrizione(entity.getDescrizione());
		
		return dto;
	}

	@Override
	public DeStatoPratica fromDTO(DeStatoPraticaDTO dto) {
		if (dto == null)
			return null;
		DeStatoPratica entity = super.fromDTO(dto);

		entity.setCodStatoPratica(dto.getId());
		entity.setDescrizione(dto.getDescrizione());
		
		return entity;
	}

	

}

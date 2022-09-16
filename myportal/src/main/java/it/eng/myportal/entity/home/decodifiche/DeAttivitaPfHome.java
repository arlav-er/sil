package it.eng.myportal.entity.home.decodifiche;

// Generated 1-set-2011 12.32.46 by Hibernate Tools 3.4.0.CR1

import it.eng.myportal.dtos.DeAttivitaPfDTO;
import it.eng.myportal.entity.decodifiche.DeAttivitaPf;

import javax.ejb.Stateless;

/**
 * Home object for domain model class DeOrario.
 * 
 * @see it.eng.myportal.entity.decodifiche.DeOrario
 * @author Rodi A.
 */
@Stateless
public class DeAttivitaPfHome extends AbstractDecodeHome<DeAttivitaPf, DeAttivitaPfDTO> {


	public DeAttivitaPf findById(String id) {
		return findById(DeAttivitaPf.class, id);
	}

	@Override
	public DeAttivitaPfDTO toDTO(final DeAttivitaPf entity) {
		if (entity == null)
			return null;
		final DeAttivitaPfDTO dto = super.toDTO(entity);
		dto.setId(entity.getCodAttivitaPf());
		dto.setDescrizione(entity.getDescrizione());

		return dto;
	}

	@Override
	public DeAttivitaPf fromDTO(final DeAttivitaPfDTO dto) {
		if (dto == null)
			return null;
		DeAttivitaPf entity = super.fromDTO(dto);
		entity.setCodAttivitaPf(dto.getId());
		entity.setDescrizione(dto.getDescrizione());

		return entity;
	}
}

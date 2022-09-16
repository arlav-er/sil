package it.eng.myportal.entity.home.decodifiche;

// Generated 1-set-2011 12.32.46 by Hibernate Tools 3.4.0.CR1

import it.eng.myportal.dtos.DeGradoLinDTO;
import it.eng.myportal.entity.decodifiche.DeGradoLin;

import javax.ejb.Stateless;

/**
 * Home object for domain model class DeGradoLin.
 * 
 * @see it.eng.myportal.entity.decodifiche.DeGradoLin
 * @author Rodi A.
 */
@Stateless
public class DeGradoLinHome extends AbstractSuggestibleHome<DeGradoLin, DeGradoLinDTO> {

	private static final String SELECT_ITEMS_QUERY = "SELECT new javax.faces.model.SelectItem(t.codGrado,t.descrizione)"
			+ " from DeGradoLin t";

	public DeGradoLin findById(final String id) {
		return findById(DeGradoLin.class, id);
	}

	@Override
	public DeGradoLinDTO toDTO(DeGradoLin entity) {
		if (entity == null)
			return null;
		DeGradoLinDTO dto = super.toDTO(entity);
		dto.setId(entity.getCodGrado());
		dto.setDescrizione(entity.getDescrizione());
		return dto;
	}

	@Override
	public DeGradoLin fromDTO(DeGradoLinDTO dto) {
		if (dto == null)
			return null;
		final DeGradoLin entity = super.fromDTO(dto);
		entity.setCodGrado(dto.getId());
		entity.setDescrizione(dto.getDescrizione());
		return entity;
	}

	public String getSelectItemQuery() {
		return SELECT_ITEMS_QUERY;
	}

}

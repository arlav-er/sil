package it.eng.myportal.entity.home.decodifiche;

// Generated 1-set-2011 12.32.46 by Hibernate Tools 3.4.0.CR1

import it.eng.myportal.dtos.DeModalitaLavoroDTO;
import it.eng.myportal.entity.decodifiche.DeModalitaLavoro;

import javax.ejb.Stateless;

/**
 * Home object for domain model class DeModalitaLavoro.
 * 
 * @see it.eng.myportal.entity.decodifiche.DeModalitaLavoro
 * @author Turrini.
 */
@Stateless
public class DeModalitaLavoroHome extends AbstractSuggestibleHome<DeModalitaLavoro, DeModalitaLavoroDTO> {

	private static final String SELECT_ITEMS_QUERY = "SELECT new javax.faces.model.SelectItem(t.codModalitaLavoro,t.descrizione)"
			+ " from DeModalitaLavoro t";

	public DeModalitaLavoro findById(final String id) {
		return findById(DeModalitaLavoro.class, id);
	}

	@Override
	public DeModalitaLavoroDTO toDTO(DeModalitaLavoro entity) {
		if (entity == null)
			return null;
		DeModalitaLavoroDTO dto = super.toDTO(entity);
		dto.setId(entity.getCodModalitaLavoro());
		dto.setDescrizione(entity.getDescrizione());
		return dto;
	}

	@Override
	public DeModalitaLavoro fromDTO(DeModalitaLavoroDTO dto) {
		if (dto == null)
			return null;
		final DeModalitaLavoro entity = super.fromDTO(dto);
		entity.setCodModalitaLavoro(dto.getId());
		entity.setDescrizione(dto.getDescrizione());
		return entity;
	}

	public String getSelectItemQuery() {
		return SELECT_ITEMS_QUERY;
	}
	
	

}

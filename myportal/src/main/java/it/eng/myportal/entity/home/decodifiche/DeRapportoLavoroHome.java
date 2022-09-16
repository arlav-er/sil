package it.eng.myportal.entity.home.decodifiche;

// Generated 1-set-2011 12.32.46 by Hibernate Tools 3.4.0.CR1

import it.eng.myportal.dtos.DeRapportoLavoroDTO;
import it.eng.myportal.entity.decodifiche.DeRapportoLavoro;

import javax.ejb.Stateless;

/**
 * Home object for domain model class DeRapportoLavoro.
 * 
 * @see it.eng.myportal.entity.decodifiche.DeRapportoLavoro
 * @author Turrini.
 */
@Stateless
public class DeRapportoLavoroHome extends AbstractSuggestibleHome<DeRapportoLavoro, DeRapportoLavoroDTO> {

	private static final String SELECT_ITEMS_QUERY = "SELECT new javax.faces.model.SelectItem(t.codRapportoLavoro,t.descrizione)"
			+ " from DeRapportoLavoro t";

	public DeRapportoLavoro findById(final String id) {
		return findById(DeRapportoLavoro.class, id);
	}

	@Override
	public DeRapportoLavoroDTO toDTO(DeRapportoLavoro entity) {
		if (entity == null)
			return null;
		DeRapportoLavoroDTO dto = super.toDTO(entity);
		dto.setId(entity.getCodRapportoLavoro());
		dto.setDescrizione(entity.getDescrizione());
		return dto;
	}

	@Override
	public DeRapportoLavoro fromDTO(DeRapportoLavoroDTO dto) {
		if (dto == null)
			return null;
		final DeRapportoLavoro entity = super.fromDTO(dto);
		entity.setCodRapportoLavoro(dto.getId());
		entity.setDescrizione(dto.getDescrizione());
		return entity;
	}

	public String getSelectItemQuery() {
		return SELECT_ITEMS_QUERY;
	}

}

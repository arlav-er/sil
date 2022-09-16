package it.eng.myportal.entity.home.decodifiche;

// Generated 1-set-2011 12.32.46 by Hibernate Tools 3.4.0.CR1

import it.eng.myportal.dtos.DeMotivoChiusuraDTO;
import it.eng.myportal.entity.decodifiche.DeMotivoChiusura;

import javax.ejb.Stateless;

/**
 * Home object for domain model class DeMotivoChiusura.
 * 
 * @see it.eng.myportal.entity.decodifiche.DeMotivoChiusura
 * @author Turrini.
 */
@Stateless
public class DeMotivoChiusuraHome extends AbstractSuggestibleHome<DeMotivoChiusura, DeMotivoChiusuraDTO> {

	private static final String SELECT_ITEMS_QUERY = "SELECT new javax.faces.model.SelectItem(t.codMotivoChiusura,t.descrizione)"
			+ " from DeMotivoChiusura t";

	public DeMotivoChiusura findById(final String id) {
		return findById(DeMotivoChiusura.class, id);
	}

	@Override
	public DeMotivoChiusuraDTO toDTO(DeMotivoChiusura entity) {
		if (entity == null)
			return null;
		DeMotivoChiusuraDTO dto = super.toDTO(entity);
		dto.setId(entity.getCodMotivoChiusura());
		dto.setDescrizione(entity.getDescrizione());
		return dto;
	}

	@Override
	public DeMotivoChiusura fromDTO(DeMotivoChiusuraDTO dto) {
		if (dto == null)
			return null;
		final DeMotivoChiusura entity = super.fromDTO(dto);
		entity.setCodMotivoChiusura(dto.getId());
		entity.setDescrizione(dto.getDescrizione());
		return entity;
	}

	public String getSelectItemQuery() {
		return SELECT_ITEMS_QUERY;
	}
	
	

}

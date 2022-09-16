package it.eng.myportal.entity.home.decodifiche;

import it.eng.myportal.dtos.DeAutorizzazioneSareDTO;
import it.eng.myportal.entity.decodifiche.DeAutorizzazioneSare;

import javax.ejb.Stateless;

/**
 * 
 * @author Girotti S.
 */
@Stateless
public class DeAutorizzazioneSareHome extends AbstractDecodeHome<DeAutorizzazioneSare, DeAutorizzazioneSareDTO> {

	//private static final String SELECT_ITEM_QUERY = "select new javax.faces.model.SelectItem(c.codAutorizzazioneSare,c.descrizione)"
	//		+ " from DeAutorizzazioneSare c ";

	public DeAutorizzazioneSare findById(final String id) {
		return findById(DeAutorizzazioneSare.class, id);
	}

	@Override
	public DeAutorizzazioneSareDTO toDTO(final DeAutorizzazioneSare entity) {
		if (entity == null)
			return null;
		final DeAutorizzazioneSareDTO dto = super.toDTO(entity);
		dto.setId(entity.getCodAutorizzazioneSare());
		dto.setDescrizione(entity.getDescrizione());

		return dto;
	}

	@Override
	public DeAutorizzazioneSare fromDTO(final DeAutorizzazioneSareDTO dto) {
		if (dto == null)
			return null;
		DeAutorizzazioneSare entity = super.fromDTO(dto);
		entity.setCodAutorizzazioneSare(dto.getId());
		entity.setDescrizione(dto.getDescrizione());

		return entity;
	}

	public String getExtraQuery() {
		return " ORDER BY e.descrizione ";
	}

}

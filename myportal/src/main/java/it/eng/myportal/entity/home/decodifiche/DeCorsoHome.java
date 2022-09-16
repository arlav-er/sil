package it.eng.myportal.entity.home.decodifiche;

import it.eng.myportal.dtos.DeCorsoDTO;
import it.eng.myportal.entity.decodifiche.DeCorso;

import javax.ejb.Stateless;

/**
 * Home object for domain model class DeCorso.
 * 
 * @see it.eng.myportal.entity.decodifiche.DeCorso
 * @author Rodi A.
 */
@Stateless
public class DeCorsoHome extends AbstractSuggestibleHome<DeCorso, DeCorsoDTO> {

	public DeCorso findById(String id) {
		return findById(DeCorso.class, id);
	}

	@Override
	public DeCorso fromDTO(DeCorsoDTO dto) {
		if (dto == null)
			return null;
		DeCorso entity = super.fromDTO(dto);
		entity.setCodCorso(dto.getId());
		entity.setDescrizione(dto.getDescrizione());

		return entity;
	}

	@Override
	public DeCorsoDTO toDTO(DeCorso entity) {
		if (entity == null)
			return null;
		DeCorsoDTO dto = super.toDTO(entity);
		dto.setId(entity.getCodCorso());
		dto.setDescrizione(entity.getDescrizione());
		return dto;
	}

	@Override
	public String getExtraQuery() {
		return " ORDER BY descrizione ";
	}
}

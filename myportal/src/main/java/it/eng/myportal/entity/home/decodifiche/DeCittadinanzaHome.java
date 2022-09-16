package it.eng.myportal.entity.home.decodifiche;

import it.eng.myportal.dtos.DeCittadinanzaDTO;
import it.eng.myportal.entity.decodifiche.DeCittadinanza;

import javax.ejb.Stateless;

/**
 * 
 * @author Turrini
 */
@Stateless
public class DeCittadinanzaHome extends
		AbstractSuggestibleHome<DeCittadinanza, DeCittadinanzaDTO> {

	public DeCittadinanza findById(final String id) {
		return findById(DeCittadinanza.class, id);
	}

	@Override
	public DeCittadinanzaDTO toDTO(final DeCittadinanza entity) {
		if (entity == null)
			return null;
		final DeCittadinanzaDTO dto = super.toDTO(entity);

		dto.setDescrizione(entity.getDescrizione());
		dto.setId(entity.getCodCittadinanza());
		dto.setDtInizioVal(entity.getDtInizioVal());
		dto.setDtFineVal(entity.getDtFineVal());
		dto.setFlgCee(entity.getFlgCee());

		return dto;
	}

	@Override
	public DeCittadinanza fromDTO(final DeCittadinanzaDTO dto) {
		if (dto == null)
			return null;
		final DeCittadinanza entity = super.fromDTO(dto);
		
		entity.setDescrizione(dto.getDescrizione());
		entity.setFlgCee(dto.getFlgCee());


		return entity;
	}

	@Override
	public String getExtraQuery() {
		return " ORDER BY descrizione ";
	}

	

}

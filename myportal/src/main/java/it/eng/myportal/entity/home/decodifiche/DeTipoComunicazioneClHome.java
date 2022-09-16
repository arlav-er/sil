package it.eng.myportal.entity.home.decodifiche;

import it.eng.myportal.dtos.DeTipoComunicazioneClDTO;
import it.eng.myportal.entity.decodifiche.DeTipoComunicazioneCl;

import javax.ejb.Stateless;

/**
 * Home object for domain model class DeTipoComunicazioneCl.
 * 
 * @see it.eng.myportal.entity.decodifiche.DeTipoComunicazioneCl
 * @author Turrini
 */
@Stateless
public class DeTipoComunicazioneClHome extends AbstractSuggestibleHome<DeTipoComunicazioneCl, DeTipoComunicazioneClDTO> {

	public DeTipoComunicazioneCl findById(final String id) {
		return findById(DeTipoComunicazioneCl.class, id);
	}

	@Override
	public DeTipoComunicazioneClDTO toDTO(final DeTipoComunicazioneCl entity) {
		if (entity == null)
			return null;
		DeTipoComunicazioneClDTO dto = super.toDTO(entity);
		dto.setId(entity.getCodTipoComunicazioneCl());
		dto.setDescrizione(entity.getDescrizione());
		return dto;
	}

	@Override
	public DeTipoComunicazioneCl fromDTO(DeTipoComunicazioneClDTO dto) {
		if (dto == null)
			return null;
		final DeTipoComunicazioneCl entity = super.fromDTO(dto);
		entity.setCodTipoComunicazioneCl(dto.getId());
		entity.setDescrizione(dto.getDescrizione());
		return entity;
	}

}

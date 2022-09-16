package it.eng.myportal.entity.home.decodifiche;

// Generated 1-set-2011 12.32.46 by Hibernate Tools 3.4.0.CR1

import it.eng.myportal.dtos.DeTipoAppuntamentoDTO;
import it.eng.myportal.entity.decodifiche.DeTipoAppuntamento;

import javax.ejb.Stateless;

/**
 * Classe Home per la tabella de_tipo_appuntamento
 * 
 * @see it.eng.myportal.entity.decodifiche.DeTipoAppuntamento
 * @author Enrico
 */
@Stateless
public class DeTipoAppuntamentoHome extends AbstractDecodeHome<DeTipoAppuntamento, DeTipoAppuntamentoDTO> {

	public DeTipoAppuntamento findById(String id) {
		return findById(DeTipoAppuntamento.class, id);
	}

	@Override
	public DeTipoAppuntamentoDTO toDTO(final DeTipoAppuntamento entity) {
		if (entity == null) {
			return null;
		}
		DeTipoAppuntamentoDTO dto = super.toDTO(entity);
		dto.setId(entity.getCodTipoAppuntamento());
		dto.setDescrizione(entity.getDescrizione());
		return dto;
	}
}

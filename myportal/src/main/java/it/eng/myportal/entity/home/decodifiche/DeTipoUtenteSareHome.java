package it.eng.myportal.entity.home.decodifiche;

// Generated 1-set-2011 12.32.46 by Hibernate Tools 3.4.0.CR1

import it.eng.myportal.dtos.DeTipoUtenteSareDTO;
import it.eng.myportal.entity.decodifiche.DeTipoUtenteSare;

import javax.ejb.Stateless;

/**
 * Classe Home per la tabella de_tipo_utente_sare
 * 
 * @see it.eng.myportal.entity.decodifiche.DeTipoUtenteSare
 * @author Rodi A.
 */
@Stateless
public class DeTipoUtenteSareHome extends AbstractDecodeHome<DeTipoUtenteSare, DeTipoUtenteSareDTO> {

	public DeTipoUtenteSare findById(String id) {
		return findById(DeTipoUtenteSare.class, id);
	}

	@Override
	public DeTipoUtenteSareDTO toDTO(final DeTipoUtenteSare entity) {
		if (entity == null)
			return null;
		DeTipoUtenteSareDTO dto = super.toDTO(entity);
		dto.setId(entity.getCodTipoUtenteSare());
		dto.setDescrizione(entity.getDescrizione());
		return dto;
	}
}

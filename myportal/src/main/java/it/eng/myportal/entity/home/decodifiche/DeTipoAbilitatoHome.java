package it.eng.myportal.entity.home.decodifiche;

// Generated 1-set-2011 12.32.46 by Hibernate Tools 3.4.0.CR1

import it.eng.myportal.dtos.DeTipoAbilitatoDTO;
import it.eng.myportal.entity.decodifiche.DeTipoAbilitato;

import javax.ejb.Stateless;

/**
 * Classe Home per la tabella de_tipo_abilitato
 * 
 * @see it.eng.myportal.entity.decodifiche.DeTipoAbilitato
 * @author Rodi A.
 */
@Stateless
public class DeTipoAbilitatoHome extends AbstractDecodeHome<DeTipoAbilitato, DeTipoAbilitatoDTO> {

	public DeTipoAbilitato findById(String id) {
		return findById(DeTipoAbilitato.class, id);
	}

	@Override
	public DeTipoAbilitatoDTO toDTO(final DeTipoAbilitato entity) {
		if (entity == null)
			return null;
		DeTipoAbilitatoDTO dto = super.toDTO(entity);
		dto.setId(entity.getCodTipoAbilitato());
		dto.setDescrizione(entity.getDescrizione());
		return dto;
	}
}

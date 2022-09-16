package it.eng.myportal.entity.home.decodifiche;

import it.eng.myportal.dtos.DeTipoDelegatoDTO;
import it.eng.myportal.entity.decodifiche.DeTipoDelegato;

import javax.ejb.Stateless;

/**
 * Classe Home per la tabella de_tipo_delegato
 * 
 * @see it.eng.myportal.entity.decodifiche.DeTipoAbilitato
 * @author Rodi A.
 */
@Stateless
public class DeTipoDelegatoHome extends AbstractDecodeHome<DeTipoDelegato, DeTipoDelegatoDTO> {

	public DeTipoDelegato findById(String id) {
		return findById(DeTipoDelegato.class, id);
	}

	@Override
	public DeTipoDelegatoDTO toDTO(DeTipoDelegato entity) {
		if (entity == null)
			return null;
		DeTipoDelegatoDTO dto = super.toDTO(entity);
		dto.setId(entity.getCodTipoDelegato());
		dto.setDescrizione(entity.getDescrizione());
		return dto;
	}

}

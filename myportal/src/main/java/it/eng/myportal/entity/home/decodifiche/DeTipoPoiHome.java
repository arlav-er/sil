package it.eng.myportal.entity.home.decodifiche;

// Generated 2-nov-2011 14.43.14 by Hibernate Tools 3.4.0.CR1

import it.eng.myportal.dtos.DeTipoPoiDTO;
import it.eng.myportal.entity.decodifiche.DeTipoPoi;

import javax.ejb.Stateless;

/**
 * Home object for domain model class DeTipoPoi.
 * 
 * @see it.eng.myportal.entity.decodifiche.DeTipoPoi
 * @author Hibernate Tools
 */
@Stateless
public class DeTipoPoiHome extends AbstractSuggestibleHome<DeTipoPoi, DeTipoPoiDTO> {

	public DeTipoPoi findById(final String id) {
		return findById(DeTipoPoi.class, id);
	}

	@Override
	public DeTipoPoiDTO toDTO(final DeTipoPoi deTipoPoi) {
		if (deTipoPoi == null)
			return null;
		DeTipoPoiDTO ret = super.toDTO(deTipoPoi);
		ret.setId(deTipoPoi.getCodTipoPoi());
		ret.setDescrizione(deTipoPoi.getDescrizione());
		return ret;
	}

	@Override
	public DeTipoPoi fromDTO(final DeTipoPoiDTO deTipoPoi) {
		if (deTipoPoi == null)
			return null;
		DeTipoPoi ret = super.fromDTO(deTipoPoi);
		ret.setCodTipoPoi(deTipoPoi.getId());
		ret.setDescrizione(deTipoPoi.getDescrizione());
		return ret;
	}

}

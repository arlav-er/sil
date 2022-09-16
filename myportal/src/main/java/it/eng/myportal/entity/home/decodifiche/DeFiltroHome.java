package it.eng.myportal.entity.home.decodifiche;

// Generated 1-set-2011 12.32.46 by Hibernate Tools 3.4.0.CR1

import it.eng.myportal.dtos.DeFiltroDTO;
import it.eng.myportal.entity.decodifiche.DeFiltro;

import javax.ejb.Stateless;


@Stateless
public class DeFiltroHome extends AbstractDecodeHome<DeFiltro, DeFiltroDTO> {

	public DeFiltro findById(String id) {
		return findById(DeFiltro.class, id);
	}

	@Override
	public DeFiltroDTO toDTO(DeFiltro filtro) {
		if (filtro == null)
			return null;
		DeFiltroDTO ret = super.toDTO(filtro);
		ret.setId(filtro.getCodFiltro());
		ret.setDescrizione(filtro.getDescrizione());
		ret.setFiltro(filtro.getCodFiltro());
		return ret;
	}

	@Override
	public DeFiltro fromDTO(DeFiltroDTO filtro) {
		if (filtro == null)
			return null;
		DeFiltro ret = super.fromDTO(filtro);
		ret.setCodFiltro(filtro.getId());
		ret.setDescrizione(filtro.getDescrizione());
		ret.setCodFiltro(filtro.getFiltro());
		return ret;
	}
	
}

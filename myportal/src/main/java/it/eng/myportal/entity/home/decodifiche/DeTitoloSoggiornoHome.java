package it.eng.myportal.entity.home.decodifiche;

// Generated 1-set-2011 12.32.46 by Hibernate Tools 3.4.0.CR1

import it.eng.myportal.dtos.DeTitoloSoggiornoDTO;
import it.eng.myportal.entity.decodifiche.DeTitoloSoggiorno;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;

/**
 * Home object for domain model class DeTitoloSoggiorno
 * 
 * @see it.eng.myportal.entity.decodifiche.DeTitoloSoggiorno
 * @author Turrini
 */
@Stateless
public class DeTitoloSoggiornoHome extends AbstractTreeableHome<DeTitoloSoggiorno, DeTitoloSoggiornoDTO> {
	
	public DeTitoloSoggiorno findById(String id) {
		return findById(DeTitoloSoggiorno.class, id);
	}

	@Override
	public DeTitoloSoggiornoDTO toDTO(DeTitoloSoggiorno titolo) {
		if (titolo == null)
			return null;
		DeTitoloSoggiornoDTO ret = super.toDTO(titolo);
		ret.setId(titolo.getCodTitoloSoggiorno().toString());
		ret.setDescrizione(titolo.getDescrizione());
		return ret;
	}

	@Override
	public DeTitoloSoggiorno fromDTO(DeTitoloSoggiornoDTO titolo) {
		if (titolo == null)
			return null;
		DeTitoloSoggiorno ret = super.fromDTO(titolo);
		ret.setCodTitoloSoggiorno(titolo.getId());
		ret.setDescrizione(titolo.getDescrizione());
		return ret;
	}

	@Override
	public String getFieldName() {
		return "descrizione";
	}


	/**
	 * E' ad un solo livello!
	 */
	@Override
	public List<DeTitoloSoggiornoDTO> findByCodPadre(String par) {
		if ("0".equals(par))
			return findAllDTO();
		else
			return new ArrayList<DeTitoloSoggiornoDTO>();
	}

	

}

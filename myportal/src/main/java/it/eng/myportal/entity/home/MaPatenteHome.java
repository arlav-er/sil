package it.eng.myportal.entity.home;

import it.eng.myportal.dtos.MaPatenteDTO;
import it.eng.myportal.entity.MaPatente;
import it.eng.myportal.entity.home.decodifiche.AbstractDecodeHome;
import it.eng.myportal.entity.home.decodifiche.DePatenteHome;

import javax.ejb.EJB;
import javax.ejb.Stateless;

/**
 * @author Turro
 *
 */
@Stateless
public class MaPatenteHome extends
		AbstractDecodeHome<MaPatente, MaPatenteDTO> {

	@EJB
	DePatenteHome dePatenteHome;
	
	public MaPatente findById(final String id) {
		return findById(MaPatente.class, id);
	}

	@Override
	public MaPatenteDTO toDTO(final MaPatente entity) {
		if (entity == null)
			return null;
		MaPatenteDTO ret = super.toDTO(entity);
		
		ret.setId(entity.getCodPatente());
		ret.setCodPatenteMin(entity.getCodPatenteMin());
		ret.setPatente(dePatenteHome.toDTO(entity.getPatente()));
		return ret;
	}

	@Override
	public MaPatente fromDTO(final MaPatenteDTO dto) {
		if (dto == null)
			return null;
		MaPatente ret = super.fromDTO(dto);
		
		ret.setCodPatenteMin(dto.getCodPatenteMin());
		ret.setCodPatente(dto.getCodPatente());
		ret.setPatente(dePatenteHome.findById(dto.getPatente().getId()));
		
		return ret;
	}



	

}

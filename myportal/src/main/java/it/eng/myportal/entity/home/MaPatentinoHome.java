package it.eng.myportal.entity.home;

import it.eng.myportal.dtos.MaPatentinoDTO;
import it.eng.myportal.entity.MaPatentino;
import it.eng.myportal.entity.home.decodifiche.AbstractDecodeHome;
import it.eng.myportal.entity.home.decodifiche.DePatentinoHome;

import javax.ejb.EJB;
import javax.ejb.Stateless;

/**
 * @author Turro
 *
 */
@Stateless
public class MaPatentinoHome extends
		AbstractDecodeHome<MaPatentino, MaPatentinoDTO> {

	@EJB
	DePatentinoHome dePatentinoHome;
	
	public MaPatentino findById(final String id) {
		return findById(MaPatentino.class, id);
	}

	@Override
	public MaPatentinoDTO toDTO(final MaPatentino entity) {
		if (entity == null)
			return null;
		MaPatentinoDTO ret = super.toDTO(entity);
		
		ret.setId(entity.getCodPatentino());
		ret.setCodPatentinoMin(entity.getCodPatentinoMin());
		ret.setPatentino(dePatentinoHome.toDTO(entity.getPatentino()));
		return ret;
	}

	@Override
	public MaPatentino fromDTO(final MaPatentinoDTO dto) {
		if (dto == null)
			return null;
		MaPatentino ret = super.fromDTO(dto);
		
		ret.setCodPatentinoMin(dto.getCodPatentinoMin());
		ret.setCodPatentino(dto.getCodPatentino());
		ret.setPatentino(dePatentinoHome.findById(dto.getPatentino().getId()));
		
		return ret;
	}



	

}

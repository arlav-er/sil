package it.eng.myportal.entity.home;

import it.eng.myportal.dtos.MaRapportoLavoroDTO;
import it.eng.myportal.entity.MaRapportoLavoro;
import it.eng.myportal.entity.home.decodifiche.AbstractDecodeHome;
import it.eng.myportal.entity.home.decodifiche.DePatenteHome;
import it.eng.myportal.entity.home.decodifiche.DeRapportoLavoroHome;

import javax.ejb.EJB;
import javax.ejb.Stateless;

/**
 * @author Turro
 * 
 */
@Stateless
public class MaRapportoLavoroHome extends
		AbstractDecodeHome<MaRapportoLavoro, MaRapportoLavoroDTO> {

	@EJB
	DePatenteHome dePatenteHome;

	@EJB
	DeRapportoLavoroHome deRapportoLavoroHome;

	public MaRapportoLavoro findById(final String id) {
		return findById(MaRapportoLavoro.class, id);
	}

	@Override
	public MaRapportoLavoroDTO toDTO(final MaRapportoLavoro entity) {
		if (entity == null)
			return null;
		MaRapportoLavoroDTO ret = super.toDTO(entity);

		ret.setId(entity.getCodContratto());
		ret.setDeRapportoLavoroDTO(deRapportoLavoroHome.toDTO(entity
				.getDeRapportoLavoro()));

		return ret;
	}

	@Override
	public MaRapportoLavoro fromDTO(final MaRapportoLavoroDTO dto) {
		if (dto == null)
			return null;
		MaRapportoLavoro ret = super.fromDTO(dto);

		ret.setCodContratto(dto.getCodcontratto());
		if (dto.getDeRapportoLavoroDTO() != null)
			ret.setDeRapportoLavoro(deRapportoLavoroHome.findById(dto
					.getDeRapportoLavoroDTO().getId()));

		return ret;
	}

}

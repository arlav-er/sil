package it.eng.myportal.entity.home.decodifiche;

import it.eng.myportal.dtos.DeTipoMessaggioDTO;
import it.eng.myportal.entity.decodifiche.DeTipoMessaggio;

import javax.ejb.Stateless;

@Stateless
public class DeTipoMessaggioHome extends AbstractDecodeHome<DeTipoMessaggio, DeTipoMessaggioDTO> {

	@Override
	public DeTipoMessaggio findById(String id) {
		return findById(DeTipoMessaggio.class, id);
	}

}

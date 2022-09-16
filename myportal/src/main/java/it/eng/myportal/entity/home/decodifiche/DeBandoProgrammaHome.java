package it.eng.myportal.entity.home.decodifiche;

import javax.ejb.Stateless;

import it.eng.myportal.dtos.DeBandoProgrammaDTO;
import it.eng.myportal.entity.decodifiche.DeBandoProgramma;

@Stateless
public class DeBandoProgrammaHome extends AbstractDecodeHome<DeBandoProgramma, DeBandoProgrammaDTO> {

	
	public DeBandoProgramma findById(String id) {
		return findById(DeBandoProgramma.class, id);
	}

	@Override
	public DeBandoProgrammaDTO toDTO(DeBandoProgramma entity) {
		if (entity == null){
			return null;
		}
		DeBandoProgrammaDTO ret = super.toDTO(entity);
		ret.setId(entity.getCodBandoProgramma());
		ret.setDescrizione(entity.getDescrizione());
		return ret;
	}

	@Override
	public DeBandoProgramma fromDTO(DeBandoProgrammaDTO dto) {
		if (dto == null)
			return null;
		DeBandoProgramma ret = super.fromDTO(dto);
		ret.setCodBandoProgramma(dto.getCodBandoProgramma());
		ret.setDescrizione(dto.getDescrizione());
		return ret;
	}
	
}

package it.eng.myportal.entity.home.decodifiche;

import it.eng.myportal.dtos.DeTipoFbConvenzioneDTO;
import it.eng.myportal.entity.decodifiche.DeTipoFbConvenzione;

import javax.ejb.Stateless;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

@Stateless
public class DeTipoFbConvenzioneHome extends AbstractDecodeHome<DeTipoFbConvenzione, DeTipoFbConvenzioneDTO> {
	protected final Log log = LogFactory.getLog(DeTipoFbConvenzioneHome.class);

	public DeTipoFbConvenzione fromDTO(DeTipoFbConvenzioneDTO dto) {
		if (dto == null) {
			return null;
		}
		DeTipoFbConvenzione entity = super.fromDTO(dto);
		entity.setCodTipoConvenzione(dto.getId());
		entity.setDescrizione(dto.getDescrizione());

		return entity;
	}

	public DeTipoFbConvenzioneDTO toDTO(DeTipoFbConvenzione entity) {
		DeTipoFbConvenzioneDTO dto = super.toDTO(entity);
		dto.setId(entity.getCodTipoConvenzione());
		dto.setDescrizione(entity.getDescrizione());
		return dto;
	}

	public DeTipoFbConvenzione findById(String cod_tipo_convenzione) {
		return findById(DeTipoFbConvenzione.class, cod_tipo_convenzione);
	}

}

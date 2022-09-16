package it.eng.myportal.entity.home.decodifiche;

import it.eng.myportal.dtos.DeSvSezioneDTO;
import it.eng.myportal.entity.decodifiche.DeSvSezione;

import javax.ejb.Stateless;

/**
 * @see DeSvSezioneDTO
 * 
 * @author Rodi A
 */
@Stateless
public class DeSvSezioneHome extends AbstractDecodeHome<DeSvSezione, DeSvSezioneDTO> {

	@Override
	public DeSvSezione findById(String id) {
		return findById(DeSvSezione.class, id);
	}

	public DeSvSezioneDTO toDTO(DeSvSezione template) {
		if (template == null)
			return null;
		DeSvSezioneDTO dto = super.toDTO(template);
		dto.setId(template.getCodSvSezione());
		dto.setDescrizione(template.getDescrizione());
		return dto;
	}

}

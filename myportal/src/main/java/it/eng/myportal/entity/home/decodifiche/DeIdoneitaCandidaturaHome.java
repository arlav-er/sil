package it.eng.myportal.entity.home.decodifiche;

import javax.ejb.Stateless;

// Generated 1-set-2011 12.32.46 by Hibernate Tools 3.4.0.CR1

import it.eng.myportal.dtos.DeIdoneitaCandidaturaDTO;
import it.eng.myportal.entity.decodifiche.DeIdoneitaCandidatura;

/**
 * Classe Home per la tabella de_idoneita_candidatura
 * 
 * @see it.eng.myportal.entity.decodifiche.DeIdoneitaCandidatura
 * @author D'Angelo Enrico
 */
@Stateless
public class DeIdoneitaCandidaturaHome extends AbstractDecodeHome<DeIdoneitaCandidatura, DeIdoneitaCandidaturaDTO> {

	public static final String NON_ESAMINATO = "-1";

	public DeIdoneitaCandidatura findById(String id) {
		return findById(DeIdoneitaCandidatura.class, id);
	}

	@Override
	public DeIdoneitaCandidaturaDTO toDTO(final DeIdoneitaCandidatura entity) {
		if (entity == null) {
			return null;
		}
		DeIdoneitaCandidaturaDTO dto = super.toDTO(entity);
		dto.setId(entity.getCodIdoneitaCandidatura());
		dto.setDescrizione(entity.getDescrizione());
		return dto;
	}
	
	@Override
	public DeIdoneitaCandidatura fromDTO(DeIdoneitaCandidaturaDTO dto) {
		DeIdoneitaCandidatura ret = super.fromDTO(dto);
		ret.setCodIdoneitaCandidatura(dto.getId());
		return ret;
	}
}

package it.eng.myportal.entity.home;

import it.eng.myportal.dtos.AcCandidaturaDatiDTO;
import it.eng.myportal.entity.AcCandidaturaDati;
import it.eng.myportal.entity.home.decodifiche.DeProvenienzaHome;

import javax.ejb.EJB;
import javax.ejb.Stateless;

@Stateless
public class AcCandidaturaDatiHome extends AbstractUpdatableHome<AcCandidaturaDati, AcCandidaturaDatiDTO> {

	@EJB
	AcCandidaturaHome acCandidaturaHome;

	@EJB
	DeProvenienzaHome deProvenienzaHome;

	@Override
	public AcCandidaturaDati findById(Integer id) {
		return findById(AcCandidaturaDati.class, id);
	}

	@Override
	public AcCandidaturaDatiDTO toDTO(AcCandidaturaDati entity) {
		if (entity == null) {
			return null;
		}

		AcCandidaturaDatiDTO dto = super.toDTO(entity);
		dto.setIdAcCandidatura(entity.getAcCandidatura().getIdAcCandidatura());
		dto.setCodStatoOccupazionale(entity.getCodStatoOccupazionale());
		dto.setDescStatoOccupazionale(entity.getDescStatoOccupazionale());
		dto.setListeSpeciali(entity.getListeSpeciali());
		dto.setFlagDisabile(entity.getFlagDisabile());
		dto.setFlagIntermittente(entity.getFlagIntermittente());
		dto.setDataDichiarazione(entity.getDataDichiarazione());
		dto.setDeProvenienzaDTO(deProvenienzaHome.toDTO(entity.getDeProvenienza()));
		return dto;
	}

	@Override
	public AcCandidaturaDati fromDTO(AcCandidaturaDatiDTO dto) {
		if (dto == null) {
			return null;
		}

		AcCandidaturaDati entity = super.fromDTO(dto);
		if (dto.getIdAcCandidatura() != null) {
			entity.setAcCandidatura(acCandidaturaHome.findById(dto.getIdAcCandidatura()));
		}

		entity.setCodStatoOccupazionale(dto.getCodStatoOccupazionale());
		entity.setDescStatoOccupazionale(dto.getDescStatoOccupazionale());
		entity.setListeSpeciali(dto.getListeSpeciali());
		entity.setFlagDisabile(dto.getFlagDisabile());
		entity.setFlagIntermittente(dto.getFlagIntermittente());
		entity.setDataDichiarazione(dto.getDataDichiarazione());
		entity.setDeProvenienza(deProvenienzaHome.fromDTO(dto.getDeProvenienzaDTO()));
		return entity;
	}
}

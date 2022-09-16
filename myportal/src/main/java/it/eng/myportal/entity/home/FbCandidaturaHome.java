package it.eng.myportal.entity.home;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import it.eng.myportal.dtos.FbCandidaturaDTO;
import it.eng.myportal.entity.FbCandidatura;

@Stateless
public class FbCandidaturaHome extends AbstractUpdatableHome<FbCandidatura, FbCandidaturaDTO> {

	@EJB
	FbSchedaFabbisognoHome fbSchedaFabbisognoHome;

	@EJB
	AziendaInfoHome aziendaInfoHome;

	@Override
	public FbCandidatura findById(Integer id) {
		return findById(FbCandidatura.class, id);
	}

	public FbCandidaturaDTO toDTO(FbCandidatura entity) {
		FbCandidaturaDTO dto = super.toDTO(entity);
		dto.setId(entity.getIdFbCandidatura());
		dto.setIdFbSchedaFabbisogno(entity.getFbSchedaFabbisogno().getIdFbSchedaFabbisogno());
		dto.setNome(entity.getNome());
		dto.setCognome(entity.getCognome());
		dto.setCodiceFiscale(entity.getCodiceFiscale());
		dto.setEmail(entity.getEmail());
		dto.setFlagIdoneo(entity.isFlagIdoneo());

		// Setto la ragione sociale del soggetto promotore per il front-end
		if (entity.getPfPrincipalIns() != null && entity.getPfPrincipalIns().getAziendaInfo() != null) {
			dto.setSoggettoPromotore(entity.getPfPrincipalIns().getAziendaInfo().getRagioneSociale());
		}

		return dto;
	}

	public FbCandidatura fromDTO(FbCandidaturaDTO dto) {
		FbCandidatura entity = super.fromDTO(dto);
		entity.setIdFbCandidatura(dto.getId());
		entity.setFbSchedaFabbisogno(fbSchedaFabbisognoHome.findById(dto.getIdFbSchedaFabbisogno()));
		entity.setNome(dto.getNome());
		entity.setCognome(dto.getCognome());
		entity.setCodiceFiscale(dto.getCodiceFiscale());
		entity.setEmail(dto.getEmail());
		entity.setFlagIdoneo(dto.isFlagIdoneo());
		return entity;
	}

}

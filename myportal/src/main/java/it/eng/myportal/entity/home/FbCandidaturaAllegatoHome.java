package it.eng.myportal.entity.home;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.TypedQuery;

import it.eng.myportal.dtos.FbCandidaturaAllegatoDTO;
import it.eng.myportal.entity.FbCandidaturaAllegato;

@Stateless
public class FbCandidaturaAllegatoHome extends AbstractUpdatableHome<FbCandidaturaAllegato, FbCandidaturaAllegatoDTO> {

	@EJB
	private FbCandidaturaHome fbCandidaturaHome;

	@Override
	public FbCandidaturaAllegato findById(Integer id) {

		return findById(FbCandidaturaAllegato.class, id);
	}

	public FbCandidaturaAllegato fromDTO(FbCandidaturaAllegatoDTO dto) {
		if (dto == null)
			return null;

		FbCandidaturaAllegato entity = super.fromDTO(dto);
		entity.setIdAllegato(dto.getId());
		entity.setNomeFile(dto.getNomeFile());
		entity.setMimeFile(dto.getMimeFile());
		entity.setDataFile(dto.getDataFile());
		if (dto.getFbCandidaturaDTO() != null && dto.getFbCandidaturaDTO().getId() != null)
			entity.setIdCandidatura(fbCandidaturaHome.findById(dto.getFbCandidaturaDTO().getId()));

		return entity;
	}

	public FbCandidaturaAllegatoDTO toDTO(FbCandidaturaAllegato entity) {
		if (entity == null) {
			return null;
		}

		FbCandidaturaAllegatoDTO dto = super.toDTO(entity);
		dto.setId(entity.getIdAllegato());
		dto.setMimeFile(entity.getMimeFile());
		dto.setNomeFile(entity.getNomeFile());
		dto.setDataFile(entity.getDataFile());
		dto.setFbCandidaturaDTO(fbCandidaturaHome.toDTO(entity.getIdCandidatura()));

		return dto;
	}

	public List<FbCandidaturaAllegatoDTO> findAllegatoById(Integer candidaturaId) {
		List<FbCandidaturaAllegatoDTO> allegotDTO = new ArrayList<FbCandidaturaAllegatoDTO>();

		TypedQuery<FbCandidaturaAllegato> queryAllegato = entityManager.createNamedQuery("findAllegatoByCandidaturaId",
				FbCandidaturaAllegato.class);
		queryAllegato.setParameter("idCandidatura", candidaturaId);
		List<FbCandidaturaAllegato> queryResults = queryAllegato.getResultList();
		for (FbCandidaturaAllegato allegato : queryResults)
			allegotDTO.add(toDTO(allegato));

		return allegotDTO;
	}
}

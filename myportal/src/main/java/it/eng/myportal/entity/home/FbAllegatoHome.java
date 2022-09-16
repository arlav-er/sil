package it.eng.myportal.entity.home;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.TypedQuery;

import it.eng.myportal.dtos.FbAllegatoDTO;
import it.eng.myportal.entity.FbAllegato;

@Stateless
public class FbAllegatoHome extends AbstractUpdatableHome<FbAllegato, FbAllegatoDTO> {

	@EJB
	private FbConvenzioneHome fbConvenzioneHome;

	@Override
	public FbAllegato findById(Integer id) {

		return findById(FbAllegato.class, id);
	}

	public FbAllegato fromDTO(FbAllegatoDTO dto) {
		if (dto == null)
			return null;
		FbAllegato entity = super.fromDTO(dto);
		entity.setIdAllegato(dto.getId());
		entity.setMimeFile(dto.getMimeFile());
		entity.setNomeFile(dto.getNomeFile());
		entity.setTipoAllegato(dto.getTipoAllegato());
		entity.setPdf(dto.getPdf());
		if (dto.getIdFbConvenzione() != null && dto.getIdFbConvenzione().getId() != null)
			entity.setIdFbConvenzione(fbConvenzioneHome.findById(dto.getIdFbConvenzione().getId()));
		return entity;
	}

	public FbAllegatoDTO toDTO(FbAllegato entity) {
		if (entity == null) {
			return null;
		}

		FbAllegatoDTO dto = super.toDTO(entity);
		dto.setId(entity.getIdAllegato());
		dto.setMimeFile(entity.getMimeFile());
		dto.setNomeFile(entity.getNomeFile());
		dto.setTipoAllegato(entity.getTipoAllegato());
		dto.setPdf(entity.getPdf());
		dto.setIdFbConvenzione(fbConvenzioneHome.toDTO(entity.getIdFbConvenzione()));

		return dto;
	}

	public List<FbAllegatoDTO> findAllegatoById(Integer convenzioneId) {
		List<FbAllegatoDTO> allegotDTOList = new ArrayList<FbAllegatoDTO>();

		TypedQuery<FbAllegato> queryAllegato = entityManager.createNamedQuery("findAllegatoByConvenzioneId",
				FbAllegato.class);
		queryAllegato.setParameter("convenzioneId", convenzioneId);
		List<FbAllegato> queryResults = queryAllegato.getResultList();

		for (FbAllegato allegato : queryResults) {
			allegotDTOList.add(toDTO(allegato));
		}

		return allegotDTOList;
	}
}

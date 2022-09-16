package it.eng.myportal.entity.home;

import it.eng.myportal.dtos.SvAziendaInfoDTO;
import it.eng.myportal.entity.PfPrincipal;
import it.eng.myportal.entity.SvAziendaInfo;
import it.eng.myportal.entity.SvAziendaInfo_;
import it.eng.myportal.entity.home.decodifiche.DeSvTemplateHome;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

/**
 * 
 * @author Rodi A.
 * 
 */
@Stateless
public class SvAziendaInfoHome extends AbstractUpdatableHome<SvAziendaInfo, SvAziendaInfoDTO> {

	@EJB
	AziendaInfoHome aziendaInfoHome;

	@EJB
	DeSvTemplateHome svDeTemplateHome;

	@Override
	public SvAziendaInfo findById(Integer id) {
		return findById(SvAziendaInfo.class, id);
	}

	@Override
	public SvAziendaInfoDTO toDTO(SvAziendaInfo entity) {
		if (entity == null)
			return null;
		SvAziendaInfoDTO dto = super.toDTO(entity);
		dto.setProfiliRichiesti(entity.getProfiliRichiesti());
		dto.setChiSiamo(entity.getChiSiamo());
		dto.setDataPubblicazione(entity.getDataPubblicazione());
		dto.setDoveSiamo(entity.getDoveSiamo());
		dto.setFormazioneCrescita(entity.getFormazioneCrescita());
		dto.setId(entity.getIdPfPrincipal());
		dto.setCodiceTemplate(entity.getDeSvTemplate().getCodSvTemplate());
		dto.setMission(entity.getMission());
		dto.setPubblicabile(entity.getFlagPubblicabile());
		dto.setStoria(entity.getStoria());
		dto.setRagioneSociale(aziendaInfoHome.findById(entity.getIdPfPrincipal()).getRagioneSociale());
		return dto;
	}

	@Override
	public SvAziendaInfo fromDTO(SvAziendaInfoDTO dto) {
		if (dto == null)
			return null;
		SvAziendaInfo entity = super.fromDTO(dto);
		entity.setProfiliRichiesti(dto.getProfiliRichiesti());
		entity.setChiSiamo(dto.getChiSiamo());
		entity.setDataPubblicazione(dto.getDataPubblicazione());
		entity.setDoveSiamo(dto.getDoveSiamo());
		entity.setFormazioneCrescita(dto.getFormazioneCrescita());
		entity.setPfPrincipal(pfPrincipalHome.findById(dto.getId()));
		entity.setIdPfPrincipal(dto.getId());
		entity.setDeSvTemplate(svDeTemplateHome.findById(dto.getCodiceTemplate()));
		entity.setMission(dto.getMission());
		entity.setFlagPubblicabile(dto.getPubblicabile());
		entity.setStoria(dto.getStoria());
		return entity;
	}

	public SvAziendaInfoDTO findDTOByIdPfPrincipal(Integer idPfPrincipalAzienda) {
		PfPrincipal pfPrincipal = pfPrincipalHome.findById(idPfPrincipalAzienda);
		SvAziendaInfo svAzienda = pfPrincipal.getSvAziendaInfo();
		if (svAzienda == null)
			return null;
		return toDTO(svAzienda);
	}

	/**
	 * Restituisce true se esiste una vetrina con id uguale all'idAziendaInfo passato come parametro, false altrimenti.
	 * 
	 * @param idAziendaInfo
	 * @return
	 */
	public boolean exists(Integer idAziendaInfo) {
		boolean result = false;

		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<Long> criteriaQuery = criteriaBuilder.createQuery(Long.class);
		Root<SvAziendaInfo> svAziendaInfo = criteriaQuery.from(SvAziendaInfo.class);
		criteriaQuery.select(criteriaBuilder.countDistinct(svAziendaInfo));
		criteriaQuery.where(criteriaBuilder.equal(svAziendaInfo.get(SvAziendaInfo_.idPfPrincipal), idAziendaInfo));
		TypedQuery<Long> typedQuery = entityManager.createQuery(criteriaQuery);
		int count = typedQuery.getSingleResult().intValue();

		if (count == 1) {
			result = true;
		}

		return result;
	}

}

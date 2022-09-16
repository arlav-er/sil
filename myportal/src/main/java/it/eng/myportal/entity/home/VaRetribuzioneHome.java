package it.eng.myportal.entity.home;

import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import it.eng.myportal.dtos.VaRetribuzioneDTO;
import it.eng.myportal.entity.PfPrincipal;
import it.eng.myportal.entity.VaDatiVacancy;
import it.eng.myportal.entity.VaRetribuzione;
import it.eng.myportal.entity.decodifiche.DeRetribuzione;
import it.eng.myportal.entity.home.decodifiche.DeRetribuzioneHome;
import it.eng.myportal.exception.MyPortalException;
import it.eng.myportal.exception.MyPortalNoResultFoundException;

/**
 * @author Rodi A.
 */
@Stateless
@LocalBean
public class VaRetribuzioneHome extends AbstractVacancyEntityHome<VaRetribuzione, VaRetribuzioneDTO> implements InoDTOejb<VaRetribuzione> {

	@EJB
	DeRetribuzioneHome deRetribuzioneHome;

	public VaRetribuzione findById(Integer id) {
		return findById(VaRetribuzione.class, id);
	}

	/**
	 * Recupera i dati partendo dall'id del curriculum
	 * 
	 * @param vacancyId
	 *            int
	 * @return Set<VaIstruzione>
	 */
	public VaRetribuzione findByVacancyId(int vacancyId) {
		return vaDatiVacancyHome.findById(vacancyId).getVaRetribuzione();
	}

	/**
	 * Converte un'Entity in un DTO.
	 * 
	 * @param entity
	 *            VaIstruzione
	 * @return VaIstruzioneDTO
	 */
	@Override
	public VaRetribuzioneDTO toDTO(VaRetribuzione entity) {
		if (entity == null)
			return null;
		VaRetribuzioneDTO dto = super.toDTO(entity);
		dto.setId(entity.getIdVaDatiVacancy());
		DeRetribuzione deRetribuzione = entity.getDeRetribuzione();
		dto.setCodice(deRetribuzione.getCodRetribuzione());
		dto.setDescrizione(deRetribuzione.getLimInfDecimale() + " - " + deRetribuzione.getLimSupDecimale());

		return dto;
	}

	/**
	 * Genera l'Entity a partire dal DTO.
	 * 
	 * @param dto
	 *            VaIstruzioneDTO
	 * @return VaIstruzione
	 */
	@Override
	public VaRetribuzione fromDTO(VaRetribuzioneDTO dto) {
		if (dto == null)
			return null;
		VaRetribuzione entity = super.fromDTO(dto);
		entity.setDeRetribuzione(deRetribuzioneHome.findById(dto.getCodice()));
		entity.setIdVaDatiVacancy(dto.getId());

		return entity;
	}

	public void copyById(Integer pfPrincipalId, VaRetribuzione vaRetribuzione, VaDatiVacancy vaDatiVacancy) {
		if (vaRetribuzione != null) {
			Date now = new Date();
			entityManager.detach(vaRetribuzione);
			vaRetribuzione.setVaDatiVacancy(vaDatiVacancy);
			vaRetribuzione.setDtmIns(now);
			vaRetribuzione.setDtmMod(now);
			vaRetribuzione.setPfPrincipalIns(pfPrincipalHome.findById(pfPrincipalId));
			vaRetribuzione.setPfPrincipalMod(pfPrincipalHome.findById(pfPrincipalId));
			persist(vaRetribuzione);
		} else {
			throw new EJBException("Impossibile trovare l'entity corrispondente a va_retribuzione con id "
					+ vaDatiVacancy.getIdVaDatiVacancy());
		}
	}

	public VaRetribuzione findProperByVacancyId(Integer vacancyId) throws MyPortalNoResultFoundException {
		List<VaRetribuzione> deQualificaSrqList = entityManager
				.createNamedQuery("VaRetribuzioneFindByVacancyId", VaRetribuzione.class).setParameter("vaId", vacancyId)
				.getResultList();
		if (deQualificaSrqList.isEmpty()) {
			throw new MyPortalNoResultFoundException();
		} else if ((deQualificaSrqList.size() > 1) || deQualificaSrqList.isEmpty()) {
			throw new MyPortalException("VaRetribuzione doppio/non trovato per Vacancy:" + vacancyId);
		}
		return deQualificaSrqList.get(0);
	}

	@Override
	public VaRetribuzione merge(VaRetribuzione in, Integer userId) {
		PfPrincipal usr = pfPrincipalHome.findById(userId);
		in.setDtmMod(new Date());
		in.setPfPrincipalMod(usr);
		return entityManager.merge(in);
	}

	@Override
	public VaRetribuzione persist(VaRetribuzione entity, Integer actingUser) {
		PfPrincipal usr = pfPrincipalHome.findById(actingUser);
		entity.setDtmIns(new Date());
		entity.setDtmMod(new Date());
		entity.setPfPrincipalIns(usr);
		entity.setPfPrincipalMod(usr);
		entityManager.persist(entity);
		entityManager.flush();
		return entity;
	}
}

package it.eng.myportal.entity.home;

// Generated 1-set-2011 12.32.46 by Hibernate Tools 3.4.0.CR1

import it.eng.myportal.dtos.CvCompetenzeTrasvDTO;
import it.eng.myportal.entity.CvAltreInfo;
import it.eng.myportal.entity.CvCompetenzeTrasv;
import it.eng.myportal.entity.CvDatiPersonali;
import it.eng.myportal.entity.CvInformatica;
import it.eng.myportal.entity.PfPrincipal;

import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.ejb.EJBException;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

/**
 * Home object for domain model class CvCompetenzeTrasv.
 * 
 * @see it.eng.myportal.entity.CvCompetenzeTrasv
 * @author Rodi A.
 */
@Stateless
@LocalBean
public class CvCompetenzeTrasvHome extends
		AbstractCurriculumEntityHome<CvCompetenzeTrasv, CvCompetenzeTrasvDTO> 
			implements InoDTOejb<CvCompetenzeTrasv>{

	@Override
	public CvCompetenzeTrasv findById(Integer id) {
		return findById(CvCompetenzeTrasv.class, id);
	}

	@Override
	protected CvCompetenzeTrasv findByCurriculumId(int curriculumId) {
		Set<CvCompetenzeTrasv> comps = cvDatiPersonaliHome.findById(
				curriculumId).getCvCompetenzeTrasvs();
		if (comps.isEmpty())
			return null;
		return comps.iterator().next();
	}
	
	public List<CvCompetenzeTrasv> findProperByCurriculumId(Integer curriculumId){
		List<CvCompetenzeTrasv> cvCompetenzeTrasv = entityManager
				.createNamedQuery("CvCompetenzeTrasvByCvId", CvCompetenzeTrasv.class)
				.setParameter("cvId", curriculumId).getResultList();
			return cvCompetenzeTrasv;
	}

	@Override
	public CvCompetenzeTrasvDTO toDTO(CvCompetenzeTrasv entity) {
		if (entity == null) return null;
		CvCompetenzeTrasvDTO dto = super.toDTO(entity);
		
			dto.setId(entity.getIdCvCompetenzeTrasv());
			dto.setCapCompOrganizzative(entity.getCapCompOrganizzative());
			dto.setCapCompRelInterpersonali(entity
					.getCapCompRelInterpersonali());
			dto.setCapCompTecniche(entity.getCapCompTecniche());
			dto.setIdCvDatiPersonali(entity.getCvDatiPersonali()
					.getIdCvDatiPersonali());

			
	
		return dto;
	}

	@Override
	public CvCompetenzeTrasv fromDTO(CvCompetenzeTrasvDTO dto) {
		if (dto == null) return null;
		CvCompetenzeTrasv entity = super.fromDTO(dto);
		
			entity.setIdCvCompetenzeTrasv(dto.getId());
			entity.setCapCompOrganizzative(dto.getCapCompOrganizzative());
			entity.setCapCompRelInterpersonali(dto
					.getCapCompRelInterpersonali());
			entity.setCapCompTecniche(dto.getCapCompTecniche());
			entity.setCvDatiPersonali(cvDatiPersonaliHome.findById(dto
					.getIdCvDatiPersonali()));

		
		return entity;
	}

	public void copyById(Integer pfPrincipalId,
			CvCompetenzeTrasv cvCompetenzeTrasv, CvDatiPersonali cvDatiPersonali) {
		if (cvCompetenzeTrasv != null) {
			Date now = new Date();
			entityManager.detach(cvCompetenzeTrasv);
			cvCompetenzeTrasv.setIdCvCompetenzeTrasv(null);
			cvCompetenzeTrasv.setCvDatiPersonali(cvDatiPersonali);
			cvCompetenzeTrasv.setDtmIns(now);
			cvCompetenzeTrasv.setDtmMod(now);
			cvCompetenzeTrasv.setPfPrincipalIns(pfPrincipalHome
					.findById(pfPrincipalId));
			cvCompetenzeTrasv.setPfPrincipalMod(pfPrincipalHome
					.findById(pfPrincipalId));
			persist(cvCompetenzeTrasv);
		} else {
			throw new EJBException(
					"Impossibile trovare l'entity corrispondente a cv_competenze_trasv associato a cv_dati_personali con id "
							+ cvDatiPersonali.getIdCvDatiPersonali());
		}
	}

	@Override
	public CvCompetenzeTrasv merge(CvCompetenzeTrasv entity, Integer actingUser) {
		PfPrincipal usr = pfPrincipalHome.findById(actingUser);
		entity.setDtmMod(new Date());
		entity.setPfPrincipalMod(usr);
		return entityManager.merge(entity);
	}

	@Override
	public CvCompetenzeTrasv persist(CvCompetenzeTrasv entity, Integer actingUser) {
		// persistenza che aggira i DTO
		PfPrincipal usr = pfPrincipalHome.findById(actingUser);
		entity.setDtmIns(new Date());
		entity.setDtmMod(new Date());
		entity.setPfPrincipalIns(usr);
		entity.setPfPrincipalMod(usr);
		entityManager.persist(entity);
		entityManager.flush();// forza insert, per avere ID
		return entity;
	}

	@Override
	public void remove(CvCompetenzeTrasv detached) {
		if (detached != null) {
			CvCompetenzeTrasv persistentInstance = findById(detached.getIdCvCompetenzeTrasv());
			super.remove(persistentInstance);
		}
	}
	
}

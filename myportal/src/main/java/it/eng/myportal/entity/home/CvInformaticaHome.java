package it.eng.myportal.entity.home;

import it.eng.myportal.dtos.CvInformaticaDTO;
import it.eng.myportal.entity.CvAltreInfo;
import it.eng.myportal.entity.CvDatiPersonali;
import it.eng.myportal.entity.CvInformatica;
import it.eng.myportal.entity.PfPrincipal;

import java.util.Date;
import java.util.List;

import javax.ejb.EJBException;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;

/**
 * Home object for domain model class CvInformatica.
 * 
 * @see it.eng.myportal.entity.CvInformatica
 * @author Rodi A.
 */
@Stateless
@LocalBean
public class CvInformaticaHome extends AbstractCurriculumEntityHome<CvInformatica, CvInformaticaDTO>
	implements InoDTOejb<CvInformatica>{

	@Override
	public CvInformatica findById(Integer id) {
		return findById(CvInformatica.class, id);
	}

	/**
	 * Recupera i dati partendo dall'id del curriculum
	 * 
	 * @param curriculumId
	 *            int
	 * @return Set<CvInformatica>
	 */
	@Override
	protected CvInformatica findByCurriculumId(int curriculumId) {
		return cvDatiPersonaliHome.findById(curriculumId).getCvInformatica();
	}
	
	public List<CvInformatica> findProperByCurriculumId(Integer curriculumId) throws NoResultException, NonUniqueResultException {
		List<CvInformatica> cvInformatica = entityManager
			.createNamedQuery("CvInformaticaByCvId", CvInformatica.class)
			.setParameter("cvId", curriculumId).getResultList();
		return cvInformatica;
	}

	@Override
	public CvInformaticaDTO toDTO(CvInformatica entity) {
		if (entity == null)
			return null;
		CvInformaticaDTO dto = super.toDTO(entity);

		dto.setId(entity.getIdCvDatiPersonali());
		dto.setDescrizione(entity.getDescrizione());

		return dto;
	}

	@Override
	public CvInformatica fromDTO(CvInformaticaDTO dto) {
		if (dto == null) return null;
		CvInformatica entity = super.fromDTO(dto);
		
			entity.setDescrizione(dto.getDescrizione());
			entity.setIdCvDatiPersonali(dto.getIdCvDatiPersonali());
			entity.setCvDatiPersonali(cvDatiPersonaliHome.findById(dto.getIdCvDatiPersonali()));

			
		return entity;
	}

	public void copyById(Integer pfPrincipalId, CvInformatica cvInformatica, CvDatiPersonali cvDatiPersonali) {
		if (cvInformatica != null) {
			Date now = new Date();
			entityManager.detach(cvInformatica);
			cvInformatica.setCvDatiPersonali(cvDatiPersonali);
			cvInformatica.setDtmIns(now);
			cvInformatica.setDtmMod(now);
			cvInformatica.setPfPrincipalIns(pfPrincipalHome.findById(pfPrincipalId));
			cvInformatica.setPfPrincipalMod(pfPrincipalHome.findById(pfPrincipalId));
			persist(cvInformatica);
		} else {
			throw new EJBException("Impossibile trovare l'entity corrispondente a cv_informatica con id "
					+ cvDatiPersonali.getIdCvDatiPersonali());
		}
	}
	
	@Override
	public CvInformatica merge(CvInformatica entity, Integer actingUser) {
		PfPrincipal usr = pfPrincipalHome.findById(actingUser);
		entity.setDtmMod(new Date());
		entity.setPfPrincipalMod(usr);
		return entityManager.merge(entity);
	}

	@Override
	public CvInformatica persist(CvInformatica entity, Integer actingUser) {
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
	public void remove(CvInformatica detached) {
		if(detached != null) {
		  CvInformatica persistentInstance = findById(detached.getIdCvDatiPersonali());
		  super.remove(persistentInstance);
		}
	}
}

package it.eng.myportal.entity.home;

import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import it.eng.myportal.dtos.CvPatenteDTO;
import it.eng.myportal.entity.CvDatiPersonali;
import it.eng.myportal.entity.CvPatente;
import it.eng.myportal.entity.PfPrincipal;
import it.eng.myportal.entity.home.decodifiche.DePatenteHome;
import it.eng.myportal.entity.home.decodifiche.DePatenteSilHome;

/**
 * @author Enrico D'Angelo
 */
@Stateless
@LocalBean
public class CvPatenteHome extends AbstractCurriculumEntityListHome<CvPatente, CvPatenteDTO>
		implements InoDTOejb<CvPatente> {

	@EJB
	DePatenteHome dePatenteHome;

	@EJB
	DePatenteSilHome dePatenteSilHome;

	public CvPatente findById(Integer id) {
		return findById(CvPatente.class, id);
	}

	@Override
	public CvPatenteDTO toDTO(CvPatente entity) {
		if (entity == null)
			return null;
		CvPatenteDTO dto = super.toDTO(entity);

		dto.setId(entity.getIdCvPatente());
		dto.setIdCvDatiPersonali(entity.getCvDatiPersonali().getIdCvDatiPersonali());

		if (CvDatiPersonali.OpzTipoDecodifiche.SIL.equals(entity.getCvDatiPersonali().getOpzTipoDecodifiche())
				&& entity.getDePatenteSil() != null) {
			dto.setCodPatente(entity.getDePatenteSil().getCodPatenteSil());
			dto.setDescrizione(entity.getDePatenteSil().getDescrizione());
		} else if (entity.getDePatente() != null) {
			dto.setCodPatente(entity.getDePatente().getCodPatente());
			dto.setDescrizione(entity.getDePatente().getDescrizione());
		}

		return dto;
	}

	@Override
	public CvPatente fromDTO(CvPatenteDTO dto) {
		if (dto == null)
			return null;
		CvPatente entity = super.fromDTO(dto);

		entity.setIdCvPatente(dto.getId());
		entity.setCvDatiPersonali(cvDatiPersonaliHome.findById(dto.getIdCvDatiPersonali()));

		if (CvDatiPersonali.OpzTipoDecodifiche.SIL.equals(entity.getCvDatiPersonali().getOpzTipoDecodifiche())) {
			entity.setDePatenteSil(dePatenteSilHome.findById(dto.getCodPatente()));
		} else {
			entity.setDePatente(dePatenteHome.findById(dto.getCodPatente()));
		}

		return entity;
	}

	@Override
	@Deprecated // TERRIBILE! ND Ale 03/2020 solleva lazy
	public Set<CvPatente> findByCurriculumId(int curriculumId) {
		return cvDatiPersonaliHome.findById(curriculumId).getCvPatentes();
	}

	public List<CvPatente> findProperByCurriculumId(Integer curriculumId) {
		List<CvPatente> deQualificaSrqList = entityManager.createNamedQuery("CvPatenteFindByCvId", CvPatente.class)
				.setHint("org.hibernate.cacheable", true).setParameter("cvId", curriculumId).getResultList();

		return deQualificaSrqList;
	}

	public void copyById(Integer pfPrincipalId, CvPatente cvPatente, CvDatiPersonali cvDatiPersonali) {
		if (cvPatente != null) {
			Date now = new Date();
			entityManager.detach(cvPatente);
			cvPatente.setIdCvPatente(null);
			cvPatente.setCvDatiPersonali(cvDatiPersonali);
			cvPatente.setDtmIns(now);
			cvPatente.setDtmMod(now);
			cvPatente.setPfPrincipalIns(pfPrincipalHome.findById(pfPrincipalId));
			cvPatente.setPfPrincipalMod(pfPrincipalHome.findById(pfPrincipalId));
			persist(cvPatente);
		} else {
			throw new EJBException(
					"Impossibile trovare l'entity corrispondente a cv_patente associato a cv_dati_personali con id "
							+ cvDatiPersonali.getIdCvDatiPersonali());
		}
	}

	@Override
	public CvPatente merge(CvPatente entity, Integer actingUser) {
		PfPrincipal usr = pfPrincipalHome.findById(actingUser);
		entity.setDtmMod(new Date());
		entity.setPfPrincipalMod(usr);
		return entityManager.merge(entity);
	}

	@Override
	public CvPatente persist(CvPatente entity, Integer actingUser) {
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
	public void remove(CvPatente detached) {
		CvPatente persistentInstance = findById(detached.getIdCvPatente());
		super.remove(persistentInstance);
	}
}

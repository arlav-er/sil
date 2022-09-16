package it.eng.myportal.entity.home;

import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import it.eng.myportal.dtos.CvPatentinoDTO;
import it.eng.myportal.entity.CvDatiPersonali;
import it.eng.myportal.entity.CvPatentino;
import it.eng.myportal.entity.PfPrincipal;
import it.eng.myportal.entity.home.decodifiche.DePatentinoHome;
import it.eng.myportal.entity.home.decodifiche.DePatentinoSilHome;

/**
 * @author Enrico D'Angelo
 */
@Stateless
@LocalBean
public class CvPatentinoHome extends AbstractCurriculumEntityListHome<CvPatentino, CvPatentinoDTO> implements InoDTOejb<CvPatentino> {

	@EJB
	DePatentinoHome dePatentinoHome;

	@EJB
	DePatentinoSilHome dePatentinoSilHome;

	public CvPatentino findById(Integer id) {
		return findById(CvPatentino.class, id);
	}

	@Override
	public CvPatentinoDTO toDTO(CvPatentino entity) {
		if (entity == null)
			return null;
		CvPatentinoDTO dto = super.toDTO(entity);

		dto.setId(entity.getIdCvPatentino());
		dto.setIdCvDatiPersonali(entity.getCvDatiPersonali().getIdCvDatiPersonali());

		if (CvDatiPersonali.OpzTipoDecodifiche.SIL.equals(entity.getCvDatiPersonali().getOpzTipoDecodifiche())
				&& entity.getDePatentinoSil() != null) {
			dto.setCodPatentino(entity.getDePatentinoSil().getCodPatentinoSil());
			dto.setDescrizione(entity.getDePatentinoSil().getDescrizione());
		} else if (entity.getDePatentino() != null) {
			dto.setCodPatentino(entity.getDePatentino().getCodPatentino());
			dto.setDescrizione(entity.getDePatentino().getDescrizione());
		}

		return dto;
	}

	@Override
	public CvPatentino fromDTO(CvPatentinoDTO dto) {
		if (dto == null)
			return null;
		CvPatentino entity = super.fromDTO(dto);

		entity.setIdCvPatentino(dto.getId());
		entity.setCvDatiPersonali(cvDatiPersonaliHome.findById(dto.getIdCvDatiPersonali()));

		if (CvDatiPersonali.OpzTipoDecodifiche.SIL.equals(entity.getCvDatiPersonali().getOpzTipoDecodifiche())) {
			entity.setDePatentinoSil(dePatentinoSilHome.findById(dto.getCodPatentino()));
		} else {
			entity.setDePatentino(dePatentinoHome.findById(dto.getCodPatentino()));
		}

		return entity;
	}

	@Override
	protected Set<CvPatentino> findByCurriculumId(int curriculumId) {
		return cvDatiPersonaliHome.findById(curriculumId).getCvPatentinos();
	}

	public void copyById(Integer pfPrincipalId, CvPatentino cvPatentino, CvDatiPersonali cvDatiPersonali) {
		if (cvPatentino != null) {
			Date now = new Date();
			entityManager.detach(cvPatentino);
			cvPatentino.setIdCvPatentino(null);
			cvPatentino.setCvDatiPersonali(cvDatiPersonali);
			cvPatentino.setDtmIns(now);
			cvPatentino.setDtmMod(now);
			cvPatentino.setPfPrincipalIns(pfPrincipalHome.findById(pfPrincipalId));
			cvPatentino.setPfPrincipalMod(pfPrincipalHome.findById(pfPrincipalId));
			persist(cvPatentino);
		} else {
			throw new EJBException(
					"Impossibile trovare l'entity corrispondente a cv_patentino associato a cv_dati_personali con id "
							+ cvDatiPersonali.getIdCvDatiPersonali());
		}
	}

	public List<CvPatentino> findProperByCurriculumId(Integer cvId) {
		List<CvPatentino> deQualificaSrqList = entityManager.createNamedQuery("CvPatentinoFindByCvId", CvPatentino.class)
				.setHint("org.hibernate.cacheable", true).setParameter("cvId", cvId).getResultList();

		return deQualificaSrqList;
	}
	@Override
	public CvPatentino merge(CvPatentino entity, Integer actingUser) {
		PfPrincipal usr = pfPrincipalHome.findById(actingUser);
		entity.setDtmMod(new Date());
		entity.setPfPrincipalMod(usr);
		return entityManager.merge(entity);
	}

	@Override
	public CvPatentino persist(CvPatentino entity, Integer actingUser) {
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
	public void remove(CvPatentino detached) {
		CvPatentino persistentInstance = findById(detached.getIdCvPatentino());
		super.remove(persistentInstance);
	}
}

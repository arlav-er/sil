package it.eng.myportal.entity.home;

import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import it.eng.myportal.dtos.CvAlboDTO;
import it.eng.myportal.entity.CvAlbo;
import it.eng.myportal.entity.CvDatiPersonali;
import it.eng.myportal.entity.PfPrincipal;
import it.eng.myportal.entity.home.decodifiche.DeAlboHome;
import it.eng.myportal.entity.home.decodifiche.DeAlboSilHome;

/**
 * @author Enrico D'Angelo
 */
@Stateless
@LocalBean
public class CvAlboHome extends AbstractCurriculumEntityListHome<CvAlbo, CvAlboDTO> implements InoDTOejb<CvAlbo> {

	@EJB
	DeAlboHome deAlboHome;

	@EJB
	DeAlboSilHome deAlboSilHome;

	public CvAlbo findById(Integer id) {
		return findById(CvAlbo.class, id);
	}

	@Override
	public CvAlboDTO toDTO(CvAlbo entity) {
		if (entity == null)
			return null;
		CvAlboDTO dto = super.toDTO(entity);

		dto.setId(entity.getIdCvAlbo());
		dto.setIdCvDatiPersonali(entity.getCvDatiPersonali().getIdCvDatiPersonali());

		if (CvDatiPersonali.OpzTipoDecodifiche.SIL.equals(entity.getCvDatiPersonali().getOpzTipoDecodifiche())
				&& entity.getDeAlboSil() != null) {
			// Se uso decodifiche SIL
			dto.setCodAlbo(entity.getDeAlboSil().getCodAlboSil());
			dto.setDescrizione(entity.getDeAlboSil().getDescrizione());
		} else if (entity.getDeAlbo() != null) {
			// Se uso decodifiche ministeriali
			dto.setCodAlbo(entity.getDeAlbo().getCodAlbo());
			dto.setDescrizione(entity.getDeAlbo().getDescrizione());
		}

		return dto;
	}

	@Override
	public CvAlbo fromDTO(CvAlboDTO dto) {
		if (dto == null)
			return null;
		CvAlbo entity = super.fromDTO(dto);

		entity.setIdCvAlbo(dto.getId());
		entity.setCvDatiPersonali(cvDatiPersonaliHome.findById(dto.getIdCvDatiPersonali()));

		if (CvDatiPersonali.OpzTipoDecodifiche.SIL.equals(entity.getCvDatiPersonali().getOpzTipoDecodifiche())) {
			entity.setDeAlboSil(deAlboSilHome.findById(dto.getCodAlbo()));
		} else {
			entity.setDeAlbo(deAlboHome.findById(dto.getCodAlbo()));
		}

		return entity;
	}

	@Override
	protected Set<CvAlbo> findByCurriculumId(int curriculumId) {
		return cvDatiPersonaliHome.findById(curriculumId).getCvAlbos();
	}

	public void copyById(Integer pfPrincipalId, CvAlbo cvAlbo, CvDatiPersonali cvDatiPersonali) {
		if (cvAlbo != null) {
			Date now = new Date();
			entityManager.detach(cvAlbo);
			cvAlbo.setIdCvAlbo(null);
			cvAlbo.setCvDatiPersonali(cvDatiPersonali);
			cvAlbo.setDtmIns(now);
			cvAlbo.setDtmMod(now);
			cvAlbo.setPfPrincipalIns(pfPrincipalHome.findById(pfPrincipalId));
			cvAlbo.setPfPrincipalMod(pfPrincipalHome.findById(pfPrincipalId));
			persist(cvAlbo);
		} else {
			throw new EJBException(
					"Impossibile trovare l'entity corrispondente a cv_albo associato a cv_dati_personali con id "
							+ cvDatiPersonali.getIdCvDatiPersonali());
		}
	}
	@Override
	public CvAlbo merge(CvAlbo entity, Integer actingUser) {
		PfPrincipal usr = pfPrincipalHome.findById(actingUser);
		entity.setDtmMod(new Date());
		entity.setPfPrincipalMod(usr);
		return entityManager.merge(entity);
	}

	@Override
	public CvAlbo persist(CvAlbo entity, Integer actingUser) {
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
	public void remove(CvAlbo detached) {
		CvAlbo persistentInstance = findById(detached.getIdCvAlbo());
		super.remove(persistentInstance);
	}

	public List<CvAlbo> findProperByCurriculumId(Integer idCvDatiPersonali) {
		List<CvAlbo> deQualificaSrqList = entityManager.createNamedQuery("CvAlboFindByCvId", CvAlbo.class)
				.setParameter("cvId", idCvDatiPersonali).getResultList();

		return deQualificaSrqList;
	}
}

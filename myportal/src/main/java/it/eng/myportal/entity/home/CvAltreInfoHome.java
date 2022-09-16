package it.eng.myportal.entity.home;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;

import it.eng.myportal.dtos.CvAltreInfoDTO;
import it.eng.myportal.dtos.DeTrasfertaDTO;
import it.eng.myportal.dtos.IDecode;
import it.eng.myportal.entity.CvAgevolazione;
import it.eng.myportal.entity.CvAltreInfo;
import it.eng.myportal.entity.CvDatiPersonali;
import it.eng.myportal.entity.CvEsperienzeProf;
import it.eng.myportal.entity.PfPrincipal;
import it.eng.myportal.entity.decodifiche.DeAgevolazione;
import it.eng.myportal.entity.home.decodifiche.DeAbilitazioneGenHome;
import it.eng.myportal.entity.home.decodifiche.DeAgevolazioneHome;
import it.eng.myportal.entity.home.decodifiche.DeTrasfertaHome;

/**
 * Home object for domain model class CvAltreInfo.
 *
 * @see it.eng.myportal.entity.CvAltreInfo
 * @author Rodi A.
 */

@Stateless
@LocalBean
public class CvAltreInfoHome extends AbstractCurriculumEntityHome<CvAltreInfo, CvAltreInfoDTO>
		implements InoDTOejb<CvAltreInfo> {

	@EJB
	DeAbilitazioneGenHome deAbilitazioneGenHome;

	@EJB
	DeAgevolazioneHome deAgevolazioneHome;

	@EJB
	DeTrasfertaHome deTrasfertaHome;

	@EJB
	CvAgevolazioneHome cvAgevolazioneHome;

	@Override
	public CvAltreInfo findById(Integer id) {
		return findById(CvAltreInfo.class, id);
	}

	@Override
	public CvAltreInfo findByCurriculumId(int curriculumId) {
		return cvDatiPersonaliHome.findById(curriculumId).getCvAltreInfo();
	}
	
	public List<CvAltreInfo> findProperByCurriculumId(Integer curriculumId) throws NoResultException, NonUniqueResultException {
		List<CvAltreInfo> altreInfo = entityManager
			.createNamedQuery("CvAltreInfoByCvId", CvAltreInfo.class)
			.setParameter("cvId", curriculumId).getResultList();
		return altreInfo;
	}

	@Override
	public CvAltreInfo fromDTO(CvAltreInfoDTO dto) {
		if (dto == null)
			return null;
		CvAltreInfo entity = super.fromDTO(dto);

		entity.setIdCvDatiPersonali(dto.getId());
		entity.setCvDatiPersonali(cvDatiPersonaliHome.findById(dto.getIdCvDatiPersonali()));

		entity.setFlagAutomunito(dto.getAutomunito());
		entity.setFlagMotomunito(dto.getMotomunito());

		DeTrasfertaDTO tipoTrasferta = dto.getTipoTrasferta();
		if (tipoTrasferta != null && tipoTrasferta.getId() != null) {
			entity.setDeTrasferta(deTrasfertaHome.findById(tipoTrasferta.getId()));
		}

		entity.setUlterioriInfo(dto.getUlterioriInfo());
		entity.setLimitazioni(dto.getLimitazioni());

		return entity;
	}

	@Override
	public CvAltreInfoDTO toDTO(CvAltreInfo entity) {
		if (entity == null)
			return null;
		CvAltreInfoDTO dto = super.toDTO(entity);

		dto.setId(entity.getIdCvDatiPersonali());

		dto.setAutomunito(entity.getFlagAutomunito());
		dto.setMotomunito(entity.getFlagMotomunito());

		if (entity.getDeTrasferta() != null) {
			dto.setTipoTrasferta(deTrasfertaHome.toDTO(entity.getDeTrasferta()));
		}

		Set<CvAgevolazione> agevolaziones = cvAgevolazioneHome.findByCurriculumId(entity.getIdCvDatiPersonali());
		for (CvAgevolazione cvAgevolazione : agevolaziones) {
			dto.getListaAgevolazioniDTO().add(deAgevolazioneHome.toDTO(cvAgevolazione.getDeAgevolazione()));
		}

		dto.setUlterioriInfo(entity.getUlterioriInfo());
		dto.setLimitazioni(entity.getLimitazioni());

		return dto;
	}

	public CvAltreInfoDTO mergeDTO(CvAltreInfoDTO data, Integer idPrincipalMod) {
		updateAgevolazioni(data, idPrincipalMod);
		return super.mergeDTO(data, idPrincipalMod);
	}

	/**
	 * Inserisce le agevolazioni collegate al curriculum
	 *
	 * @param data
	 * @param idPrincipalMod
	 */
	private void updateAgevolazioni(CvAltreInfoDTO data, Integer idPrincipalMod) {
		CvDatiPersonali cvDatiPersonali = cvDatiPersonaliHome.findById(data.getIdCvDatiPersonali());
		Set<CvAgevolazione> vecchieAgevolazioni = cvDatiPersonali.getCvAgevolaziones();

		// cancella le vecchie agevolazioni
		if (vecchieAgevolazioni != null && vecchieAgevolazioni.size() > 0) {
			for (CvAgevolazione cvAgevolazione : vecchieAgevolazioni) {
				cvAgevolazioneHome.remove(cvAgevolazione);
			}
		}

		// inserisci le nuove agevolazioni
		persistAgevolazioni(data, idPrincipalMod);
	}

	@Override
	public CvAltreInfoDTO persistDTO(CvAltreInfoDTO data, Integer idPrincipalIns) {
		persistAgevolazioni(data, idPrincipalIns);
		return super.persistDTO(data, idPrincipalIns);
	}

	/**
	 * Rende persistenti le modifiche apportate alle agevolazioni collegate al curriculum
	 *
	 * @param data
	 * @param idPrincipalMod
	 */
	private void persistAgevolazioni(CvAltreInfoDTO data, Integer idPrincipal) {
		PfPrincipal pfPrincipal = pfPrincipalHome.findById(idPrincipal);
		CvDatiPersonali cvDatiPersonali = cvDatiPersonaliHome.findById(data.getIdCvDatiPersonali());
		List<IDecode> nuoveAgevolazioni = data.getListaAgevolazioniDTO();
		Set<CvAgevolazione> insertAgevolazioni = new HashSet<CvAgevolazione>();

		// inserisci le agevolazioni
		if (nuoveAgevolazioni != null && nuoveAgevolazioni.size() > 0) {

			for (int i = 0; i < nuoveAgevolazioni.size(); i++) {
				IDecode iDecode = nuoveAgevolazioni.get(i);
				DeAgevolazione deAgevolazione = deAgevolazioneHome.findById(iDecode.getId());
				CvAgevolazione agevolazione = new CvAgevolazione(null, pfPrincipal, pfPrincipal, cvDatiPersonali,
						deAgevolazione, new Date(), new Date());
				cvAgevolazioneHome.persist(agevolazione);
				insertAgevolazioni.add(agevolazione);
			}
		}
		cvDatiPersonali.setCvAgevolaziones(insertAgevolazioni);
	}

	public void copyById(Integer pfPrincipalId, CvAltreInfo cvAltreInfo, CvDatiPersonali cvDatiPersonali) {
		if (cvAltreInfo != null) {
			Date now = new Date();
			entityManager.detach(cvAltreInfo);
			cvAltreInfo.setCvDatiPersonali(cvDatiPersonali);
			cvAltreInfo.setDtmIns(now);
			cvAltreInfo.setDtmMod(now);
			cvAltreInfo.setPfPrincipalIns(pfPrincipalHome.findById(pfPrincipalId));
			cvAltreInfo.setPfPrincipalMod(pfPrincipalHome.findById(pfPrincipalId));
			persist(cvAltreInfo);
		} else {
			throw new EJBException("Impossibile trovare l'entity corrispondente a cv_altre_info con id "
					+ cvDatiPersonali.getIdCvDatiPersonali());
		}
	}

	@Override
	public CvAltreInfo merge(CvAltreInfo entity, Integer actingUser) {
		PfPrincipal usr = pfPrincipalHome.findById(actingUser);
		entity.setDtmMod(new Date());
		entity.setPfPrincipalMod(usr);
		return entityManager.merge(entity);
	}

	@Override
	public CvAltreInfo persist(CvAltreInfo entity, Integer actingUser) {
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
	public void remove(CvAltreInfo detached) {
	     if (detached != null) {
			CvAltreInfo persistentInstance = findById(detached.getIdCvDatiPersonali());
			super.remove(persistentInstance);
		}
	}
}

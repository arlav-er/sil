package it.eng.myportal.entity.home;

import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import org.apache.commons.lang3.StringUtils;

import it.eng.myportal.dtos.CvFormazioneDTO;
import it.eng.myportal.dtos.DeCorsoDTO;
import it.eng.myportal.entity.CvDatiPersonali;
import it.eng.myportal.entity.CvFormazione;
import it.eng.myportal.entity.PfPrincipal;
import it.eng.myportal.entity.home.decodifiche.DeAmbitoDisciplinareHome;
import it.eng.myportal.entity.home.decodifiche.DeComuneHome;
import it.eng.myportal.entity.home.decodifiche.DeCorsoHome;
import it.eng.myportal.entity.home.decodifiche.DeTipoCorsoSilHome;

/**
 * Home object for domain model class CvFormazione.
 *
 * @see it.eng.myportal.entity.CvFormazione
 * @author Rodi A.
 */
@Stateless
@LocalBean
public class CvFormazioneHome extends AbstractCurriculumEntityListHome<CvFormazione, CvFormazioneDTO>
		implements InoDTOejb<CvFormazione> {

	@EJB
	DeCorsoHome deCorsoHome;

	@EJB
	DeComuneHome deComuneHome;

	@EJB
	DeTipoCorsoSilHome deTipoCorsoHome;

	@EJB
	DeAmbitoDisciplinareHome deAmbitoDisciplinareHome;

	public CvFormazione findById(Integer id) {
		return findById(CvFormazione.class, id);
	}

	/**
	 * Recupera i dati partendo dall'id del curriculum
	 *
	 * @param curriculumId
	 *            int
	 * @return Set<CvFormazione>
	 */
	public Set<CvFormazione> findByCurriculumId(int curriculumId) {
		return cvDatiPersonaliHome.findById(curriculumId).getCvFormaziones();
	}

	/**
	 * Converte un'Entity in un DTO.
	 *
	 * @param entity
	 *            CvFormazione
	 * @return CvFormazioneDTO
	 */
	@Override
	public CvFormazioneDTO toDTO(CvFormazione entity) {
		if (entity == null)
			return null;
		CvFormazioneDTO dto = super.toDTO(entity);

		dto.setId(entity.getIdCvFormazione());
		dto.setIdCvDatiPersonali(entity.getCvDatiPersonali().getIdCvDatiPersonali());
		dto.setTitoloCorso(entity.getTitoloCorso());
		dto.setPrincipaliTematiche(entity.getPrincipaliTematiche());
		dto.setNomeIstituto(entity.getNomeIstituto());
		dto.setLivelloClassificazione(entity.getLivelloClassificazione());
		dto.setAnno(entity.getNumAnno());
		dto.setDurata(entity.getDurata());
		dto.setDescrizione(entity.getDescrizione());
		dto.setFlgCompletato(entity.getFlgCompletato());

		if (entity.getDeCorso() != null) {
			dto.setCorso(deCorsoHome.toDTO(entity.getDeCorso()));
		}

		if (entity.getDeComuneEnte() != null) {
			dto.setDeComuneEnte(deComuneHome.toDTO(entity.getDeComuneEnte()));
		}

		if (entity.getDeTipoCorso() != null) {
			dto.setDeTipoCorso(deTipoCorsoHome.toDTO(entity.getDeTipoCorso()));
		}

		if (entity.getDeAmbitoDisciplinare() != null) {
			dto.setDeAmbitoDisciplinare(deAmbitoDisciplinareHome.toDTO(entity.getDeAmbitoDisciplinare()));
		}

		return dto;
	}

	/**
	 * Genera l'Entity a partire dal DTO.
	 *
	 * @param dto
	 *            CvFormazioneDTO
	 * @return CvFormazione con gli attributi settati
	 */
	@Override
	public CvFormazione fromDTO(CvFormazioneDTO dto) {
		if (dto == null)
			return null;
		CvFormazione entity = super.fromDTO(dto);

		entity.setIdCvFormazione(dto.getId());
		entity.setCvDatiPersonali(cvDatiPersonaliHome.findById(dto.getIdCvDatiPersonali()));
		DeCorsoDTO corso = dto.getCorso();
		if (corso != null) {
			String idCorso = dto.getCorso().getId();
			if (StringUtils.isNotBlank(idCorso)) {
				entity.setDeCorso(deCorsoHome.findById(idCorso));
			} else {
				entity.setDeCorso(null);
			}
		}
		entity.setTitoloCorso(dto.getTitoloCorso());
		entity.setPrincipaliTematiche(dto.getPrincipaliTematiche());
		entity.setNomeIstituto(dto.getNomeIstituto());
		entity.setLivelloClassificazione(dto.getLivelloClassificazione());
		entity.setNumAnno(dto.getAnno());
		entity.setDurata(dto.getDurata());
		entity.setDescrizione(dto.getDescrizione());
		entity.setFlgCompletato(dto.getFlgCompletato());

		if (dto.getDeComuneEnte() != null && dto.getDeComuneEnte().getId() != null) {
			entity.setDeComuneEnte(deComuneHome.findById(dto.getDeComuneEnte().getId()));
		}

		if (dto.getDeTipoCorso() != null && dto.getDeTipoCorso().getId() != null) {
			entity.setDeTipoCorso(deTipoCorsoHome.findById(dto.getDeTipoCorso().getId()));
		}

		if (dto.getDeAmbitoDisciplinare() != null && dto.getDeAmbitoDisciplinare().getId() != null) {
			entity.setDeAmbitoDisciplinare(deAmbitoDisciplinareHome.findById(dto.getDeAmbitoDisciplinare().getId()));
		}

		return entity;
	}

	public void copyById(Integer pfPrincipalId, CvFormazione cvFormazione, CvDatiPersonali cvDatiPersonali) {
		if (cvFormazione != null) {
			Date now = new Date();
			entityManager.detach(cvFormazione);
			cvFormazione.setIdCvFormazione(null);
			cvFormazione.setCvDatiPersonali(cvDatiPersonali);
			cvFormazione.setDtmIns(now);
			cvFormazione.setDtmMod(now);
			cvFormazione.setPfPrincipalIns(pfPrincipalHome.findById(pfPrincipalId));
			cvFormazione.setPfPrincipalMod(pfPrincipalHome.findById(pfPrincipalId));
			persist(cvFormazione);
		} else {
			throw new EJBException(
					"Impossibile trovare l'entity corrispondente a cv_formazione associato a cv_dati_personali con id "
							+ cvDatiPersonali.getIdCvDatiPersonali());
		}
	}

	public List<CvFormazione> findProperByCurriculumId(Integer idCvDatiPersonali) {
		List<CvFormazione> deQualificaSrqList = entityManager
				.createNamedQuery("CvFormazioneFindByCvId", CvFormazione.class).setHint("org.hibernate.cacheable", true)
				.setParameter("cvId", idCvDatiPersonali).getResultList();

		return deQualificaSrqList;
	}

	@Override
	public CvFormazione merge(CvFormazione entity, Integer actingUser) {
		PfPrincipal usr = pfPrincipalHome.findById(actingUser);
		entity.setDtmMod(new Date());
		entity.setPfPrincipalMod(usr);
		return entityManager.merge(entity);
	}

	@Override
	public CvFormazione persist(CvFormazione entity, Integer actingUser) {
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
	public void remove(CvFormazione detached) {
		CvFormazione persistentInstance = findById(detached.getIdCvFormazione());
		super.remove(persistentInstance);
	}
}

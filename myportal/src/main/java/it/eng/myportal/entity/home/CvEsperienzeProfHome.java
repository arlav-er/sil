package it.eng.myportal.entity.home;

import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import it.eng.myportal.dtos.CvEsperienzeProfDTO;
import it.eng.myportal.entity.CvDatiPersonali;
import it.eng.myportal.entity.CvEsperienzeProf;
import it.eng.myportal.entity.PfPrincipal;
import it.eng.myportal.entity.home.decodifiche.DeAreaSilHome;
import it.eng.myportal.entity.home.decodifiche.DeAttivitaHome;
import it.eng.myportal.entity.home.decodifiche.DeComuneHome;
import it.eng.myportal.entity.home.decodifiche.DeContrattoHome;
import it.eng.myportal.entity.home.decodifiche.DeContrattoSilHome;
import it.eng.myportal.entity.home.decodifiche.DeMansioneHome;
import it.eng.myportal.entity.home.decodifiche.DeMansioneMinHome;
import it.eng.myportal.entity.home.decodifiche.DeMotivoCessazioneHome;

/**
 * Home object for domain model class CvEsperienzeProf.
 *
 * @see it.eng.myportal.entity.CvEsperienzeProf
 * @author Hibernate Tools
 */
@Stateless
@LocalBean
public class CvEsperienzeProfHome extends AbstractCurriculumEntityListHome<CvEsperienzeProf, CvEsperienzeProfDTO>
		implements InoDTOejb<CvEsperienzeProf> {

	@EJB
	DeMansioneHome deMansione;

	@EJB
	DeAttivitaHome deAttivita;

	@EJB
	DeContrattoHome deContrattoHome;

	@EJB
	DeMansioneMinHome deMansioneMinHome;

	@EJB
	DeContrattoSilHome deContrattoSilHome;

	@EJB
	DeAreaSilHome deAreaSilHome;

	@EJB
	DeComuneHome deComuneHome;

	@EJB
	DeMotivoCessazioneHome deMotivoCessazioneHome;

	@Override
	public CvEsperienzeProf findById(Integer id) {
		return findById(CvEsperienzeProf.class, id);
	}

	/**
	 * Recupera i dati partendo dall'id del curriculum
	 *
	 * @param curriculumId
	 *            int
	 * @return Set<CvEsperienzeProf>
	 */
	@Override // ORENDO <- Si scrive con due RR
	@Deprecated
	protected Set<CvEsperienzeProf> findByCurriculumId(int curriculumId) {
		// query = entityManager.createNamedQuery(FIND_ESP_PROF_BY_CURRICULUM_ID);
		return cvDatiPersonaliHome.findById(curriculumId).getCvEsperienzeProfs();
	}

	public List<CvEsperienzeProf> findProperByCurriculumId(Integer curriculumId) {
		List<CvEsperienzeProf> espList = entityManager
				.createNamedQuery("CvEsperienzeFindByCvId", CvEsperienzeProf.class).setParameter("cvId", curriculumId)
				.getResultList();
		return espList;
	}

	@Override
	public CvEsperienzeProfDTO toDTO(CvEsperienzeProf entity) {
		if (entity == null)
			return null;
		CvEsperienzeProfDTO dto = super.toDTO(entity);

		dto.setId(entity.getIdCvEsperienzeProf());
		dto.setIdCvDatiPersonali(entity.getCvDatiPersonali().getIdCvDatiPersonali());
		dto.setDa(entity.getDa());
		dto.setA(entity.getA());
		dto.setInCorso((entity.getA() == null) ? true : false);
		dto.setAttivitaResponsabilita(entity.getAttivitaResponsabilita());
		dto.setDatoreLavoro(entity.getDatoreLavoro());
		dto.setCodiceFiscale(entity.getCodiceFiscale());
		dto.setPartitaIva(entity.getPartitaIva());
		dto.setAltroMotivoCessazione(entity.getAltroMotivoCessazione());

		if (CvDatiPersonali.OpzTipoDecodifiche.SIL.equals(entity.getCvDatiPersonali().getOpzTipoDecodifiche())
				&& entity.getDeContrattoSil() != null) {
			// Uso decodifiche SIL
			dto.setDeContratto(deContrattoSilHome.toDTO(entity.getDeContrattoSil()));
		} else if (entity.getDeContratto() != null) {
			// Uso decodifiche ministeriali
			dto.setDeContratto(deContrattoHome.toDTO(entity.getDeContratto()));
		}

		if (entity.getDeMansione() != null) {
			dto.setCodMansione(entity.getDeMansione().getCodMansione());
			dto.setStrMansione(entity.getDeMansione().getDescrizione());
		}

		if (entity.getDeAttivita() != null) {
			dto.setCodAteco(entity.getDeAttivita().getCodAteco());
			dto.setStrAteco(entity.getDeAttivita().getDescrizione());
		}

		if (entity.getDeMansioneMin() != null) {
			dto.setQualificaSvolta(deMansioneMinHome.toDTO(entity.getDeMansioneMin()));
		}

		if (entity.getDeAreaSil() != null) {
			dto.setDeAreaSil(deAreaSilHome.toDTO(entity.getDeAreaSil()));
		}

		if (entity.getDeMotivoCessazione() != null) {
			dto.setDeMotivoCessazione(deMotivoCessazioneHome.toDTO(entity.getDeMotivoCessazione()));
		}

		if (entity.getDeComuneDatore() != null) {
			dto.setDeComuneDatore(deComuneHome.toDTO(entity.getDeComuneDatore()));
		}

		return dto;
	}

	@Override
	public CvEsperienzeProf fromDTO(CvEsperienzeProfDTO dto) {
		if (dto == null)
			return null;

		CvEsperienzeProf entity = super.fromDTO(dto);
		entity.setIdCvEsperienzeProf(dto.getId());
		entity.setCvDatiPersonali(cvDatiPersonaliHome.findById(dto.getIdCvDatiPersonali()));
		entity.setDeMansione(deMansione.findById(dto.getCodMansione()));
		entity.setDeAttivita(deAttivita.findById(dto.getCodAteco()));
		entity.setDa(dto.getDa());
		entity.setAttivitaResponsabilita(dto.getAttivitaResponsabilita());
		entity.setDatoreLavoro(dto.getDatoreLavoro());
		entity.setCodiceFiscale(dto.getCodiceFiscale());
		entity.setPartitaIva(dto.getPartitaIva());
		entity.setAltroMotivoCessazione(dto.getAltroMotivoCessazione());

		if (!dto.getInCorso()) {
			entity.setA(dto.getA());
		}

		if (dto.getDeContratto() != null && dto.getDeContratto().getId() != null) {
			if (CvDatiPersonali.OpzTipoDecodifiche.SIL.equals(entity.getCvDatiPersonali().getOpzTipoDecodifiche())) {
				entity.setDeContrattoSil(deContrattoSilHome.findById(dto.getDeContratto().getId()));
			} else {
				entity.setDeContratto(deContrattoHome.findById(dto.getDeContratto().getId()));
			}
		}

		if (dto.getQualificaSvolta() != null && dto.getQualificaSvolta().getId() != null) {
			entity.setDeMansioneMin(deMansioneMinHome.findById(dto.getQualificaSvolta().getId()));
		}
		if (dto.getDeAreaSil() != null && dto.getDeAreaSil().getId() != null) {
			entity.setDeAreaSil(deAreaSilHome.findById(dto.getDeAreaSil().getId()));
		}
		if (dto.getDeMotivoCessazione() != null && dto.getDeMotivoCessazione().getId() != null) {
			entity.setDeMotivoCessazione(deMotivoCessazioneHome.findById(dto.getDeMotivoCessazione().getId()));
		}
		if (dto.getDeComuneDatore() != null && dto.getDeComuneDatore().getId() != null) {
			entity.setDeComuneDatore(deComuneHome.findById(dto.getDeComuneDatore().getId()));
		}

		return entity;
	}

	public void copyById(Integer pfPrincipalId, CvEsperienzeProf cvEsperienzeProf, CvDatiPersonali cvDatiPersonali) {
		if (cvEsperienzeProf != null) {
			Date now = new Date();
			entityManager.detach(cvEsperienzeProf);
			cvEsperienzeProf.setIdCvEsperienzeProf(null);
			cvEsperienzeProf.setCvDatiPersonali(cvDatiPersonali);
			cvEsperienzeProf.setDtmIns(now);
			cvEsperienzeProf.setDtmMod(now);
			cvEsperienzeProf.setPfPrincipalIns(pfPrincipalHome.findById(pfPrincipalId));
			cvEsperienzeProf.setPfPrincipalMod(pfPrincipalHome.findById(pfPrincipalId));
			persist(cvEsperienzeProf);
		} else {
			throw new EJBException(
					"Impossibile trovare l'entity corrispondente a cv_esperienze_prof associato a cv_dati_personali con id "
							+ cvDatiPersonali.getIdCvDatiPersonali());
		}
	}

	@Override
	public CvEsperienzeProf merge(CvEsperienzeProf entity, Integer actingUser) {
		PfPrincipal usr = pfPrincipalHome.findById(actingUser);
		entity.setDtmMod(new Date());
		entity.setPfPrincipalMod(usr);
		return entityManager.merge(entity);
	}

	@Override
	public CvEsperienzeProf persist(CvEsperienzeProf entity, Integer actingUser) {
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
	public void remove(CvEsperienzeProf detached) {
		CvEsperienzeProf persistentInstance = findById(detached.getIdCvEsperienzeProf());
		super.remove(persistentInstance);
	}
}
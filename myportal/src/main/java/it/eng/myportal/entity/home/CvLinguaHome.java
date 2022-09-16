package it.eng.myportal.entity.home;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import it.eng.myportal.dtos.CvLinguaDTO;
import it.eng.myportal.entity.CvDatiPersonali;
import it.eng.myportal.entity.CvLingua;
import it.eng.myportal.entity.PfPrincipal;
import it.eng.myportal.entity.decodifiche.DeGradoLin;
import it.eng.myportal.entity.decodifiche.sil.DeGradoLinSil;
import it.eng.myportal.entity.home.decodifiche.DeGradoLinHome;
import it.eng.myportal.entity.home.decodifiche.DeGradoLinSilHome;
import it.eng.myportal.entity.home.decodifiche.DeLinguaHome;
import it.eng.myportal.entity.home.decodifiche.DeModalitaLinguaHome;

/**
 * Home object for domain model class CvLingua.
 * 
 * @see it.eng.myportal.entity.CvLingua
 * @author Rodi A.
 */
@Stateless
@LocalBean
public class CvLinguaHome extends AbstractCurriculumEntityListHome<CvLingua, CvLinguaDTO> implements InoDTOejb<CvLingua>{

	@EJB
	DeLinguaHome deLinguaHome;

	@EJB
	DeGradoLinHome deGradoLinHome;

	@EJB
	DeGradoLinSilHome deGradoLinSilHome;

	@EJB
	DeModalitaLinguaHome deModalitaLinguaHome;

	/**
	 * Restituisce l'entity CvLingua a partire dall'id
	 * 
	 * @param id
	 *            Integer
	 * @return CvLingua
	 */
	public CvLingua findById(Integer id) {
		return findById(CvLingua.class, id);
	}

	@Override
	public CvLinguaDTO toDTO(CvLingua entity) {
		if (entity == null)
			return null;
		CvLinguaDTO dto = super.toDTO(entity);

		dto.setId(entity.getIdCvLingua());
		dto.setIdCvDatiPersonali(entity.getCvDatiPersonali().getIdCvDatiPersonali());

		DeGradoLin gradoLinguaLetto = entity.getDeGradoLinLetto();
		DeGradoLin gradoLinguaScritto = entity.getDeGradoLinScritto();
		DeGradoLin gradoLinguaParlato = entity.getDeGradoLinParlato();
		DeGradoLinSil gradoLinguaSilLetto = entity.getDeGradoLinSilLetto();
		DeGradoLinSil gradoLinguaSilScritto = entity.getDeGradoLinSilScritto();
		DeGradoLinSil gradoLinguaSilParlato = entity.getDeGradoLinSilParlato();

		if (entity.getFlagMadrelingua()) {
			// Se sono madrelingua, non ho gradi di conoscenza
			dto.setCodGradoLinguaLetto("");
			dto.setStrGradoLinguaLetto("MADRELINGUA");
			dto.setNumGradoLinguaLetto(0);

			dto.setCodGradoLinguaScritto("");
			dto.setStrGradoLinguaScritto("MADRELINGUA");
			dto.setNumGradoLinguaScritto(0);

			dto.setCodGradoLinguaParlato("");
			dto.setStrGradoLinguaParlato("MADRELINGUA");
			dto.setNumGradoLinguaParlato(0);
		} else if (CvDatiPersonali.OpzTipoDecodifiche.SIL.equals(entity.getCvDatiPersonali().getOpzTipoDecodifiche())) {
			// Se il curriculum usa decodifiche SIL
			if (gradoLinguaSilLetto != null) {
				dto.setCodGradoLinguaLetto(gradoLinguaSilLetto.getCodGradoLinSil());
				dto.setStrGradoLinguaLetto(gradoLinguaSilLetto.getDescrizione());
			}

			if (gradoLinguaSilScritto != null) {
				dto.setCodGradoLinguaScritto(gradoLinguaSilScritto.getCodGradoLinSil());
				dto.setStrGradoLinguaScritto(gradoLinguaSilScritto.getDescrizione());

			}

			if (gradoLinguaSilParlato != null) {
				dto.setCodGradoLinguaParlato(gradoLinguaSilParlato.getCodGradoLinSil());
				dto.setStrGradoLinguaParlato(gradoLinguaSilParlato.getDescrizione());
			}
		} else {
			if (gradoLinguaLetto != null) {
				// Se il curriculum non usa decodifiche SIL
				dto.setCodGradoLinguaLetto(gradoLinguaLetto.getCodGrado());
				dto.setStrGradoLinguaLetto(gradoLinguaLetto.getDescrizione());
				dto.setNumGradoLinguaLetto(gradoLinguaLetto.getNumOrdine());
			}

			if (gradoLinguaScritto != null) {
				dto.setCodGradoLinguaScritto(gradoLinguaScritto.getCodGrado());
				dto.setStrGradoLinguaScritto(gradoLinguaScritto.getDescrizione());
				dto.setNumGradoLinguaScritto(gradoLinguaScritto.getNumOrdine());
			}

			if (gradoLinguaParlato != null) {
				dto.setCodGradoLinguaParlato(gradoLinguaParlato.getCodGrado());
				dto.setStrGradoLinguaParlato(gradoLinguaParlato.getDescrizione());
				dto.setNumGradoLinguaParlato(gradoLinguaParlato.getNumOrdine());
			}
		}

		dto.setMadrelingua(entity.getFlagMadrelingua());
		dto.setCodLingua(entity.getDeLingua().getCodLingua());
		dto.setStrLingua(entity.getDeLingua().getDenominazione());
		dto.setFlgCertificata(entity.getFlgCertificata());
		dto.setAltraModalita(entity.getAltraModalita());

		if (entity.getDeModalitaLingua() != null) {
			dto.setDeModalitaLinguaDTO(deModalitaLinguaHome.toDTO(entity.getDeModalitaLingua()));
		}

		return dto;
	}

	@Override
	public CvLingua fromDTO(CvLinguaDTO dto) {
		if (dto == null) {
			return null;
		}

		CvLingua entity = super.fromDTO(dto);
		entity.setIdCvLingua(dto.getId());
		entity.setCvDatiPersonali(cvDatiPersonaliHome.findById(dto.getIdCvDatiPersonali()));
		entity.setDeLingua(deLinguaHome.findById(dto.getCodLingua()));
		entity.setFlagMadrelingua(dto.isMadrelingua());
		entity.setAltraModalita(dto.getAltraModalita());
		entity.setFlgCertificata(dto.getFlgCertificata());

		String gradoLinguaLetto = dto.getCodGradoLinguaLetto();
		String gradoLinguaScritto = dto.getCodGradoLinguaScritto();
		String gradoLinguaParlato = dto.getCodGradoLinguaParlato();

		if (dto.isMadrelingua()) {
			entity.setDeGradoLinLetto(null);
			entity.setDeGradoLinScritto(null);
			entity.setDeGradoLinParlato(null);
		} else if (CvDatiPersonali.OpzTipoDecodifiche.SIL.equals(entity.getCvDatiPersonali().getOpzTipoDecodifiche())) {
			if (gradoLinguaLetto != null) {
				entity.setDeGradoLinSilLetto(deGradoLinSilHome.findById(gradoLinguaLetto));
			}
			if (gradoLinguaScritto != null) {
				entity.setDeGradoLinSilScritto(deGradoLinSilHome.findById(gradoLinguaScritto));
			}
			if (gradoLinguaParlato != null) {
				entity.setDeGradoLinSilParlato(deGradoLinSilHome.findById(gradoLinguaParlato));
			}
		} else {
			if (gradoLinguaLetto != null) {
				entity.setDeGradoLinLetto(deGradoLinHome.findById(gradoLinguaLetto));
			}
			if (gradoLinguaScritto != null) {
				entity.setDeGradoLinScritto(deGradoLinHome.findById(gradoLinguaScritto));
			}
			if (gradoLinguaParlato != null) {
				entity.setDeGradoLinParlato(deGradoLinHome.findById(gradoLinguaParlato));
			}
		}

		if (dto.getDeModalitaLinguaDTO() != null && dto.getDeModalitaLinguaDTO().getId() != null) {
			entity.setDeModalitaLingua(deModalitaLinguaHome.findById(dto.getDeModalitaLinguaDTO().getId()));
		}

		return entity;
	}

	/**
	 * Recupera i dati partendo dall'id del curriculum
	 * 
	 * @param curriculumId
	 *            int
	 * @return Set<CvLingua>
	 */
	@Override
	protected Set<CvLingua> findByCurriculumId(int curriculumId) {
		return cvDatiPersonaliHome.findById(curriculumId).getCvLinguas();
	}


	public List<CvLingua> findLinguaByCurriculumId(Integer curriculumId) {
		List<CvLingua> cvLinguaList = entityManager.createNamedQuery("findLinguaByCvId", CvLingua.class)
				.setHint("org.hibernate.cacheable", true).setParameter("cvId", curriculumId).getResultList();

		return cvLinguaList;
	}

	public void copyById(Integer pfPrincipalId, CvLingua cvLingua, CvDatiPersonali cvDatiPersonali) {
		if (cvLingua != null) {
			Date now = new Date();
			entityManager.detach(cvLingua);
			cvLingua.setIdCvLingua(null);
			cvLingua.setCvDatiPersonali(cvDatiPersonali);
			cvLingua.setDtmIns(now);
			cvLingua.setDtmMod(now);
			cvLingua.setPfPrincipalIns(pfPrincipalHome.findById(pfPrincipalId));
			cvLingua.setPfPrincipalMod(pfPrincipalHome.findById(pfPrincipalId));
			persist(cvLingua);
		} else {
			throw new EJBException(
					"Impossibile trovare l'entity corrispondente a cv_lingua associato a cv_dati_personali con id "
							+ cvDatiPersonali.getIdCvDatiPersonali());
		}
	}

	@Override
	public CvLingua persist(CvLingua entity, Integer userAction) {
		PfPrincipal usr = pfPrincipalHome.findById(userAction);
		entity.setDtmIns(new Date());
		entity.setDtmMod(new Date());
		entity.setPfPrincipalIns(usr);
		entity.setPfPrincipalMod(usr);
		entityManager.persist(entity);
		entityManager.flush();
		return entity;
	}


	@Override
	public void remove(CvLingua entity) {
		CvLingua entityCvLingua = findById(entity.getIdCvLingua());
		super.remove(entityCvLingua);
	}

	@Override
	public CvLingua merge(CvLingua in, Integer userId) {
		PfPrincipal usr = pfPrincipalHome.findById(userId);
		in.setDtmMod(new Date());
		in.setPfPrincipalMod(usr);
		return entityManager.merge(in);
	}

	public List<CvLingua> findProperByCurriculumId(Integer idCvDatiPersonali) {
		// TODO Auto-generated method stub
		return new ArrayList<>();
	}
}

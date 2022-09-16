package it.eng.myportal.entity.home;

import it.eng.myportal.dtos.CvAgevolazioneDTO;
import it.eng.myportal.entity.CvAgevolazione;
import it.eng.myportal.entity.CvDatiPersonali;
import it.eng.myportal.entity.home.decodifiche.DeAgevolazioneHome;

import java.util.Date;
import java.util.Set;

import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.ejb.Stateless;

/**
 * @author Enrico D'Angelo
 */
@Stateless
public class CvAgevolazioneHome extends
		AbstractCurriculumEntityListHome<CvAgevolazione, CvAgevolazioneDTO> {

	@EJB
	DeAgevolazioneHome deAgevolazioneHome;

	public CvAgevolazione findById(Integer id) {
		return findById(CvAgevolazione.class, id);
	}

	@Override
	public CvAgevolazioneDTO toDTO(CvAgevolazione entity) {
		if (entity == null) return null;
		CvAgevolazioneDTO dto = super.toDTO(entity);
	
			dto = new CvAgevolazioneDTO();
			dto.setId(entity.getIdCvAgevolazione());
			dto.setIdCvDatiPersonali(entity.getCvDatiPersonali()
					.getIdCvDatiPersonali());
			dto.setCodAgevolazione(entity.getDeAgevolazione()
					.getCodAgevolazione());
			dto.setDescrizione(entity.getDeAgevolazione().getDescrizione());

		return dto;
	}

	@Override
	public CvAgevolazione fromDTO(CvAgevolazioneDTO dto) {
		if (dto == null) return null;
		CvAgevolazione entity = super.fromDTO(dto);
		
			entity.setIdCvAgevolazione(dto.getId());
			entity.setCvDatiPersonali(cvDatiPersonaliHome.findById(dto
					.getIdCvDatiPersonali()));
			entity.setDeAgevolazione(deAgevolazioneHome.findById(dto
					.getCodAgevolazione()));

		
		return entity;
	}

	@Override
	public Set<CvAgevolazione> findByCurriculumId(int curriculumId) {
		return cvDatiPersonaliHome.findById(curriculumId).getCvAgevolaziones();
	}

	public void copyById(Integer pfPrincipalId, CvAgevolazione cvAgevolazione,
			CvDatiPersonali cvDatiPersonali) {
		if (cvAgevolazione != null) {
			Date now = new Date();
			entityManager.detach(cvAgevolazione);
			cvAgevolazione.setIdCvAgevolazione(null);
			cvAgevolazione.setCvDatiPersonali(cvDatiPersonali);
			cvAgevolazione.setDtmIns(now);
			cvAgevolazione.setDtmMod(now);
			cvAgevolazione.setPfPrincipalIns(pfPrincipalHome
					.findById(pfPrincipalId));
			cvAgevolazione.setPfPrincipalMod(pfPrincipalHome
					.findById(pfPrincipalId));
			persist(cvAgevolazione);
		} else {
			throw new EJBException(
					"Impossibile trovare l'entity corrispondente a cv_agevolazione associato a cv_dati_personali con id "
							+ cvDatiPersonali.getIdCvDatiPersonali());
		}
	}
}

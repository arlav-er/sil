package it.eng.myportal.entity.home;

import it.eng.myportal.dtos.CvIstruzioneDTO;
import it.eng.myportal.entity.CvDatiPersonali;
import it.eng.myportal.entity.CvIstruzione;
import it.eng.myportal.entity.CvPatente;
import it.eng.myportal.entity.PfPrincipal;
import it.eng.myportal.entity.decodifiche.DeComune;
import it.eng.myportal.entity.decodifiche.DeTitolo;
import it.eng.myportal.entity.home.decodifiche.DeComuneHome;
import it.eng.myportal.entity.home.decodifiche.DeStatoTitoloHome;
import it.eng.myportal.entity.home.decodifiche.DeTitoloHome;

import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

/**
 * Home object per l'entity CvIstruzione Estende una classe astratta per avere
 * buona parte dei metodi già implementati.
 * 
 * @see it.eng.myportal.entity.CvIstruzione
 * @author Rodi A.
 */
@Stateless
@LocalBean
public class CvIstruzioneHome extends AbstractCurriculumEntityListHome<CvIstruzione, CvIstruzioneDTO>
		implements InoDTOejb<CvIstruzione> {

	@EJB
	DeTitoloHome deTitoloHome;

	@EJB
	DeComuneHome deComuneHome;

	@EJB
	DeStatoTitoloHome deStatoTitoloHome;

	public CvIstruzione findById(Integer id) {
		return findById(CvIstruzione.class, id);
	}

	/**
	 * Recupera i dati partendo dall'id del curriculum
	 * 
	 * @param curriculumId int
	 * @return Set<CvIstruzione>
	 */
	@Override
	@Deprecated // MUNNEZZ
	public Set<CvIstruzione> findByCurriculumId(int curriculumId) {
		return cvDatiPersonaliHome.findById(curriculumId).getCvIstruziones();
	}

	public List<CvIstruzione> findProperByCurriculumId(Integer curriculumId) {
		List<CvIstruzione> istruzioniList = entityManager.createNamedQuery("CvIstruzioneFindByCvId", CvIstruzione.class)
				.setHint("org.hibernate.cacheable", true).setParameter("cvId", curriculumId).getResultList();
		return istruzioniList;
	}

	/**
	 * Converte un'Entity in un DTO.
	 * 
	 * @param entity CvIstruzione
	 * @return CvIstruzioneDTO
	 */
	@Override
	public CvIstruzioneDTO toDTO(CvIstruzione entity) {
		if (entity == null)
			return null;
		CvIstruzioneDTO dto = super.toDTO(entity);

		dto.setId(entity.getIdCvIstruzione());
		dto.setIdCvDatiPersonali(entity.getCvDatiPersonali().getIdCvDatiPersonali());
		DeTitolo titolo = entity.getDeTitolo();
		if (titolo != null) {
			dto.setTitolo(deTitoloHome.toDTO(titolo));
		}
		DeComune comune = entity.getDeComune();
		if (comune != null) {
			dto.setComuneIstitutoCod(comune.getCodCom());
			dto.setComuneIstitutoDenominazione(deComuneHome.toDTO(comune).getDescrizione());
		}
		dto.setTematicheTrattate(entity.getTematicheTrattate());
		dto.setNomeIstituto(entity.getNomeIstituto());
		dto.setVotazione(entity.getVotazione());
		dto.setAnno(entity.getNumAnno());
		dto.setSpecifica(entity.getSpecifica());

		if (entity.getDeStatoTitolo() != null) {
			dto.setDeStatoTitolo(deStatoTitoloHome.toDTO(entity.getDeStatoTitolo()));
		}

		return dto;
	}

	/**
	 * Genera l'Entity a partire dal DTO.
	 * 
	 * @param dto CvIstruzioneDTO
	 * @return CvIstruzione
	 */
	@Override
	public CvIstruzione fromDTO(CvIstruzioneDTO dto) {
		if (dto == null)
			return null;
		CvIstruzione entity = super.fromDTO(dto);

		entity.setIdCvIstruzione(dto.getId());
		entity.setCvDatiPersonali(cvDatiPersonaliHome.findById(dto.getIdCvDatiPersonali()));
		if (dto.getTitolo() != null)
			entity.setDeTitolo(deTitoloHome.findById(dto.getTitolo().getId()));
		String comuneIstitutoCod = dto.getComuneIstitutoCod();
		if (comuneIstitutoCod != null) {
			entity.setDeComune(deComuneHome.findById(comuneIstitutoCod));
		}

		entity.setTematicheTrattate(dto.getTematicheTrattate());
		entity.setNomeIstituto(dto.getNomeIstituto());
		entity.setVotazione(dto.getVotazione());
		entity.setNumAnno(dto.getAnno());
		entity.setSpecifica(dto.getSpecifica());

		if (dto.getDeStatoTitolo() != null && dto.getDeStatoTitolo().getId() != null) {
			entity.setDeStatoTitolo(deStatoTitoloHome.findById(dto.getDeStatoTitolo().getId()));
		}

		return entity;
	}

	public void copyById(Integer pfPrincipalId, CvIstruzione cvIstruzione, CvDatiPersonali cvDatiPersonali) {
		if (cvIstruzione != null) {
			Date now = new Date();
			entityManager.detach(cvIstruzione);
			cvIstruzione.setIdCvIstruzione(null);
			cvIstruzione.setCvDatiPersonali(cvDatiPersonali);
			cvIstruzione.setDtmIns(now);
			cvIstruzione.setDtmMod(now);
			cvIstruzione.setPfPrincipalIns(pfPrincipalHome.findById(pfPrincipalId));
			cvIstruzione.setPfPrincipalMod(pfPrincipalHome.findById(pfPrincipalId));
			persist(cvIstruzione);
		} else {
			throw new EJBException(
					"Impossibile trovare l'entity corrispondente a cv_istruzione associato a cv_dati_personali con id "
							+ cvDatiPersonali.getIdCvDatiPersonali());
		}
	}

	@Override
	public CvIstruzione persist(CvIstruzione entity, Integer actingUser) {
		PfPrincipal usr = pfPrincipalHome.findById(actingUser);
		entity.setDtmIns(new Date());
		entity.setDtmMod(new Date());
		entity.setPfPrincipalIns(usr);
		entity.setPfPrincipalMod(usr);
		entityManager.persist(entity);
		entityManager.flush();
		return entity;
	}

	@Override
	public void remove(CvIstruzione entity) {
		CvIstruzione entityCvIstruzione = findById(entity.getIdCvIstruzione());
		super.remove(entityCvIstruzione);
	}

	@Override
	public CvIstruzione merge(CvIstruzione in, Integer userId) {
		PfPrincipal usr = pfPrincipalHome.findById(userId);
		in.setDtmMod(new Date());
		in.setPfPrincipalMod(usr);
		return entityManager.merge(in);
	}

}

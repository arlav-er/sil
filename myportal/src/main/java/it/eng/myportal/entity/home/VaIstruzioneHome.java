package it.eng.myportal.entity.home;

import it.eng.myportal.dtos.VaIstruzioneDTO;
import it.eng.myportal.entity.CvIstruzione;
import it.eng.myportal.entity.PfPrincipal;
import it.eng.myportal.entity.VaDatiVacancy;
import it.eng.myportal.entity.VaIstruzione;
import it.eng.myportal.entity.VaOrario;
import it.eng.myportal.entity.decodifiche.DeTitolo;
import it.eng.myportal.entity.home.decodifiche.DeTitoloHome;

import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

/**
 * Home object for domain model class VaIstruzione.
 * 
 * @see it.eng.myportal.entity.VaIstruzione
 * @author Rodi A.
 */
@Stateless
@LocalBean
public class VaIstruzioneHome extends AbstractVacancyEntityListHome<VaIstruzione, VaIstruzioneDTO> implements InoDTOejb<VaIstruzione>{

	@EJB
	DeTitoloHome deTitoloHome;

	public VaIstruzione findById(Integer id) {
		return findById(VaIstruzione.class, id);
	}

	public List<VaIstruzione> findProperByVacancyId(Integer vacancyId) {
		List<VaIstruzione> deVacancyIstruzioniList = entityManager
				.createNamedQuery("VaIstruzioneFindByVacancyId", VaIstruzione.class).setParameter("vaId", vacancyId)
				.getResultList();
		return deVacancyIstruzioniList;
	}

	/**
	 * Recupera i dati partendo dall'id del curriculum
	 * 
	 * @param vacancyId int
	 * @return Set<VaIstruzione>
	 */
	public Set<VaIstruzione> findByVacancyId(int vacancyId) {
		return vaDatiVacancyHome.findById(vacancyId).getVaIstruziones();
	}

	/**
	 * Converte un'Entity in un DTO.
	 * 
	 * @param entity VaIstruzione
	 * @return VaIstruzioneDTO
	 */
	@Override
	public VaIstruzioneDTO toDTO(VaIstruzione entity) {
		if (entity == null)
			return null;
		VaIstruzioneDTO dto = super.toDTO(entity);

		dto.setId(entity.getIdVaIstruzione());
		dto.setIdVaDatiVacancy(entity.getVaDatiVacancy().getIdVaDatiVacancy());
		dto.setVotazione(entity.getVotazione());
		dto.setSpecifica(entity.getSpecifica());
		dto.setOpzConseguito(entity.getOpzConseguito());
		dto.setOpzIndispensabile(entity.getOpzIndispensabile());

		// DeTitolo titolo = entity.getDeTitolo();
		// if (titolo != null) {
		// dto.setCodTitolo(entity.getDeTitolo().getCodTitolo());
		// dto.setStrTitolo(entity.getDeTitolo().getDescrizione());
		//
		// String codPadre = deTitoloHome.toDTO(titolo).getCodPadre();
		// String codTipoTitolo;
		// if (codPadre != null) {
		// codTipoTitolo = deTitoloHome.getCodTipoTitolo(codPadre);
		//
		// } else {
		// codTipoTitolo = deTitoloHome.getCodTipoTitolo(dto.getCodTitolo());
		// }
		// if (!codTipoTitolo.isEmpty()) {
		// dto.setTipoTitolo(deTitoloHome.findDTOById(codTipoTitolo));
		// }
		// }
		DeTitolo titolo = entity.getDeTitolo();
		if (titolo != null) {
			dto.setTitolo(deTitoloHome.toDTO(titolo));
		}

		return dto;
	}

	/**
	 * Genera l'Entity a partire dal DTO.
	 * 
	 * @param dto VaIstruzioneDTO
	 * @return VaIstruzione
	 */
	@Override
	public VaIstruzione fromDTO(VaIstruzioneDTO dto) {
		if (dto == null)
			return null;
		VaIstruzione entity = super.fromDTO(dto);

		entity.setVaDatiVacancy(vaDatiVacancyHome.findById(dto.getIdVaDatiVacancy()));

		// entity.setDeTitolo(deTitoloHome.findById(dto.getCodTitolo()));
		if (dto.getTitolo() != null) {
			entity.setDeTitolo(deTitoloHome.findById(dto.getTitolo().getId()));
		}
		entity.setVotazione(dto.getVotazione());
		entity.setIdVaIstruzione(dto.getId());
		entity.setSpecifica(dto.getSpecifica());
		entity.setOpzConseguito(dto.getOpzConseguito());
		entity.setOpzIndispensabile(dto.getOpzIndispensabile());

		return entity;
	}

	public void copyById(Integer pfPrincipalId, VaIstruzione vaIstruzione, VaDatiVacancy vaDatiVacancy) {
		if (vaIstruzione != null) {
			Date now = new Date();
			entityManager.detach(vaIstruzione);
			vaIstruzione.setIdVaIstruzione(null);
			vaIstruzione.setVaDatiVacancy(vaDatiVacancy);
			vaIstruzione.setDtmIns(now);
			vaIstruzione.setDtmMod(now);
			vaIstruzione.setPfPrincipalIns(pfPrincipalHome.findById(pfPrincipalId));
			vaIstruzione.setPfPrincipalMod(pfPrincipalHome.findById(pfPrincipalId));
			persist(vaIstruzione);
		} else {
			throw new EJBException(
					"Impossibile trovare l'entity corrispondente a va_istruzione associato a va_dati_vacancy con id "
							+ vaDatiVacancy.getIdVaDatiVacancy());
		}
	}
	
	@Override
	public VaIstruzione persist(VaIstruzione entity, Integer actingUser) {
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
	public void remove(VaIstruzione entity) {
		VaIstruzione entityVaIstruzione = findById(entity.getIdVaIstruzione());
		super.remove(entityVaIstruzione);
	}

	@Override
	public VaIstruzione merge(VaIstruzione in, Integer userId) {
		PfPrincipal usr = pfPrincipalHome.findById(userId);
		in.setDtmMod(new Date());
		in.setPfPrincipalMod(usr);
		return entityManager.merge(in);
	}
}

package it.eng.myportal.entity.home;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.TypedQuery;

import it.eng.myportal.dtos.VaContrattoDTO;
import it.eng.myportal.entity.PfPrincipal;
import it.eng.myportal.entity.VaContratto;
import it.eng.myportal.entity.VaDatiVacancy;
import it.eng.myportal.entity.home.decodifiche.DeContrattoHome;
import it.eng.myportal.entity.home.decodifiche.DeContrattoSilHome;

/**
 * @author Rodi A.
 */
@Stateless
@LocalBean
public class VaContrattoHome extends AbstractVacancyEntityListHome<VaContratto, VaContrattoDTO> implements InoDTOejb<VaContratto>  {

	@EJB
	DeContrattoHome deContrattoHome;

	@EJB
	DeContrattoSilHome deContrattoSilHome;
 
	@Override
	public VaContratto findById(Integer id) {
		return findById(VaContratto.class, id);
	}

	/**
	 * Recupera i dati partendo dall'id del curriculum
	 * 
	 * @param vacancyId
	 *            int
	 * @return Set<VaIstruzione>
	 */
	public Set<VaContratto> findByVacancyId(int vacancyId) {
		List<VaContratto> contrattos = vaDatiVacancyHome.findById(vacancyId).getVaContrattos();
		HashSet<VaContratto> set = new HashSet<VaContratto>();
		set.addAll(contrattos);
		return set;
	}

	public Set<VaContratto> findNonScaduteByVacancyId(int vacancyId) {
		TypedQuery<VaContratto> q = entityManager
				.createNamedQuery("findNonScaduteByIdVaDatiVacancy", VaContratto.class);
		q.setParameter("idVaDatiVacancy", vacancyId);
		List<VaContratto> contrattos = q.getResultList();

		HashSet<VaContratto> set = new HashSet<VaContratto>();
		set.addAll(contrattos);
		return set;
	}

	/**
	 * Converte un'Entity in un DTO.
	 * 
	 * @param entity
	 *            VaIstruzione
	 * @return VaIstruzioneDTO
	 */
	@Override
	public VaContrattoDTO toDTO(VaContratto entity) {
		if (entity == null)
			return null;
		VaContrattoDTO dto = super.toDTO(entity);
		dto.setId(entity.getIdVaContratto());

		if (entity.getDeContratto() != null) {
			dto.setCodice(entity.getDeContratto().getCodContratto());
			dto.setDescrizione(entity.getDeContratto().getDescrizione());
		} else if (entity.getDeContrattoSil() != null) {
			dto.setCodice(entity.getDeContrattoSil().getCodContrattoSil());
			dto.setDescrizione(entity.getDeContrattoSil().getDescrizione());
		}

		return dto;
	}

	/**
	 * Genera l'Entity a partire dal DTO.
	 * 
	 * @param dto
	 *            VaIstruzioneDTO
	 * @return VaIstruzione
	 */
	@Override
	public VaContratto fromDTO(VaContrattoDTO dto) {
		if (dto == null)
			return null;
		VaContratto entity = super.fromDTO(dto);
		entity.setVaDatiVacancy(vaDatiVacancyHome.findById(dto.getIdVaDatiVacancy()));
		entity.setIdVaContratto(dto.getId());
		if (VaDatiVacancy.OpzTipoDecodifiche.SIL.equals(entity.getVaDatiVacancy().getOpzTipoDecodifiche())) {
			entity.setDeContrattoSil(deContrattoSilHome.findById(dto.getCodice()));
		} else {
			entity.setDeContratto(deContrattoHome.findById(dto.getCodice()));
		}

		return entity;
	}

	public void copyById(Integer pfPrincipalId, VaContratto vaContratto, VaDatiVacancy vaDatiVacancy) {
		if (vaContratto != null) {
			Date now = new Date();
			entityManager.detach(vaContratto);
			vaContratto.setIdVaContratto(null);
			vaContratto.setVaDatiVacancy(vaDatiVacancy);
			vaContratto.setDtmIns(now);
			vaContratto.setDtmMod(now);
			vaContratto.setPfPrincipalIns(pfPrincipalHome.findById(pfPrincipalId));
			vaContratto.setPfPrincipalMod(pfPrincipalHome.findById(pfPrincipalId));
			persist(vaContratto);
		} else {
			throw new EJBException(
					"Impossibile trovare l'entity corrispondente a va_contratto associato a va_dati_vacancy con id "
							+ vaDatiVacancy.getIdVaDatiVacancy());
		}
	}

	public List<VaContratto> findProperByVacancyId(Integer vacancyId) {
		List<VaContratto> deQualificaSrqList = entityManager.createNamedQuery("VaContrattoFindByVacancyId", VaContratto.class)
				.setParameter("vaId", vacancyId).getResultList();

		return deQualificaSrqList;
	}

	@Override
	public VaContratto merge(VaContratto in, Integer userId) {
		PfPrincipal usr = pfPrincipalHome.findById(userId);
		in.setDtmMod(new Date());
		in.setPfPrincipalMod(usr);
		return entityManager.merge(in);
	}

	@Override
	public VaContratto persist(VaContratto entity, Integer actingUser) {
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
	public void remove(VaContratto detached) {
		VaContratto persistentInstance = findById(detached.getIdVaContratto());
		super.remove(persistentInstance);
		
	}
}

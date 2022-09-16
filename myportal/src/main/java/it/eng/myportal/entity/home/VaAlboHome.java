package it.eng.myportal.entity.home;

import it.eng.myportal.dtos.VaAlboDTO;
import it.eng.myportal.entity.PfPrincipal;
import it.eng.myportal.entity.VaAlbo;
import it.eng.myportal.entity.VaDatiVacancy;
import it.eng.myportal.entity.VaPatentino;
import it.eng.myportal.entity.home.decodifiche.DeAlboHome;
import it.eng.myportal.entity.home.decodifiche.DeAlboSilHome;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

/**
 * @author Rodi A.
 */
@Stateless
@LocalBean
public class VaAlboHome extends AbstractVacancyEntityListHome<VaAlbo, VaAlboDTO> implements InoDTOejb<VaAlbo> {

	@EJB
	DeAlboHome deAlboHome;

	@EJB
	DeAlboSilHome deAlboSilHome;

	public VaAlbo findById(Integer id) {
		return findById(VaAlbo.class, id);
	}

	/**
	 * Recupera i dati partendo dall'id del curriculum
	 * 
	 * @param vacancyId int
	 * @return Set<VaIstruzione>
	 */
	// TODO rifattorizzare il metodo, deve ritornare List<E>
	public Set<VaAlbo> findByVacancyId(int vacancyId) {
		Set<VaAlbo> vaAlbos = new HashSet<VaAlbo>();
		List<VaAlbo> vaAlbosList = vaDatiVacancyHome.findById(vacancyId).getVaAlbos();
		vaAlbos.addAll(vaAlbosList);
		return vaAlbos;
	}

	/**
	 * Converte un'Entity in un DTO.
	 * 
	 * @param entity VaIstruzione
	 * @return VaIstruzioneDTO
	 */
	@Override
	public VaAlboDTO toDTO(VaAlbo entity) {
		if (entity == null)
			return null;
		VaAlboDTO dto = super.toDTO(entity);
		dto.setId(entity.getIdVaAlbo());
		dto.setOpzIndispensabile(entity.getOpzIndispensabile());
		if (entity.getDeAlbo() != null) {
			dto.setCodice(entity.getDeAlbo().getCodAlbo());
			dto.setDescrizione(entity.getDeAlbo().getDescrizione());
		} else if (entity.getDeAlboSil() != null) {
			dto.setCodice(entity.getDeAlboSil().getCodAlboSil());
			dto.setDescrizione(entity.getDeAlboSil().getDescrizione());
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
	public VaAlbo fromDTO(VaAlboDTO dto) {
		if (dto == null)
			return null;
		VaAlbo entity = super.fromDTO(dto);
		entity.setVaDatiVacancy(vaDatiVacancyHome.findById(dto.getIdVaDatiVacancy()));
		entity.setIdVaAlbo(dto.getId());
		entity.setOpzIndispensabile(dto.getOpzIndispensabile());
		if (VaDatiVacancy.OpzTipoDecodifiche.SIL.equals(entity.getVaDatiVacancy().getOpzTipoDecodifiche())) {
			entity.setDeAlboSil(deAlboSilHome.findById(dto.getCodice()));
		} else {
			entity.setDeAlbo(deAlboHome.findById(dto.getCodice()));
		}

		return entity;
	}

	public void copyById(Integer pfPrincipalId, VaAlbo vaAlbo, VaDatiVacancy vaDatiVacancy) {
		if (vaAlbo != null) {
			Date now = new Date();
			entityManager.detach(vaAlbo);
			vaAlbo.setIdVaAlbo(null);
			vaAlbo.setVaDatiVacancy(vaDatiVacancy);
			vaAlbo.setDtmIns(now);
			vaAlbo.setDtmMod(now);
			vaAlbo.setPfPrincipalIns(pfPrincipalHome.findById(pfPrincipalId));
			vaAlbo.setPfPrincipalMod(pfPrincipalHome.findById(pfPrincipalId));
			persist(vaAlbo);
		} else {
			throw new EJBException(
					"Impossibile trovare l'entity corrispondente a va_albo associato a va_dati_vacancy con id "
							+ vaDatiVacancy.getIdVaDatiVacancy());
		}

	}

	public List<VaAlbo> findProperByVacancyId(Integer vacancyId) {
		List<VaAlbo> deAlboList = entityManager.createNamedQuery("VaAlboFindByVaId", VaAlbo.class)
				.setHint("org.hibernate.cacheable", true).setParameter("vaId", vacancyId).getResultList();

		return deAlboList;
	}

	@Override
	public VaAlbo merge(VaAlbo entity, Integer actingUser) {
		PfPrincipal usr = pfPrincipalHome.findById(actingUser);
		entity.setDtmMod(new Date());
		entity.setPfPrincipalMod(usr);
		return entityManager.merge(entity);
	}

	@Override
	public VaAlbo persist(VaAlbo entity, Integer actingUser) {
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
	public void remove(VaAlbo detached) {
		VaAlbo persistentInstance = findById(detached.getIdVaAlbo());
		super.remove(persistentInstance);
	}
}

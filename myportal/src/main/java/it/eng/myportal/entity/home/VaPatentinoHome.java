package it.eng.myportal.entity.home;

import it.eng.myportal.dtos.VaPatentinoDTO;
import it.eng.myportal.entity.PfPrincipal;
import it.eng.myportal.entity.VaDatiVacancy;
import it.eng.myportal.entity.VaPatente;
import it.eng.myportal.entity.VaPatentino;
import it.eng.myportal.entity.home.decodifiche.DePatentinoHome;
import it.eng.myportal.entity.home.decodifiche.DePatentinoSilHome;

import java.util.Date;
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
public class VaPatentinoHome extends AbstractVacancyEntityListHome<VaPatentino, VaPatentinoDTO>
		implements InoDTOejb<VaPatentino> {

	@EJB
	DePatentinoHome dePatentinoHome;

	@EJB
	DePatentinoSilHome dePatentinoSilHome;

	public VaPatentino findById(Integer id) {
		return findById(VaPatentino.class, id);
	}

	/**
	 * Recupera i dati partendo dall'id del curriculum
	 * 
	 * @param vacancyId int
	 * @return Set<VaIstruzione>
	 */
	public Set<VaPatentino> findByVacancyId(int vacancyId) {
		return vaDatiVacancyHome.findById(vacancyId).getVaPatentinos();
	}

	/**
	 * Converte un'Entity in un DTO.
	 * 
	 * @param entity VaIstruzione
	 * @return VaIstruzioneDTO
	 */
	@Override
	public VaPatentinoDTO toDTO(VaPatentino entity) {
		if (entity == null)
			return null;
		VaPatentinoDTO dto = super.toDTO(entity);
		dto.setId(entity.getIdVaPatentino());
		dto.setOpzIndispensabile(entity.getOpzIndispensabile());

		if (entity.getDePatentino() != null) {
			dto.setCodice(entity.getDePatentino().getCodPatentino());
			dto.setDescrizione(entity.getDePatentino().getDescrizione());
		} else if (entity.getDePatentinoSil() != null) {
			dto.setCodice(entity.getDePatentinoSil().getCodPatentinoSil());
			dto.setDescrizione(entity.getDePatentinoSil().getDescrizione());
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
	public VaPatentino fromDTO(VaPatentinoDTO dto) {
		if (dto == null)
			return null;
		VaPatentino entity = super.fromDTO(dto);
		entity.setVaDatiVacancy(vaDatiVacancyHome.findById(dto.getIdVaDatiVacancy()));
		entity.setIdVaPatentino(dto.getId());
		entity.setOpzIndispensabile(dto.getOpzIndispensabile());

		if (VaDatiVacancy.OpzTipoDecodifiche.SIL.equals(entity.getVaDatiVacancy().getOpzTipoDecodifiche())) {
			entity.setDePatentinoSil(dePatentinoSilHome.findById(dto.getCodice()));
		} else {
			entity.setDePatentino(dePatentinoHome.findById(dto.getCodice()));
		}

		return entity;
	}

	public void copyById(Integer pfPrincipalId, VaPatentino vaPatentino, VaDatiVacancy vaDatiVacancy) {
		if (vaPatentino != null) {
			Date now = new Date();
			entityManager.detach(vaPatentino);
			vaPatentino.setIdVaPatentino(null);
			vaPatentino.setVaDatiVacancy(vaDatiVacancy);
			vaPatentino.setDtmIns(now);
			vaPatentino.setDtmMod(now);
			vaPatentino.setPfPrincipalIns(pfPrincipalHome.findById(pfPrincipalId));
			vaPatentino.setPfPrincipalMod(pfPrincipalHome.findById(pfPrincipalId));
			persist(vaPatentino);
		} else {
			throw new EJBException(
					"Impossibile trovare l'entity corrispondente a va_patentino associato a va_dati_vacancy con id "
							+ vaDatiVacancy.getIdVaDatiVacancy());
		}
	}

	public List<VaPatentino> findProperByVacancyId(Integer vacancyId) {
		List<VaPatentino> dePatentinoList = entityManager.createNamedQuery("VaPatentinoFindByVaId", VaPatentino.class)
				.setHint("org.hibernate.cacheable", true).setParameter("vaId", vacancyId).getResultList();

		return dePatentinoList;
	}

	@Override
	public VaPatentino merge(VaPatentino entity, Integer actingUser) {
		PfPrincipal usr = pfPrincipalHome.findById(actingUser);
		entity.setDtmMod(new Date());
		entity.setPfPrincipalMod(usr);
		return entityManager.merge(entity);
	}

	@Override
	public VaPatentino persist(VaPatentino entity, Integer actingUser) {
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
	public void remove(VaPatentino detached) {
		VaPatentino persistentInstance = findById(detached.getIdVaPatentino());
		super.remove(persistentInstance);
	}
}

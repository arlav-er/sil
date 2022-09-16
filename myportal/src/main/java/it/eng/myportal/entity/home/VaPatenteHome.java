package it.eng.myportal.entity.home;

import it.eng.myportal.dtos.VaPatenteDTO;
import it.eng.myportal.entity.CvPatente;
import it.eng.myportal.entity.PfPrincipal;
import it.eng.myportal.entity.VaDatiVacancy;
import it.eng.myportal.entity.VaPatente;
import it.eng.myportal.entity.home.decodifiche.DePatenteHome;
import it.eng.myportal.entity.home.decodifiche.DePatenteSilHome;

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
public class VaPatenteHome extends AbstractVacancyEntityListHome<VaPatente, VaPatenteDTO>
		implements InoDTOejb<VaPatente> {

	@EJB
	DePatenteHome dePatenteHome;

	@EJB
	DePatenteSilHome dePatenteSilHome;

	public VaPatente findById(Integer id) {
		return findById(VaPatente.class, id);
	}

	/**
	 * Recupera i dati partendo dall'id del curriculum
	 * 
	 * @param vacancyId int
	 * @return Set<VaIstruzione>
	 */
	public Set<VaPatente> findByVacancyId(int vacancyId) {
		return vaDatiVacancyHome.findById(vacancyId).getVaPatentes();
	}


	
	public List<VaPatente> findProperByVacancyId(Integer vacancyId) {
		List<VaPatente> dePatentiList = entityManager.createNamedQuery("VaPatenteFindByVaId", VaPatente.class)
				.setHint("org.hibernate.cacheable", true).setParameter("vaId", vacancyId).getResultList();

		return dePatentiList;
	}
	

	/**
	 * Converte un'Entity in un DTO.
	 * 
	 * @param entity VaIstruzione
	 * @return VaIstruzioneDTO
	 */
	@Override
	public VaPatenteDTO toDTO(VaPatente entity) {
		if (entity == null)
			return null;
		VaPatenteDTO dto = super.toDTO(entity);
		dto.setId(entity.getIdVaPatente());
		dto.setOpzIndispensabile(entity.getOpzIndispensabile());

		if (entity.getDePatente() != null) {
			dto.setCodice(entity.getDePatente().getCodPatente());
			dto.setDescrizione(entity.getDePatente().getDescrizione());
		} else if (entity.getDePatenteSil() != null) {
			dto.setCodice(entity.getDePatenteSil().getCodPatenteSil());
			dto.setDescrizione(entity.getDePatenteSil().getDescrizione());
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
	public VaPatente fromDTO(VaPatenteDTO dto) {
		if (dto == null)
			return null;
		VaPatente entity = super.fromDTO(dto);
		entity.setVaDatiVacancy(vaDatiVacancyHome.findById(dto.getIdVaDatiVacancy()));
		entity.setIdVaPatente(dto.getId());
		entity.setOpzIndispensabile(dto.getOpzIndispensabile());

		if (VaDatiVacancy.OpzTipoDecodifiche.SIL.equals(entity.getVaDatiVacancy().getOpzTipoDecodifiche())) {
			entity.setDePatenteSil(dePatenteSilHome.findById(dto.getCodice()));
		} else {
			entity.setDePatente(dePatenteHome.findById(dto.getCodice()));
		}

		return entity;
	}

	public void copyById(Integer pfPrincipalId, VaPatente vaPatente, VaDatiVacancy vaDatiVacancy) {
		if (vaPatente != null) {
			Date now = new Date();
			entityManager.detach(vaPatente);
			vaPatente.setIdVaPatente(null);
			vaPatente.setVaDatiVacancy(vaDatiVacancy);
			vaPatente.setDtmIns(now);
			vaPatente.setDtmMod(now);
			vaPatente.setPfPrincipalIns(pfPrincipalHome.findById(pfPrincipalId));
			vaPatente.setPfPrincipalMod(pfPrincipalHome.findById(pfPrincipalId));
			persist(vaPatente);
		} else {
			throw new EJBException(
					"Impossibile trovare l'entity corrispondente a va_patente associato a va_dati_vacancy con id "
							+ vaDatiVacancy.getIdVaDatiVacancy());
		}
	}

	@Override
	public VaPatente merge(VaPatente entity, Integer actingUser) {
		PfPrincipal usr = pfPrincipalHome.findById(actingUser);
		entity.setDtmMod(new Date());
		entity.setPfPrincipalMod(usr);
		return entityManager.merge(entity);
	}

	@Override
	public VaPatente persist(VaPatente entity, Integer actingUser) {
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
	public void remove(VaPatente detached) {
		VaPatente persistentInstance = findById(detached.getIdVaPatente());
		super.remove(persistentInstance);
	}
}

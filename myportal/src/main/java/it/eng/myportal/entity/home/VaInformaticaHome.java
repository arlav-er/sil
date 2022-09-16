package it.eng.myportal.entity.home;

import java.util.Date;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;

// Generated 1-set-2011 12.32.46 by Hibernate Tools 3.4.0.CR1

import it.eng.myportal.dtos.VaInformaticaDTO;
import it.eng.myportal.entity.PfPrincipal;
import it.eng.myportal.entity.VaDatiVacancy;
import it.eng.myportal.entity.VaInformatica;

/**
 * Home object for domain model class VaInformatica.
 * 
 * @see it.eng.myportal.entity.VaInformatica
 * @author Rodi A.
 */
@Stateless
@LocalBean
public class VaInformaticaHome extends
		AbstractVacancyEntityHome<VaInformatica, VaInformaticaDTO> implements InoDTOejb<VaInformatica>{

	public VaInformatica findById(Integer id) {
		return findById(VaInformatica.class, id);
	}

	@Override
	public VaInformatica findByVacancyId(int vacancyId) {
		return vaDatiVacancyHome.findById(vacancyId).getVaInformatica();
	}

	@Override
	public VaInformaticaDTO toDTO(VaInformatica entity) {
		if (entity == null) return null;
		VaInformaticaDTO dto = super.toDTO(entity);
		
			dto.setId(entity.getIdVaDatiVacancy());
			dto.setDescrizione(entity.getConoscenzaInfo());
			dto.setIdVaDatiVacancy(entity.getIdVaDatiVacancy());
		
		return dto;
	}

	@Override
	public VaInformatica fromDTO(VaInformaticaDTO dto) {
		if (dto == null) return null;
		VaInformatica entity = super.fromDTO(dto);
		
			entity.setIdVaDatiVacancy(dto.getIdVaDatiVacancy());
			entity.setConoscenzaInfo(dto.getDescrizione());
		
			entity.setVaDatiVacancy(vaDatiVacancyHome.findById(dto
					.getIdVaDatiVacancy()));
		
		
		return entity;
	}

	/**
	 * Copia vaInformatica e sostituisce il parametro no la sua copia.
	 * 
	 * @param pfPrincipalId
	 * @param vaInformatica
	 * @param vaDatiVacancy
	 */
	public void copyById(Integer pfPrincipalId, VaInformatica vaInformatica,
			VaDatiVacancy vaDatiVacancy) {
		if (vaInformatica == null) {
			String msg = "Impossibile trovare l'entity corrispondente a va_informatica con id "
					+ vaDatiVacancy.getIdVaDatiVacancy();
			log.error(msg);
			return;
		}
		Date now = new Date();
		entityManager.detach(vaInformatica);
		vaInformatica.setVaDatiVacancy(vaDatiVacancy);
		vaInformatica.setDtmIns(now);
		vaInformatica.setDtmMod(now);
		vaInformatica.setPfPrincipalIns(pfPrincipalHome
				.findById(pfPrincipalId));
		vaInformatica.setPfPrincipalMod(pfPrincipalHome
				.findById(pfPrincipalId));
		persist(vaInformatica);

	}
	
	@Override
	public void remove(VaInformatica persistentInstance) {
		VaInformatica entityVaIstruzione = findById(persistentInstance.getIdVaDatiVacancy());
		super.remove(entityVaIstruzione);
	}

	@Override
	public VaInformatica merge(VaInformatica in, Integer actingUser) {
		PfPrincipal usr = pfPrincipalHome.findById(actingUser);
		in.setDtmMod(new Date());
		in.setPfPrincipalMod(usr);
		return entityManager.merge(in);
	}

	@Override
	public VaInformatica persist(VaInformatica entity, Integer actingUser) {
		PfPrincipal usr = pfPrincipalHome.findById(actingUser);
		entity.setDtmIns(new Date());
		entity.setDtmMod(new Date());
		entity.setPfPrincipalIns(usr);
		entity.setPfPrincipalMod(usr);
		entityManager.persist(entity);
		entityManager.flush();
		return entity;
	}
}

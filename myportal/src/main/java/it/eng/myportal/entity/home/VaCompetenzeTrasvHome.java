package it.eng.myportal.entity.home;

import java.util.Date;

import javax.ejb.EJBException;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

// Generated 1-set-2011 12.32.46 by Hibernate Tools 3.4.0.CR1

import it.eng.myportal.dtos.VaCompetenzeTrasvDTO;
import it.eng.myportal.entity.PfPrincipal;
import it.eng.myportal.entity.VaCompetenzeTrasv;
import it.eng.myportal.entity.VaDatiVacancy;

/**
 * Home object for domain model class VaCompetenzeTrasv.
 * 
 * @see it.eng.myportal.entity.VaCompetenzeTrasv
 * @author Rodi A.
 */
@Stateless
@LocalBean
public class VaCompetenzeTrasvHome extends AbstractVacancyEntityHome<VaCompetenzeTrasv, VaCompetenzeTrasvDTO> implements InoDTOejb<VaCompetenzeTrasv> {

	public VaCompetenzeTrasv findById(Integer id) {
		return findById(VaCompetenzeTrasv.class, id);
	}

	@Override
	public VaCompetenzeTrasv findByVacancyId(int vacancyId) {
		return vaDatiVacancyHome.findById(vacancyId).getVaCompetenzeTrasv();
	}

	@Override
	public VaCompetenzeTrasvDTO toDTO(VaCompetenzeTrasv entity) {
		if (entity == null)
			return null;
		VaCompetenzeTrasvDTO dto = super.toDTO(entity);
		dto.setId(entity.getIdVaDatiVacancy());
		dto.setCapCompOrganizzative(entity.getCapCompOrganizzative());
		dto.setCapCompRelInterpersonali(entity.getCapCompRelInterpersonali());
		dto.setCapCompTecniche(entity.getCapCompTecniche());
		dto.setCapCompSintesiCl(entity.getCapCompSintesiCl());
		dto.setIdVaDatiVacancy(entity.getVaDatiVacancy().getIdVaDatiVacancy());

		return dto;
	}

	@Override
	public VaCompetenzeTrasv fromDTO(VaCompetenzeTrasvDTO dto) {
		if (dto == null)
			return null;
		VaCompetenzeTrasv entity = super.fromDTO(dto);
		entity.setIdVaDatiVacancy(dto.getId());
		entity.setCapCompOrganizzative(dto.getCapCompOrganizzative());
		entity.setCapCompRelInterpersonali(dto.getCapCompRelInterpersonali());
		entity.setCapCompTecniche(dto.getCapCompTecniche());
		entity.setCapCompSintesiCl(dto.getCapCompSintesiCl());
		entity.setVaDatiVacancy(vaDatiVacancyHome.findById(dto.getIdVaDatiVacancy()));

		return entity;
	}

	public void copyById(Integer pfPrincipalId, VaCompetenzeTrasv vaCompetenzeTrasv, VaDatiVacancy vaDatiVacancy) {
		if (vaCompetenzeTrasv != null) {
			Date now = new Date();
			entityManager.detach(vaCompetenzeTrasv);
			vaCompetenzeTrasv.setVaDatiVacancy(vaDatiVacancy);
			vaCompetenzeTrasv.setDtmIns(now);
			vaCompetenzeTrasv.setDtmMod(now);
			vaCompetenzeTrasv.setPfPrincipalIns(pfPrincipalHome.findById(pfPrincipalId));
			vaCompetenzeTrasv.setPfPrincipalMod(pfPrincipalHome.findById(pfPrincipalId));
			persist(vaCompetenzeTrasv);
		} else {
			throw new EJBException("Impossibile trovare l'entity corrispondente a va_competenze_trasv con id "
					+ vaDatiVacancy.getIdVaDatiVacancy());
		}
	}

	@Override
	public VaCompetenzeTrasv merge(VaCompetenzeTrasv in, Integer userId) {
		PfPrincipal usr = pfPrincipalHome.findById(userId);
		in.setDtmMod(new Date());
		in.setPfPrincipalMod(usr);
		return entityManager.merge(in);
	}

	@Override
	public VaCompetenzeTrasv persist(VaCompetenzeTrasv entity, Integer actingUser) {
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

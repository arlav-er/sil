package it.eng.myportal.entity.home;

import it.eng.myportal.dtos.IHasPrimaryKey;
import it.eng.myportal.dtos.IVacancySection;
import it.eng.myportal.entity.IEntity;
import it.eng.myportal.entity.VaDatiVacancy;
import it.eng.myportal.entity.home.local.IVacancyEntityListHome;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.ejb.EJB;



/**
 * Classe Home astratta per le Entity che sono relative
 * alle sezioni della vacancy.
 * Fornisce un riferimento all'Home delle tabelle CvDatiAzienda e CvDatiVacancy
 * 
 * 
 * @author Dangelo E.
 *
 * @param <Entity> Classe dell'Entity al quale viene associato l'Home.
 */
public abstract class AbstractVacancyEntityListHome<Entity extends IEntity,DTO extends IVacancySection & IHasPrimaryKey<Integer>>  
	extends AbstractUpdatableHome<Entity,DTO> 
	implements IVacancyEntityListHome<DTO> {
	
	@EJB
	protected VaDatiVacancyHome vaDatiVacancyHome;
	
	// TODO rifattorizzare il metodo, deve ritornare List<E>
	public abstract Set<Entity> findByVacancyId(int vacancyId);
	
	public List<DTO> findDTOByVacancyId(int vacancyId) {		
		final Set<Entity> set = findByVacancyId(vacancyId);
		final List<DTO> ret = new ArrayList<DTO>();
		for (Entity entity : set) {
			final DTO dto = toDTO(entity);
			ret.add(dto);
		}
		return ret;						
	}
	
	public abstract void copyById(Integer pfPrincipalId, Entity entity,	VaDatiVacancy vaDatiVacancy);
	
	@Override
	public void removeById(Integer id, Integer idPfPrincipalMod)
	{
		DTO dto = findDTOById(id);
		Integer idVa = dto.getIdVaDatiVacancy();
		VaDatiVacancy vacancy = vaDatiVacancyHome.findById(idVa);
		Date now = new Date();
		vacancy.setDtmMod(now);
		vacancy.setPfPrincipalMod(pfPrincipalHome.findById(idPfPrincipalMod));
		vaDatiVacancyHome.merge(vacancy);
		super.removeById(id,idPfPrincipalMod);
	}
	
	@Override
	public DTO persistDTO(DTO data, Integer idPrincipalIns) {
		DTO dto = super.persistDTO(data, idPrincipalIns);
		Integer idVaDatiVacancy = dto.getIdVaDatiVacancy();
		Date dataMod = dto.getDtmMod();
		VaDatiVacancy vacancy = vaDatiVacancyHome.findById(idVaDatiVacancy);
		vacancy.setDtmMod(dataMod);
		vacancy.setPfPrincipalMod(pfPrincipalHome.findById(idPrincipalIns));
		vaDatiVacancyHome.merge(vacancy);
		return dto;
	}
	
	@Override
	public DTO mergeDTO(DTO data, Integer idPrincipalMod) {
		DTO dto = super.mergeDTO(data, idPrincipalMod);
		Integer idVaDatiVacancy = dto.getIdVaDatiVacancy();
		Date dataMod = dto.getDtmMod();
		VaDatiVacancy vacancy = vaDatiVacancyHome.findById(idVaDatiVacancy);
		vacancy.setDtmMod(dataMod);
		vacancy.setPfPrincipalMod(pfPrincipalHome.findById(idPrincipalMod));
		vaDatiVacancyHome.merge(vacancy);
		return dto;
	}
	
}

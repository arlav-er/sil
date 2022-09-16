package it.eng.myportal.entity.home;

import it.eng.myportal.dtos.IHasPrimaryKey;
import it.eng.myportal.dtos.IVacancySection;
import it.eng.myportal.entity.IEntity;
import it.eng.myportal.entity.VaDatiVacancy;
import it.eng.myportal.entity.home.local.IVacancyEntityHome;

import java.util.Date;

import javax.ejb.EJB;

/**
 * Classe Home astratta per le Entity che sono relative alle sezioni della
 * vacancy. Fornisce un riferimento all'Home delle tabelle CvDatiAzienda e
 * CvDatiVacancy
 * 
 * 
 * @author Dangelo E.
 * 
 * @param <Entity>
 *            Classe dell'Entity al quale viene associato l'Home.
 */
public abstract class AbstractVacancyEntityHome<Entity extends IEntity, DTO extends IVacancySection & IHasPrimaryKey<Integer>> extends
		AbstractUpdatableHome<Entity, DTO> implements IVacancyEntityHome<DTO> {

	@EJB
	protected VaDatiVacancyHome vaDatiVacancyHome;

	protected abstract Entity findByVacancyId(int vacancyId);

	public DTO findDTOByVacancyId(int vacancyId) {
		Entity entity = findByVacancyId(vacancyId);
		DTO ret = toDTO(entity);
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
		Integer idVaDatiVacancy = data.getIdVaDatiVacancy();
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

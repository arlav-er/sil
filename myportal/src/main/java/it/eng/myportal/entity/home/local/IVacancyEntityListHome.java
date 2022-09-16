package it.eng.myportal.entity.home.local;

import it.eng.myportal.dtos.IVacancySection;

import java.util.List;

import javax.ejb.Local;

@Local
public interface IVacancyEntityListHome<DTO extends IVacancySection> extends IDTOHome<DTO> {

	/**
	 * Cerca una lista di elementi, collegati alla vacancy, a partire dall'id della vacancy.
	 * @param vacancyId
	 * @return
	 */
	public List<DTO> findDTOByVacancyId(int vacancyId);
}

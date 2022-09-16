package it.eng.myportal.entity.home.local;

import it.eng.myportal.dtos.IVacancySection;

import javax.ejb.Local;

@Local
public interface IVacancyEntityHome<DTO extends IVacancySection> extends IDTOHome<DTO> {

	/**
	 * Cerca l'elemento, collegato alla vacancy, a partire dall'id della vacancy.
	 * @param vacancyId
	 * @return
	 */
	public DTO findDTOByVacancyId(int vacancyId);

	public void removeById(Integer deleteId, Integer idPfPrincipalMod);
	
	
}
     
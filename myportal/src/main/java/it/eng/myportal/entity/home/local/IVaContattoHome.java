package it.eng.myportal.entity.home.local;

import it.eng.myportal.dtos.VaContattoDTO;
import it.eng.myportal.entity.VaContatto;
import it.eng.myportal.entity.VaDatiVacancy;

import javax.ejb.Local;

@Local
public interface IVaContattoHome extends IVacancyEntityHome<VaContattoDTO> {

	VaContatto findById(Integer id);
	
	/**
	 * 
	 * @param vacancyId
	 *            id della vacancy di riferimento
	 * @return DTO del contatto secondario corrispondente alla vacancy
	 */
	VaContattoDTO findAltDTOByVacancyId(Integer vacancyId);

	void persist(VaContatto entity);

	/**
	 * Copia una sezione 'Contatto' e
	 * la collega alla vacancy.
	 * @param pfPrincipalId
	 * @param vaContatto
	 * @param vaDatiVacancy vacancy a cui collegare il nuovo contatto
	 */
	void copyById(Integer pfPrincipalId, VaContatto vaContatto,
			VaDatiVacancy vaDatiVacancy);
	
	void remove(VaContatto entity);
	
}

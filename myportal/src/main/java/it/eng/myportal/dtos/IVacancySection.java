package it.eng.myportal.dtos;


/**
 * Interfaccia che deve implementare il DTO che 
 * rappresenta i dati di una sezione della vacancy.
 * 
 * @author Rodi A.
 *
 */
public interface IVacancySection extends IUpdatable {
		
	/**
	 * Restituisce l'ID della vacancy alla quale è collegata la sezione.
	 * @return ID della vacancy
	 */
	Integer getIdVaDatiVacancy();
	
	/**
	 * Imposta l'ID della vacancy alla quale è collegata la sezione.
	 * @param idVaDatiVacancy ID della vacancy
	 */
	void setIdVaDatiVacancy(Integer idVaDatiVacancy);
	
}

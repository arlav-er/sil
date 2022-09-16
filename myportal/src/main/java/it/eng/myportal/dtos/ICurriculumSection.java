package it.eng.myportal.dtos;


/**
 * Interfaccia che deve implementare il DTO che 
 * rappresenta i dati di una sezione del curriculum vitae.
 * 
 * @author Rodi A.
 *
 */
public interface ICurriculumSection extends IUpdatable {
		
	/**
	 * Restituisce l'ID del curriculum al quale è collegata la sezione.
	 * @return ID del cv
	 */
	Integer getIdCvDatiPersonali();
	
	/**
	 * Imposta l'ID del curriculum al quale è collegata la sezione.
	 * @param idCvDatiPersonali ID del cv
	 */
	void setIdCvDatiPersonali(Integer idCvDatiPersonali);
	
}

package it.eng.myportal.entity.home.local;

import it.eng.myportal.dtos.ICurriculumSection;

import javax.ejb.Local;

@Local
public interface ICurriculumEntityHome<DTO extends ICurriculumSection> extends IDTOHome<DTO> {

	/**
	 * Cerca l'elemento, collegato al cv, a partire dall'id del cv.
	 * @param curriculumId
	 * @return
	 */
	public DTO findDTOByCurriculumId(int curriculumId);
	
	
	/**
	 * Cancella l'istanza dell'Entity da DB partendo dalla PrimaryKey
	 * 
	 * @param id
	 *            PKType nel caso avvengano errori durante la rimozione
	 * @param idPfPrincipalMod id del principal che esegue la modifica
	 */
	public void removeById(Integer id, Integer idPfPrincipalMod);
}

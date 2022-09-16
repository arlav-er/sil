package it.eng.myportal.entity.home.local;

import it.eng.myportal.dtos.ICurriculumSection;

import java.util.List;

import javax.ejb.Local;

@Local
public interface ICurriculumEntityListHome<DTO extends ICurriculumSection> extends IDTOHome<DTO> {

	/**
	 * Cerca una lista di elementi, collegati al cv, a partire dall'id del cv.
	 * @param curriculumId
	 * @return
	 */
	public List<DTO> findDTOByCurriculumId(int curriculumId);
}

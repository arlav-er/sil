package it.eng.myportal.entity.home;

import it.eng.myportal.dtos.ICurriculumSection;
import it.eng.myportal.dtos.IHasPrimaryKey;
import it.eng.myportal.entity.CvDatiPersonali;
import it.eng.myportal.entity.IEntity;
import it.eng.myportal.entity.home.local.ICurriculumEntityHome;

import java.util.Date;

import javax.ejb.EJB;

/**
 * Classe Home astratta per le Entity che sono relative alle sezioni del
 * curriculum. Fornisce un riferimento all'Home della tabella CvatiPersonali
 * 
 * 
 * @author Rodi A.
 * 
 * @param <Entity>
 *            Classe dell'Entity al quale viene associato l'Home.
 */
public abstract class AbstractCurriculumEntityHome<Entity extends IEntity, DTO extends ICurriculumSection & IHasPrimaryKey<Integer>>
		extends AbstractUpdatableHome<Entity, DTO> implements ICurriculumEntityHome<DTO> {

	@EJB
	protected CvDatiPersonaliHome cvDatiPersonaliHome;

	protected abstract Entity findByCurriculumId(int curriculumId);

	public DTO findDTOByCurriculumId(int curriculumId) {
			Entity entity = findByCurriculumId(curriculumId);
			DTO ret = toDTO(entity);
			return ret;
	}

	public abstract void copyById(Integer pfPrincipalId, Entity entity, CvDatiPersonali cvDatiPersonali);

	@Override
	public void removeById(Integer id, Integer idPfPrincipalMod) {
		DTO dto = findDTOById(id);
		Integer idCv = dto.getIdCvDatiPersonali();
		CvDatiPersonali curriculum = cvDatiPersonaliHome.findById(idCv);
		Date now = new Date();
		curriculum.setDtmMod(now);
		curriculum.setPfPrincipalMod(pfPrincipalHome.findById(idPfPrincipalMod));
		cvDatiPersonaliHome.merge(curriculum);

		super.removeById(id, idPfPrincipalMod);
	}

	@Override
	public DTO persistDTO(DTO data, Integer idPrincipalIns) {
		DTO dto = super.persistDTO(data, idPrincipalIns);
		Integer idCvDatiPersonali = dto.getIdCvDatiPersonali();
		Date dataMod = dto.getDtmMod();
		CvDatiPersonali curriculum = cvDatiPersonaliHome.findById(idCvDatiPersonali);
		curriculum.setDtmMod(dataMod);
		curriculum.setPfPrincipalMod(pfPrincipalHome.findById(idPrincipalIns));
		cvDatiPersonaliHome.merge(curriculum);

		return dto;
	}

	@Override
	public DTO mergeDTO(DTO data, Integer idPrincipalMod) {
		DTO dto = super.mergeDTO(data, idPrincipalMod);
		Integer idCvDatiPersonali = dto.getIdCvDatiPersonali();
		Date dataMod = dto.getDtmMod();
		CvDatiPersonali curriculum = cvDatiPersonaliHome.findById(idCvDatiPersonali);
		curriculum.setDtmMod(dataMod);
		curriculum.setPfPrincipalMod(pfPrincipalHome.findById(idPrincipalMod));
		cvDatiPersonaliHome.merge(curriculum);

		return dto;
	}
}

package it.eng.myportal.entity.home;

import it.eng.myportal.dtos.IHasPrimaryKey;
import it.eng.myportal.dtos.IUpdatable;
import it.eng.myportal.entity.IEntity;
import it.eng.myportal.exception.MyPortalException;

import java.util.Date;

import javax.ejb.EJB;

/**
 * Classe Home astratta che contiene una serie di metodi generici e comuni a tutte le Entity che hanno come chiave
 * primaria un Integer e sono aggiornabili. Gestisce automaticamenti il salvataggio dei campi dtmIns e dtmMod
 * 
 * @author Rodi A.
 * 
 * @param <Entity>
 *            Classe dell'Entity al quale viene associato l'Home.
 * @param <DTO>
 *            DTO associato all'entity
 * 
 * @pegoraro: quando riuscirete anche voi a dichiarare una classe così, potrete chiedere l'aumento.
 */
public abstract class AbstractUpdatableHome<Entity extends IEntity, DTO extends IHasPrimaryKey<Integer> & IUpdatable>
		extends AbstractHome<Entity, DTO, Integer> {
	@EJB
	protected PfPrincipalHome pfPrincipalHome;

	@Override
	public DTO persistDTO(DTO data, Integer idPrincipalIns) {
		Date now = new Date();
		data.setDtmIns(now);
		data.setDtmMod(now);
		data.setIdPrincipalIns(idPrincipalIns);
		data.setIdPrincipalMod(idPrincipalIns);
		return super.persistDTO(data, idPrincipalIns);
	}

	@Override
	public DTO mergeDTO(DTO data, Integer idPrincipalMod) {
		data.setDtmMod(new Date());
		data.setIdPrincipalMod(idPrincipalMod);
		return super.mergeDTO(data, idPrincipalMod);
	}

	/**
	 * Inizializza il DTO e ne popola i campi dtmIns, dtmMod, idPrincipaIns, idPrincipalMod
	 * 
	 * @param entity
	 * @param dto
	 */
	@Override
	@SuppressWarnings("unchecked")
	public DTO toDTO(Entity entity) {
		if (entity == null)
			return null;
		Class<DTO> dtoClass;
		try {
			dtoClass = (Class<DTO>) Class.forName(DTO_PACKAGE_PREFIX + getEntityName() + DTO_SUFFIX);
			DTO dto = dtoClass.getConstructor((Class<?>[]) null).newInstance((Object[]) null);

			popolaDTO(dto, entity);

			return dto;
		} catch (Exception e) {
			throw new MyPortalException("error.retrieve_data", e);
		}
	}

	protected void popolaDTO(IUpdatable dto, Entity entity) {
		dto.setDtmIns(entity.getDtmIns());
		dto.setDtmMod(entity.getDtmMod());
		dto.setIdPrincipalIns(entity.getPfPrincipalIns().getIdPfPrincipal());
		dto.setIdPrincipalMod(entity.getPfPrincipalMod().getIdPfPrincipal());
	}

	/**
	 * Inizializza l'Entity e ne popola i campi dtmIns, dtmMod, pfPrincipalIns, pfPrincipalMod
	 * 
	 * @param entity
	 * @param dto
	 */
	@Override
	@SuppressWarnings("unchecked")
	public Entity fromDTO(DTO dto) {
		if (dto == null)
			return null;
		Class<Entity> entityClass;
		try {
			Entity entity = null;
			if (dto.getId() != null) {
				entity = findById(dto.getId());
			}
			if (entity == null) {
				entityClass = (Class<Entity>) Class.forName(ENTITY_PACKAGE_PREFIX + getEntityName());
				entity = entityClass.getConstructor((Class<?>[]) null).newInstance((Object[]) null);
			}
			entity.setDtmIns(dto.getDtmIns());
			entity.setDtmMod(dto.getDtmMod());
			entity.setPfPrincipalIns(pfPrincipalHome.findById(dto.getIdPrincipalIns()));
			entity.setPfPrincipalMod(pfPrincipalHome.findById(dto.getIdPrincipalMod()));

			return entity;
		} catch (Exception e) {
			throw new MyPortalException("error.retrieve_data");
		}
	}

}

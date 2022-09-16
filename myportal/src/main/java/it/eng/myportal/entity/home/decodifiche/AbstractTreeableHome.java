package it.eng.myportal.entity.home.decodifiche;

import it.eng.myportal.dtos.ITreeable;
import it.eng.myportal.entity.AbstractDecodeEntity;
import it.eng.myportal.entity.home.local.ITreeableHome;

import java.util.List;

/**
 * Classe Home per le codifiche alberate
 *
 *
 *
 * @author Rodi A.
 *
 * @param <Entity>
 *            Classe dell'Entity al quale viene associato l'Home.
 * @param <T>
 */
public abstract class AbstractTreeableHome<Entity extends AbstractDecodeEntity, DTO extends ITreeable> extends AbstractSuggestibleHome<Entity, DTO>
		implements ITreeableHome<DTO> {

	public abstract List<DTO> findByCodPadre(String par);

}

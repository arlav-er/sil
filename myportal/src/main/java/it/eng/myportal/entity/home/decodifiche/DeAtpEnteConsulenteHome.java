package it.eng.myportal.entity.home.decodifiche;

import it.eng.myportal.dtos.DeAtpEnteConsulenteDTO;
import it.eng.myportal.entity.decodifiche.DeAtpEnteConsulente;

import java.util.List;

import javax.ejb.Stateless;
import javax.faces.model.SelectItem;
import javax.persistence.TypedQuery;

/**
 * Home object for domain model class DeAtpContratto.
 * 
 * @see it.eng.myportal.entity.decodifiche.DeAtpContratto
 * @author enrico
 */
@Stateless
public class DeAtpEnteConsulenteHome extends AbstractDecodeHome<DeAtpEnteConsulente, DeAtpEnteConsulenteDTO> {

	private static final String QUERY_SELECT_ITEMS = "select new javax.faces.model.SelectItem(c.codEnteConsulente,c.descrizione ) from DeAtpEnteConsulente c";

	public DeAtpEnteConsulente findById(String id) {
		return findById(DeAtpEnteConsulente.class, id);
	}

	@Override
	public DeAtpEnteConsulenteDTO toDTO(DeAtpEnteConsulente entity) {
		if (entity == null) {
			return null;
		}
		DeAtpEnteConsulenteDTO dto = super.toDTO(entity);
		dto.setId(entity.getCodEnteConsulente());
		dto.setDescrizione(entity.getDescrizione());
		return dto;
	}

	@Override
	public DeAtpEnteConsulente fromDTO(DeAtpEnteConsulenteDTO dto) {
		if (dto == null) {
			return null;
		}
		DeAtpEnteConsulente entity = super.fromDTO(dto);
		entity.setCodEnteConsulente(dto.getId());
		entity.setDescrizione(dto.getDescrizione());

		return entity;
	}

	public List<SelectItem> getListaSelectItem(boolean rigaBianca) {
		TypedQuery<SelectItem> typedQuery = entityManager.createQuery(QUERY_SELECT_ITEMS, SelectItem.class);
		return getListItems(typedQuery, rigaBianca);
	}

}

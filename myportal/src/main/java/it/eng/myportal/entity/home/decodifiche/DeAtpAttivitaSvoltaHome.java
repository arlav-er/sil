package it.eng.myportal.entity.home.decodifiche;

// Generated 1-set-2011 12.32.46 by Hibernate Tools 3.4.0.CR1

import it.eng.myportal.dtos.DeAtpAttivitaSvoltaDTO;
import it.eng.myportal.entity.decodifiche.DeAtpAttivitaSvolta;

import java.util.List;

import javax.ejb.Stateless;
import javax.faces.model.SelectItem;
import javax.persistence.TypedQuery;

/**
 * Home object for domain model class DeAtpAttivitaSvolta.
 * 
 * @see it.eng.myportal.entity.decodifiche.DeAtpAttivitaSvolta
 * @author Rodi A.
 */
@Stateless
public class DeAtpAttivitaSvoltaHome extends AbstractSuggestibleHome<DeAtpAttivitaSvolta, DeAtpAttivitaSvoltaDTO> {

	private static final String QUERY_SELECT_ITEMS = "select new javax.faces.model.SelectItem(c.codAtpAttivitaSvolta,c.descrizione ) from DeAtpAttivitaSvolta c";
	
	public DeAtpAttivitaSvolta findById(String id) {
		return findById(DeAtpAttivitaSvolta.class, id);
	}

	@Override
	public DeAtpAttivitaSvoltaDTO toDTO(DeAtpAttivitaSvolta atpAttivita) {
		if (atpAttivita == null)
			return null;
		DeAtpAttivitaSvoltaDTO ret = super.toDTO(atpAttivita);
		ret.setId(atpAttivita.getCodAtpAttivitaSvolta());
		ret.setDescrizione(atpAttivita.getDescrizione());
		return ret;
	}

	@Override
	public DeAtpAttivitaSvolta fromDTO(DeAtpAttivitaSvoltaDTO dto) {
		if (dto == null)
			return null;
		DeAtpAttivitaSvolta ret = super.fromDTO(dto);
		ret.setCodAtpAttivitaSvolta(dto.getId());
		ret.setDescrizione(dto.getDescrizione());
		return ret;
	}
	
	public List<SelectItem> getListaSelectItem(boolean rigaBianca) {
		TypedQuery<SelectItem> typedQuery = entityManager.createQuery(QUERY_SELECT_ITEMS, SelectItem.class).setHint("org.hibernate.cacheable", true);
		return getListItems(typedQuery, rigaBianca);
	}

}

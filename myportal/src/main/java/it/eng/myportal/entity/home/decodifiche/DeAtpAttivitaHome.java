package it.eng.myportal.entity.home.decodifiche;

// Generated 1-set-2011 12.32.46 by Hibernate Tools 3.4.0.CR1

import it.eng.myportal.dtos.DeAtpAttivitaDTO;
import it.eng.myportal.entity.decodifiche.DeAtpAttivita;

import java.util.List;

import javax.ejb.Stateless;
import javax.faces.model.SelectItem;
import javax.persistence.TypedQuery;

/**
 * Home object for domain model class DeAtpAttivita.
 * 
 * @see it.eng.myportal.entity.decodifiche.DeAtpAttivita
 * @author Rodi A.
 */
@Stateless
public class DeAtpAttivitaHome extends AbstractSuggestibleHome<DeAtpAttivita, DeAtpAttivitaDTO> {

	
	private static final String QUERY_SELECT_ITEMS = "select new javax.faces.model.SelectItem(c.codAtpAttivita,c.descrizione ) from DeAtpAttivita c";
	
	public DeAtpAttivita findById(String id) {
		return findById(DeAtpAttivita.class, id);
	}

	@Override
	public DeAtpAttivitaDTO toDTO(DeAtpAttivita atpAttivita) {
		if (atpAttivita == null)
			return null;
		DeAtpAttivitaDTO ret = super.toDTO(atpAttivita);
		ret.setId(atpAttivita.getCodAtpAttivita());
		ret.setDescrizione(atpAttivita.getDescrizione());
		return ret;
	}

	@Override
	public DeAtpAttivita fromDTO(DeAtpAttivitaDTO dto) {
		if (dto == null)
			return null;
		DeAtpAttivita ret = super.fromDTO(dto);
		ret.setCodAtpAttivita(dto.getId());
		ret.setDescrizione(dto.getDescrizione());
		return ret;
	}
	
	public List<SelectItem> getListaSelectItem(boolean rigaBianca) {
		TypedQuery<SelectItem> typedQuery = entityManager.createQuery(QUERY_SELECT_ITEMS, SelectItem.class).setHint("org.hibernate.cacheable", true);
		return getListItems(typedQuery, rigaBianca);
	}

}

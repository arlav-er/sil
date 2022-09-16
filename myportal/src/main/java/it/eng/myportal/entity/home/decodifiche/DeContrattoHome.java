package it.eng.myportal.entity.home.decodifiche;

import it.eng.myportal.dtos.DeContrattoDTO;
import it.eng.myportal.entity.decodifiche.DeContratto;

import java.util.Date;
import java.util.List;

import javax.ejb.Stateless;
import javax.faces.model.SelectItem;
import javax.persistence.TypedQuery;

/**
 * 
 * @author Rodi A.
 */
@Stateless
public class DeContrattoHome extends AbstractSuggestibleHome<DeContratto, DeContrattoDTO> {

	private static final String QUERY_RICERCA = "select c from DeContratto c where current_date between c.dtInizioVal and c.dtFineVal order by c.descrizione";

	public DeContratto findById(final String id) {
		return findById(DeContratto.class, id);
	}

	@Override
	public DeContrattoDTO toDTO(final DeContratto entity) {
		if (entity == null)
			return null;
		final DeContrattoDTO dto = super.toDTO(entity);
		dto.setId(entity.getCodContratto());
		dto.setDescrizione(entity.getDescrizione());
		dto.setFlagAtipico(entity.getFlagAtipico());

		return dto;
	}

	@Override
	public DeContratto fromDTO(final DeContrattoDTO dto) {
		if (dto == null)
			return null;
		final DeContratto entity = super.fromDTO(dto);
		entity.setCodContratto(dto.getId());
		entity.setDescrizione(dto.getDescrizione());
		entity.setFlagAtipico(dto.isFlagAtipico());
		return entity;
	}

	/**
	 * Restituisce true se la codifica del contratto passato come parametro e' scaduta, false altrimenti.
	 * 
	 * @param codContratto
	 * @return
	 */
	public boolean isScaduto(String codContratto) {
		DeContratto deContratto = findById(codContratto);
		return (deContratto.getDtFineVal().before(new Date()));
	}

	@Override
	public String getExtraQuery() {
		return " ORDER BY descrizione ";
	}

	private static final String QUERY_ATIPICI = "select new javax.faces.model.SelectItem(c.codContratto,c.descrizione) from DeContratto c where c.flagAtipico = true and  current_date between c.dtInizioVal and c.dtFineVal ";

	public List<SelectItem> getPartitaIvaListItems(boolean b) {
		return getListItems(b);
	}

	public List<SelectItem> getAtipiciListItems(boolean b) {
		TypedQuery<SelectItem> q = entityManager.createQuery(QUERY_ATIPICI, SelectItem.class);
		return getListItems(q, b);
	}

	public List<DeContratto> getAll() {
		TypedQuery<DeContratto> query = entityManager.createQuery(QUERY_RICERCA, DeContratto.class)
				.setHint("org.hibernate.cacheable", true).setHint("org.hibernate.cacheable", true);
		List<DeContratto> resultList = query.getResultList();
		return resultList;
	}
}

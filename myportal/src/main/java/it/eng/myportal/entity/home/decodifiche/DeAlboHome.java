package it.eng.myportal.entity.home.decodifiche;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.Stateless;
import javax.faces.model.SelectItem;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;

import it.eng.myportal.dtos.DeAlboDTO;
import it.eng.myportal.entity.decodifiche.DeAlbo;

/**
 * 
 * @author Rodi A.
 */
@Stateless
public class DeAlboHome extends AbstractSuggestibleHome<DeAlbo, DeAlboDTO> {

	public DeAlbo findById(final String id) {
		return findById(DeAlbo.class, id);
	}

	@Override
	public DeAlboDTO toDTO(final DeAlbo entity) {
		if (entity == null)
			return null;
		final DeAlboDTO dto = super.toDTO(entity);
		dto.setId(entity.getCodAlbo());
		dto.setDescrizione(entity.getDescrizione());

		return dto;
	}

	@Override
	public DeAlbo fromDTO(final DeAlboDTO dto) {
		if (dto == null)
			return null;
		DeAlbo entity = super.fromDTO(dto);
		entity.setCodAlbo(dto.getId());
		entity.setDescrizione(dto.getDescrizione());

		return entity;
	}
	
	public String getExtraQuery() {
		return " ORDER BY e.descrizione ";
	}
	
	
	/**
	 * Metodo per ottenere una lista Ordinata di SelectItem a partire da una
	 * query generica. Questo metodo non è esposto nelle interfaccie per evitare
	 * che venga usato impropriamente.
	 * 
	 * @param addBlank
	 *            booleano per aggiungere una riga bianca in cima alle opzioni;
	 * @param order
	 *            String nome del campo da ordinare
	 * @return lista delle opzioni per un elemento ti tipo select
	 */
	public List<SelectItem> getListItems(boolean addBlank, boolean addScadute, String order) {
		List<SelectItem> selectItems = new ArrayList<SelectItem>();
		String nomeEntity = getEntityName();
		try {
			String query = "SELECT a FROM " + nomeEntity + " a ";
			if (!addScadute) {
				query += " where current_date between a.dtInizioVal and a.dtFineVal and a.flagMin = true";
			}
			if (order != null && order.length() > 0) {
				query += " order by a." + order;
			}
			TypedQuery<DeAlbo> typedQuery = entityManager.createQuery(query, DeAlbo.class).setHint("org.hibernate.cacheable", true);
			List<DeAlbo> res = typedQuery.getResultList();
			if (addBlank)
				selectItems.add(0, new SelectItem(null, ""));
			for (DeAlbo entity : res) {
				DeAlboDTO dto = toDTO(entity);
				String descrizione = dto.getDescrizione();
				if (dto.getDtFineVal().before(new Date())) {
					descrizione += " (codifica scaduta)";
				}
				selectItems.add(new SelectItem(dto, descrizione));
			}

		} catch (NoResultException nre) {
			log.error("No result found for type SelectItem using this statement");

		} catch (RuntimeException re) {
			log.error("Cannot find results for type SelectItem using statement: ");
		}
		return selectItems;
	}

	public List<DeAlbo> findBySuggestionNoDto(String word) {
		List<DeAlbo> deList = entityManager
				.createNamedQuery("DeAlboFindSuggestionValide", DeAlbo.class)
				.setHint("org.hibernate.cacheable", true).setParameter("query", "%"+word+"%").getResultList();
		
		return deList;
	}

}

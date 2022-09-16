package it.eng.myportal.entity.home.decodifiche;

import java.util.List;

import javax.persistence.TypedQuery;

import org.apache.commons.lang3.StringUtils;

import it.eng.myportal.dtos.ISuggestible;
import it.eng.myportal.entity.AbstractDecodeEntity;
import it.eng.myportal.entity.home.local.ISuggestibleHome;

/**
 * Classe Home astratta che contiene una serie di metodi generici e comuni a tutte le Entity e i relativi Home che hanno
 * come chiave primaria una String
 *
 *
 *
 * @author Rodi A.
 *
 * @param <Entity>
 *            Classe dell'Entity al quale viene associato l'Home.
 * 
 * @param <T>
 */
public abstract class AbstractSuggestibleHome<Entity extends AbstractDecodeEntity, DTO extends ISuggestible>
		extends AbstractDecodeHome<Entity, DTO> implements ISuggestibleHome<DTO> {

	public String getSuggestionQuery() {
		return String.format(
				" select e from %1$s e where current_date between e.dtInizioVal and e.dtFineVal and lower(trim(e.%2$s)) like :query %3$s",
				getEntityName(), getFieldName(), getExtraQuery());
	}

	/**
	 * recupera tutte le decodifiche anche quelle scadute!!! utilizzata nella ricerca vacancy
	 * 
	 * @return
	 */
	public String getSuggestionAllQuery() {
		return String.format(" select e from %1$s e where lower(trim(e.%2$s)) like :query %3$s", getEntityName(),
				getFieldName(), getExtraQuery());
	}

	public String getFieldName() {
		return "descrizione";
	}

	public String getExtraQuery() {
		return String.format(" ORDER BY %s ", getFieldName());
	}

	/**
	 * @param par
	 *            String termine per la ricerca dei suggerimenti
	 * @return List<DTO> lista suggerimenti per elemento autocomplete
	 * @see it.eng.myportal.entity.home.local.IDecodeHome#findBySuggestion(java.lang.String)
	 */
	@SuppressWarnings("unchecked")
	public List<DTO> findBySuggestion(String par) {

		String trimmedSuggestion = StringUtils.trimToEmpty(par);
		return findByDescription("%" + trimmedSuggestion + "%");
	}

	@SuppressWarnings("unchecked")
	public List<DTO> findByDescription(String desc) {

		TypedQuery<Entity> query = (TypedQuery<Entity>) entityManager.createQuery(getSuggestionQuery());

		String lowerCase = StringUtils.trimToEmpty(desc).toLowerCase();
		query.setParameter("query", lowerCase);
		query.getResultList();
		// query.setMaxResults(10);

		return findDTOByQuery(query);

	}

	@SuppressWarnings("unchecked")
	public List<DTO> findByDescriptionAll(String desc) {

		TypedQuery<Entity> query = (TypedQuery<Entity>) entityManager.createQuery(getSuggestionAllQuery());

		String lowerCase = StringUtils.trimToEmpty(desc).toLowerCase();
		query.setParameter("query", lowerCase);
		query.setMaxResults(10);

		return findDTOByQuery(query);

	}

	/**
	 * Effettua alcuni controlli su un parametro da inserire in una NativeQuery, in modo da assicurarsi che la query
	 * sarà formata correttamente. Controlli: elimina i caratteri che non siano lettere, numeri o apici; elimina gli
	 * spazi multipli; evita che il parametro sia una stringa vuota.
	 * 
	 * @param parameter
	 * @return
	 */
	protected String checkSqlParameter(String parameter) {
		parameter = parameter.replaceAll("[^a-zA-Z0-9àèéìòù' ]+", "");
		parameter = parameter.replaceAll(" +", " ");
		parameter = parameter.trim();

		if (parameter.equals(""))
			parameter = "abcdefg";
		return parameter;
	}
}

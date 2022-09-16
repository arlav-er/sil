package it.eng.myportal.entity.home.decodifiche;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.faces.model.SelectItem;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import it.eng.myportal.dtos.IDecode;
import it.eng.myportal.entity.IDecodeEntity;
import it.eng.myportal.entity.home.AbstractHome;
import it.eng.myportal.entity.home.local.IDecodeHome;
import it.eng.myportal.exception.MyPortalException;

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
 * @param <T>
 */
public abstract class AbstractDecodeHome<Entity extends IDecodeEntity, DTO extends IDecode> extends
		AbstractHome<Entity, DTO, String> implements IDecodeHome<DTO> {

	protected final static String ENTITY_DECODE_PACKAGE_PREFIX = "decodifiche.";

	/**
	 * Metodo per ottenere una lista di SelectItem a partire da una query generica. Questo metodo non è esposto nelle
	 * interfaccie per evitare che venga usato impropriamente.
	 * 
	 * @param query
	 *            String da utilizzare per la ricerca delle opzioni;
	 * @param addBlank
	 *            booleano per aggiungere una riga bianca in cima alle opzioni;
	 * @return lista delle opzioni per un elemento ti tipo select
	 * @see it.eng.myportal.entity.home.local.IDecodeHome#getListItems(java.lang.String,boolean)
	 */
	protected List<SelectItem> getListItems(TypedQuery<SelectItem> query, boolean addBlank) {
		List<SelectItem> selectItems = null;

		try {

			selectItems = query.setHint("org.hibernate.cacheable", true).getResultList();
			if (addBlank)
				selectItems.add(0, new SelectItem(null, ""));

		} catch (NoResultException nre) {
			log.error("Non sono stati trovati risultati per il dato SelectItem usando la seguente query: " + query + " - errore: " + nre.getMessage());

		} catch (RuntimeException re) {
			log.error("Non è possibile trovare risultati per il dato SelectItem usando la seguente query: " + query + " - errore: " + re.getMessage());
		}
		return selectItems;
	}

	/**
	 * Metodo per ottenere una lista di SelectItem a partire da una query generica. Questo metodo non è esposto nelle
	 * interfaccie per evitare che venga usato impropriamente.
	 * 
	 * @param query
	 *            String da utilizzare per la ricerca delle opzioni;
	 * @param addBlank
	 *            booleano per aggiungere una riga bianca in cima alle opzioni;
	 * @param addScadute
	 *            booleano per aggiungere anche gli elementi con codifica scaduta;
	 * @return lista delle opzioni per un elemento ti tipo select
	 */
	public List<SelectItem> getListItems(boolean addBlank) {
		return getListItems(addBlank, false, null);
	}

	public List<SelectItem> getListItems(boolean addBlank, String order) {
		return getListItems(addBlank, false, order);
	}

	public List<SelectItem> getListItems(boolean addBlank, boolean addScadute) {
		return getListItems(addBlank, addScadute, null);
	}

	/**
	 * Metodo per ottenere una lista Ordinata di SelectItem a partire da una query generica. Questo metodo non è esposto
	 * nelle interfaccie per evitare che venga usato impropriamente.
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
				query += " where current_date between a.dtInizioVal and a.dtFineVal ";
			}
			if (order != null && order.length() > 0) {
				query += " order by a." + order;
			}
			entityManager.setProperty("org.hibernate.cacheable", true);
			Query typedQuery = entityManager.createQuery(query);
			List<Entity> res = typedQuery.getResultList();
			if (addBlank)
				selectItems.add(0, new SelectItem(null, ""));
			for (Entity entity : res) {
				DTO dto = toDTO(entity);
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

	/**
	 * Inizializza il DTO e ne popola i campi dtInizioVal e dtFineVal
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

			dto.setDtFineVal(entity.getDtFineVal());
			dto.setDtInizioVal(entity.getDtInizioVal());

			return dto;
		} catch (Exception e) {
			throw new MyPortalException("error.retrieve_data", e);
		}
	}

	/**
	 * Inizializza l'Entity e ne popola i campi dtInizioVal e dtFineVal
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
			entityClass = (Class<Entity>) Class.forName(ENTITY_PACKAGE_PREFIX + ENTITY_DECODE_PACKAGE_PREFIX
					+ getEntityName());
			Entity entity = entityClass.getConstructor((Class<?>[]) null).newInstance((Object[]) null);

			entity.setDtFineVal(dto.getDtFineVal());
			entity.setDtInizioVal(dto.getDtInizioVal());

			return entity;
		} catch (Exception e) {
			throw new MyPortalException("error.retrieve_data");
		}
	}

	/**
	 * Restituisce tutte le righe della tabella corrispondente all'entity concreta che invoca il metodo.
	 * 
	 * @return tutte le righe in tabella
	 */
	public List<Entity> findAll() {

		String nomeEntity = getEntityName();

		final String queryHQL = "SELECT a FROM " + nomeEntity
				+ " a where current_date between a.dtInizioVal and a.dtFineVal";

		Query query = entityManager.createQuery(queryHQL).setHint("org.hibernate.cacheable", true);

		return (List<Entity>) query.getResultList();
	}

}

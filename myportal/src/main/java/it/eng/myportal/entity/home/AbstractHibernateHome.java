package it.eng.myportal.entity.home;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Set;

import javax.ejb.EJBException;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.metamodel.Attribute;
import javax.persistence.metamodel.EntityType;
import javax.persistence.metamodel.Metamodel;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Classe Home astratta che contiene una serie di metodi generici e comuni a
 * tutte le Entity e i relativi Home.
 * 
 * @author Rodi A.
 * 
 * @param <Entity>
 *            Classe dell'Entity al quale viene associato l'Home.
 * @param <PKType>
 *            Classe della PrimaryKey.
 */
public abstract class AbstractHibernateHome<Entity, PKType> {

	protected final Log log = LogFactory.getLog(this.getClass());

	@PersistenceContext
	protected EntityManager entityManager;

	/**
	 * <b>Attenzione!<br/>
	 * Non richiamare questo metodo direttamente dai BackingBean in quanto
	 * restituisce un'Entity e non un DTO</b><br/>
	 * Salva una istanza dell'Entity e ne genera l'ID se questo viene
	 * autogenerato.<br/>
	 * Lancia una RuntimeException nel caso di errori
	 * 
	 * @param transientInstance
	 *            istanza dell'Entity da salvare su DB.
	 */
	public void persist(Entity transientInstance) {		
		log.debug("persisting " + transientInstance.getClass().getSimpleName() + " instance");		
		
		entityManager.persist(transientInstance);
		
		log.debug("persist successful");		
	}

	
	public Entity persistFlush(Entity transientInstance) {
		log.debug("persistFlush " + transientInstance.getClass().getSimpleName() + " instance");

		entityManager.persist(transientInstance);
		entityManager.flush();
		
		log.debug("persistFlush successful");
		return transientInstance;
	}
	
	/**
	 * <b>Attenzione!<br/>
	 * Non richiamare questo metodo direttamente dai BackingBean in quanto
	 * restituisce un'Entity e non un DTO</b><br/>
	 * Cancella l'istanza dell'Entity da DB.<br/>
	 * Lancia una RuntimeException nel caso di errori
	 * 
	 * @param persistentInstance
	 *            Entity
	 */
	public void remove(Entity persistentInstance) {
		if (persistentInstance == null) {
			log.error("Hai cercato di distruggere una entity null.");
			return;
		}
		if (log.isDebugEnabled())
			log.debug("removing " + persistentInstance.getClass().getSimpleName() + " instance: "+persistentInstance.toString());
		entityManager.remove(persistentInstance);

	}

	/**
	 * <b>Attenzione!<br/>
	 * Non richiamare questo metodo direttamente dai BackingBean in quanto
	 * restituisce un'Entity e non un DTO</b><br/>
	 * Aggiorna l'istanza sincronizzandola con il DB.<br/>
	 * Lancia una RuntimeException nel caso di errori
	 * 
	 * @param detachedInstance
	 *            Entity
	 * @return Entity
	 */
	public Entity merge(Entity detachedInstance) {
//		if (log.isDebugEnabled())
//			log.debug("merging " + detachedInstance.getClass().getSimpleName() + " instance");

		Entity result;
		result = entityManager.merge(detachedInstance);

		return result;
	}

	/**
	 * <b>Attenzione!<br/>
	 * Non richiamare questo metodo direttamente dai BackingBean in quanto
	 * restituisce un'Entity e non un DTO</b><br/>
	 * Esegue una ricerca sulla base dell'id.<br/>
	 * Lancia una RuntimeException nel caso di errori.
	 * 
	 * @param entityClass
	 *            Class<Entity>
	 * @param id
	 *            PKType
	 * @return la Entity cercata wrappando il metodo find dell EntityManager
	 */
	public Entity findById(Class<Entity> entityClass, PKType id) {
		//non ce ne frega un cazzo di 'sta roba
		//if (log.isDebugEnabled())
		//	log.debug("getting " + entityClass.getSimpleName() + " instance with id: " + id);

		Entity instance;
		instance = entityManager.find(entityClass, id);

		return instance;
	}

	public Entity findByIdNoCache(Class<Entity> entityClass, PKType id) {
		if (log.isDebugEnabled())
			log.debug("getting " + entityClass.getSimpleName() + " instance with id: " + id);

		Entity instance;
		instance = entityManager.find(entityClass, id);
		entityManager.refresh(instance);

		return instance;
	}

	/**
	 * <b>Attenzione!<br/>
	 * Non richiamare questo metodo direttamente dai BackingBean in quanto
	 * restituisce un'Entity e non un DTO</b><br/>
	 * Lancia una RuntimeException nel caso di errori.
	 * 
	 * @param id
	 *            PKType
	 * @return Entity
	 */
	public abstract Entity findById(PKType id);

	/**
	 * Cancella l'istanza dell'Entity da DB partendo dalla PrimaryKey
	 * 
	 * @param id
	 *            PKType nel caso avvengano errori durante la rimozione
	 */
	public void removeById(PKType id, Integer idPrincipalMod) {
		Entity entity = findById(id);
		remove(entity);
	}

	/**
	 * Cancella l'istanza dell'Entity a cascata con tutti gli elementi
	 * collegati. La versione implementata nell'Abstract non fa altro che
	 * richiamare la removeById() Le classi concrete devono fare un Override del
	 * metodo se vi sono oggetti colelgati all'Entity.
	 * 
	 * @param id
	 *            PKType
	 */
	public void removeCascadeById(PKType id, Integer idPfPrincipalMod) {
		removeById(id, idPfPrincipalMod);
	}

	public List<Entity> findByExample(Entity example) {
		try {
			Class<Entity> clazz = (Class<Entity>) example.getClass();
			CriteriaBuilder cb = entityManager.getCriteriaBuilder();
			CriteriaQuery<Entity> cq = cb.createQuery(clazz);
			Root<Entity> r = cq.from(clazz);
			Predicate p = cb.conjunction();
			Metamodel mm = entityManager.getMetamodel();
			EntityType<Entity> et = mm.entity(clazz);
			Set<Attribute<? super Entity, ?>> attrs = et.getAttributes();
			for (Attribute<? super Entity, ?> a : attrs) {
				String name = a.getName();
				String javaName = a.getJavaMember().getName();
				String getter = javaName;
				Method m = clazz.getMethod(getter, (Class<?>[]) null);
				if (m.invoke(example, (Object[]) null) != null)
					p = cb.and(p, cb.equal(r.get(name), m.invoke(example, (Object[]) null)));
			}
			cq.select(r).where(p);
			TypedQuery<Entity> query = entityManager.createQuery(cq);
			return query.getResultList();
		} catch (Exception e) {
			throw new EJBException("Erore durante l'utilizzo della findByExample", e);
		}
	}

	public String getEntityName() {
		String nomeHome = this.getClass().getSimpleName();
		// Entity entity = new Entity();
		// nomeEntity
		// nomeEntity = nomeEntity.substring(0, nomeEntity.indexOf("Home"));
		return StringUtils.replace(nomeHome, "Home", "");
	}

	/**
	 * Restituisce tutte le righe della tabella corrispondente all'entity
	 * concreta che invoca il metodo.
	 * 
	 * @return tutte le righe in tabella
	 */
	public List<Entity> findAll() {
		String nomeEntity = getEntityName();

		final String queryHQL = "SELECT a FROM " + nomeEntity + " a";

		Query query = entityManager.createQuery(queryHQL);

		return (List<Entity>) query.getResultList();
	}
}

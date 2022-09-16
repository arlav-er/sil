package it.eng.myportal.entity.ejb.tirocini;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import it.eng.myportal.entity.AziendaInfo;
import it.eng.myportal.entity.DoTirocini;
import it.eng.myportal.entity.DoTirocini_;
import it.eng.myportal.entity.PfPrincipal;
import it.eng.myportal.entity.PfPrincipal_;
import it.eng.myportal.entity.Provincia;
import it.eng.myportal.entity.decodifiche.DeCpi;
import it.eng.myportal.entity.decodifiche.DeCpi_;
import it.eng.myportal.entity.decodifiche.DeProvincia;
import it.eng.myportal.entity.decodifiche.DeProvincia_;
import it.eng.myportal.entity.home.AbstractHibernateHome;
import it.eng.myportal.entity.home.PfPrincipalHome;
import it.eng.myportal.entity.home.ProvinciaHome;
import it.eng.myportal.entity.home.decodifiche.DeProvinciaHome;
import it.eng.myportal.exception.MyPortalException;

/**
 * @author pegoraro
 * 
 */
@Stateless
public class DoTirociniEjb extends AbstractHibernateHome<DoTirocini, Integer> {

	protected final Log log = LogFactory.getLog(DoTirociniEjb.class);

	@EJB
	private PfPrincipalHome pfHome;

	@EJB
	private DeProvinciaHome provinciaHome;

	@EJB
	private ProvinciaHome provinciaInfoHome;

	@PersistenceContext
	protected EntityManager entityManager;

	public List<?> executeNativeSelect(String nativeSelect) {
		Query nq = entityManager.createNativeQuery(nativeSelect);
		return nq.getResultList();
	}

	public Integer executeNativeUpdate(String nativeUpdate) {
		Query nq = entityManager.createNativeQuery(nativeUpdate);
		return nq.executeUpdate();
	}

	public Integer executeNativeDelete(String nativeDelete) {
		Query nq = entityManager.createNativeQuery(nativeDelete);
		return nq.executeUpdate();
	}

	@Override
	public DoTirocini findById(Integer id) {
		return findById(DoTirocini.class, id);
	}

	public DoTirocini findMineById(Integer id, PfPrincipal whose) throws Exception {
		DoTirocini isIt = findById(DoTirocini.class, id);
		if (isIt.getPfPrincipal().equals(whose))
			return isIt;

		throw new MyPortalException("DoTirocinio id=" + id + " is not property of user " + whose.getUsername());

	}

	/**
	 * Paged Count, uses Root query and copunts results
	 * 
	 * @param firstResult
	 * @param pageSize
	 * @param pfPrincipalId
	 * @return
	 */
	public Long findCountAllPaged(Integer pfPrincipalId) {
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<Long> query = cb.createQuery(Long.class);

		Root<DoTirocini> froms = query.from(DoTirocini.class);

		Predicate equalIdpfPrincipal = cb.equal(froms.get(DoTirocini_.pfPrincipal), pfPrincipalId);
		Predicate whereClause = cb.and(equalIdpfPrincipal);

		query.where(whereClause);
		query.select(cb.count(froms));

		TypedQuery<Long> typedQuery = entityManager.createQuery(query);

		return typedQuery.getSingleResult();

	}

	public List<DoTirocini> findAllPaged(int firstResult, int pageSize, Integer pfPrincipalId) {
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<DoTirocini> query = cb.createQuery(DoTirocini.class);

		Root<DoTirocini> from = query.from(DoTirocini.class);
		Join<DoTirocini, PfPrincipal> pfPrincipal = from.join(DoTirocini_.pfPrincipal);

		Predicate equalIdpfPrincipal = cb.equal(pfPrincipal.get(PfPrincipal_.idPfPrincipal), pfPrincipalId);
		Predicate whereClause = cb.and(equalIdpfPrincipal);

		query.where(whereClause);
		query.select(from);

		TypedQuery<DoTirocini> typedQuery = entityManager.createQuery(query);

		if (firstResult != 0) {
			typedQuery.setFirstResult(firstResult);
		}

		if (pageSize != 0) {
			typedQuery.setMaxResults(pageSize);
		}

		List<DoTirocini> list = typedQuery.getResultList();

		for (DoTirocini doTirocini : list) {
			String fv = doTirocini.getPfPrincipal().getAziendaInfo().getRagioneSociale();
			// log.debug("FRANCO GURU: "+fv);
		}

		return list;
	}

	public List<DoTirocini> findByExamplePagedProvincia(int firstResult, int pageSize, Integer pfPrincipalId,
			DoTirocini example) {
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<DoTirocini> query = cb.createQuery(DoTirocini.class);
		List<Predicate> whereConditions = new ArrayList<Predicate>();

		Provincia itPr = provinciaInfoHome.findByIdPfPrincipal(pfPrincipalId);
		itPr = provinciaInfoHome.findById(itPr.getIdProvincia());
		DeProvincia dep = itPr.getDeProvincia();
		// dep = provinciaHome.findById(dep.getCodProvincia());

		Root<DoTirocini> from = query.from(DoTirocini.class);
		Join<DoTirocini, PfPrincipal> pfPrincipal = from.join(DoTirocini_.pfPrincipal);
		Join<PfPrincipal, AziendaInfo> az = pfPrincipal.join(PfPrincipal_.aziendaInfo);
		Join<DoTirocini, DeCpi> deCpi = from.join(DoTirocini_.deCpi, JoinType.LEFT);
		Join<DeCpi, DeProvincia> deProvinciaCpi = deCpi.join(DeCpi_.deProvincia, JoinType.LEFT);

		// Predicate equalIdpfPrincipal = cb.equal(
		// pfPrincipal.get(DoTirocini_.deProvincia), dep);

		Predicate provFilter = cb.equal(deProvinciaCpi.get(DeProvincia_.codProvincia), dep.getCodProvincia());
		whereConditions.add(cb.and(provFilter));

		if (example.getDataRichiesta() != null) {
			Path<Date> dataFineStatoAdesione = from.<Date> get("dataRichiesta");
			whereConditions.add(cb.equal(dataFineStatoAdesione, example.getDataRichiesta()));
		}
		// LUOGO DI LAVORO
		if (example.getDeComune() != null) {
			Predicate ciaRifPredicate = cb.equal(from.get(DoTirocini_.deComune), example.getDeComune());
			whereConditions.add(ciaRifPredicate);
		}
		if (example.getDeProvincia() != null) {
			Predicate provinciaRifPredicate = cb.equal(from.get(DoTirocini_.deProvincia), example.getDeProvincia());
			whereConditions.add(provinciaRifPredicate);
		}
		// AMBITO PROFESSIONALE
		if (example.getDeMansione() != null) {
			Predicate provinciaRifPredicate = cb.equal(from.get(DoTirocini_.deMansione), example.getDeMansione());
			whereConditions.add(provinciaRifPredicate);
		}

		if (example.getDeMansioneMin() != null) {
			Predicate provinciaRifPredicate = cb.equal(from.get(DoTirocini_.deMansioneMin), example.getDeMansioneMin());
			whereConditions.add(provinciaRifPredicate);
		}

		if (example.getDeQualificaSrq() != null) {
			Predicate provinciaRifPredicate = cb.equal(from.get(DoTirocini_.deQualificaSrq),
					example.getDeQualificaSrq());
			whereConditions.add(provinciaRifPredicate);
		}

		query.where(whereConditions.toArray(new Predicate[whereConditions.size()]));
		// query.select(from);
		query.select(from);
		TypedQuery<DoTirocini> typedQuery = entityManager.createQuery(query);

		if (firstResult != 0) {
			typedQuery.setFirstResult(firstResult);
		}

		if (pageSize != 0) {
			typedQuery.setMaxResults(pageSize);
		}

		List<DoTirocini> list = typedQuery.getResultList();

		for (DoTirocini doTirocini : list) {
			String fv = doTirocini.getPfPrincipal().getAziendaInfo().getRagioneSociale();
			// log.debug("FRANCO GURU: "+fv);
		}

		return list;
	}

	/**
	 * Called from BB
	 * 
	 * @param transientInstance
	 */
	public DoTirocini sync(DoTirocini transientInstance, PfPrincipal idPrincipalIns) {
		Date now = new Date();

		transientInstance.setDtmMod(now);
		transientInstance.setPfPrincipalMod(idPrincipalIns);
		transientInstance.setPfPrincipal(idPrincipalIns);
		if (transientInstance.getIdDoTirocinio() == null) {
			transientInstance.setDtmIns(now);
			transientInstance.setPfPrincipalIns(idPrincipalIns);
			return persistFlush(transientInstance);
		} else {
			return merge(transientInstance);
		}
	}

	@Override
	public DoTirocini persistFlush(DoTirocini transientInstance) {
		Calendar tgt = Calendar.getInstance();
		// FIXME verificare, Donato smemorato
		// tgt.setTime(transientInstance.getDataRichiesta());
		tgt.add(Calendar.DAY_OF_MONTH, 60);
		transientInstance.setDataScadenza(tgt.getTime());
		transientInstance.setCfRif(transientInstance.getCfRif().toUpperCase());
		return super.persistFlush(transientInstance);
	}

	public Long findCountByExamplePagedProvincia(int pfPrincipalId, DoTirocini example) {
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<Long> query = cb.createQuery(Long.class);
		List<Predicate> whereConditions = new ArrayList<Predicate>();

		Provincia itPr = provinciaInfoHome.findByIdPfPrincipal(pfPrincipalId);
		itPr = provinciaInfoHome.findById(itPr.getIdProvincia());
		DeProvincia dep = itPr.getDeProvincia();
		// dep = provinciaHome.findById(dep.getCodProvincia());

		Root<DoTirocini> from = query.from(DoTirocini.class);

		Join<DoTirocini, DeCpi> deCpi = from.join(DoTirocini_.deCpi, JoinType.LEFT);
		Join<DeCpi, DeProvincia> deProvinciaCpi = deCpi.join(DeCpi_.deProvincia, JoinType.LEFT);

		// Predicate equalIdpfPrincipal = cb.equal(
		// pfPrincipal.get(DoTirocini_.deProvincia), dep);

		Predicate provFilter = cb.equal(deProvinciaCpi.get(DeProvincia_.codProvincia), dep.getCodProvincia());
		whereConditions.add(cb.and(provFilter));

		if (example.getDataRichiesta() != null) {
			Path<Date> dataFineStatoAdesione = from.<Date> get("dataRichiesta");
			whereConditions.add(cb.equal(dataFineStatoAdesione, example.getDataRichiesta()));
		}
		// LUOGO DI LAVORO
		if (example.getDeComune() != null) {
			Predicate ciaRifPredicate = cb.equal(from.get(DoTirocini_.deComune), example.getDeComune());
			whereConditions.add(ciaRifPredicate);
		}
		if (example.getDeProvincia() != null) {
			Predicate provinciaRifPredicate = cb.equal(from.get(DoTirocini_.deProvincia), example.getDeProvincia());
			whereConditions.add(provinciaRifPredicate);
		}
		// AMBITO PROFESSIONALE
		if (example.getDeMansione() != null) {
			Predicate provinciaRifPredicate = cb.equal(from.get(DoTirocini_.deMansione), example.getDeMansione());
			whereConditions.add(provinciaRifPredicate);
		}

		if (example.getDeMansioneMin() != null) {
			Predicate provinciaRifPredicate = cb.equal(from.get(DoTirocini_.deMansioneMin), example.getDeMansioneMin());
			whereConditions.add(provinciaRifPredicate);
		}

		if (example.getDeQualificaSrq() != null) {
			Predicate provinciaRifPredicate = cb.equal(from.get(DoTirocini_.deQualificaSrq),
					example.getDeQualificaSrq());
			whereConditions.add(provinciaRifPredicate);
		}

		query.where(whereConditions.toArray(new Predicate[whereConditions.size()]));
		query.select(cb.count(from));
		// query.multiselect(from, az.get(AziendaInfo_.ragioneSociale));
		TypedQuery<Long> typedQuery = entityManager.createQuery(query);

		return typedQuery.getSingleResult();
	}

}

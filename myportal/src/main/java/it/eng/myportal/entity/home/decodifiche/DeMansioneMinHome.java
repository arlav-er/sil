package it.eng.myportal.entity.home.decodifiche;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang3.StringUtils;

// Generated 1-set-2011 12.32.46 by Hibernate Tools 3.4.0.CR1
import it.eng.myportal.dtos.DeMansioneMinDTO;
import it.eng.myportal.entity.decodifiche.DeMansione;
import it.eng.myportal.entity.decodifiche.DeMansione_;
import it.eng.myportal.entity.decodifiche.min.DeMansioneMin;
import it.eng.myportal.entity.decodifiche.min.DeMansioneMin_;
import it.eng.myportal.entity.decodifiche.sil.DeMansioneSil;
import it.eng.myportal.entity.decodifiche.sil.DeMansioneSil_;
import it.eng.myportal.exception.MyPortalException;

/**
 * Home object for domain model class DeMansioneMin.
 * 
 * @see it.eng.myportal.entity.decodifiche.min.DeMansioneMin
 * @author Turrini
 */
@Stateless
public class DeMansioneMinHome extends AbstractTreeableHome<DeMansioneMin, DeMansioneMinDTO> {

	@EJB
	DeMansioneSilHome deMansioneSilHome;

	private final static String SQL_FIND_DOT = "SELECT t1 " + " FROM DeMansioneMin t1 "
			+ " WHERE t1.deMansioneSil.codMansioneSil = :codMansioneDot ";

	public DeMansioneMin findById(String id) {
		return findById(DeMansioneMin.class, id);
	}

	@Override
	public DeMansioneMinDTO toDTO(DeMansioneMin entity) {
		if (entity == null)
			return null;
		DeMansioneMinDTO dto = super.toDTO(entity);
		dto.setId(entity.getCodMansioneMin());
		DeMansioneSil deMansioneSil = entity.getDeMansioneSil();
		if (deMansioneSil != null) {
			dto.setCodMansioneDot(deMansioneSil.getCodMansioneSil());
		}
		dto.setDescrizione(entity.getDescrizione().toUpperCase());
		if (entity.getPadre() != null) {
			dto.setCodMansionePadre(entity.getPadre().getCodMansioneMin());
		} else {
			dto.setCodMansionePadre("0");
		}
		dto.setNumeroFigli((long) entity.getFigli().size());

		return dto;
	}

	@Override
	public DeMansioneMin fromDTO(DeMansioneMinDTO dto) {
		if (dto == null)
			return null;

		Class<DeMansioneMin> entityClass;
		try {
			// entityClass = (Class<DeMansioneMin>) Class
			// .forName(ENTITY_PACKAGE_PREFIX + "decodifiche.min." + getEntityName());
			// DeMansioneMin entity = entityClass.getConstructor((Class<?>[]) null).newInstance((Object[]) null);

			DeMansioneMin entity = new DeMansioneMin();
			entity.setDtFineVal(dto.getDtFineVal());
			entity.setDtInizioVal(dto.getDtInizioVal());

			// DeMansioneMin entity = super.fromDTO(dto);
			entity.setCodMansioneMin(dto.getId());
			entity.setDescrizione(dto.getDescrizione());
			String codMansioneDot = dto.getCodMansioneDot();
			if (codMansioneDot != null) {
				entity.setDeMansioneSil(deMansioneSilHome.findById(codMansioneDot));
			}
			if (StringUtils.isNotBlank(dto.getCodMansionePadre())) {
				entity.setPadre(findById(dto.getCodMansionePadre()));
			}
			return entity;

		} catch (Exception e) {
			throw new MyPortalException("error.retrieve_data");
		}

	}

	@Override
	public String getFieldName() {
		return "descrizione";
	}

	@Override
	public String getExtraQuery() {
		return "ORDER BY e.descrizione";
	}

	public TreeSet<DeMansioneMinDTO> findByCodMansioneMin(List<String> listCodMansioneMin) {
		TreeSet<DeMansioneMinDTO> returnList = new TreeSet<DeMansioneMinDTO>();

		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<DeMansioneMin> criteriaQuery = criteriaBuilder.createQuery(DeMansioneMin.class);

		Root<DeMansioneMin> deMansioneMin = criteriaQuery.from(DeMansioneMin.class);

		criteriaQuery.where(deMansioneMin.get(DeMansioneMin_.codMansioneMin).in(listCodMansioneMin));

		TypedQuery<DeMansioneMin> typedQuery = entityManager.createQuery(criteriaQuery);

		List<DeMansioneMin> results = typedQuery.getResultList();

		for (DeMansioneMin result : results) {
			DeMansioneMinDTO newDTO = this.toDTO(result);

			returnList.add(newDTO);
		}

		return returnList;
	}

	/**
	 * Restituisce tutti gli elementi che hanno come codPadre il codice passato in input
	 */
	@Override
	public List<DeMansioneMinDTO> findByCodPadre(String codMansionePadre) {
		List<String> listCodMansionePadre = new ArrayList<String>();
		if (!codMansionePadre.isEmpty()) {
			listCodMansionePadre.add(codMansionePadre);
		}

		return findByCodPadre("", listCodMansionePadre);
	}

	/**
	 * Restituisce tutti gli elementi la cui descrizione contiene la stringa passata in input, ma che sono anche figli
	 * di uno dei nodi passati come input. NB. si ferma soltanto ad un livello
	 * 
	 * @param desc
	 * @param listCodMansionePadre
	 * @return
	 */
	public List<DeMansioneMinDTO> findByCodPadre(String desc, List<String> listCodMansionePadre) {
		List<DeMansioneMinDTO> returnList = new ArrayList<DeMansioneMinDTO>();
		List<Predicate> predicates = new ArrayList<Predicate>();
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<DeMansioneMin> criteriaQuery = criteriaBuilder.createQuery(DeMansioneMin.class);

		Root<DeMansioneMin> deMansioneMin = criteriaQuery.from(DeMansioneMin.class);

		Predicate p1;
		if (listCodMansionePadre.isEmpty()) {
			p1 = criteriaBuilder.isNull(deMansioneMin.get(DeMansioneMin_.padre));
		} else {
			Join<DeMansioneMin, DeMansioneMin> join = deMansioneMin.join(DeMansioneMin_.padre);
			p1 = join.get(DeMansioneMin_.codMansioneMin).in(listCodMansionePadre);
		}
		predicates.add(p1);

		if (desc != null && !desc.isEmpty()) {
			desc = desc.trim();
			Predicate p2 = criteriaBuilder.like(deMansioneMin.get(DeMansioneMin_.descrizione), "%" + desc + "%");
			predicates.add(p2);
		}

		criteriaQuery.where(predicates.toArray(new Predicate[0]));

		/* ordino per descrizione */
		criteriaQuery.orderBy(criteriaBuilder.asc(deMansioneMin.get(DeMansioneMin_.descrizione)));

		TypedQuery<DeMansioneMin> typedQuery = entityManager.createQuery(criteriaQuery)
				.setHint("org.hibernate.cacheable", true);

		List<DeMansioneMin> results = typedQuery.getResultList();

		for (DeMansioneMin result : results) {
			DeMansioneMinDTO newDTO = this.toDTO(result);

			newDTO.setNumeroFigli((long) result.getFigli().size());
			returnList.add(newDTO);
		}

		return returnList;
	}

	@Override
	public String getSuggestionQuery() {
		return "select m from DeMansioneMin m where lower(m.descrizione) like :query and m.padre is not null";
	}

	/**
	 * Restituisce i cod_mansione_padre della tabella de_mansione_min corrispondenti al cod_mansione della tabella
	 * de_mansione tramite la mappatura della tabella de_mansione_sil.
	 * 
	 * @param codMansione
	 * @return
	 */
	public List<String> getCodMansionePadreByCodMansione(String codMansione) {
		List<String> result;

		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<String> criteriaQuery = criteriaBuilder.createQuery(String.class);

		Root<DeMansione> deMansione = criteriaQuery.from(DeMansione.class);
		Root<DeMansioneSil> deMansioneSil = criteriaQuery.from(DeMansioneSil.class);
		Root<DeMansioneMin> deMansioneMin = criteriaQuery.from(DeMansioneMin.class);

		Predicate p1 = criteriaBuilder.equal(deMansione.get(DeMansione_.codMansione),
				deMansioneSil.get(DeMansioneSil_.deMansione));
		Predicate p2 = criteriaBuilder.equal(deMansioneSil.get(DeMansioneSil_.codMansioneSil),
				deMansioneMin.get(DeMansioneMin_.deMansioneSil));
		Predicate p3 = criteriaBuilder.isNull(deMansioneMin.get(DeMansioneMin_.padre));
		Predicate p4 = criteriaBuilder.equal(deMansione.get(DeMansione_.codMansione), codMansione);

		criteriaQuery.where(p1, p2, p3, p4);

		criteriaQuery.select(deMansioneMin.get(DeMansioneMin_.codMansioneMin));

		TypedQuery<String> typedQuery = entityManager.createQuery(criteriaQuery);

		result = typedQuery.getResultList();

		return result;
	}

	public DeMansioneMin findByDot(String codMansioneDot) {

		// List<DeMansioneMinDTO> returnList = new ArrayList<DeMansioneMinDTO>();

		TypedQuery<DeMansioneMin> query;

		query = entityManager.createQuery(SQL_FIND_DOT, DeMansioneMin.class);
		query.setHint("org.hibernate.cacheable", true).setParameter("codMansioneDot", codMansioneDot);

		List<DeMansioneMin> results = query.getResultList();
		if (results == null || results.isEmpty())
			return null;
		return results.get(0);

	}

	/**
	 * Query per autosuggestion, no DTO
	 * 
	 * @param startsWith
	 * @return
	 */
	public List<DeMansioneMin> findMansioniStartingWith(String startsWith) {
		return entityManager.createNamedQuery("DeMansioneMinFindSuggestionFigli", DeMansioneMin.class)
				.setHint("org.hibernate.cacheable", true).setParameter("query", "%" + startsWith + "%")
				.setMaxResults(10).getResultList();
	}

	/**
	 * Come la precedente, ma wrappa DTO
	 * 
	 * @param query
	 * @return
	 */
	public List<DeMansioneMinDTO> findSuggestion(String query) {
		List<DeMansioneMinDTO> deMansioneMinDTOList = new ArrayList<DeMansioneMinDTO>();
		List<DeMansioneMin> deMansioneMinList = findMansioniStartingWith(query);
		if (deMansioneMinList != null && !deMansioneMinList.isEmpty()) {
			for (DeMansioneMin deMansioneMin : deMansioneMinList)
				deMansioneMinDTOList.add(toDTO(deMansioneMin));
		}

		return deMansioneMinDTOList;
	}

	public List<DeMansioneMin> findMansioniMinByCodMansione(String codMansione) {
		return entityManager.createNamedQuery("DeMansioneMinFindByCodmansione", DeMansioneMin.class)
				.setHint("org.hibernate.cacheable", true).setParameter("query", "%" + codMansione + "%")
				.setMaxResults(10).getResultList();
	}

	public List<DeMansioneMinDTO> findDTOBySuggestionAndCodMansione(String par, String codMansione) {
		List<DeMansioneMin> entityList = null;
		if (par == null || par.trim().isEmpty()) {
			TypedQuery<DeMansioneMin> query = entityManager
					.createNamedQuery("findDeMansioneMinByCodMansione", DeMansioneMin.class)
					.setParameter("codMansione", codMansione);
			entityList = query.getResultList();
		} else {
			TypedQuery<DeMansioneMin> query = entityManager
					.createNamedQuery("findDeMansioneMinBySuggestionAndCodMansione", DeMansioneMin.class)
					.setParameter("codMansione", codMansione).setParameter("par", "%" + par.trim().toUpperCase() + "%");
			entityList = query.getResultList();
		}

		List<DeMansioneMinDTO> dtoList = new ArrayList<DeMansioneMinDTO>(entityList.size());
		for (DeMansioneMin entity : entityList) {
			dtoList.add(toDTO(entity));
		}
		return dtoList;
	}

	public List<DeMansioneMin> findBySuggestionNoDTO(String startsWith) {
		return findMansioniStartingWith(startsWith);
	}

}

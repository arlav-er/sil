package it.eng.myportal.entity.home.decodifiche;

// Generated 1-set-2011 12.32.46 by Hibernate Tools 3.4.0.CR1
import it.eng.myportal.dtos.DeProvinciaDTO;
import it.eng.myportal.entity.decodifiche.DeProvincia;
import it.eng.myportal.entity.decodifiche.DeProvincia_;
import it.eng.myportal.entity.decodifiche.DeRegione;
import it.eng.myportal.exception.MyPortalException;
import it.eng.myportal.utils.ConstantsSingleton;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.faces.model.SelectItem;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

/**
 * Home object for domain model class DeProvincia.
 * 
 * @see it.eng.myportal.entity.decodifiche.DeProvincia
 * @author Rodi A.
 */
@Stateless
public class DeProvinciaHome extends AbstractSuggestibleHome<DeProvincia, DeProvinciaDTO> {

	private static final String QUERY_SEL_BY_DESCR = " select c from DeProvincia c where upper(c.denominazione) like upper(:denominazione)";

	@EJB
	DeRegioneHome deRegioneHome;

	public DeProvincia findById(String id) {
		return findById(DeProvincia.class, id);
	}

	/**
	 * @deprecated utilizzare findByDescription()
	 * @param denominazione
	 * @return
	 */
	public DeProvincia findByDenominazione(String denominazione) {
		log.debug("getting DeProvincia instance with name: " + denominazione);
		DeProvincia instance = null;
		try {
			TypedQuery<DeProvincia> query = entityManager.createQuery(QUERY_SEL_BY_DESCR, DeProvincia.class);
			query.setParameter("denominazione", denominazione);
			instance = findById(((DeProvincia) query.getSingleResult()).getCodProvincia());
			log.debug("get DeProvincia successful");

		} catch (NoResultException nre) {
			log.error("Non è stato possibile trovare un'istanza DeProvincia usando la seguente query: " + denominazione
					+ " - errore: " + nre.getMessage());

		} catch (RuntimeException re) {
			log.error("Errore durante il tentativo di ricerca dell'istanza DeProvincia: " + re.getMessage());
		}
		return instance;
	}

	public List<DeProvinciaDTO> findByRegione(String codRegione) {
		log.debug("getting DeProvincia instance with name: " + codRegione);
		List<DeProvinciaDTO> provs = null;
		try {

			DeRegione reg = deRegioneHome.findById(codRegione);
			CriteriaBuilder qb = entityManager.getCriteriaBuilder();
			CriteriaQuery<DeProvincia> query = qb.createQuery(DeProvincia.class);
			Root<DeProvincia> provincia = query.from(DeProvincia.class);
			query.where(qb.equal(provincia.get(DeProvincia_.deRegione), reg));

			TypedQuery<DeProvincia> q = entityManager.createQuery(query).setHint("org.hibernate.cacheable", true);
			provs = findDTOByQuery(q);

			log.debug("get DeProvinciaDTO successful");

		} catch (NoResultException nre) {
			log.error("Non è stato possibile trovare un'istanza DeProvinciaDTO usando la seguente query: " + codRegione
					+ " - errore: " + nre.getMessage());

		} catch (RuntimeException re) {
			log.error("Errore durante il tentativo di ricerca dell'istanza DeProvinciaDTO: " + " - errore: "
					+ re.getMessage());
		}
		return provs;
	}

	public DeProvinciaDTO findDTOById(String codProvincia) {
		return toDTO(findById(codProvincia));
	}

	public DeProvinciaDTO findDTOBytarga(String targa) {

		CriteriaBuilder qb = entityManager.getCriteriaBuilder();
		CriteriaQuery<DeProvincia> query = qb.createQuery(DeProvincia.class);
		Root<DeProvincia> provincia = query.from(DeProvincia.class);
		query.where(qb.equal(provincia.get(DeProvincia_.targa), targa));

		TypedQuery<DeProvincia> q = entityManager.createQuery(query).setHint("org.hibernate.cacheable", true);
		DeProvincia deProvincia = null;

		try {
			deProvincia = q.getSingleResult();
		} catch (NoResultException e) {
			throw new MyPortalException("generic.error", e);
		} catch (NonUniqueResultException e) {
			throw new MyPortalException("generic.error", e);
		}

		return toDTO(deProvincia);
	}

	public DeProvinciaDTO findDTOByDenominazione(String denominazione) {
		log.debug("getting DeProvincia instance with name: " + denominazione);
		DeProvincia instance = null;
		try {
			TypedQuery<DeProvincia> query = entityManager.createQuery(QUERY_SEL_BY_DESCR, DeProvincia.class);
			query.setHint("org.hibernate.cacheable", true).setParameter("denominazione", denominazione);
			instance = findById(((DeProvincia) query.getSingleResult()).getCodProvincia());
			log.debug("get DeProvincia successful");

		} catch (NoResultException nre) {
			log.error("Non è stato possibile trovare un'istanza DeProvincia usando la seguente query: " + denominazione
					+ " - errore: " + nre.getMessage());

		} catch (RuntimeException re) {
			log.error("Errore durante il tentativo di ricerca dell'istanza DeProvincia: " + re.getMessage());
		}
		return toDTO(instance);
	}

	@Override
	public DeProvinciaDTO toDTO(DeProvincia entity) {
		if (entity == null) {
			return null;
		}

		DeProvinciaDTO dto = super.toDTO(entity);
		dto.setId(entity.getCodProvincia());
		dto.setDescrizione(entity.getDenominazione());
		dto.setIdRegione(entity.getDeRegione().getCodRegione());
		dto.setTarga(entity.getTarga());
		dto.setDestinatarioSare(entity.getDestinatarioSare());
		dto.setFaxRichiestaSare(entity.getFaxRichiestaSare());

		return dto;
	}

	@Override
	public DeProvincia fromDTO(DeProvinciaDTO dto) {
		if (dto == null)
			return null;
		DeProvincia entity = super.fromDTO(dto);

		entity.setCodProvincia(dto.getId());
		entity.setDenominazione(dto.getDescrizione());
		entity.setDeRegione(deRegioneHome.findById(dto.getIdRegione()));
		entity.setTarga(dto.getTarga());
		entity.setDestinatarioSare(dto.getDestinatarioSare());
		entity.setFaxRichiestaSare(dto.getFaxRichiestaSare());

		return entity;
	}

	@Override
	public String getFieldName() {
		return "denominazione";
	}

	public List<DeProvincia> findProvinceStartingWith(String startsWith) {
		return entityManager.createNamedQuery("findProvinciaLike", DeProvincia.class)
				.setParameter("codLike", startsWith + "%").setHint("org.hibernate.cacheable", true).getResultList();
	}

	/** I possibili destinatari di una notifica sono le province della regione del portale */
	public List<SelectItem> getListItemsDestinatariNotifica() {
		DeRegione regioneAttuale = deRegioneHome.findById(ConstantsSingleton.COD_REGIONE.toString());

		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<DeProvincia> criteria = cb.createQuery(DeProvincia.class);
		Root<DeProvincia> deProvinciaRoot = criteria.from(DeProvincia.class);
		criteria.select(deProvinciaRoot);
		criteria.where(cb.equal(deProvinciaRoot.get(DeProvincia_.deRegione), regioneAttuale));
		TypedQuery<DeProvincia> query = entityManager.createQuery(criteria);

		List<DeProvincia> entityList = query.getResultList();
		List<SelectItem> result = new ArrayList<SelectItem>();
		for (DeProvincia entity : entityList) {
			SelectItem item = new SelectItem();
			item.setValue(entity.getCodProvincia());
			item.setLabel(entity.getDenominazione());
			result.add(item);
		}
		return result;
	}
}

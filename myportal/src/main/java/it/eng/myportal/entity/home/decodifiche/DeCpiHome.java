package it.eng.myportal.entity.home.decodifiche;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.faces.model.SelectItem;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import it.eng.myportal.dtos.DeCpiDTO;
import it.eng.myportal.entity.decodifiche.DeComune;
import it.eng.myportal.entity.decodifiche.DeComune_;
import it.eng.myportal.entity.decodifiche.DeCpi;
import it.eng.myportal.entity.decodifiche.DeCpi_;
import it.eng.myportal.entity.decodifiche.DeProvincia;
import it.eng.myportal.entity.decodifiche.DeProvincia_;
import it.eng.myportal.entity.decodifiche.DeRegione;
import it.eng.myportal.entity.decodifiche.DeRegione_;
import it.eng.myportal.exception.MyPortalException;

/**
 * Home object for domain model class DeAbilitazioneGen.
 * 
 * 
 * @see it.eng.myportal.entity.decodifiche.DeAbilitazioneGen
 * @author Rodi A.
 */
@Stateless
public class DeCpiHome extends AbstractSuggestibleHome<DeCpi, DeCpiDTO> {

	@EJB
	private DeComuneHome deComuneHome;

	@EJB
	private DeProvinciaHome deProvinciaHome;

	@EJB
	private DeRegioneHome deRegioneHome;

	@EJB
	private DeCpiHome deCpiHome;
	
	@Override
	public DeCpi findById(String id) {
		return findById(DeCpi.class, id);
	}

	public DeCpiDTO findDTOByCodComune(String codCom) {
		DeComune deComune = deComuneHome.findById(codCom);
		if (deComune != null && deComune.getDeCpi() != null) {
			return toDTO(deComune.getDeCpi());
		}
		return null;
	}

	@Override
	public DeCpiDTO toDTO(DeCpi entity) {
		if (entity == null) {
			return null;
		}
		DeCpiDTO dto = super.toDTO(entity);
		dto.setId(entity.getCodCpi());
		DeComune deComune = entity.getDeComune();
		if (deComune != null) {
			dto.setCodCom(deComune.getCodCom());
			dto.setStrCom(deComune.getDenominazione());
		}

		dto.setEmailServiziOnline(entity.getEmailServiziOnline());
		dto.setDescrizione(entity.getDescrizione());
		dto.setIndirizzo(entity.getIndirizzo());
		dto.setLocalita(entity.getLocalita());
		dto.setCap(entity.getCap());
		dto.setTel(entity.getTel());
		dto.setFax(entity.getFax());
		dto.setEmail(entity.getEmail());
		dto.setOrario(entity.getOrario());
		dto.setNote(entity.getNote());
		dto.setResponsabile(entity.getResponsabile());
		dto.setEmailMigrazione(entity.getEmailMigrazione());
		dto.setIndirizzoStampa(entity.getIndirizzoStampa());
		dto.setRifSms(entity.getRifSms());
		dto.setEmailRifCl(entity.getEmailRifCl());
		dto.setTelRifCl(entity.getTelRifCl());
		dto.setEmailPortale(entity.getEmailPortale());
		dto.setDescrizioneMin(entity.getDescrizioneMin());
		dto.setFlgPatronato(entity.getFlgPatronato());
		dto.setTelPatronato(entity.getTelPatronato());
		dto.setCodCpiMin(entity.getCodCpiMin());
		if (entity.getDeProvincia() != null) {
			dto.setCodProvincia(entity.getDeProvincia().getCodProvincia());
		}

		return dto;
	}

	@Override
	public DeCpi fromDTO(DeCpiDTO dto) {
		if (dto == null) {
			return null;
		}
		DeCpi e = super.fromDTO(dto);
		e.setCodCpi(dto.getId());
		e.setDeComune(deComuneHome.findById(dto.getCodCom()));
		e.setEmailServiziOnline(dto.getEmailServiziOnline());

		e.setDescrizione(dto.getDescrizione());
		e.setIndirizzo(dto.getIndirizzo());
		e.setLocalita(dto.getLocalita());
		e.setCap(dto.getCap());
		e.setTel(dto.getTel());
		e.setFax(dto.getFax());
		e.setEmail(dto.getEmail());
		e.setOrario(dto.getOrario());
		e.setNote(dto.getNote());
		e.setResponsabile(dto.getResponsabile());
		e.setEmailMigrazione(dto.getEmailMigrazione());
		e.setIndirizzoStampa(dto.getIndirizzoStampa());
		e.setRifSms(dto.getRifSms());
		e.setEmailRifCl(dto.getEmailRifCl());
		e.setTelRifCl(dto.getTelRifCl());
		e.setEmailPortale(dto.getEmailPortale());
		e.setDescrizioneMin(dto.getDescrizioneMin());
		e.setFlgPatronato(dto.getFlgPatronato());
		e.setTelPatronato(dto.getTelPatronato());
		e.setCodCpiMin(dto.getCodCpiMin());
		if (dto.getCodProvincia() != null) {
			e.setDeProvincia(deProvinciaHome.findById(dto.getCodProvincia()));
		}

		return e;
	}

	public DeCpi findByCodMin(String codMin) {
		TypedQuery<DeCpi> query = entityManager.createNamedQuery("findDeCpiByCodMin", DeCpi.class);
		query.setParameter("codMin", codMin.trim().toUpperCase());
		List<DeCpi> results = query.getResultList();
		if (results != null && !results.isEmpty()) {
			return results.get(0);
		} else {
			throw new MyPortalException("Errore nella findDeCpiByCodMin per codMin: " + codMin);
		}
	}

	public DeCpiDTO findDTOByCodMin(String codMin) {
		return toDTO(findByCodMin(codMin));
	}

	private TypedQuery<DeCpi> getByCodRegioneQuery(String codRegione) {
		DeRegione reg = deRegioneHome.findById(codRegione);
		CriteriaBuilder qb = entityManager.getCriteriaBuilder();
		CriteriaQuery<DeCpi> query = qb.createQuery(DeCpi.class);
		Root<DeCpi> cpi = query.from(DeCpi.class);
		Join<DeCpi, DeComune> comune = cpi.join(DeCpi_.deComune);
		Join<DeComune, DeProvincia> provincia = comune.join(DeComune_.deProvincia);

		query.where(qb.equal(provincia.get(DeProvincia_.deRegione), reg));

		TypedQuery<DeCpi> q = entityManager.createQuery(query).setHint("org.hibernate.cacheable", true);
		return q;
	}
	
	private List<DeCpiDTO> getValidiByCodRegioneQuery(String codRegione) {
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<DeCpi> cq = cb.createQuery(DeCpi.class);
		Root<DeComune> comune = cq.from(DeComune.class);
		Join<DeComune, DeCpi> cpi = comune.join(DeComune_.deCpi);
		Join<DeComune, DeProvincia> provincia = comune.join(DeComune_.deProvincia);
		Join<DeProvincia, DeRegione> regione = provincia.join(DeProvincia_.deRegione);
		
		Date currentDate = new Date();
		List<Predicate> whereConditions = new ArrayList<Predicate>();
		whereConditions.add(cb.notEqual(cpi.get(DeCpi_.codCpi), "NT"));
		whereConditions.add(cb.lessThanOrEqualTo(cpi.get(DeCpi_.dtInizioVal), currentDate));
		whereConditions.add(cb.greaterThan(cpi.get(DeCpi_.dtFineVal), currentDate));
		whereConditions.add(cb.equal(regione.get(DeRegione_.codRegione), codRegione));
		cq.select(cpi).distinct(true);
		cq.where(whereConditions.toArray(new Predicate[whereConditions.size()]));
		cq.orderBy(cb.asc(cpi.get(DeCpi_.descrizione)));
		
		TypedQuery<DeCpi> tq = entityManager.createQuery(cq);
		tq.setHint("org.hibernate.cacheable", true);
		List<DeCpi> cpis = tq.getResultList();
		List<DeCpiDTO> cpiDTOs = new ArrayList<DeCpiDTO>();
		for (DeCpi deCpi : cpis) {
			cpiDTOs.add(deCpiHome.toDTO(deCpi));
		}
		return cpiDTOs;
	}

	private TypedQuery<DeCpi> getActiveByCodRegioneQuery(String codRegione) {
		DeRegione reg = deRegioneHome.findById(codRegione);
		CriteriaBuilder qb = entityManager.getCriteriaBuilder();
		CriteriaQuery<DeCpi> query = qb.createQuery(DeCpi.class);
		Root<DeCpi> cpi = query.from(DeCpi.class);
		Join<DeCpi, DeComune> comune = cpi.join(DeCpi_.deComune);
		Join<DeComune, DeProvincia> provincia = comune.join(DeComune_.deProvincia);

		List<Predicate> whereConditions = new ArrayList<Predicate>();
		whereConditions.add(qb.equal(provincia.get(DeProvincia_.deRegione), reg));
		whereConditions.add(qb.isNotNull(cpi.get(DeCpi_.codIntermediarioCl)));

		query.where(whereConditions.toArray(new Predicate[whereConditions.size()]));

		TypedQuery<DeCpi> q = entityManager.createQuery(query).setHint("org.hibernate.cacheable", true);
		return q;
	}

	public List<DeCpiDTO> findDTOByCodRegione(String codRegione) {

		TypedQuery<DeCpi> q = getByCodRegioneQuery(codRegione);
		List<DeCpiDTO> ret = findDTOByQuery(q);
		return ret;
	}
	
	public List<DeCpiDTO> findDTOValidiByCodRegione(String codRegione) {
		List<DeCpiDTO> ret = getValidiByCodRegioneQuery(codRegione);
		return ret;
	}

	public List<DeCpi> findByCodRegione(String codRegione) {

		TypedQuery<DeCpi> q = getByCodRegioneQuery(codRegione);
		return q.getResultList();
	}

	public List<DeCpi> findActiveByCodRegione(String codRegione) {

		TypedQuery<DeCpi> q = getActiveByCodRegioneQuery(codRegione);
		return q.getResultList();
	}

	public DeCpi findByCodIntermediarioCl(String codIntermediarioCl) {

		try {
			DeCpi ret = entityManager.createNamedQuery("findByCodIntermediarioCl", DeCpi.class)
					.setHint("org.hibernate.cacheable", true).setParameter("codIntermediarioCl", codIntermediarioCl)
					.getSingleResult();

			return ret;
		} catch (NoResultException e) {
			return null;
		}
	}

	public List<DeCpiDTO> findBySuggestionAndProvincia(String suggestion, String codProvincia) {

		List<DeCpi> listOfEntities = entityManager.createNamedQuery("findBySuggestionAndProvincia", DeCpi.class)
				.setParameter("suggestion", "%" + suggestion.toUpperCase() + "%")
				.setHint("org.hibernate.cacheable", true).setParameter("codProvincia", codProvincia).getResultList();

		List<DeCpiDTO> listOfDtos = new ArrayList<DeCpiDTO>();
		if (listOfEntities != null) {
			for (Object deCpi : listOfEntities) {
				listOfDtos.add(toDTO((DeCpi) deCpi));
			}
		}

		return listOfDtos;

	}

	private static final String SELECT_CPI_BY_PROVINCIA_ITEM_QUERY = "SELECT new javax.faces.model.SelectItem(p.codCpi, CONCAT(upper(p.descrizione) , ' - ' , p.indirizzo) ) FROM DeCpi p , DeComune com WHERE com.deCpi.codCpi = p.codCpi AND com.deProvincia.codProvincia = :codProvincia AND p.codCpi != 'NT' AND p.dtInizioVal < :now AND :now < p.dtFineVal GROUP BY p.codCpi,upper(p.descrizione) ORDER BY p.descrizione";

	public List<SelectItem> getListItemsCpiByProvincia(String codProvincia, boolean addBlank) {
		TypedQuery<SelectItem> typedQuery = entityManager.createQuery(SELECT_CPI_BY_PROVINCIA_ITEM_QUERY,
				SelectItem.class);
		typedQuery.setParameter("codProvincia", codProvincia);
		typedQuery.setParameter("now", new Date());
		return getListItems(typedQuery, addBlank);
	}

	public List<DeCpiDTO> findDTOValidiByProvincia(String codProvincia) {
		TypedQuery<DeCpi> query = entityManager.createNamedQuery("findDeCpiValideByProvincia", DeCpi.class);
		query.setHint("org.hibernate.cacheable", true);
		query.setParameter("codProvincia", codProvincia);
		query.setParameter("now", new Date());
		List<DeCpi> tempResult = query.getResultList();
		List<DeCpiDTO> finalResult = new ArrayList<DeCpiDTO>();
		for (DeCpi cpi : tempResult) {
			finalResult.add(toDTO(cpi));
		}
		return finalResult;
	}

	private static final String SELECT_CPI_BY_PROVINCIA_PATRONATO_ITEM_QUERY = "select new javax.faces.model.SelectItem(p.codCpi, CONCAT(upper(p.descrizione) , ' - ' , p.indirizzo) ) from DeCpi p , DeComune com where com.deCpi.codCpi = p.codCpi and com.deProvincia.codProvincia = :codProvincia and p.codCpi != 'NT' and p.flgPatronato = TRUE group by p.codCpi,upper(p.descrizione) order by p.descrizione";

	public List<SelectItem> getListItemsCpiByProvinciaPatronato(String codProvincia, boolean addBlank) {
		TypedQuery<SelectItem> typedQuery = entityManager.createQuery(SELECT_CPI_BY_PROVINCIA_PATRONATO_ITEM_QUERY,
				SelectItem.class);
		typedQuery.setParameter("codProvincia", codProvincia);
		return getListItems(typedQuery, addBlank);
	}

}

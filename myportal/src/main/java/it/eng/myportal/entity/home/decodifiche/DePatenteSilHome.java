package it.eng.myportal.entity.home.decodifiche;

import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;

import it.eng.myportal.dtos.DePatenteDTO;
import it.eng.myportal.entity.decodifiche.sil.DePatenteSil;

@Stateless
public class DePatenteSilHome extends AbstractSuggestibleHome<DePatenteSil, DePatenteDTO> {
	private static final String QUERY_RICERCA = "select c from DePatenteSil c where current_date between c.dtInizioVal and c.dtFineVal order by c.descrizione";
	private static final String QUERY_SEL_BY_DESCR = " select c from DePatenteSil c where upper(c.descrizione) like :descrizione";

	@Override
	public DePatenteSil findById(String id) {
		return findById(DePatenteSil.class, id);
	}

	@Override
	public DePatenteDTO toDTO(final DePatenteSil entity) {
		if (entity == null)
			return null;
		DePatenteDTO dto = new DePatenteDTO();
		dto.setDtInizioVal(entity.getDtInizioVal());
		dto.setDtFineVal(entity.getDtFineVal());
		dto.setId(entity.getCodPatenteSil());
		dto.setDescrizione(entity.getDescrizione());
		return dto;
	}

	@Override
	public DePatenteSil fromDTO(final DePatenteDTO dto) {
		if (dto == null)
			return null;
		DePatenteSil entity = super.fromDTO(dto);
		entity.setCodPatenteSil(dto.getId());
		entity.setDescrizione(dto.getDescrizione());
		return entity;
	}

	public List<DePatenteSil> getAll() {
		TypedQuery<DePatenteSil> query = entityManager.createQuery(QUERY_RICERCA, DePatenteSil.class);
		List<DePatenteSil> resultList = query.getResultList();
		return resultList;
	}

	/**
	 * Query usata dalla ricerca vacancy (tramite SOLR) per prendere anche le decodifiche scadute.
	 * 
	 * @param descrizione
	 * @return
	 */
	public DePatenteSil findByDescrizione(String descrizione) {
		log.debug("getting DePatenteSil instance with name: " + descrizione);
		DePatenteSil instance = null;
		try {
			TypedQuery<DePatenteSil> query = entityManager.createQuery(QUERY_SEL_BY_DESCR, DePatenteSil.class);

			query.setParameter("descrizione", descrizione.toUpperCase());
			instance = findById(((DePatenteSil) query.getSingleResult()).getCodPatenteSil());
			log.debug("get DePatenteSil successful");

		} catch (NoResultException nre) {
			log.error("Non è stato possibile trovare un'istanza DePatenteSil usando la seguente query: " + descrizione
					+ " - errore: " + nre.getMessage());

		} catch (RuntimeException re) {
			log.error("Errore durante il tentativo di ricerca dell'istanza DePatenteSil: " + re.getMessage());
		}
		return instance;
	}

	public List<DePatenteSil> findBySuggestionNoDto(String word) {
		List<DePatenteSil> deList = entityManager
				.createNamedQuery("DePatenteSilFindSuggestionValide", DePatenteSil.class)
				.setHint("org.hibernate.cacheable", true).setParameter("query", "%"+word+"%").getResultList();
		
		return deList;
	}
}

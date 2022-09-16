package it.eng.myportal.entity.home.decodifiche;

import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;

import it.eng.myportal.dtos.DePatenteDTO;
import it.eng.myportal.entity.decodifiche.DePatente;

/**
 * 
 * @author Rodi A.
 */
@Stateless
public class DePatenteHome extends AbstractSuggestibleHome<DePatente, DePatenteDTO> {

	private static final String QUERY_SEL_BY_DESCR = " select c from DePatente c where upper(c.descrizione) like :descrizione";
	
	private static final String QUERY_RICERCA = "select c from DePatente c where current_date between c.dtInizioVal and c.dtFineVal order by c.descrizione";
	
	public DePatente findById(final String id) {
		return findById(DePatente.class, id);
	}

	@Override
	public DePatenteDTO toDTO(final DePatente entity) {
		if (entity == null)
			return null;
		DePatenteDTO dto = super.toDTO(entity);
		dto.setId(entity.getCodPatente());
		dto.setDescrizione(entity.getDescrizione());
		return dto;
	}

	@Override
	public DePatente fromDTO(final DePatenteDTO dto) {
		if (dto == null)
			return null;
		DePatente entity = super.fromDTO(dto);
		entity.setCodPatente(dto.getId());
		entity.setDescrizione(dto.getDescrizione());
		return entity;
	}

	@Override
	public String getFieldName() {
		return "descrizioneInfo";
	}
		
	public DePatente findByDescrizione(String descrizione) {
		log.debug("getting DePatente instance with name: " + descrizione);
		DePatente instance = null;
		try {
			TypedQuery<DePatente> query = entityManager.createQuery(QUERY_SEL_BY_DESCR, DePatente.class);
			
			query.setParameter("descrizione", descrizione.toUpperCase());
			instance = findById(((DePatente) query.getSingleResult()).getCodPatente());
			log.debug("get DePatente successful");

		} catch (NoResultException nre) {
			log.error("Non è stato possibile trovare un'istanza DePatente usando la seguente query: " + descrizione + " - errore: " + nre.getMessage());

		} catch (RuntimeException re) {
			log.error("Errore durante il tentativo di ricerca dell'istanza DePatente: " + re.getMessage());
		}
		return instance;
	}

	public List<DePatente> getAll() {		
		TypedQuery<DePatente> query = entityManager.createQuery(QUERY_RICERCA, DePatente.class);
		List<DePatente> resultList = query.getResultList();		
		return resultList;
	}

	public List<DePatente> findBySuggestionNoDto(String word) {
		List<DePatente> deList = entityManager
				.createNamedQuery("DePatenteFindSuggestionValide", DePatente.class)
				.setHint("org.hibernate.cacheable", true).setParameter("query", "%"+word+"%").getResultList();
		
		return deList;
	}
	
}

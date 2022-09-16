package it.eng.myportal.entity.home.decodifiche;


import java.util.List;

import javax.ejb.Stateless;
import javax.faces.model.SelectItem;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import it.eng.myportal.dtos.DeOrarioDTO;
import it.eng.myportal.entity.decodifiche.DeOrario;

/**
 * Home object for domain model class DeOrario.
 * 
 * @see it.eng.myportal.entity.decodifiche.DeOrario
 * @author Rodi A.
 */
@Stateless
public class DeOrarioHome extends AbstractDecodeHome<DeOrario, DeOrarioDTO> {

	private static final String QUERY_SEL_ORARI = "select new javax.faces.model.SelectItem(c.codOrario,c.descrizione ) from DeOrario c ";
	private static final String QUERY_SEL_BY_DESCR = " select c from DeOrario c where upper(c.descrizione) like :descrizione";

	private static final String QUERY_RICERCA = "select c from DeOrario c where current_date between c.dtInizioVal and c.dtFineVal order by c.descrizione";
	
	
	@SuppressWarnings("unchecked")
	public List<SelectItem> selectOrariItems(boolean addBlank) {
		List<SelectItem> selectItems = null;

		try {
			Query query = entityManager.createQuery(QUERY_SEL_ORARI);
			selectItems = (List<SelectItem>) query.getResultList();
			if (addBlank)
				selectItems.add(0, new SelectItem("", ""));

		} catch (NoResultException nre) {
			log.error("Nessun risultato trovato selectOrariItems(): "+ nre.getMessage());
		} catch (RuntimeException re) {
			log.error("Errore RuntimeException in selectOrariItems(): "+ re.getMessage());
		}
		return selectItems;
	}

	public DeOrario findById(String id) {
		return findById(DeOrario.class, id);
	}

	@Override
	public DeOrarioDTO toDTO(DeOrario entity) {
		if (entity == null)
			return null;
		DeOrarioDTO dto = super.toDTO(entity);
		dto.setId(entity.getCodOrario());
		dto.setDescrizione(entity.getDescrizione());
		return dto;
	}

	@Override
	public DeOrario fromDTO(DeOrarioDTO dto) {
		if (dto == null)
			return null;
		DeOrario entity = super.fromDTO(dto);
		entity.setCodOrario(dto.getId());
		entity.setDescrizione(dto.getDescrizione());
		return entity;
	}
	
	
	public DeOrario findByDescrizione(String descrizione) {
		log.debug("getting DeOrario instance with name: " + descrizione);
		DeOrario instance = null;
		try {
			TypedQuery<DeOrario> query = entityManager.createQuery(QUERY_SEL_BY_DESCR, DeOrario.class);

			// query.setParameter("denominazione", denominazione.replace("'",
			// "''"));
			query.setParameter("descrizione", descrizione.toUpperCase());
			instance = findById(((DeOrario) query.getSingleResult()).getCodOrario());
			log.debug("get DeOrario successful");

		} catch (NoResultException nre) {
			log.error("Non è stato possibile trovare un'istanza DeOrario usando la seguente query: " + descrizione + " - errore: " + nre.getMessage());

		} catch (RuntimeException re) {
			log.error("Errore durante il tentativo di ricerca dell'istanza DeOrario: " + re.getMessage());
		} 
		return instance;
	}
	
	
	public List<DeOrario> getAll() {		
		TypedQuery<DeOrario> query = entityManager.createQuery(QUERY_RICERCA, DeOrario.class).setHint("org.hibernate.cacheable", true);
		List<DeOrario> resultList = query.getResultList();		
		return resultList;
	}
	
}

package it.eng.myportal.entity.home.decodifiche;

import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;

import it.eng.myportal.dtos.DeOrarioDTO;
import it.eng.myportal.entity.decodifiche.sil.DeContrattoSil;
import it.eng.myportal.entity.decodifiche.sil.DeOrarioSil;

@Stateless
public class DeOrarioSilHome extends AbstractDecodeHome<DeOrarioSil, DeOrarioDTO> {
	private static final String QUERY_RICERCA = "select c from DeOrarioSil c where current_date between c.dtInizioVal and c.dtFineVal order by c.descrizione";
	private static final String QUERY_SEL_BY_DESCR = " select c from DeOrarioSil c where upper(c.descrizione) like :descrizione";

	@Override
	public DeOrarioSil findById(String id) {
		return findById(DeOrarioSil.class, id);
	}

	@Override
	public DeOrarioDTO toDTO(DeOrarioSil entity) {
		if (entity == null)
			return null;
		DeOrarioDTO dto = new DeOrarioDTO();
		dto.setDtInizioVal(entity.getDtInizioVal());
		dto.setDtFineVal(entity.getDtFineVal());
		dto.setId(entity.getCodOrarioSil());
		dto.setDescrizione(entity.getDescrizione());
		return dto;
	}

	@Override
	public DeOrarioSil fromDTO(DeOrarioDTO dto) {
		if (dto == null)
			return null;
		DeOrarioSil entity = super.fromDTO(dto);
		entity.setCodOrarioSil(dto.getId());
		entity.setDescrizione(dto.getDescrizione());
		return entity;
	}

	public List<DeOrarioSil> getAll() {
		TypedQuery<DeOrarioSil> query = entityManager.createQuery(QUERY_RICERCA, DeOrarioSil.class).setHint(
				"org.hibernate.cacheable", true);
		List<DeOrarioSil> resultList = query.getResultList();
		return resultList;
	}

	/**
	 * Funziona usata dalla ricerca vacancy (tramite SOLR): prende anche le righe scadute
	 * 
	 * @param descrizione
	 * @return
	 */
	public DeOrarioSil findByDescrizione(String descrizione) {
		log.debug("getting DeOrarioSil instance with name: " + descrizione);
		DeOrarioSil instance = null;
		try {
			TypedQuery<DeOrarioSil> query = entityManager.createQuery(QUERY_SEL_BY_DESCR, DeOrarioSil.class);

			// query.setParameter("denominazione", denominazione.replace("'",
			// "''"));
			query.setParameter("descrizione", descrizione.toUpperCase());
			instance = findById(((DeOrarioSil) query.getSingleResult()).getCodOrarioSil());
			log.debug("get DeOrarioSil successful");

		} catch (NoResultException nre) {
			log.error("Non è stato possibile trovare un'istanza DeOrarioSil usando la seguente query: " + descrizione
					+ " - errore: " + nre.getMessage());
		} catch (RuntimeException re) {
			log.error("Errore durante il tentativo di ricerca dell'istanza DeOrarioSil: " + re.getMessage());
		}
		return instance;
	}

	public List<DeOrarioSil> findBySuggestionNoDto(String word) {
		List<DeOrarioSil> deList = entityManager
				.createNamedQuery("DeOrarioSilFindSuggestionValide", DeOrarioSil.class)
				.setHint("org.hibernate.cacheable", true).setParameter("query", "%"+word+"%").getResultList();
		
		return deList;
	}
	
	public List<DeOrarioSil> findBySuggestionFlagIdoTrueNoDto(String word) {
		List<DeOrarioSil> deList = entityManager
				.createNamedQuery("deOrarioSilFindSuggestionValideFlagIdoTrue", DeOrarioSil.class)
				.setHint("org.hibernate.cacheable", true).setParameter("query", "%"+word+"%").getResultList();
		
		return deList;
	}
	
	public List<DeOrarioSil> findValideByFlagIdo(boolean flagIdo) {
		List<DeOrarioSil> deOrarioSilS = entityManager
					.createNamedQuery("deOrarioValideByFlagIdo", DeOrarioSil.class)
					.setParameter("flagIdo", flagIdo)
					.getResultList();
		return deOrarioSilS;
	}
	
	public DeOrarioSil findValideSILByFlagIdo(boolean flagIdo, String tipoOrario) {
		DeOrarioSil deOrarioSilS = entityManager
					.createNamedQuery("deOrarioValideSILByFlagIdo", DeOrarioSil.class)
					.setParameter("flagIdo", flagIdo)
					.setParameter("tipoOrario", tipoOrario)
					.getSingleResult();
		return deOrarioSilS;
	}
}

package it.eng.myportal.entity.home.decodifiche;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;

import it.eng.myportal.dtos.DeProfessioneDTO;
import it.eng.myportal.entity.decodifiche.DeProfessione;

/**
 * Home object for domain model class DeProfessione.
 * 
 * @see it.eng.myportal.entity.decodifiche.DeProfessione
 * @author Rodi A.
 */
@Stateless
public class DeProfessioneHome extends AbstractTreeableHome<DeProfessione, DeProfessioneDTO> {

	private static final String QUERY_SEL_BY_DESCR = " select c from DeProfessione c where upper(c.denominazione) like upper(:denominazione)";

	public DeProfessione findById(final String id) {
		return findById(DeProfessione.class, id);
	}

	/**
	 * @deprecated utilizzare findByDescription()
	 * @param denominazione
	 * @return
	 */
	public DeProfessione findByDenominazione(String denominazione) {
		log.debug("getting DeProfessione instance with name: " + denominazione);
		DeProfessione instance = null;
		try {
			TypedQuery<DeProfessione> query = entityManager.createQuery(QUERY_SEL_BY_DESCR, DeProfessione.class);

			query.setParameter("denominazione", denominazione.replace("'", "''"));
			instance = findById(((DeProfessione) query.getSingleResult()).getCodProfessione());
			log.debug("get DeProfessione successful");

		} catch (NoResultException nre) {
			log.error("Non è stato possibile trovare un'istanza DeProfessione usando la seguente query: " + denominazione + " - errore: " + nre.getMessage());

		} catch (RuntimeException re) {
			log.error("Errore durante il tentativo di ricerca dell'istanza DeProfessione: " + re.getMessage());
		}
		return instance;
	}

	@Override
	public DeProfessioneDTO toDTO(DeProfessione entity) {
		if (entity == null) return null;
		DeProfessioneDTO dto = super.toDTO(entity);
		dto.setId(entity.getCodProfessione());
		dto.setDescrizione(entity.getDescrizione());
		return dto;
	}

	@Override
	public DeProfessione fromDTO(DeProfessioneDTO dto) {
		if (dto == null) return null;
		DeProfessione entity = super.fromDTO(dto);
		entity.setCodProfessione(dto.getId());
		entity.setDescrizione(dto.getDescrizione());
		return entity;
	}

	@Override
	public String getFieldName() {
		return "descrizione";
	}

	/**
	 * E' ad un solo livello!
	 */
	@Override
	public List<DeProfessioneDTO> findByCodPadre(String par) {
		if ("0".equals(par))
			return findAllDTO();
		else
			return new ArrayList<DeProfessioneDTO>();
	}

}

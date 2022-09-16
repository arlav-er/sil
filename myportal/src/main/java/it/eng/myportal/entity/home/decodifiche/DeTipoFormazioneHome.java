package it.eng.myportal.entity.home.decodifiche;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;

import it.eng.myportal.dtos.DeTipoFormazioneDTO;
import it.eng.myportal.entity.decodifiche.DeTipoFormazione;

/**
 * Classe Home
 * 
 * @see it.eng.myportal.dtos.it.eng.myportal.dtos.decodifiche.DeTipoFormazioneDTO
 * @author Rodi A.
 */
@Stateless
public class DeTipoFormazioneHome extends AbstractTreeableHome<DeTipoFormazione, DeTipoFormazioneDTO> {

	private static final String QUERY_SUGG_BY_DESCR = " select c from DeTipoFormazione c where lower(c.descrizione) like :query";

	public DeTipoFormazione findById(String id) {
		return findById(DeTipoFormazione.class, id);
	}

	public DeTipoFormazione findByDescrizione(String descrizione) {
		log.debug("getting DeTipoFormazione instance with name: " + descrizione);
		DeTipoFormazione instance = null;
		try {
			TypedQuery<DeTipoFormazione> query = entityManager.createQuery(QUERY_SUGG_BY_DESCR, DeTipoFormazione.class);

			query.setParameter("query", descrizione.toLowerCase().replace("'", "''"));
			instance = findById(((DeTipoFormazione) query.getSingleResult()).getCodTipoFormazione());
			log.debug("get DeTipoFormazione successful");

		} catch (NoResultException nre) {
			log.error("Non è stato possibile trovare un'istanza DeTipoFormazione usando la seguente query: " + descrizione + " - errore: " + nre.getMessage());

		} catch (RuntimeException re) {
			log.error("Errore durante il tentativo di ricerca dell'istanza DeTipoFormazione: " + re.getMessage());
		}
		return instance;
	}

	@Override
	public DeTipoFormazioneDTO toDTO(DeTipoFormazione entity) {
		if (entity == null)
			return null;
		DeTipoFormazioneDTO dto = super.toDTO(entity);
		dto.setId(entity.getCodTipoFormazione());
		dto.setDenominazione(entity.getDescrizione());
		dto.setDescrizione(entity.getDescrizione());
		return dto;
	}

	@Override
	public DeTipoFormazione fromDTO(DeTipoFormazioneDTO dto) {
		if (dto == null)
			return null;
		DeTipoFormazione entity = super.fromDTO(dto);
		entity = new DeTipoFormazione();
		entity.setDescrizione(dto.getDenominazione());
		return entity;
	}

	@Override
	public String getFieldName() {
		return "descrizione";
	}

	@Override
	public String getSuggestionQuery() {
		return QUERY_SUGG_BY_DESCR;
	}

	/**
	 * E' ad un solo livello!
	 */
	@Override
	public List<DeTipoFormazioneDTO> findByCodPadre(String par) {
		if ("0".equals(par))
			return findAllDTO();
		else
			return new ArrayList<DeTipoFormazioneDTO>();
	}

}

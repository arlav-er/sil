package it.eng.myportal.entity.home;

import it.eng.myportal.dtos.StNotiziaDTO;
import it.eng.myportal.entity.StNotizia;
import it.eng.myportal.entity.StNotizia_;

import java.util.Date;
import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

@Stateless
public class StNotiziaHome extends AbstractUpdatableHome<StNotizia, StNotiziaDTO> {
	@Override
	public StNotizia findById(Integer id) {
		return findById(StNotizia.class, id);
	}

	@Override
	public StNotiziaDTO toDTO(StNotizia entity) {
		if (entity == null)
			return null;
		StNotiziaDTO dto = super.toDTO(entity);

		dto.setId(entity.getIdStNotizia());
		dto.setCodTipoMessaggio(entity.getCodTipoMessaggio());
		dto.setContenuto(entity.getContenuto());
		dto.setOggetto(entity.getOggetto());
		dto.setDtmPubblicazione(entity.getDtmPubblicazione());
		dto.setDtmScadenza(entity.getDtmScadenza());
		dto.setOrdine(entity.getOrdine());
		return dto;
	}

	@Override
	public StNotizia fromDTO(StNotiziaDTO dto) {
		if (dto == null)
			return null;
		StNotizia entity = super.fromDTO(dto);
		entity.setIdStNotizia(dto.getId());
		entity.setDtmPubblicazione(dto.getDtmPubblicazione());
		entity.setDtmScadenza(dto.getDtmScadenza());
		entity.setContenuto(dto.getContenuto());
		entity.setOggetto(dto.getOggetto());
		entity.setCodTipoMessaggio(dto.getCodTipoMessaggio());
		entity.setOrdine(dto.getOrdine());
		return entity;
	}

	/**
	 * Restituisce la lista di tutte le notizie ordinate da mostrare a schermo
	 * 
	 * @return la lista delle notizie da mostrare
	 */
	public List<StNotiziaDTO> findAllCurrentDTO() {
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<StNotizia> c = cb.createQuery(StNotizia.class);
		Root<StNotizia> f = c.from(StNotizia.class);
		c.orderBy(cb.asc(f.get(StNotizia_.ordine)));
		c = c.where(cb.greaterThanOrEqualTo(f.get(StNotizia_.dtmScadenza), new Date()),
				cb.lessThanOrEqualTo(f.get(StNotizia_.dtmPubblicazione), new Date()));
		TypedQuery<StNotizia> createdQuery = entityManager.createQuery(c);
		return findDTOByQuery(createdQuery);
	}

	@Override
	public List<StNotizia> findAll() {
		final String queryHQL = "SELECT a FROM StNotizia a ORDER BY dtmIns DESC, dtmPubblicazione DESC";

		TypedQuery<StNotizia> query = entityManager.createQuery(queryHQL, StNotizia.class);

		return (List<StNotizia>) query.getResultList();
	}
}

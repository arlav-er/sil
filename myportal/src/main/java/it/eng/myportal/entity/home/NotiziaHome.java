package it.eng.myportal.entity.home;

import it.eng.myportal.dtos.NotiziaDTO;
import it.eng.myportal.entity.StNotizia;
import it.eng.myportal.entity.StNotizia_;

import java.util.Date;
import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

/**
 * @deprecated utilizzare StNotiziaHome
 * @author Rodi A.
 *
 */
@Stateless
public class NotiziaHome extends AbstractUpdatableHome<StNotizia, NotiziaDTO> {
	@Override
	public StNotizia findById(Integer id) {
		return findById(StNotizia.class, id);
	}

	@Override
	public NotiziaDTO toDTO(StNotizia entity) {
		if (entity == null)
			return null;
		NotiziaDTO dto = super.toDTO(entity);

		dto.setId(entity.getIdStNotizia());
		dto.setCodTipoMessaggio(entity.getCodTipoMessaggio());
		dto.setContenuto(entity.getContenuto());
		dto.setOggetto(entity.getOggetto());
		dto.setDtmPubblicazione(entity.getDtmPubblicazione());
		dto.setDtmScadenza(entity.getDtmScadenza());
				return dto;
	}

	@Override
	public StNotizia fromDTO(NotiziaDTO dto) {
		if (dto == null)
			return null;
		StNotizia entity = super.fromDTO(dto);
		entity.setIdStNotizia(dto.getId());
		//TODO
		return entity;
	}

	
	/**
	 * Restituisce la lista di tutte le notizie ordinate da mostrare a schermo
	 * @return la lista delle notizie da mostrare
	 */
	public List<NotiziaDTO> findAllCurrentDTO() {
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<StNotizia> c = cb.createQuery(StNotizia.class);
		Root<StNotizia> f = c.from(StNotizia.class);
		c.orderBy(cb.asc(f.get(StNotizia_.ordine)));
		c = c.where(cb.greaterThanOrEqualTo(f.get(StNotizia_.dtmScadenza),new Date()),
					cb.lessThanOrEqualTo(f.get(StNotizia_.dtmPubblicazione),new Date()));
		TypedQuery<StNotizia> createdQuery = entityManager.createQuery(c);
		return findDTOByQuery(createdQuery);
	}

}

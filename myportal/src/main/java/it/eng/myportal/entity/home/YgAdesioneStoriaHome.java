package it.eng.myportal.entity.home;

import it.eng.myportal.dtos.YgAdesioneStoriaDTO;
import it.eng.myportal.entity.YgAdesione;
import it.eng.myportal.entity.YgAdesioneStoria;
import it.eng.myportal.entity.YgAdesioneStoria_;
import it.eng.myportal.entity.home.decodifiche.DeStatoAdesioneHome;
import it.eng.myportal.entity.home.decodifiche.DeStatoAdesioneMinHome;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

@Stateless
public class YgAdesioneStoriaHome extends AbstractUpdatableHome<YgAdesioneStoria, YgAdesioneStoriaDTO> {

	@EJB
	private DeStatoAdesioneMinHome deStatoAdesioneMinHome;

	@EJB
	private DeStatoAdesioneHome deStatoAdesioneHome;

	@EJB
	private YgAdesioneHome ygAdesioneHome;

	@EJB
	private YgAdesioneStoriaHome ygAdesioneStoriaHome;

	@Override
	public YgAdesioneStoria findById(Integer id) {
		return findById(YgAdesioneStoria.class, id);
	}

	@Override
	public YgAdesioneStoria fromDTO(YgAdesioneStoriaDTO dto) {
		YgAdesioneStoria entity = super.fromDTO(dto);

		entity.setIdYgAdesioneStoria(dto.getId());
		if (dto.getDeStatoAdesioneMinDTO() != null) {
			entity.setDeStatoAdesioneMin(deStatoAdesioneMinHome.findById(dto.getDeStatoAdesioneMinDTO().getId()));
		}
		entity.setDtmFine(dto.getDtFine());
		entity.setDtmInizio(dto.getDtInizio());
		entity.setDtmStatoAdesione(dto.getDtStatoAdesione());
		if (dto.getDeStatoAdesioneDTO() != null) {
			entity.setDeStatoAdesione(deStatoAdesioneHome.findById(dto.getDeStatoAdesioneDTO().getId()));
		}
		if (dto.getYgAdesioneDTO() != null) {
			entity.setYgAdesione(ygAdesioneHome.findById(dto.getYgAdesioneDTO().getId()));
		}

		return entity;
	}

	@Override
	public YgAdesioneStoriaDTO toDTO(YgAdesioneStoria entity) {
		YgAdesioneStoriaDTO dto = super.toDTO(entity);

		dto.setId(entity.getIdYgAdesioneStoria());
		dto.setDeStatoAdesioneDTO(deStatoAdesioneHome.toDTO(entity.getDeStatoAdesione()));
		dto.setDeStatoAdesioneMinDTO(deStatoAdesioneMinHome.toDTO(entity.getDeStatoAdesioneMin()));
		dto.setDtFine(entity.getDtmFine());
		dto.setDtInizio(entity.getDtmInizio());
		dto.setDtStatoAdesione(entity.getDtmStatoAdesione());
		dto.setYgAdesioneDTO(ygAdesioneHome.toDTO(entity.getYgAdesione()));

		return dto;
	}

	/**
	 * Cerco l'ultimo storico dell'adesione passata in input. L'ultimo storico e' quello con dt_fine piu' recente, nel
	 * caso di risultati multipli prendo quello con id maggiore.
	 * 
	 * @param idYgAdesione
	 * @return
	 */
	public YgAdesioneStoria findLastByIdYgAdesione(Integer idYgAdesione) {
		CriteriaBuilder qb = entityManager.getCriteriaBuilder();
		CriteriaQuery<YgAdesioneStoria> query = qb.createQuery(YgAdesioneStoria.class);

		Root<YgAdesioneStoria> ygAdesioneStoria = query.from(YgAdesioneStoria.class);

		List<Predicate> whereConditions = new ArrayList<Predicate>();
		whereConditions.add(qb.equal(ygAdesioneStoria.get(YgAdesioneStoria_.ygAdesione),
				ygAdesioneHome.findById(idYgAdesione)));
		query.where(whereConditions.toArray(new Predicate[whereConditions.size()]));

		query.orderBy(qb.desc(ygAdesioneStoria.get(YgAdesioneStoria_.dtmFine)),
				qb.desc(ygAdesioneStoria.get(YgAdesioneStoria_.idYgAdesioneStoria)));

		TypedQuery<YgAdesioneStoria> q = entityManager.createQuery(query);
		q.setMaxResults(1);

		List<YgAdesioneStoria> list = q.getResultList();
		if (list.size() == 1) {
			return list.get(0);
		} else {
			return null;
		}
	}

	/**
	 * Inserisce una voce nello storico dell'adesione passata in input. STORICIZZA SEMPRE PRIMA DI AGGIORNARE
	 * YG_ADESIONE!!!
	 * 
	 * @param adesione
	 */
	public void storicizza(YgAdesione adesione, Integer idPfPrincipal) {
		/* recupero l'ultimo elemento della sotira dell'adesione */
		YgAdesioneStoria ultimoStorico = ygAdesioneStoriaHome.findLastByIdYgAdesione(adesione.getIdYgAdesione());

		YgAdesioneStoria ygAdesioneStoria = new YgAdesioneStoria();
		ygAdesioneStoria.setYgAdesione(adesione);
		ygAdesioneStoria.setDeStatoAdesioneMin(adesione.getDeStatoAdesioneMin());
		ygAdesioneStoria.setDeStatoAdesione(adesione.getDeStatoAdesione());
		ygAdesioneStoria.setDtmStatoAdesione(adesione.getDtStatoAdesioneMin());
		ygAdesioneStoria.setDtmFine(new Date());
		ygAdesioneStoria.setDtmInizio(ultimoStorico != null ? ultimoStorico.getDtmFine() : adesione
				.getDtStatoAdesioneMin());
		Date now = new Date();
		ygAdesioneStoria.setDtmIns(now);
		ygAdesioneStoria.setDtmMod(now);
		ygAdesioneStoria.setPfPrincipalIns(pfPrincipalHome.findById(idPfPrincipal));
		ygAdesioneStoria.setPfPrincipalMod(pfPrincipalHome.findById(idPfPrincipal));

		persist(ygAdesioneStoria);
	}

	/**
	 * Recupera tutti i YgAdesioneStoria di una certa adesione, ordinati per dtmFine desc.
	 */
	public List<YgAdesioneStoriaDTO> findDTOByIdYgAdesione(Integer idYgAdesione) {
		CriteriaBuilder qb = entityManager.getCriteriaBuilder();
		CriteriaQuery<YgAdesioneStoria> query = qb.createQuery(YgAdesioneStoria.class);
		Root<YgAdesioneStoria> ygAdesioneStoria = query.from(YgAdesioneStoria.class);
		List<Predicate> whereConditions = new ArrayList<Predicate>();
		whereConditions.add(qb.equal(ygAdesioneStoria.get(YgAdesioneStoria_.ygAdesione),
				ygAdesioneHome.findById(idYgAdesione)));
		query.where(whereConditions.toArray(new Predicate[whereConditions.size()]));

		query.orderBy(qb.desc(ygAdesioneStoria.get(YgAdesioneStoria_.dtmStatoAdesione)),
				qb.desc(ygAdesioneStoria.get(YgAdesioneStoria_.dtmInizio)));

		TypedQuery<YgAdesioneStoria> q = entityManager.createQuery(query);
		List<YgAdesioneStoria> resultList = q.getResultList();
		List<YgAdesioneStoriaDTO> resultListDTO = new ArrayList<YgAdesioneStoriaDTO>(resultList.size());
		for (YgAdesioneStoria result : resultList) {
			resultListDTO.add(toDTO(result));
		}
		return resultListDTO;
	}

}

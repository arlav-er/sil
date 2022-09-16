package it.eng.myportal.entity.home;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.primefaces.model.SortOrder;

import it.eng.myportal.dtos.FbChecklistDTO;
import it.eng.myportal.dtos.FbSchedaFabbisognoDTO;
import it.eng.myportal.dtos.filter.FbChecklistFilterDTO;
import it.eng.myportal.entity.AziendaInfo;
import it.eng.myportal.entity.AziendaInfo_;
import it.eng.myportal.entity.FbChecklist;
import it.eng.myportal.entity.FbChecklist_;
import it.eng.myportal.entity.FbSchedaFabbisogno;
import it.eng.myportal.entity.FbSchedaFabbisogno_;
import it.eng.myportal.entity.PfPrincipal;
import it.eng.myportal.entity.PfPrincipal_;
import it.eng.myportal.entity.decodifiche.DeStatoFbChecklist;
import it.eng.myportal.entity.decodifiche.DeStatoFbChecklist_;
import it.eng.myportal.entity.decodifiche.DeStatoFbScheda;
import it.eng.myportal.entity.decodifiche.DeStatoFbScheda_;
import it.eng.myportal.entity.home.decodifiche.DeFbCategoriaHome;
import it.eng.myportal.entity.home.decodifiche.DeStatoFbChecklistHome;
import it.eng.myportal.utils.ConstantsSingleton;

@Stateless
public class FbChecklistHome extends AbstractUpdatableHome<FbChecklist, FbChecklistDTO> {

	@EJB
	DeFbCategoriaHome deFbCategoriaHome;

	@EJB
	DeStatoFbChecklistHome deStatoFbChecklistHome;

	@EJB
	PfPrincipalHome pfPrincipalHome;

	@EJB
	FbSchedaFabbisognoHome fbSchedaFabbisognoHome;

	public FbChecklistDTO toDTO(FbChecklist entity) {
		FbChecklistDTO dto = super.toDTO(entity);
		dto.setId(entity.getIdFbChecklist());
		dto.setIdPfPrincipal(entity.getPfPrincipal().getIdPfPrincipal());
		dto.setDtValutazione(entity.getDtValutazione());
		dto.setIdValutatore(entity.getIdValutatore());
		dto.setDeFbCategoria(deFbCategoriaHome.toDTO(entity.getDeFbCategoria()));
		dto.setDeStatoFbChecklist(deStatoFbChecklistHome.toDTO(entity.getDeStatoFbChecklist()));
		dto.setMotivoRevoca(entity.getMotivoRevoca());
		dto.setDtPubblicazione(entity.getDtPubblicazione());

		// Necessario per non dovercelo andare a prendere dopo
		if (entity.getPfPrincipal() != null && entity.getPfPrincipal().getAziendaInfo() != null) {
			dto.setAziendaOspitante(entity.getPfPrincipal().getAziendaInfo().getRagioneSociale());
			dto.setCodiceFiscale(entity.getPfPrincipal().getAziendaInfo().getCodiceFiscale());
		}

		for (FbSchedaFabbisogno fbScheda : entity.getFbSchedaFabbisognoList()) {
			dto.getFbSchedaFabbisognoList().add(fbSchedaFabbisognoHome.toDTO(fbScheda));
		}

		return dto;
	}

	public FbChecklist fromDTO(FbChecklistDTO dto) {
		FbChecklist entity = super.fromDTO(dto);
		entity.setIdFbChecklist(dto.getId());
		entity.setPfPrincipal(pfPrincipalHome.findById(dto.getIdPfPrincipal()));
		entity.setDtValutazione(dto.getDtValutazione());
		entity.setIdValutatore(dto.getIdValutatore());
		entity.setDeFbCategoria(deFbCategoriaHome.fromDTO(dto.getDeFbCategoria()));
		entity.setDeStatoFbChecklist(deStatoFbChecklistHome.fromDTO(dto.getDeStatoFbChecklist()));
		entity.setMotivoRevoca(dto.getMotivoRevoca());
		entity.setDtPubblicazione(dto.getDtPubblicazione());

		for (FbSchedaFabbisognoDTO fbSchedaDto : dto.getFbSchedaFabbisognoList()) {
			entity.getFbSchedaFabbisognoList().add(fbSchedaFabbisognoHome.fromDTO(fbSchedaDto));
		}

		return entity;
	}

	@Override
	public FbChecklist findById(Integer id) {
		return findById(FbChecklist.class, id);
	}

	public List<FbChecklist> findByIdPfPrincipal(Integer idPfPrincipal) {
		TypedQuery<FbChecklist> query = entityManager.createNamedQuery("findFbChecklistByIdPfPrincipal",
				FbChecklist.class);
		query.setParameter("idPfPrincipal", idPfPrincipal);
		return query.getResultList();
	}

	public List<FbChecklistDTO> findDTOByIdPfPrincipal(Integer idPfPrincipal) {
		List<FbChecklist> entityList = findByIdPfPrincipal(idPfPrincipal);
		List<FbChecklistDTO> dtoList = new ArrayList<FbChecklistDTO>(entityList.size());
		for (FbChecklist entity : entityList) {
			dtoList.add(toDTO(entity));
		}
		return dtoList;
	}

	public List<FbChecklist> findByFilter(FbChecklistFilterDTO searchParams, Integer startFrom, Integer maxValue,
			String sortField, SortOrder order) {
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<FbChecklist> criteria = cb.createQuery(FbChecklist.class);
		Root<FbChecklist> fbChecklistRoot = criteria.from(FbChecklist.class);

		List<Predicate> whereConditions = createFindByFilterWhereConditions(searchParams, fbChecklistRoot);
		criteria.where(whereConditions.toArray(new Predicate[whereConditions.size()]));

		if (sortField != null && !"".equals(sortField)) {
			if (sortField.contains("."))
				sortField = sortField.substring(0, sortField.indexOf("."));

			// Campi non presenti sul DB solo in DTO (FbSchedaFabbisognoDTO)
			if ("aziendaOspitante".equals(sortField)) {
				if (SortOrder.ASCENDING.equals(order)) {
					criteria.orderBy(
							cb.asc(fbChecklistRoot.get("pfPrincipal").get("aziendaInfo").get("ragioneSociale")));
				} else {
					criteria.orderBy(
							cb.desc(fbChecklistRoot.get("pfPrincipal").get("aziendaInfo").get("ragioneSociale")));
				}
			} else {

				if (SortOrder.ASCENDING.equals(order)) {
					criteria.orderBy(cb.asc(fbChecklistRoot.get(sortField)));
				} else {
					criteria.orderBy(cb.desc(fbChecklistRoot.get(sortField)));
				}
			}
		} else {
			criteria.orderBy(cb.desc(fbChecklistRoot.get(FbChecklist_.dtPubblicazione)));
		}
		TypedQuery<FbChecklist> query = entityManager.createQuery(criteria);

		if (startFrom != null && startFrom > 0) {
			query.setFirstResult(startFrom);
		}

		if (maxValue != null && maxValue > 0) {
			query.setMaxResults(maxValue);
		}

		return query.getResultList();

	}

	public List<FbChecklistDTO> findDTOByFilter(FbChecklistFilterDTO searchParams, Integer startFrom, Integer maxValue,
			String sortField, SortOrder order) {

		List<FbChecklist> entityList = findByFilter(searchParams, startFrom, maxValue, sortField, order);

		List<FbChecklistDTO> dtoList = new ArrayList<FbChecklistDTO>(entityList.size());
		for (FbChecklist entity : entityList) {
			dtoList.add(toDTO(entity));
		}
		return dtoList;
	}

	public int findCountByFilter(FbChecklistFilterDTO searchParams) {
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<Long> criteria = cb.createQuery(Long.class);
		Root<FbChecklist> fbChecklistRoot = criteria.from(FbChecklist.class);
		criteria.select(cb.count(fbChecklistRoot));

		List<Predicate> whereConditions = createFindByFilterWhereConditions(searchParams, fbChecklistRoot);
		criteria.where(whereConditions.toArray(new Predicate[whereConditions.size()]));
		TypedQuery<Long> query = entityManager.createQuery(criteria);
		return query.getSingleResult().intValue();
	}

	private List<Predicate> createFindByFilterWhereConditions(FbChecklistFilterDTO searchParams,
			Root<FbChecklist> fbChecklistRoot) {
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		ArrayList<Predicate> predicateList = new ArrayList<Predicate>();
		Join<FbChecklist, DeStatoFbChecklist> deStatoFbChecklistJoin = fbChecklistRoot
				.join(FbChecklist_.deStatoFbChecklist);

		if (searchParams.getIdPfPrincipal() != null) {
			Join<FbChecklist, PfPrincipal> pfPrincipalJoin = fbChecklistRoot.join(FbChecklist_.pfPrincipal);
			predicateList
					.add(cb.equal(pfPrincipalJoin.get(PfPrincipal_.idPfPrincipal), searchParams.getIdPfPrincipal()));
		}

		if (searchParams.getDataDa() != null) {
			predicateList.add(cb.greaterThanOrEqualTo(fbChecklistRoot.get(FbChecklist_.dtPubblicazione),
					searchParams.getDataDa()));
		}

		if (searchParams.getDataA() != null) {
			predicateList.add(
					cb.lessThanOrEqualTo(fbChecklistRoot.get(FbChecklist_.dtPubblicazione), searchParams.getDataA()));
		}

		Join<FbChecklist, PfPrincipal> pfPrincipalJoin = fbChecklistRoot.join(FbChecklist_.pfPrincipal);
		Join<PfPrincipal, AziendaInfo> aziendaInfoJoin = pfPrincipalJoin.join(PfPrincipal_.aziendaInfo);

		if (searchParams.getAziendaOspitante() != null && !searchParams.getAziendaOspitante().trim().isEmpty()) {

			predicateList.add(cb.like(cb.upper(aziendaInfoJoin.get(AziendaInfo_.ragioneSociale)),
					"%" + searchParams.getAziendaOspitante().trim().toUpperCase() + "%"));
		}

		if (searchParams.getCodiceFiscale() != null) {
			predicateList.add(cb.equal(aziendaInfoJoin.get(AziendaInfo_.codiceFiscale),
					searchParams.getCodiceFiscale().trim().toUpperCase()));
		}

		if (searchParams.getCodStato() != null && !searchParams.getCodStato().trim().isEmpty()) {
			predicateList.add(cb.equal(deStatoFbChecklistJoin.get(DeStatoFbChecklist_.codStatoFbChecklist),
					searchParams.getCodStato().toUpperCase()));
		}

		if (searchParams.isSoloSchedaFabbisogno()) {
			Join<FbChecklist, FbSchedaFabbisogno> fbSchedeJoin = fbChecklistRoot
					.join(FbChecklist_.fbSchedaFabbisognoList);
			Join<FbSchedaFabbisogno, DeStatoFbScheda> deStatoFbSchedaJoin = fbSchedeJoin
					.join(FbSchedaFabbisogno_.deStatoScheda);
			predicateList.add(cb.and(cb.isNotEmpty(fbChecklistRoot.get(FbChecklist_.fbSchedaFabbisognoList)),
					cb.notEqual(deStatoFbSchedaJoin.get(DeStatoFbScheda_.codStatoFbScheda), ConstantsSingleton.DeStatoFbScheda.IN_LAVORAZIONE)));	
		}

		if (searchParams.isEscludiInLavorazione()) {
			predicateList.add(cb.not(cb.equal(deStatoFbChecklistJoin.get(DeStatoFbChecklist_.codStatoFbChecklist),
					ConstantsSingleton.DeStatoFbChecklist.IN_LAVORAZIONE)));
		}

		return predicateList;
	}

	public FbChecklistDTO chiudiChecklist(Integer idFbChecklist) {
		FbChecklist checklist = findById(idFbChecklist);
		DeStatoFbChecklist statoChiusa = deStatoFbChecklistHome.findById(ConstantsSingleton.DeStatoFbChecklist.CHIUSA);
		checklist.setDeStatoFbChecklist(statoChiusa);
		checklist = merge(checklist);
		return toDTO(checklist);
	}

}

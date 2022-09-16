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

import it.eng.myportal.dtos.FbCandidaturaDTO;
import it.eng.myportal.dtos.FbSchedaFabbisognoDTO;
import it.eng.myportal.dtos.filter.FbSchedaFabbisognoFilterDTO;
import it.eng.myportal.entity.AziendaInfo;
import it.eng.myportal.entity.AziendaInfo_;
import it.eng.myportal.entity.FbCandidatura;
import it.eng.myportal.entity.FbChecklist;
import it.eng.myportal.entity.FbChecklist_;
import it.eng.myportal.entity.FbSchedaFabbisogno;
import it.eng.myportal.entity.FbSchedaFabbisogno_;
import it.eng.myportal.entity.PfPrincipal;
import it.eng.myportal.entity.PfPrincipal_;
import it.eng.myportal.entity.decodifiche.DeComune;
import it.eng.myportal.entity.decodifiche.DeComune_;
import it.eng.myportal.entity.decodifiche.DeMansione;
import it.eng.myportal.entity.decodifiche.DeMansione_;
import it.eng.myportal.entity.decodifiche.DeStatoFbScheda;
import it.eng.myportal.entity.decodifiche.DeStatoFbScheda_;
import it.eng.myportal.entity.decodifiche.DeTitolo;
import it.eng.myportal.entity.decodifiche.DeTitolo_;
import it.eng.myportal.entity.decodifiche.min.DeMansioneMin;
import it.eng.myportal.entity.decodifiche.min.DeMansioneMin_;
import it.eng.myportal.entity.home.decodifiche.DeComuneHome;
import it.eng.myportal.entity.home.decodifiche.DeFbTipoTirocinioHome;
import it.eng.myportal.entity.home.decodifiche.DeMansioneHome;
import it.eng.myportal.entity.home.decodifiche.DeMansioneMinHome;
import it.eng.myportal.entity.home.decodifiche.DeStatoFbSchedaHome;
import it.eng.myportal.entity.home.decodifiche.DeTitoloHome;
import it.eng.myportal.utils.ConstantsSingleton;

@Stateless
public class FbSchedaFabbisognoHome extends AbstractUpdatableHome<FbSchedaFabbisogno, FbSchedaFabbisognoDTO> {

	@EJB
	DeStatoFbSchedaHome deStatoFbSchedaHome;

	@EJB
	FbChecklistHome fbChecklistHome;

	@EJB
	FbDatiAziendaHome fbDatiAziendaHome;

	@EJB
	DeFbTipoTirocinioHome deFbTipoTirocinioHome;

	@EJB
	DeMansioneHome deMansioneHome;

	@EJB
	DeMansioneMinHome deMansioneMinHome;

	@EJB
	DeTitoloHome deTitoloHome;

	@EJB
	DeComuneHome deComuneHome;

	@EJB
	FbCandidaturaHome fbCandidaturaHome;

	@Override
	public FbSchedaFabbisogno findById(Integer id) {
		return findById(FbSchedaFabbisogno.class, id);
	}

	public FbSchedaFabbisognoDTO toDTO(FbSchedaFabbisogno entity) {
		if (entity == null) {
			return null;
		}

		FbSchedaFabbisognoDTO dto = super.toDTO(entity);
		dto.setId(entity.getIdFbSchedaFabbisogno());
		dto.setIdFbChecklist(entity.getFbChecklist().getIdFbChecklist());
		dto.setDeStatoScheda(deStatoFbSchedaHome.toDTO(entity.getDeStatoScheda()));
		dto.setFbDatiAzienda(fbDatiAziendaHome.toDTO(entity.getFbDatiAzienda()));
		dto.setProfilo(entity.getProfilo());
		dto.setDeFbTipoTirocinio(deFbTipoTirocinioHome.toDTO(entity.getDeFbTipoTirocinio()));
		dto.setAttivitaDescr(entity.getAttivitaDescr());
		dto.setNumLavoratori(entity.getNumLavoratori());
		dto.setDeMansione(deMansioneHome.toDTO(entity.getDeMansione()));
		dto.setDeMansioneMin(deMansioneMinHome.toDTO(entity.getDeMansioneMin()));
		dto.setDeTitolo(deTitoloHome.toDTO(entity.getDeTitolo()));
		dto.setEtaMin(entity.getEtaMin());
		dto.setEtaMax(entity.getEtaMax());
		// dto.setNazione(deComuneHome.toDTO(entity.getNazione()));
		dto.setDeComune(deComuneHome.toDTO(entity.getDeComune()));
		dto.setCap(entity.getCap());
		dto.setIndirizzo(entity.getIndirizzo());
		dto.setNomeTutor(entity.getNomeTutor());
		dto.setCognomeTutor(entity.getCognomeTutor());
		dto.setEmailTutor(entity.getEmailTutor());
		dto.setFinalitaDescr(entity.getFinalitaDescr());
		dto.setInfoCompetenzeRichieste(entity.getInfoCompetenzeRichieste());
		dto.setInfoCompetenzeBase(entity.getInfoCompetenzeBase());
		dto.setInfoCompetenzeTecniche(entity.getInfoCompetenzeTecniche());
		dto.setInfoCompetenzeTrasversali(entity.getInfoCompetenzeTrasversali());
		dto.setInfoPercorso(entity.getInfoPercorso());
		dto.setInfoIndennita(entity.getInfoIndennita());
		dto.setInfoFacilitazione(entity.getInfoFacilitazione());
		dto.setInfoOrario(entity.getInfoOrario());
		dto.setFlgConformita(entity.getFlgConformita());
		dto.setDtPubblicazione(entity.getDtPubblicazione());
		dto.setMotivoChiusura(entity.getMotivoChiusura());
		dto.setFlgChiusuraManuale(entity.getFlgChiusuraManuale());
		dto.setDtChiusura(entity.getDtChisura());
		if (entity.getNoteEsito() != null)
			dto.setNoteEsito(entity.getNoteEsito());

		dto.setAziendaOspitante(entity.getFbChecklist().getPfPrincipal().getAziendaInfo().getRagioneSociale());

		List<FbCandidaturaDTO> candidatureList = new ArrayList<FbCandidaturaDTO>();
		for (FbCandidatura fbCandidatura : entity.getFbCandidaturaList()) {
			candidatureList.add(fbCandidaturaHome.toDTO(fbCandidatura));
		}
		
		dto.setCandidature(candidatureList);
		return dto;
	}

	public FbSchedaFabbisogno fromDTO(FbSchedaFabbisognoDTO dto) {
		if (dto == null) {
			return null;
		}

		FbSchedaFabbisogno entity = super.fromDTO(dto);
		entity.setIdFbSchedaFabbisogno(dto.getId());
		entity.setFbChecklist(fbChecklistHome.findById(dto.getIdFbChecklist()));
		entity.setProfilo(dto.getProfilo());
		entity.setAttivitaDescr(dto.getAttivitaDescr());
		entity.setNumLavoratori(dto.getNumLavoratori());
		entity.setEtaMin(dto.getEtaMin());
		entity.setEtaMax(dto.getEtaMax());
		entity.setCap(dto.getCap());
		entity.setIndirizzo(dto.getIndirizzo());
		entity.setNomeTutor(dto.getNomeTutor());
		entity.setCognomeTutor(dto.getCognomeTutor());
		entity.setEmailTutor(dto.getEmailTutor());
		entity.setFinalitaDescr(dto.getFinalitaDescr());
		entity.setInfoCompetenzeRichieste(dto.getInfoCompetenzeRichieste());
		entity.setInfoCompetenzeBase(dto.getInfoCompetenzeBase());
		entity.setInfoCompetenzeTecniche(dto.getInfoCompetenzeTecniche());
		entity.setInfoCompetenzeTrasversali(dto.getInfoCompetenzeTrasversali());
		entity.setInfoPercorso(dto.getInfoPercorso());
		entity.setInfoIndennita(dto.getInfoIndennita());
		entity.setInfoFacilitazione(dto.getInfoFacilitazione());
		entity.setInfoOrario(dto.getInfoOrario());
		entity.setFlgConformita(dto.getFlgConformita());
		entity.setDtPubblicazione(dto.getDtPubblicazione());
		entity.setMotivoChiusura(dto.getMotivoChiusura());
		entity.setFlgChiusuraManuale(dto.getFlgChiusuraManuale());
		entity.setDtChisura(dto.getDtChiusura());
		if (dto.getNoteEsito() != null)
			entity.setNoteEsito(dto.getNoteEsito());

		if (dto.getDeStatoScheda() != null && dto.getDeStatoScheda().getId() != null)
			entity.setDeStatoScheda(deStatoFbSchedaHome.findById(dto.getDeStatoScheda().getId()));

		if (dto.getFbDatiAzienda() != null && dto.getFbDatiAzienda().getId() != null)
			entity.setFbDatiAzienda(fbDatiAziendaHome.findById(dto.getFbDatiAzienda().getId()));

		if (dto.getDeFbTipoTirocinio() != null && dto.getDeFbTipoTirocinio().getId() != null)
			entity.setDeFbTipoTirocinio(deFbTipoTirocinioHome.findById(dto.getDeFbTipoTirocinio().getId()));

		if (dto.getDeMansione() != null && dto.getDeMansione().getId() != null)
			entity.setDeMansione(deMansioneHome.findById(dto.getDeMansione().getId()));

		if (dto.getDeMansioneMin() != null && dto.getDeMansioneMin().getId() != null)
			entity.setDeMansioneMin(deMansioneMinHome.findById(dto.getDeMansioneMin().getId()));

		if (dto.getDeTitolo() != null && dto.getDeTitolo().getId() != null)
			entity.setDeTitolo(deTitoloHome.findById(dto.getDeTitolo().getId()));

		// if (dto.getNazione() != null && dto.getNazione().getId() != null)
		// entity.setNazione(deComuneHome.findById(dto.getNazione().getId()));

		if (dto.getDeComune() != null && dto.getDeComune().getId() != null)
			entity.setDeComune(deComuneHome.findById(dto.getDeComune().getId()));

		ArrayList<FbCandidatura> tmpList = new ArrayList<FbCandidatura>();
		for (FbCandidaturaDTO fbCandidaturaDTO : dto.getCandidature()) {
			tmpList.add(fbCandidaturaHome.fromDTO(fbCandidaturaDTO));
		}
		entity.setFbCandidaturaList(tmpList);

		return entity;
	}

	public List<FbSchedaFabbisognoDTO> findSchedaFabbisognByCheckListId(Integer chelistId) {

		List<FbSchedaFabbisognoDTO> fbSchedafabbignoList = new ArrayList<FbSchedaFabbisognoDTO>();
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<FbSchedaFabbisogno> criteria = cb.createQuery(FbSchedaFabbisogno.class);
		Root<FbSchedaFabbisogno> fbSchedaRoot = criteria.from(FbSchedaFabbisogno.class);
		List<Predicate> predicate = createWhereCondition(cb, fbSchedaRoot, chelistId);
		criteria.where(predicate.toArray(new Predicate[predicate.size()]));
		criteria.orderBy(cb.desc(fbSchedaRoot.get(FbSchedaFabbisogno_.dtPubblicazione)));

		TypedQuery<FbSchedaFabbisogno> query = entityManager.createQuery(criteria);
		List<FbSchedaFabbisogno> entityList = query.getResultList();

		for (FbSchedaFabbisogno entity : entityList) {
			fbSchedafabbignoList.add(toDTO(entity));
		}
		return fbSchedafabbignoList;
	}

	private List<Predicate> createWhereCondition(CriteriaBuilder cb, Root<FbSchedaFabbisogno> fbSchedaRoot,
			Integer chelistId) {
		ArrayList<Predicate> predicateList = new ArrayList<Predicate>();

		if (chelistId != null) {
			Join<FbSchedaFabbisogno, FbChecklist> fbChecklistJoin = fbSchedaRoot.join(FbSchedaFabbisogno_.fbChecklist);
			predicateList.add(cb.equal(fbSchedaRoot.get(FbSchedaFabbisogno_.fbChecklist), chelistId));
		}
		Join<FbSchedaFabbisogno, DeStatoFbScheda> deStatoSchedaJoin = fbSchedaRoot
				.join(FbSchedaFabbisogno_.deStatoScheda);
		predicateList.add(cb.not(cb.equal(deStatoSchedaJoin.get(DeStatoFbScheda_.codStatoFbScheda),
				ConstantsSingleton.DeStatoFbScheda.IN_LAVORAZIONE)));

		return predicateList;
	}

	public List<FbSchedaFabbisognoDTO> findDTOByFilter(FbSchedaFabbisognoFilterDTO searchParams, Integer first,
			Integer pageSize, String sortField, SortOrder order) {

		List<FbSchedaFabbisogno> entityList = findByFilter(searchParams, first, pageSize, sortField, order);
		List<FbSchedaFabbisognoDTO> dtoList = new ArrayList<FbSchedaFabbisognoDTO>(entityList.size());
		for (FbSchedaFabbisogno entity : entityList) {
			dtoList.add(toDTO(entity));
		}
		return dtoList;
	}

	public List<FbSchedaFabbisogno> findByFilter(FbSchedaFabbisognoFilterDTO searchParams, Integer first,
			Integer pageSize, String sortField, SortOrder order) {
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<FbSchedaFabbisogno> criteria = cb.createQuery(FbSchedaFabbisogno.class);
		Root<FbSchedaFabbisogno> fbSchedaRoot = criteria.from(FbSchedaFabbisogno.class);

		List<Predicate> whereConditions = createFindByFilterWhereConditions(searchParams, fbSchedaRoot);
		criteria.where(whereConditions.toArray(new Predicate[whereConditions.size()]));

		if (sortField != null && !"".equals(sortField)) {
			if (sortField.contains("."))
				sortField = sortField.substring(0, sortField.indexOf("."));

			// Campi non presenti sul DB solo in DTO (FbSchedaFabbisognoDTO)
			if ("aziendaOspitante".equals(sortField)) {
				sortField = "fbChecklist";
			}

			if (SortOrder.ASCENDING.equals(order)) {
				criteria.orderBy(cb.asc(fbSchedaRoot.get(sortField)));
			} else {
				criteria.orderBy(cb.desc(fbSchedaRoot.get(sortField)));
			}
		}

		criteria.orderBy(cb.desc(fbSchedaRoot.get(FbSchedaFabbisogno_.dtPubblicazione)));
		TypedQuery<FbSchedaFabbisogno> query = entityManager.createQuery(criteria);

		if (first != null && first > 0) {
			query.setFirstResult(first);
		}

		if (pageSize != null && pageSize > 0) {
			query.setMaxResults(pageSize);
		}

		return query.getResultList();
	}

	public int findCountByFilter(FbSchedaFabbisognoFilterDTO searchParams) {
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<Long> criteria = cb.createQuery(Long.class);
		Root<FbSchedaFabbisogno> fbSchedaRoot = criteria.from(FbSchedaFabbisogno.class);
		criteria.select(cb.count(fbSchedaRoot));

		List<Predicate> whereConditions = createFindByFilterWhereConditions(searchParams, fbSchedaRoot);
		criteria.where(whereConditions.toArray(new Predicate[whereConditions.size()]));
		TypedQuery<Long> query = entityManager.createQuery(criteria);
		return query.getSingleResult().intValue();
	}

	private List<Predicate> createFindByFilterWhereConditions(FbSchedaFabbisognoFilterDTO searchParams,
			Root<FbSchedaFabbisogno> fbSchedaRoot) {
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		ArrayList<Predicate> predicateList = new ArrayList<Predicate>();

		if (searchParams.getRagioneSociale() != null && !searchParams.getRagioneSociale().trim().isEmpty()) {
			Join<FbSchedaFabbisogno, FbChecklist> fbChecklistJoin = fbSchedaRoot.join(FbSchedaFabbisogno_.fbChecklist);
			Join<FbChecklist, PfPrincipal> pfPrincipalJoin = fbChecklistJoin.join(FbChecklist_.pfPrincipal);
			Join<PfPrincipal, AziendaInfo> aziendaInfoJoin = pfPrincipalJoin.join(PfPrincipal_.aziendaInfo);
			predicateList.add(cb.like(cb.upper(aziendaInfoJoin.get(AziendaInfo_.ragioneSociale)),
					"%" + searchParams.getRagioneSociale().trim().toUpperCase() + "%"));
		}

		if (searchParams.getDataDa() != null) {
			predicateList.add(cb.greaterThanOrEqualTo(fbSchedaRoot.get(FbSchedaFabbisogno_.dtPubblicazione),
					searchParams.getDataDa()));
		}

		if (searchParams.getDataA() != null) {
			predicateList.add(cb.lessThanOrEqualTo(fbSchedaRoot.get(FbSchedaFabbisogno_.dtPubblicazione),
					searchParams.getDataA()));
		}

		if (searchParams.getProfilo() != null) {
			predicateList.add(cb.equal(fbSchedaRoot.get(FbSchedaFabbisogno_.profilo), searchParams.getProfilo()));
		}

		if (searchParams.getDeComune() != null) {
			Join<FbSchedaFabbisogno, DeComune> deComuneJoin = fbSchedaRoot.join(FbSchedaFabbisogno_.deComune);
			predicateList.add(cb.equal(deComuneJoin.get(DeComune_.codCom),
					deComuneHome.fromDTO(searchParams.getDeComune()).getCodCom()));
		}

		if (searchParams.getDeMansione() != null) {
			Join<FbSchedaFabbisogno, DeMansione> deMansioneJoin = fbSchedaRoot.join(FbSchedaFabbisogno_.deMansione);
			predicateList.add(cb.equal(deMansioneJoin.get(DeMansione_.codMansione),
					deMansioneHome.fromDTO(searchParams.getDeMansione()).getCodMansione()));
		}

		if (searchParams.getDeTitolo() != null) {
			Join<FbSchedaFabbisogno, DeTitolo> deTitoloJoin = fbSchedaRoot.join(FbSchedaFabbisogno_.deTitolo);
			predicateList.add(cb.equal(deTitoloJoin.get(DeTitolo_.codTitolo),
					deTitoloHome.fromDTO(searchParams.getDeTitolo()).getCodTitolo()));
		}

		if (searchParams.getDeMansioneMin() != null) {
			Join<FbSchedaFabbisogno, DeMansioneMin> deMansioneMinJoin = fbSchedaRoot
					.join(FbSchedaFabbisogno_.deMansioneMin);
			predicateList.add(cb.equal(deMansioneMinJoin.get(DeMansioneMin_.codMansioneMin),
					deMansioneMinHome.fromDTO(searchParams.getDeMansioneMin()).getCodMansioneMin()));
		}

		if (searchParams.isEscludiInLavorazione()) {
			Join<FbSchedaFabbisogno, DeStatoFbScheda> deStatoSchedaJoin = fbSchedaRoot
					.join(FbSchedaFabbisogno_.deStatoScheda);
			predicateList.add(cb.not(cb.equal(deStatoSchedaJoin.get(DeStatoFbScheda_.codStatoFbScheda),
					ConstantsSingleton.DeStatoFbScheda.IN_LAVORAZIONE)));
		}

		return predicateList;
	}
}

package it.eng.myportal.entity.home;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import it.eng.myportal.dtos.PtScrivaniaDTO;
import it.eng.myportal.entity.PfPrincipal;
import it.eng.myportal.entity.PfPrincipal_;
import it.eng.myportal.entity.PtPortlet;
import it.eng.myportal.entity.PtPortlet_;
import it.eng.myportal.entity.PtScrivania;
import it.eng.myportal.entity.PtScrivania_;
import it.eng.myportal.entity.home.decodifiche.DeRuoloPortaleHome;

@Stateless
public class PtScrivaniaHome extends AbstractUpdatableHome<PtScrivania, PtScrivaniaDTO> {

	@EJB
	private PfPrincipalHome pfPrincipalHome;

	@EJB
	private PtPortletHome ptPortletHome;

	@EJB
	private DeRuoloPortaleHome deRuoloPortaleHome;

	@Override
	public PtScrivania findById(Integer id) {
		return findById(PtScrivania.class, id);
	}

	/**
	 * Restituisce la lista delle portlet presenti sulla scrivania dell'utente. A ciascuna portlet vengono associati tre
	 * attributi aggiuntivi: visualizza: se la portlet è visibile o meno ridotta: se è in visualizzazione ridotta
	 * posizione: la posizione che occupa all'interno del container colonna: identifica il container
	 * 
	 * @param idPfPrincipal
	 *            id dell'utente del quale si vuole la scrivania
	 * @return
	 */
	public List<PtScrivaniaDTO> findPortletsScrivania(Integer idPfPrincipl) {
		CriteriaBuilder qb = entityManager.getCriteriaBuilder();
		CriteriaQuery<PtScrivania> query = qb.createQuery(PtScrivania.class);
		Root<PtScrivania> scrivania = query.from(PtScrivania.class);
		// Join<PtScrivania, PfPrincipal> principal = scrivania.join(PtScrivania_.pfPrincipal);
		// Join<PtScrivania, PtPortlet> portlet = scrivania.join(PtScrivania_.ptPortlet);
		List<Predicate> whereConditions = new ArrayList<Predicate>();
		whereConditions.add(qb.equal(scrivania.get(PtScrivania_.pfPrincipal), idPfPrincipl));
		// whereConditions.add(qb.not(portlet.get(PtPortlet_.idPtPortlet).in(ConstantsSingleton.NO_PORTLETS)));
		query.where(whereConditions.toArray(new Predicate[whereConditions.size()]));
		TypedQuery<PtScrivania> q = entityManager.createQuery(query);
		List<PtScrivaniaDTO> ret = findDTOByQuery(q);
		return ret;
	}

	/**
	 * Converte un'Entity in un DTO.
	 * 
	 * @param entity
	 *            VaIstruzione
	 * @return VaIstruzioneDTO
	 */
	@Override
	public PtScrivaniaDTO toDTO(PtScrivania entity) {
		if (entity == null)
			return null;
		PtScrivaniaDTO dto = super.toDTO(entity);
		dto.setColonna(entity.getOptColonna());
		dto.setDescrizione(entity.getPtPortlet().getDescrizione());
		dto.setId(entity.getIdPtScrivania());
		dto.setIdPortlet(entity.getPtPortlet().getIdPtPortlet());
		dto.setIdPrincipal(entity.getPfPrincipal().getIdPfPrincipal());
		dto.setPosizione(entity.getPosizione());
		dto.setVisualizza(entity.getFlagVisualizza());
		dto.setRidotta(entity.getFlagRidotta());
		dto.setNome(entity.getPtPortlet().getNome());

		return dto;
	}

	/**
	 * Genera l'Entity a partire dal DTO.
	 * 
	 * @param dto
	 *            VaIstruzioneDTO
	 * @return VaIstruzione
	 */
	@Override
	public PtScrivania fromDTO(PtScrivaniaDTO dto) {

		if (dto == null)
			return null;
		PtScrivania entity = super.fromDTO(dto);
		entity.setIdPtScrivania(dto.getId());
		entity.setPfPrincipal(pfPrincipalHome.findById(dto.getIdPrincipal()));
		entity.setPtPortlet(ptPortletHome.findById(dto.getIdPortlet()));
		entity.setPosizione(dto.getPosizione());
		entity.setOptColonna(dto.getColonna());
		entity.setFlagVisualizza(dto.getVisualizza());
		entity.setFlagRidotta(dto.getRidotta());

		return entity;
	}

	public Integer checkMaxPosizione(Integer idPfPrincipal) {
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<Integer> c = cb.createQuery(Integer.class);
		Root<PtScrivania> f = c.from(PtScrivania.class);
		Join<PtScrivania, PfPrincipal> princip = f.join(PtScrivania_.pfPrincipal);
		Predicate equalPredicateUser = cb.equal(princip.get(PfPrincipal_.idPfPrincipal), idPfPrincipal);
		Path<Integer> path = f.get(PtScrivania_.posizione);
		Expression<Integer> obj = cb.max(path);
		c = c.select(obj);
		c = c.where(equalPredicateUser);
		TypedQuery<Integer> typedQuery = entityManager.createQuery(c);
		Integer singleResult = typedQuery.getSingleResult();

		// e' il primo numRichiesta
		if (singleResult == null) {
			singleResult = 0;
		}

		return (singleResult + 1);
	}

	public PtScrivaniaDTO findPortletScrivaniaByIdPortlet(Integer idPortlet, Integer idPfPrincipal) {
		CriteriaBuilder qb = entityManager.getCriteriaBuilder();
		CriteriaQuery<PtScrivania> query = qb.createQuery(PtScrivania.class);
		Root<PtScrivania> scrivania = query.from(PtScrivania.class);
		List<Predicate> whereConditions = new ArrayList<Predicate>();
		Join<PtScrivania, PtPortlet> portlet = scrivania.join(PtScrivania_.ptPortlet);
		Join<PtScrivania, PfPrincipal> principal = scrivania.join(PtScrivania_.pfPrincipal);

		whereConditions.add(qb.equal(portlet.get(PtPortlet_.idPtPortlet), idPortlet));
		whereConditions.add(qb.equal(principal.get(PfPrincipal_.idPfPrincipal), idPfPrincipal));

		query.where(whereConditions.toArray(new Predicate[whereConditions.size()]));

		TypedQuery<PtScrivania> q = entityManager.createQuery(query);
		List<PtScrivaniaDTO> ret = findDTOByQuery(q);

		if (ret.isEmpty()) {
			return null;
		}

		return ret.get(0);
	}

	/**
	 * Aggiunge una portlet alla scrivania dell'utente
	 * 
	 * @param idPortlet
	 *            id della portlet da aggiungere
	 * @param idPfPrincipal
	 *            id dell'utente al quale agganciarla
	 * @param position
	 * @param column
	 */
	public void addPortletToGroup(Integer idPortlet, String codGruppo, int position, String column) {

		// TODO PROFILATURA
		// MARCO
		// List<PfPrincipal> users = pfPrincipalHome.findPrincipalsByCodGruppo(codGruppo);
		//
		// PtPortlet portlet = ptPortletHome.findById(idPortlet);
		// for (PfPrincipal pfPrincipal : users) {
		// PtScrivania iesimaScrivania = new PtScrivania();
		// iesimaScrivania.setPfPrincipal(pfPrincipal);
		// iesimaScrivania.setFlagRidotta(false);
		// iesimaScrivania.setFlagVisualizza(true);
		// iesimaScrivania.setPtPortlet(portlet);
		// iesimaScrivania.setDtmIns(new Date());
		// iesimaScrivania.setDtmMod(new Date());
		// iesimaScrivania.setPfPrincipalIns(pfPrincipal);
		// iesimaScrivania.setPfPrincipalMod(pfPrincipal);
		// iesimaScrivania.setPosizione(position);
		// iesimaScrivania.setOptColonna(column);
		// persist(iesimaScrivania);
		//
		// }
	}

	/**
	 * Cancella tutti gli oggetti PtScrivania per un certo utente.
	 */
	public void deleteAllPtScrivaniaByUtente(Integer idPfPrincipal) {

		CriteriaBuilder qb = entityManager.getCriteriaBuilder();
		CriteriaQuery<PtScrivania> query = qb.createQuery(PtScrivania.class);
		Root<PtScrivania> scrivania = query.from(PtScrivania.class);
		Join<PtScrivania, PfPrincipal> principal = scrivania.join(PtScrivania_.pfPrincipal);
		List<Predicate> whereConditions = new ArrayList<Predicate>();
		whereConditions.add(qb.equal(principal.get(PfPrincipal_.idPfPrincipal), idPfPrincipal));
		query.where(whereConditions.toArray(new Predicate[whereConditions.size()]));
		TypedQuery<PtScrivania> typedQuery = entityManager.createQuery(query);
		List<PtScrivania> ptScrivaniaList = typedQuery.getResultList();

		if (ptScrivaniaList != null && !ptScrivaniaList.isEmpty()) {
			for (PtScrivania ptScrivania : ptScrivaniaList)
				this.remove(ptScrivania);
		}
	}

}

package it.eng.myportal.entity.home.nodto;

import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import it.eng.myportal.entity.AcCandidatura;
import it.eng.myportal.entity.AcCandidaturaValutazione;
import it.eng.myportal.entity.AcCandidatura_;
import it.eng.myportal.entity.CvDatiPersonali;
import it.eng.myportal.entity.CvDatiPersonali_;
import it.eng.myportal.entity.PfPrincipal;
import it.eng.myportal.entity.PfPrincipal_;
import it.eng.myportal.entity.home.AbstractHibernateHome;
import it.eng.myportal.entity.home.InoDTOejb;
import it.eng.myportal.entity.home.PfPrincipalHome;
import it.eng.myportal.exception.MyPortalNoResultFoundException;

/**
 *
 * Classe dei servizi associati alla candidatura di un utente ad una vacancy.
 *
 * @author Rodi A.
 */
@Stateless
@LocalBean
public class AcCandidaturaValutazioneHome extends AbstractHibernateHome<AcCandidaturaValutazione, Integer>
		implements InoDTOejb<AcCandidaturaValutazione> {

	@EJB
	protected PfPrincipalHome pfPrincipalHome;

	/**
	 * Restituisce la Entity che su DB ha la chiave passata come parametro.
	 *
	 * @return l'entity presente su DB
	 */
	@Override
	public AcCandidaturaValutazione findById(Integer id) {
		return findById(AcCandidaturaValutazione.class, id);
	}

	public AcCandidaturaValutazione findByAcCandidaturaId(Integer acCandidaturaId)
			throws MyPortalNoResultFoundException {
		try {
			
		
		AcCandidaturaValutazione acCandidaturaValutazione = entityManager
				.createNamedQuery("findAcCandidaturaValByCandidaturaId", AcCandidaturaValutazione.class)
				.setParameter("par", acCandidaturaId).getSingleResult();
		return acCandidaturaValutazione;
		} catch (Exception e) {
			throw new MyPortalNoResultFoundException();
		}
	}

	/**
	 * PLACEHOLDER criteria
	 *
	 * @param idCvDatiPersonali
	 *            id del curriculum
	 * @param idPfPrincipalAzienda
	 *            id dell'azienda
	 * @return
	 */
	public List<AcCandidatura> findAllByIdCvDatiPersonaliIdAziendaInfo(Integer idCvDatiPersonali,
			Integer idPfPrincipalAzienda) {
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<AcCandidatura> query = cb.createQuery(AcCandidatura.class);

		Root<AcCandidatura> from = query.from(AcCandidatura.class);
		Join<AcCandidatura, CvDatiPersonali> cvDatiPersonali = from.join(AcCandidatura_.cvDatiPersonali);
		Join<AcCandidatura, PfPrincipal> pfPrincipalAzienda = from.join(AcCandidatura_.pfPrincipal);

		Predicate equalIdCvDatiPersonali = cb.equal(cvDatiPersonali.get(CvDatiPersonali_.idCvDatiPersonali),
				idCvDatiPersonali);
		Predicate equalIdAziendaInfo = cb.equal(pfPrincipalAzienda.get(PfPrincipal_.idPfPrincipal),
				idPfPrincipalAzienda);
		Predicate whereClause = cb.and(equalIdCvDatiPersonali, equalIdAziendaInfo);

		query.where(whereClause);
		TypedQuery<AcCandidatura> typedQuery = entityManager.createQuery(query);
		List<AcCandidatura> list = typedQuery.getResultList();

		return list;
	}

	@Override
	public AcCandidaturaValutazione merge(AcCandidaturaValutazione entity, Integer actingUser) {
		PfPrincipal usr = pfPrincipalHome.findById(actingUser);
		entity.setDtmMod(new Date());
		entity.setPfPrincipalMod(usr);
		return entityManager.merge(entity);
	}

	@Override
	public AcCandidaturaValutazione persist(AcCandidaturaValutazione entity, Integer actingUser) {
		PfPrincipal usr = pfPrincipalHome.findById(actingUser);
		entity.setDtmIns(new Date());
		entity.setDtmMod(new Date());
		entity.setPfPrincipalIns(usr);
		entity.setPfPrincipalMod(usr);
		entityManager.persist(entity);
		entityManager.flush();
		return entity;
	}

}

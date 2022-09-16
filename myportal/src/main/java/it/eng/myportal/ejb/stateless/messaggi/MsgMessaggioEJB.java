package it.eng.myportal.ejb.stateless.messaggi;

import it.eng.myportal.entity.MsgMessaggio;
import it.eng.myportal.entity.MsgMessaggio_;
import it.eng.myportal.entity.PfPrincipal;
import it.eng.myportal.entity.UtenteInfo;
import it.eng.myportal.entity.decodifiche.DeProvincia;
import it.eng.myportal.entity.decodifiche.DeTipoMessaggio;
import it.eng.myportal.entity.enums.TipoAbilitazione;
import it.eng.myportal.entity.home.AbstractHibernateHome;
import it.eng.myportal.entity.home.AziendaInfoHome;
import it.eng.myportal.entity.home.PfPrincipalHome;
import it.eng.myportal.entity.home.decodifiche.DeProvinciaHome;
import it.eng.myportal.entity.home.decodifiche.DeTipoMessaggioHome;
import it.eng.myportal.utils.ConstantsSingleton;
import it.eng.myportal.utils.ConstantsSingleton.PfAttivita;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.ejb.Stateless;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;

@Stateless
public class MsgMessaggioEJB extends AbstractHibernateHome<MsgMessaggio, Integer> {

	@EJB
	PfPrincipalHome pfPrincipalHome;

	@EJB
	AziendaInfoHome aziendaInfoHome;

	@EJB
	DeTipoMessaggioHome deTipoMessaggioHome;

	@EJB
	DeProvinciaHome deProvinciaHome;

	public MsgMessaggio findById(Integer id) {
		return findById(MsgMessaggio.class, id);
	}

	/**
	 * Restituisce tutti messaggi di supporto dell'utente oppure di sistema
	 * 
	 * 
	 * @param principalId
	 * @return
	 */
	public List<MsgMessaggio> findSupportMessages(Integer principalId, int startResultsFrom, int numRigheVisualizzate) {
		PfPrincipal pfPrincipal = pfPrincipalHome.findById(principalId);
		if (pfPrincipal.isProvincia()) {
			Boolean allowed = pfPrincipalHome.checkAbilitazione(principalId, PfAttivita.MSG_SUPPORTO,
					TipoAbilitazione.LETTURA);
			if (allowed) {
				return this.findListaMessaggiTicketGruppo(principalId, ConstantsSingleton.MsgMessaggio.SUPPORTO,
						startResultsFrom, numRigheVisualizzate);
			} else {
				throw new EJBException("Non disponi dei permessi sufficienti alla lettura dei messaggi di tipo "
						+ ConstantsSingleton.MsgMessaggio.PRIMO_CONTATTO);
			}
		} else {
			return this.findListaMessaggiTicket(Boolean.FALSE, principalId, ConstantsSingleton.MsgMessaggio.SUPPORTO,
					startResultsFrom, numRigheVisualizzate);
		}
	}

	public Long findSupportMessagesCount(Integer principalId) {
		PfPrincipal pfPrincipal = pfPrincipalHome.findById(principalId);
		if (pfPrincipal.isProvincia()) {
			return this.findCountMessaggiTicketGruppo(principalId, ConstantsSingleton.MsgMessaggio.SUPPORTO);
		} else {
			return this.findCountMessaggiTicket(Boolean.FALSE, principalId, ConstantsSingleton.MsgMessaggio.SUPPORTO);
		}
	}

	private DeProvincia findProvinciaUtente(Integer principalId) {
		PfPrincipal pfPrincipal = pfPrincipalHome.findById(principalId);
		if (pfPrincipal.isProvincia()) {
			return pfPrincipal.getProvinciasForIdPfPrincipal().iterator().next().getDeProvincia();
		} else {
			return null;
		}
	}

	/**
	 * Restituisce tutte le notifiche ricevute dall'utente o dal gruppo dell'utente oppure di sistema
	 * 
	 * 
	 * @param principalId
	 * @return
	 */
	public List<MsgMessaggio> findNotificheRicevute(Integer principalId, int startResultsFrom, int numRigheVisualizzate) {
		DeProvincia provincia = findProvinciaUtente(principalId);
		// CRITERIA
		List<MsgMessaggio> ret = findListaMessaggi(Boolean.TRUE, principalId, ConstantsSingleton.MsgMessaggio.NOTIFICA,
				provincia, startResultsFrom, numRigheVisualizzate);

		return ret;
	}

	public Long findNotificheRicevuteCount(Integer principalId) {
		DeProvincia provincia = findProvinciaUtente(principalId);
		// CRITERIA
		return this.findCountListaMessaggi(Boolean.TRUE, principalId, ConstantsSingleton.MsgMessaggio.NOTIFICA,
				provincia);
	}

	/**
	 * Restituisce tutte le notifiche inviate dall'utente.
	 * 
	 * 
	 * @param principalId
	 * @return
	 */
	public List<MsgMessaggio> findNotificheInviate(Integer principalId, int startResultsFrom, int numRigheVisualizzate) {
		DeProvincia provincia = findProvinciaUtente(principalId);
		// CRITERIA
		List<MsgMessaggio> ret = findListaMessaggi(Boolean.FALSE, principalId,
				ConstantsSingleton.MsgMessaggio.NOTIFICA, provincia, startResultsFrom, numRigheVisualizzate);

		return ret;
	}

	public Long findNotificheInviateCount(Integer principalId) {
		DeProvincia provincia = findProvinciaUtente(principalId);
		// CRITERIA
		return this.findCountListaMessaggi(Boolean.FALSE, principalId, ConstantsSingleton.MsgMessaggio.NOTIFICA,
				provincia);
	}

	/**
	 * Restituisce tutti messaggi di chiedi all'esperto dell'utente oppure di sistema
	 * 
	 * 
	 * @param principalId
	 * @return
	 */
	public List<MsgMessaggio> findConsultingMessages(Integer principalId, int startResultsFrom, int numRigheVisualizzate) {
		PfPrincipal pfPrincipal = pfPrincipalHome.findById(principalId);
		if (pfPrincipal.isProvincia()) {
			Boolean allowed = pfPrincipalHome.checkAbilitazione(principalId, PfAttivita.MSG_ESPERTO,
					TipoAbilitazione.LETTURA);
			if (allowed) {
				return this.findListaMessaggiTicketGruppo(principalId, ConstantsSingleton.MsgMessaggio.ESPERTO,
						startResultsFrom, numRigheVisualizzate);
			} else {
				throw new EJBException("Non disponi dei permessi sufficienti alla lettura dei messaggi di tipo "
						+ ConstantsSingleton.MsgMessaggio.PRIMO_CONTATTO);
			}
		} else {
			return this.findListaMessaggiTicket(Boolean.FALSE, principalId, ConstantsSingleton.MsgMessaggio.ESPERTO,
					startResultsFrom, numRigheVisualizzate);
		}
	}

	public Long findConsultingMessagesCount(Integer principalId) {
		PfPrincipal pfPrincipal = pfPrincipalHome.findById(principalId);
		if (pfPrincipal.isProvincia()) {
			return this.findCountMessaggiTicketGruppo(principalId, ConstantsSingleton.MsgMessaggio.ESPERTO);
		} else {
			return this.findCountMessaggiTicket(Boolean.FALSE, principalId, ConstantsSingleton.MsgMessaggio.ESPERTO);
		}
	}

	public List<MsgMessaggio> findContactReceivedMessages(Integer principalId, int startResultsFrom,
			int numRigheVisualizzate) {
		PfPrincipal pfPrincipal = pfPrincipalHome.findById(principalId);
		if (pfPrincipal.isProvincia()) {
			Boolean allowed = pfPrincipalHome.checkAbilitazione(principalId, PfAttivita.MSG_ESPERTO,
					TipoAbilitazione.LETTURA);
			if (allowed) {
				return this.findListaMessaggiTicketGruppo(principalId,
						ConstantsSingleton.MsgMessaggio.RISPOSTA_CONTATTO, startResultsFrom, numRigheVisualizzate);
			} else {
				throw new EJBException("Non disponi dei permessi sufficienti alla lettura dei messaggi di tipo "
						+ ConstantsSingleton.MsgMessaggio.PRIMO_CONTATTO);
			}
		} else {
			return this.findListaMessaggiTicket(Boolean.FALSE, principalId,
					ConstantsSingleton.MsgMessaggio.RISPOSTA_CONTATTO, startResultsFrom, numRigheVisualizzate);
		}
	}

	public Long findContactReceivedMessagesCount(Integer principalId) {
		PfPrincipal pfPrincipal = pfPrincipalHome.findById(principalId);
		if (pfPrincipal.isProvincia()) {
			return this.findCountMessaggiTicketGruppo(principalId, ConstantsSingleton.MsgMessaggio.RISPOSTA_CONTATTO);
		} else {
			return this.findCountMessaggiTicket(Boolean.FALSE, principalId,
					ConstantsSingleton.MsgMessaggio.RISPOSTA_CONTATTO);
		}
	}

	public List<MsgMessaggio> findContactSentMessages(Integer principalId, int startResultsFrom,
			int numRigheVisualizzate) {
		PfPrincipal pfPrincipal = pfPrincipalHome.findById(principalId);
		if (pfPrincipal.isProvincia()) {
			Boolean allowed = pfPrincipalHome.checkAbilitazione(principalId, PfAttivita.MSG_ESPERTO,
					TipoAbilitazione.LETTURA);
			if (allowed) {
				return this.findListaMessaggiTicketFromTo(principalId, ConstantsSingleton.MsgMessaggio.PRIMO_CONTATTO,
						startResultsFrom, numRigheVisualizzate);
			} else {
				throw new EJBException("Non disponi dei permessi sufficienti alla lettura dei messaggi di tipo "
						+ ConstantsSingleton.MsgMessaggio.PRIMO_CONTATTO);
			}
		} else {
			return this.findListaMessaggiTicket(Boolean.FALSE, principalId,
					ConstantsSingleton.MsgMessaggio.PRIMO_CONTATTO, startResultsFrom, numRigheVisualizzate);
		}
	}

	public Long findContactSentMessagesCount(Integer principalId) {
		PfPrincipal pfPrincipal = pfPrincipalHome.findById(principalId);
		if (pfPrincipal.isProvincia()) {
			return this.findCountMessaggiTicketFromTo(principalId, ConstantsSingleton.MsgMessaggio.PRIMO_CONTATTO);
		} else {
			return this.findCountMessaggiTicket(Boolean.FALSE, principalId,
					ConstantsSingleton.MsgMessaggio.PRIMO_CONTATTO);
		}
	}

	/************************************************************************************
	 * RICERCA UTILIZZANDO QUERY CRITERIA
	 * 
	 ************************************************************************************/

	/**
	 * select m " + " from MsgMessaggio m " + " where m.dtScadenza > :dataScadenza " +
	 * " and m.deTipoMessaggio.codTipoMessaggio = :codTipoMessaggio " +
	 * " and (m.pfPrincipalTo.idPfPrincipal = :idPrincipalTo or m.deGruppo.codGruppo = :codGruppoTo " +
	 * "	   or (m.pfPrincipalTo.idPfPrincipal is null and m.deGruppo.codGruppo is null)) " + " order by m.idMsgMessaggio
	 * desc, m.dtmMod desc
	 * 
	 * @param principalId
	 * @param codTipoMessaggio
	 * @param codGruppo
	 * @param startResultsFrom
	 * @param numRigheVisualizzate
	 */
	public List<MsgMessaggio> findListaMessaggi(Boolean ricevute, Integer principalId, String codTipoMessaggio,
			DeProvincia provincia, int startResultsFrom, int numRigheVisualizzate) {
		DeTipoMessaggio tipoMsg = deTipoMessaggioHome.findById(codTipoMessaggio);
		PfPrincipal pfPrinc = pfPrincipalHome.findById(principalId);

		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<MsgMessaggio> query = cb.createQuery(MsgMessaggio.class);
		Root<MsgMessaggio> messaggio = query.from(MsgMessaggio.class);

		List<Predicate> criteria = new ArrayList<Predicate>();
		criteria.add(cb.greaterThan(messaggio.get(MsgMessaggio_.dtScadenza), new Date()));
		criteria.add(cb.equal(messaggio.get(MsgMessaggio_.deTipoMessaggio), tipoMsg));

		if (ricevute) {
			Predicate conditionA = cb.equal(messaggio.get(MsgMessaggio_.pfPrincipalTo), pfPrinc);
			Predicate conditionB = cb.equal(messaggio.get(MsgMessaggio_.deProvinciaTo), provincia);
			Predicate conditionC = cb.isNull(messaggio.get(MsgMessaggio_.deProvinciaTo));
			Predicate conditionD = cb.isNull(messaggio.get(MsgMessaggio_.pfPrincipalTo));
			Predicate conditionE = cb.and(conditionC, conditionD);
			criteria.add(cb.or(conditionA, conditionB, conditionE));
		} else {
			Predicate conditionA = cb.equal(messaggio.get(MsgMessaggio_.pfPrincipalFrom), pfPrinc);
			criteria.add(cb.and(conditionA));
		}

		query.where(criteria.toArray(new Predicate[criteria.size()]));
		query.select(cb.construct(MsgMessaggio.class, messaggio.get(MsgMessaggio_.idMsgMessaggio),
				messaggio.get(MsgMessaggio_.oggetto), messaggio.get(MsgMessaggio_.corpo),
				messaggio.get(MsgMessaggio_.ticket), messaggio.get(MsgMessaggio_.dtmIns),
				messaggio.get(MsgMessaggio_.dtmMod)));

		TypedQuery<MsgMessaggio> q = entityManager.createQuery(query);
		q.setFirstResult(startResultsFrom);
		q.setMaxResults(numRigheVisualizzate);

		return q.getResultList();

	}

	public Long findCountListaMessaggi(Boolean ricevute, Integer principalId, String codTipoMessaggio,
			DeProvincia provincia) {
		DeTipoMessaggio tipoMsg = deTipoMessaggioHome.findById(codTipoMessaggio);
		PfPrincipal pfPrinc = pfPrincipalHome.findById(principalId);

		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<Long> query = cb.createQuery(Long.class);
		Root<MsgMessaggio> messaggio = query.from(MsgMessaggio.class);

		List<Predicate> criteria = new ArrayList<Predicate>();
		criteria.add(cb.greaterThan(messaggio.get(MsgMessaggio_.dtScadenza), new Date()));
		criteria.add(cb.equal(messaggio.get(MsgMessaggio_.deTipoMessaggio), tipoMsg));
		if (ricevute) {
			Predicate conditionA = cb.equal(messaggio.get(MsgMessaggio_.pfPrincipalTo), pfPrinc);
			Predicate conditionB = cb.or();
			if (provincia != null) {
				conditionB = cb.equal(messaggio.get(MsgMessaggio_.deProvinciaTo), provincia);
			}
			Predicate conditionC = cb.isNull(messaggio.get(MsgMessaggio_.deProvinciaTo));
			Predicate conditionD = cb.isNull(messaggio.get(MsgMessaggio_.pfPrincipalTo));
			Predicate conditionE = cb.and(conditionC, conditionD);
			criteria.add(cb.or(conditionA, conditionB, conditionE));
		} else {
			Predicate conditionA = cb.equal(messaggio.get(MsgMessaggio_.pfPrincipalFrom), pfPrinc);
			criteria.add(cb.and(conditionA));
		}

		query.where(criteria.toArray(new Predicate[criteria.size()]));
		query.select(cb.count(messaggio));

		TypedQuery<Long> q = entityManager.createQuery(query);

		return q.getSingleResult();
	}

	/**
	 * Query per lista messaggi per ticket ricevuti e inviati
	 * 
	 * "select m " + " from MsgMessaggio m" + " where m.deTipoMessaggio.codTipoMessaggio = :codTipoMessaggio " +
	 * " and m.pfPrincipalFrom.idPfPrincipal = :idPrincipalFrom " + " and m.idMsgMessaggio in ( " +
	 * " select min(mex.idMsgMessaggio) " + " from MsgMessaggio mex " +
	 * " where mex.deTipoMessaggio.codTipoMessaggio = :codTipoMessaggioIn " +
	 * " and mex.pfPrincipalFrom.idPfPrincipal = :idPrincipalFromIn " + " group by mex.ticket )" +
	 * " order by m.idMsgMessaggio desc, m.dtmMod desc"
	 * 
	 * 
	 * 
	 */
	public List<MsgMessaggio> findListaMessaggiTicket(Boolean ricevuti, Integer principalId, String codTipoMessaggio,
			int startResultsFrom, int numRigheVisualizzate) {
		DeTipoMessaggio tipoMsg = deTipoMessaggioHome.findById(codTipoMessaggio);
		PfPrincipal pfPrinc = pfPrincipalHome.findById(principalId);

		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<MsgMessaggio> query = cb.createQuery(MsgMessaggio.class);
		Root<MsgMessaggio> messaggio = query.from(MsgMessaggio.class);

		List<Predicate> criteria = new ArrayList<Predicate>();
		criteria.add(cb.equal(messaggio.get(MsgMessaggio_.deTipoMessaggio), tipoMsg));

		if (ricevuti) {
			Predicate conditionA = cb.equal(messaggio.get(MsgMessaggio_.pfPrincipalTo), pfPrinc);
			criteria.add(cb.and(conditionA));
		} else {
			Predicate conditionA = cb.equal(messaggio.get(MsgMessaggio_.pfPrincipalFrom), pfPrinc);
			criteria.add(cb.and(conditionA));
		}

		// filtro subquery
		Subquery<Integer> subquery = creaSubQueryTicket(ricevuti, query, pfPrinc, tipoMsg);
		criteria.add(cb.in(messaggio.get(MsgMessaggio_.idMsgMessaggio)).value(subquery));

		query.where(criteria.toArray(new Predicate[criteria.size()]));

		query.select(cb.construct(MsgMessaggio.class, messaggio.get(MsgMessaggio_.pfPrincipalFrom),
				messaggio.get(MsgMessaggio_.idMsgMessaggio), messaggio.get(MsgMessaggio_.oggetto),
				messaggio.get(MsgMessaggio_.corpo), messaggio.get(MsgMessaggio_.ticket),
				messaggio.get(MsgMessaggio_.dtmIns), messaggio.get(MsgMessaggio_.dtmMod)));

		query.orderBy(cb.desc(messaggio.get(MsgMessaggio_.idMsgMessaggio)),
				cb.desc(messaggio.get(MsgMessaggio_.dtmMod)));

		TypedQuery<MsgMessaggio> q = entityManager.createQuery(query);
		q.setFirstResult(startResultsFrom);
		q.setMaxResults(numRigheVisualizzate);

		return q.getResultList();

	}

	public Long findCountMessaggiTicket(Boolean ricevuti, Integer principalId, String codTipoMessaggio) {
		DeTipoMessaggio tipoMsg = deTipoMessaggioHome.findById(codTipoMessaggio);
		PfPrincipal pfPrinc = pfPrincipalHome.findById(principalId);

		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<Long> query = cb.createQuery(Long.class);
		Root<MsgMessaggio> messaggio = query.from(MsgMessaggio.class);

		List<Predicate> criteria = new ArrayList<Predicate>();
		criteria.add(cb.equal(messaggio.get(MsgMessaggio_.deTipoMessaggio), tipoMsg));

		if (ricevuti) {
			Predicate conditionA = cb.equal(messaggio.get(MsgMessaggio_.pfPrincipalTo), pfPrinc);
			criteria.add(cb.and(conditionA));
		} else {
			Predicate conditionA = cb.equal(messaggio.get(MsgMessaggio_.pfPrincipalFrom), pfPrinc);
			criteria.add(cb.and(conditionA));
		}

		// filtro subquery
		Subquery<Integer> subquery = creaSubQueryCountTicket(ricevuti, query, pfPrinc, tipoMsg);
		criteria.add(cb.in(messaggio.get(MsgMessaggio_.idMsgMessaggio)).value(subquery));

		query.where(criteria.toArray(new Predicate[criteria.size()]));
		query.select(cb.count(messaggio));

		TypedQuery<Long> q = entityManager.createQuery(query);

		return q.getSingleResult();

	}

	private Subquery<Integer> creaSubQueryTicket(Boolean ricevuti, CriteriaQuery<MsgMessaggio> query,
			PfPrincipal pfPrinc, DeTipoMessaggio tipoMsg) {
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		Subquery<Integer> subquery = query.subquery(Integer.class);
		Root<MsgMessaggio> mess = subquery.from(MsgMessaggio.class);

		// Seleziono il min messaggio con lo stesso ticket
		subquery.select(cb.min(mess.get(MsgMessaggio_.idMsgMessaggio)));

		List<Predicate> criteria = new ArrayList<Predicate>();
		criteria.add(cb.equal(mess.get(MsgMessaggio_.deTipoMessaggio), tipoMsg));

		if (ricevuti) {
			Predicate conditionA = cb.equal(mess.get(MsgMessaggio_.pfPrincipalTo), pfPrinc);
			criteria.add(cb.and(conditionA));
		} else {
			Predicate conditionA = cb.equal(mess.get(MsgMessaggio_.pfPrincipalFrom), pfPrinc);
			criteria.add(cb.and(conditionA));
		}

		subquery.where(criteria.toArray(new Predicate[criteria.size()]));
		subquery.groupBy(mess.get(MsgMessaggio_.idMsgMessaggio));
		return subquery;
	}

	private Subquery<Integer> creaSubQueryCountTicket(Boolean ricevuti, CriteriaQuery<Long> query, PfPrincipal pfPrinc,
			DeTipoMessaggio tipoMsg) {
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		Subquery<Integer> subquery = query.subquery(Integer.class);
		Root<MsgMessaggio> mess = subquery.from(MsgMessaggio.class);

		// Seleziono il min messaggio con lo stesso ticket
		subquery.select(cb.min(mess.get(MsgMessaggio_.idMsgMessaggio)));

		List<Predicate> criteria = new ArrayList<Predicate>();
		criteria.add(cb.equal(mess.get(MsgMessaggio_.deTipoMessaggio), tipoMsg));

		if (ricevuti) {
			Predicate conditionA = cb.equal(mess.get(MsgMessaggio_.pfPrincipalTo), pfPrinc);
			criteria.add(cb.and(conditionA));
		} else {
			Predicate conditionA = cb.equal(mess.get(MsgMessaggio_.pfPrincipalFrom), pfPrinc);
			criteria.add(cb.and(conditionA));
		}

		subquery.where(criteria.toArray(new Predicate[criteria.size()]));
		subquery.groupBy(mess.get(MsgMessaggio_.idMsgMessaggio));
		return subquery;
	}

	/**
	 * Query per lista messaggi per ticket ricevuti o inviati
	 * 
	 * @param principalId
	 * @param codTipoMessaggio
	 * @param startResultsFrom
	 * @param numRigheVisualizzate
	 * @return
	 */
	public List<MsgMessaggio> findListaMessaggiTicketFromTo(Integer principalId, String codTipoMessaggio,
			int startResultsFrom, int numRigheVisualizzate) {
		DeTipoMessaggio tipoMsg = deTipoMessaggioHome.findById(codTipoMessaggio);
		PfPrincipal pfPrinc = pfPrincipalHome.findById(principalId);

		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<MsgMessaggio> query = cb.createQuery(MsgMessaggio.class);
		Root<MsgMessaggio> messaggio = query.from(MsgMessaggio.class);

		List<Predicate> criteria = new ArrayList<Predicate>();
		criteria.add(cb.equal(messaggio.get(MsgMessaggio_.deTipoMessaggio), tipoMsg));

		Predicate conditionA = cb.equal(messaggio.get(MsgMessaggio_.pfPrincipalTo), pfPrinc);
		Predicate conditionB = cb.equal(messaggio.get(MsgMessaggio_.pfPrincipalFrom), pfPrinc);
		criteria.add(cb.or(conditionA, conditionB));

		// filtro subquery
		Subquery<Integer> subquery = creaSubQueryTicketFromTo(query, pfPrinc, tipoMsg);
		criteria.add(cb.in(messaggio.get(MsgMessaggio_.idMsgMessaggio)).value(subquery));

		query.where(criteria.toArray(new Predicate[criteria.size()]));
		query.select(cb.construct(MsgMessaggio.class, messaggio.get(MsgMessaggio_.idMsgMessaggio),
				messaggio.get(MsgMessaggio_.oggetto), messaggio.get(MsgMessaggio_.corpo),
				messaggio.get(MsgMessaggio_.ticket), messaggio.get(MsgMessaggio_.dtmIns),
				messaggio.get(MsgMessaggio_.dtmMod)));
		query.orderBy(cb.desc(messaggio.get(MsgMessaggio_.idMsgMessaggio)),
				cb.desc(messaggio.get(MsgMessaggio_.dtmMod)));

		TypedQuery<MsgMessaggio> q = entityManager.createQuery(query);
		q.setFirstResult(startResultsFrom);
		q.setMaxResults(numRigheVisualizzate);

		return q.getResultList();

	}

	public Long findCountMessaggiTicketFromTo(Integer principalId, String codTipoMessaggio) {
		DeTipoMessaggio tipoMsg = deTipoMessaggioHome.findById(codTipoMessaggio);
		PfPrincipal pfPrinc = pfPrincipalHome.findById(principalId);

		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<Long> query = cb.createQuery(Long.class);
		Root<MsgMessaggio> messaggio = query.from(MsgMessaggio.class);

		List<Predicate> criteria = new ArrayList<Predicate>();
		criteria.add(cb.equal(messaggio.get(MsgMessaggio_.deTipoMessaggio), tipoMsg));

		Predicate conditionA = cb.equal(messaggio.get(MsgMessaggio_.pfPrincipalTo), pfPrinc);
		Predicate conditionB = cb.equal(messaggio.get(MsgMessaggio_.pfPrincipalFrom), pfPrinc);
		criteria.add(cb.or(conditionA, conditionB));

		// filtro subquery
		Subquery<Integer> subquery = creaSubQueryCountTicketFromTo(query, pfPrinc, tipoMsg);
		criteria.add(cb.in(messaggio.get(MsgMessaggio_.idMsgMessaggio)).value(subquery));

		query.where(criteria.toArray(new Predicate[criteria.size()]));
		query.select(cb.count(messaggio));

		TypedQuery<Long> q = entityManager.createQuery(query);

		return q.getSingleResult();

	}

	private Subquery<Integer> creaSubQueryTicketFromTo(CriteriaQuery<MsgMessaggio> query, PfPrincipal pfPrinc,
			DeTipoMessaggio tipoMsg) {
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		Subquery<Integer> subquery = query.subquery(Integer.class);
		Root<MsgMessaggio> mess = subquery.from(MsgMessaggio.class);

		// Seleziono il min messaggio con lo stesso ticket
		subquery.select(cb.min(mess.get(MsgMessaggio_.idMsgMessaggio)));

		List<Predicate> criteria = new ArrayList<Predicate>();
		criteria.add(cb.equal(mess.get(MsgMessaggio_.deTipoMessaggio), tipoMsg));

		Predicate conditionA = cb.equal(mess.get(MsgMessaggio_.pfPrincipalTo), pfPrinc);
		Predicate conditionB = cb.equal(mess.get(MsgMessaggio_.pfPrincipalFrom), pfPrinc);
		criteria.add(cb.or(conditionA, conditionB));

		subquery.where(criteria.toArray(new Predicate[criteria.size()]));
		subquery.groupBy(mess.get(MsgMessaggio_.idMsgMessaggio));
		return subquery;
	}

	private Subquery<Integer> creaSubQueryCountTicketFromTo(CriteriaQuery<Long> query, PfPrincipal pfPrinc,
			DeTipoMessaggio tipoMsg) {
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		Subquery<Integer> subquery = query.subquery(Integer.class);
		Root<MsgMessaggio> mess = subquery.from(MsgMessaggio.class);

		// Seleziono il min messaggio con lo stesso ticket
		subquery.select(cb.min(mess.get(MsgMessaggio_.idMsgMessaggio)));

		List<Predicate> criteria = new ArrayList<Predicate>();
		criteria.add(cb.equal(mess.get(MsgMessaggio_.deTipoMessaggio), tipoMsg));

		Predicate conditionA = cb.equal(mess.get(MsgMessaggio_.pfPrincipalTo), pfPrinc);
		Predicate conditionB = cb.equal(mess.get(MsgMessaggio_.pfPrincipalFrom), pfPrinc);
		criteria.add(cb.or(conditionA, conditionB));

		subquery.where(criteria.toArray(new Predicate[criteria.size()]));
		subquery.groupBy(mess.get(MsgMessaggio_.idMsgMessaggio));
		return subquery;
	}

	/**
	 * Lista messaggi del GRUPPO
	 * 
	 * @param principalId
	 * @param codTipoMessaggio
	 * @param startResultsFrom
	 * @param numRigheVisualizzate
	 * @return
	 */
	public List<MsgMessaggio> findListaMessaggiTicketGruppo(Integer principalId, String codTipoMessaggio,
			int startResultsFrom, int numRigheVisualizzate) {
		DeTipoMessaggio tipoMsg = deTipoMessaggioHome.findById(codTipoMessaggio);
		PfPrincipal pfPrincipal = pfPrincipalHome.findById(principalId);

		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<MsgMessaggio> query = cb.createQuery(MsgMessaggio.class);
		Root<MsgMessaggio> messaggio = query.from(MsgMessaggio.class);

		List<Predicate> criteria = new ArrayList<Predicate>();
		criteria.add(cb.equal(messaggio.get(MsgMessaggio_.deTipoMessaggio), tipoMsg));

		if (pfPrincipal.isProvincia()) {
			DeProvincia provincia = pfPrincipal.getProvinciasForIdPfPrincipal().iterator().next().getDeProvincia();
			criteria.add(cb.equal(messaggio.get(MsgMessaggio_.deProvinciaTo), provincia));
		}

		query.where(criteria.toArray(new Predicate[criteria.size()]));
		query.select(cb.construct(MsgMessaggio.class, messaggio.get(MsgMessaggio_.idMsgMessaggio),
				messaggio.get(MsgMessaggio_.oggetto), messaggio.get(MsgMessaggio_.corpo),
				messaggio.get(MsgMessaggio_.ticket), messaggio.get(MsgMessaggio_.dtmIns),
				messaggio.get(MsgMessaggio_.dtmMod)));
		query.orderBy(cb.desc(messaggio.get(MsgMessaggio_.idMsgMessaggio)),
				cb.desc(messaggio.get(MsgMessaggio_.dtmMod)));

		TypedQuery<MsgMessaggio> q = entityManager.createQuery(query);
		q.setFirstResult(startResultsFrom);
		q.setMaxResults(numRigheVisualizzate);

		return q.getResultList();

	}

	public Long findCountMessaggiTicketGruppo(Integer principalId, String codTipoMessaggio) {
		DeTipoMessaggio tipoMsg = deTipoMessaggioHome.findById(codTipoMessaggio);
		PfPrincipal pfPrincipal = pfPrincipalHome.findById(principalId);

		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<Long> query = cb.createQuery(Long.class);
		Root<MsgMessaggio> messaggio = query.from(MsgMessaggio.class);

		List<Predicate> criteria = new ArrayList<Predicate>();
		criteria.add(cb.equal(messaggio.get(MsgMessaggio_.deTipoMessaggio), tipoMsg));

		if (pfPrincipal.isProvincia()) {
			DeProvincia provincia = pfPrincipal.getProvinciasForIdPfPrincipal().iterator().next().getDeProvincia();
			criteria.add(cb.equal(messaggio.get(MsgMessaggio_.deProvinciaTo), provincia));
		}

		query.where(criteria.toArray(new Predicate[criteria.size()]));
		query.select(cb.count(messaggio));

		TypedQuery<Long> q = entityManager.createQuery(query);

		return q.getSingleResult();

	}

	private Subquery<Integer> creaSubQueryTicketGruppo(CriteriaQuery<MsgMessaggio> query, DeProvincia provincia,
			DeTipoMessaggio tipoMsg) {
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		Subquery<Integer> subquery = query.subquery(Integer.class);
		Root<MsgMessaggio> mess = subquery.from(MsgMessaggio.class);

		// Seleziono il min messaggio con lo stesso ticket
		subquery.select(cb.min(mess.get(MsgMessaggio_.idMsgMessaggio)));

		List<Predicate> criteria = new ArrayList<Predicate>();
		criteria.add(cb.equal(mess.get(MsgMessaggio_.deTipoMessaggio), tipoMsg));

		criteria.add(cb.equal(mess.get(MsgMessaggio_.deProvinciaTo), provincia));

		subquery.where(criteria.toArray(new Predicate[criteria.size()]));
		subquery.groupBy(mess.get(MsgMessaggio_.idMsgMessaggio));
		return subquery;
	}

	private Subquery<Integer> creaSubQueryCountTicketGruppo(CriteriaQuery<Long> query, DeProvincia provincia,
			DeTipoMessaggio tipoMsg) {
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		Subquery<Integer> subquery = query.subquery(Integer.class);
		Root<MsgMessaggio> mess = subquery.from(MsgMessaggio.class);

		// Seleziono il min messaggio con lo stesso ticket
		subquery.select(cb.min(mess.get(MsgMessaggio_.idMsgMessaggio)));

		List<Predicate> criteria = new ArrayList<Predicate>();
		criteria.add(cb.equal(mess.get(MsgMessaggio_.deTipoMessaggio), tipoMsg));

		criteria.add(cb.equal(mess.get(MsgMessaggio_.deProvinciaTo), provincia));

		subquery.where(criteria.toArray(new Predicate[criteria.size()]));
		subquery.groupBy(mess.get(MsgMessaggio_.idMsgMessaggio));
		return subquery;
	}

	public List<MsgMessaggio> findMessaggiByTicket(String ticket, Integer idPfPrincipal) {
		List<MsgMessaggio> result = new ArrayList<MsgMessaggio>();

		TypedQuery<MsgMessaggio> query = entityManager.createNamedQuery("findMessaggiByTicket", MsgMessaggio.class);
		query = query.setParameter("ticket", ticket);
		query = query.setParameter("idPrincipal", idPfPrincipal);

		result = query.getResultList();

		return result;
	}

	public DeProvincia getProvinciaDestinataria(PfPrincipal pfPrincipal) {
		DeProvincia provincia = null;
		// se chi invia è un cittadino
		if (pfPrincipal.isUtente()) {
			UtenteInfo utenteInfo = pfPrincipal.getUtenteInfo();
			if (utenteInfo.getDeProvincia() != null) {
				provincia = utenteInfo.getDeProvincia();
			} else if (utenteInfo.getDeComuneDomicilio() != null) {
				provincia = utenteInfo.getDeComuneDomicilio().getDeProvincia();
			} else if (utenteInfo.getDeComuneNascita() != null) {
				provincia = utenteInfo.getDeComuneNascita().getDeProvincia();
			} else {
				throw new EJBException("Impossibile spedire il messaggio per l'utente '" + pfPrincipal.getUsername()
						+ "' in quanto non ha una provincia di riferimento.");
			}
			// se chi invia è un'azienda
		} else if (pfPrincipal.isAzienda()) {
			provincia = aziendaInfoHome.getProvinciaRiferimento(pfPrincipal.getAziendaInfo());
		} else {
			throw new EJBException("Impossibile identificare il tipo di utente.");
		}

		log.info("Il messaggio dell'utente '" + pfPrincipal.getUsername() + "' verrà inviato al gruppo "
				+ provincia.getDenominazione());
		return provincia;
	}

	/**
	 * inserimento messaggio di SUPPORTO
	 * 
	 * @param message
	 * @param idPfPrincipal
	 */
	public void saveMsgSupporto(MsgMessaggio message, Integer idPfPrincipal) {
		try {
			PfPrincipal pfPrincipal = pfPrincipalHome.findById(idPfPrincipal);
			MsgMessaggio msg = new MsgMessaggio();
			msg.setCorpo(message.getCorpo());
			msg.setOggetto(message.getOggetto());
			msg.setDeTipoMessaggio(deTipoMessaggioHome.findById(ConstantsSingleton.MsgMessaggio.SUPPORTO));
			msg.setPfPrincipalFrom(pfPrincipal);
			msg.setDeProvinciaTo(getProvinciaDestinataria(pfPrincipal));
			msg.setPfPrincipalMod(pfPrincipal);
			msg.setPfPrincipalIns(pfPrincipal);
			msg.setDtmIns(new Date());
			msg.setDtmMod(new Date());

			MsgMessaggio msgPersist = this.persistFlush(msg);

			if (msgPersist.getTicket() == null) {
				msgPersist.setTicket(msgPersist.getIdMsgMessaggio().toString());

				this.merge(msgPersist);
			}
		} catch (EJBException e) {
			e.printStackTrace();
		}
	}

	public void replyMsgSupporto(MsgMessaggio message, String reply, Integer idPfPrincipal) {
		try {
			PfPrincipal pfPrincipal = pfPrincipalHome.findById(idPfPrincipal);

			MsgMessaggio msg = new MsgMessaggio();
			msg.setCorpo(reply);
			msg.setOggetto(message.getOggetto());
			msg.setTicket(message.getTicket());
			msg.setPrecedente(message);
			msg.setDeTipoMessaggio(deTipoMessaggioHome.findById(ConstantsSingleton.MsgMessaggio.SUPPORTO));

			msg.setPfPrincipalFrom(pfPrincipal);
			msg.setPfPrincipalTo(message.getPfPrincipalFrom());

			msg.setPfPrincipalMod(pfPrincipal);
			msg.setPfPrincipalIns(pfPrincipal);
			msg.setDtmIns(new Date());
			msg.setDtmMod(new Date());

			MsgMessaggio msgPersist = this.persistFlush(msg);

		} catch (EJBException e) {
			e.printStackTrace();
		}
	}

	/**
	 * salvataggio messaggio di CONSULENZA
	 * 
	 * @param message
	 * @param idPfPrincipal
	 */
	public void saveMsgConsulenza(MsgMessaggio message, Integer idPfPrincipal) {
		try {
			PfPrincipal pfPrincipal = pfPrincipalHome.findById(idPfPrincipal);
			MsgMessaggio msg = new MsgMessaggio();
			msg.setCorpo(message.getCorpo());
			msg.setOggetto(message.getOggetto());
			msg.setDeTipoMessaggio(deTipoMessaggioHome.findById(ConstantsSingleton.MsgMessaggio.ESPERTO));
			msg.setPfPrincipalFrom(pfPrincipal);
			msg.setDeProvinciaTo(getProvinciaDestinataria(pfPrincipal));
			msg.setPfPrincipalMod(pfPrincipal);
			msg.setPfPrincipalIns(pfPrincipal);
			msg.setDtmIns(new Date());
			msg.setDtmMod(new Date());

			MsgMessaggio msgPersist = this.persistFlush(msg);

			if (msgPersist.getTicket() == null) {
				msgPersist.setTicket(msgPersist.getIdMsgMessaggio().toString());

				this.merge(msgPersist);
			}
		} catch (EJBException e) {
			e.printStackTrace();
		}
	}

	public void replyMsgConsulenza(MsgMessaggio message, String reply, Integer idPfPrincipal) {
		try {
			PfPrincipal pfPrincipal = pfPrincipalHome.findById(idPfPrincipal);

			MsgMessaggio msg = new MsgMessaggio();
			msg.setCorpo(reply);
			msg.setOggetto(message.getOggetto());
			msg.setTicket(message.getTicket());
			msg.setPrecedente(message);
			msg.setDeTipoMessaggio(deTipoMessaggioHome.findById(ConstantsSingleton.MsgMessaggio.ESPERTO));

			msg.setPfPrincipalFrom(pfPrincipal);
			msg.setPfPrincipalTo(message.getPfPrincipalFrom());

			msg.setPfPrincipalMod(pfPrincipal);
			msg.setPfPrincipalIns(pfPrincipal);
			msg.setDtmIns(new Date());
			msg.setDtmMod(new Date());

			MsgMessaggio msgPersist = this.persistFlush(msg);

		} catch (EJBException e) {
			e.printStackTrace();
		}
	}

}

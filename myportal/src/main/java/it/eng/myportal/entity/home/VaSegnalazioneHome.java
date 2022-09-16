package it.eng.myportal.entity.home;
// Generated 2-apr-2019 15.59.18 by Hibernate Tools 3.5.0.Final

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import it.eng.myportal.entity.VaSegnalazione;

/**
 * Home object for domain model class VaSegnalazione.
 * 
 * @see it.eng.myportal.entity.VaSegnalazione
 * @author Hibernate Tools
 */
@Stateless
public class VaSegnalazioneHome {

	private static final Log log = LogFactory.getLog(VaSegnalazioneHome.class);

	@PersistenceContext
	private EntityManager entityManager;

	public void persist(VaSegnalazione transientInstance) {
		log.debug("persisting VaSegnalazione instance");
		try {
			entityManager.persist(transientInstance);
			log.debug("persist successful");
		} catch (RuntimeException re) {
			log.error("persist failed", re);
			throw re;
		}
	}

	public void remove(VaSegnalazione persistentInstance) {
		log.debug("removing VaSegnalazione instance");
		try {
			entityManager.remove(persistentInstance);
			log.debug("remove successful");
		} catch (RuntimeException re) {
			log.error("remove failed", re);
			throw re;
		}
	}

	public VaSegnalazione merge(VaSegnalazione detachedInstance) {
		log.debug("merging VaSegnalazione instance");
		try {
			VaSegnalazione result = entityManager.merge(detachedInstance);
			log.debug("merge successful");
			return result;
		} catch (RuntimeException re) {
			log.error("merge failed", re);
			throw re;
		}
	}

	public VaSegnalazione findById(Integer id) {
		log.debug("getting VaSegnalazione instance with id: " + id);
		try {
			VaSegnalazione instance = entityManager.find(VaSegnalazione.class, id);
			log.debug("get successful");
			return instance;
		} catch (RuntimeException re) {
			log.error("get failed", re);
			throw re;
		}
	}

	/**
	 * recupero la segnalazione associata a quella vacancy, al mittente e al destinatario
	 * 
	 * @param codComunicazioneSil
	 * @return
	 */
	public VaSegnalazione findSegnalazioneByMailIdVacMitt(String parEmail, Integer parIdVaDatiVacancy,
			Integer parIdPfPrincipal) {
		try {
			VaSegnalazione segnalazione = entityManager
					.createNamedQuery("findSegnalazioneByMailIdVacMitt", VaSegnalazione.class)
					.setParameter("parEmail", parEmail.trim().toLowerCase())
					.setParameter("parIdDatiVacancy", parIdVaDatiVacancy)
					.setParameter("parIdPfPrincipal", parIdPfPrincipal).getSingleResult();
			if (segnalazione != null)
				return segnalazione;
			else
				return null;
		} catch (Exception e) {
			return null;
		}

	}

}

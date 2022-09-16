package it.eng.myportal.timer;

import it.eng.myportal.entity.ClInvioComunicazione;
import it.eng.myportal.entity.CvCandidaturaCl;
import it.eng.myportal.entity.VaVacancyCl;
import it.eng.myportal.entity.ejb.ClicLavoroEjb;
import it.eng.myportal.entity.ejb.ClicLavoroVacancyEjb;
import it.eng.myportal.entity.ejb.DbManagerEjb;
import it.eng.myportal.entity.ejb.GenDecodRandom;
import it.eng.myportal.entity.ejb.ts.TsTimerEJB;
import it.eng.myportal.entity.home.ClInvioComunicazioneHome;
import it.eng.myportal.entity.home.CvDatiPersonaliHome;
import it.eng.myportal.entity.home.decodifiche.DeStatoInvioClHome;
import it.eng.myportal.enums.AzioneServizio;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Schedule;
import javax.ejb.Singleton;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


@Singleton
public class ReSendClicLavoroEjb {

	protected final Log log = LogFactory.getLog(ReSendClicLavoroEjb.class);
	private static final String TIMER_NAME = "reSendClickLavoroTimer";
	
	@PersistenceContext
	protected EntityManager entityManager;

	@EJB
	DbManagerEjb dbManagerEjb;

	@EJB
	GenDecodRandom genDecodeRandom;
	
	@EJB
	CvDatiPersonaliHome cvDatiPersonaliHome;
	
	@EJB
	ClicLavoroEjb clicLavoroEjb;
	
	@EJB
	ClicLavoroVacancyEjb clicLavoroVacancyEjb;
	
	@EJB
	ClInvioComunicazioneHome clInvioComunicazioneHome;
	
	@EJB
	DeStatoInvioClHome deStatoInvioClHome;
	
	@EJB
	TsTimerEJB tsTimerEJB;
		
	@Schedule(dayOfMonth="*", dayOfWeek="*", hour="6", persistent=false)	
	public void resendComunicazioniCandidaturaClicLavoro() {
		if (tsTimerEJB.isTimerHostEnabled(TIMER_NAME)) {
			log.info("===BATCH RE-INVIO COMUNICAZIONI CANDIDATURA CLICLAVORO START===");		
			List<ClInvioComunicazione> elencoCandidature = getComunicazioniDaReInviare(AzioneServizio.INVIO_CANDIDATURA);
			for (ClInvioComunicazione clInvioComunicazione : elencoCandidature) {
				try {				
					clicLavoroEjb.reinviaCandidatura(clInvioComunicazione);				
				} catch (Exception e) {
					log.error("Errore durante l'invio della comunicazione:" + e.getMessage());
				}		
			}
			log.info("===BATCH RE-INVIO COMUNICAZIONI CANDIDATURA CLICLAVORO TERMINATO===");	
		
			log.info("===BATCH RE-INVIO COMUNICAZIONI VACANCY CLICLAVORO START===");		
			List<ClInvioComunicazione> elencoVacancy = getComunicazioniDaReInviare(AzioneServizio.INVIO_VACANCY);
			for (ClInvioComunicazione clInvioComunicazione : elencoCandidature) {
				try {				
					clicLavoroVacancyEjb.reinviaVacancy(clInvioComunicazione);				
				} catch (Exception e) {
					log.error("Errore durante l'invio della comunicazione:" + e.getMessage());
				}		
			}
			log.info("===BATCH RE-INVIO COMUNICAZIONI VACANCY CLICLAVORO TERMINATO===");	
		}
	}

	/**
	 * Restituisce l'elenco di tutte le candidature da inviare ovvero che sono in uno di questi stati:
	 * <ul>
	 * <li>PA</li>
	 * <li>MA</li>
	 * <li>VA</li>
	 * <li>CA</li>
	 * </ul>
	 * @return
	 */
	private List<CvCandidaturaCl> getCandidatureDaInviare() {
		TypedQuery<CvCandidaturaCl> query = entityManager.createNamedQuery("findCandidatureDaInviare", CvCandidaturaCl.class);
		List<CvCandidaturaCl> list = query.getResultList();
		return list;
	}
	
	private List<ClInvioComunicazione> getComunicazioniDaReInviare(AzioneServizio servizio) {
		TypedQuery<ClInvioComunicazione> query = entityManager.createNamedQuery("findComunicazioniDaReInviare", ClInvioComunicazione.class).setParameter("azServ", servizio);
		List<ClInvioComunicazione> list = query.getResultList();
		return list;
	}
	
	/**
	 * Restituisce l'elenco di tutte le candidature da inserire sul portale di provenienza da CLICLAVORO e SIL
	 * 
	 * 
	 * @return
	 */
	private List<ClInvioComunicazione> getComunicazioniDaInserire(AzioneServizio servizio) {
		TypedQuery<ClInvioComunicazione> query = entityManager.createNamedQuery("findComunicazioniDaInserire", ClInvioComunicazione.class).setParameter("azServ", servizio);
		List<ClInvioComunicazione> list = query.getResultList();
		return list;
	}
	
	/**
	 * Restituisce l'elenco di tutte le vacancy da inviare ovvero che sono in uno di questi stati:
	 * <ul>
	 * <li>PA</li>
	 * <li>MA</li>
	 * <li>VA</li>
	 * <li>CA</li>
	 * </ul>
	 * @return
	 */
	private List<VaVacancyCl> getVacancyDaInviare() {
		TypedQuery<VaVacancyCl> query = entityManager.createNamedQuery("findVacancyDaInviare", VaVacancyCl.class);
		List<VaVacancyCl> list = query.getResultList();
		return list;
	}
}

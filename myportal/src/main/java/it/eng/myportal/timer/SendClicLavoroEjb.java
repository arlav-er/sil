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
public class SendClicLavoroEjb {

	protected final Log log = LogFactory.getLog(SendClicLavoroEjb.class);
	private static final String TIMER_NAME = "sendClicLavoroTimer";
	
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
	
	@Schedule(dayOfMonth="*", dayOfWeek="*", hour="23", persistent=false)
	public void sendComunicazioniCandidaturaClicLavoro() {
		if (tsTimerEJB.isTimerHostEnabled(TIMER_NAME)) {
			log.info("===BATCH CREAZIONE E INVIO COMUNICAZIONI CANDIDATURA CLICLAVORO START===");		
			List<CvCandidaturaCl> elencoCandidature = getCandidatureDaInviare();
			for (CvCandidaturaCl cvCandidaturaCl : elencoCandidature) {
				//T1 - crea la comunicazione
				ClInvioComunicazione daInviare = clicLavoroEjb.creaComunicazione(cvCandidaturaCl);
				try {   				
					//T2 - invia la candidatura
					clicLavoroEjb.inviaCandidatura(cvCandidaturaCl, daInviare);
				} catch (Exception e) {					
					log.error("Errore durante l'invio della candidatura: " + e.getMessage());					
				}		
			}
			log.info("===BATCH CREAZIONE E INVIO COMUNICAZIONI CANDIDATURA CLICLAVORO TERMINATO===");
			
			
			log.info("===BATCH CREAZIONE E INVIO COMUNICAZIONI VACANCY CLICLAVORO START===");		
			List<VaVacancyCl> elencoVacancy = getVacancyDaInviare();
			for (VaVacancyCl vaVacancyCl : elencoVacancy) {				
				//T1 - crea la comunicazione
				ClInvioComunicazione daInviare = clicLavoroVacancyEjb.creaComunicazione(vaVacancyCl);
				try {  
					//T2 - invia la candidatura
					clicLavoroVacancyEjb.inviaVacancy(vaVacancyCl, daInviare);
				} catch (Exception e) {
					log.error("Errore durante l'invio della vacancy: "+ e.getMessage());					
				}		
			}
			log.info("===BATCH CREAZIONE E INVIO COMUNICAZIONI VACANCY CLICLAVORO TERMINATO===");
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

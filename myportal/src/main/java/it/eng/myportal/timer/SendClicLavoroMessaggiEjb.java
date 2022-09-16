package it.eng.myportal.timer;

import it.eng.myportal.entity.ClInvioComunicazione;
import it.eng.myportal.entity.ejb.ClicLavoroMessaggioEjb;
import it.eng.myportal.entity.ejb.ts.TsTimerEJB;
import it.eng.myportal.entity.home.ClInvioComunicazioneHome;

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
public class SendClicLavoroMessaggiEjb {

	protected final Log log = LogFactory.getLog(SendClicLavoroMessaggiEjb.class);
	private static final String TIMER_NAME = "sendClicLavoroMessaggiTimer";
	
	@PersistenceContext
	protected EntityManager entityManager;
	
	@EJB
	ClicLavoroMessaggioEjb clicLavoroMessaggioEjb;
	
	@EJB
	ClInvioComunicazioneHome clInvioComunicazioneHome;
	
	@EJB
	TsTimerEJB tsTimerEJB;
	
	@Schedule(minute="*/5", hour="*", persistent=false)
	public void sendComunicazioniMessaggiClicLavoro() {
		if (tsTimerEJB.isTimerHostEnabled(TIMER_NAME)) {
			log.info("===BATCH CREAZIONE E INVIO COMUNICAZIONI MESSAGGI CLICLAVORO START===");		
			
			List<ClInvioComunicazione> comunicazioniDainviare = getComunicazioniDaInserire();
			for (ClInvioComunicazione messaggio : comunicazioniDainviare) {
				try {				
					clInvioComunicazioneHome.inviaMessaggio(messaggio);							
				} catch (Exception e) {
					log.error("Errore durante la creazione del messaggio: "+ e.getMessage());
				}
				
			}		
			
			log.info("===BATCH CREAZIONE E INVIO COMUNICAZIONI MESSAGGI CLICLAVORO TERMINATO===");
		}
	}
	
	
	/**
	 * Restituisce l'elenco di tutte le comunicazioni da inviare ovvero che sono in uno di questi stati:
	 * <ul>
	 * <li>PA</li>
	 * <li>MA</li>
	 * <li>VA</li>
	 * <li>CA</li>
	 * </ul>
	 * @return
	 */
	private List<ClInvioComunicazione> getComunicazioniDaInserire() {
		TypedQuery<ClInvioComunicazione> query = entityManager.createNamedQuery("findMessaggiDaInviare", ClInvioComunicazione.class);
		List<ClInvioComunicazione> list = query.getResultList();
		return list;
	}
	
	
	
}

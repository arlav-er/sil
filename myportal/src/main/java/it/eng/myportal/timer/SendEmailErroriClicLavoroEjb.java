package it.eng.myportal.timer;

import it.eng.myportal.dtos.EmailDTO;
import it.eng.myportal.entity.ejb.ClicLavoroEjb;
import it.eng.myportal.entity.ejb.ClicLavoroVacancyEjb;
import it.eng.myportal.entity.ejb.DbManagerEjb;
import it.eng.myportal.entity.ejb.GenDecodRandom;
import it.eng.myportal.entity.ejb.ts.TsTimerEJB;
import it.eng.myportal.entity.home.ClInvioComunicazioneHome;
import it.eng.myportal.entity.home.CvDatiPersonaliHome;
import it.eng.myportal.entity.home.decodifiche.DeStatoInvioClHome;
import it.eng.myportal.utils.Mailer;

import java.util.List;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.Schedule;
import javax.ejb.Singleton;
import javax.jms.ConnectionFactory;
import javax.jms.Queue;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


//@Singleton
public class SendEmailErroriClicLavoroEjb {

	protected final Log log = LogFactory.getLog(SendEmailErroriClicLavoroEjb.class);
	private static final String TIMER_NAME = "sendEmailErroriClicLavoroTimer";
	
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
	
	@Resource(name = "RemoteConnectionFactory", mappedName = "java:/RemoteConnectionFactory")
	private ConnectionFactory connectionFactory;

	@Resource(name = "email_queue", mappedName = "java:/queue/email_queue")
	private Queue emailQueue;
	
	//@Schedule(dayOfMonth="*", dayOfWeek="*", hour="8", persistent=false)  	
	public void sendEmailClicLavoro() {
		if (tsTimerEJB.isTimerHostEnabled(TIMER_NAME)) {
			log.info("===BATCH INVIO EMAIL ERRORI CLICLAVORO START===");		
			List<Object[]> elencoComunicazioni = getComunicazioniErrate();
			String elencoErrori = "";
			
			elencoErrori += "<table><tr><th>NUM COMUNICAZIONI</th><th>STATO INVIO</th><th>MITTENTE</th><th>DESTINATARIO</th></tr>";
			
			for (Object[] conteggioCom : elencoComunicazioni) {								
				elencoErrori += "<tr>"+
									"<td>"+
									conteggioCom[0].toString() +
									"</td>"+
									"<td>"+
									conteggioCom[1].toString() +
									"</td>"+
									"<td>"+
									conteggioCom[2].toString() +
									"</td>"+
									"<td>"+
									conteggioCom[3].toString() +
									"</td>"+
								"</tr>";			 
			}
			elencoErrori += "</table>";
			
			EmailDTO errorsEmail = EmailDTO.buildErrorsClicLavoroEmail(elencoErrori);
			Mailer.getInstance().putInQueue(connectionFactory, emailQueue, errorsEmail);
			
			log.info("===BATCH INVIO EMAIL ERRORI CLICLAVORO TERMINATO===");
		}
	}
	
	
	/**
	 * Restituisce l'elenco di tutte le candidature andate in errore
	 * 
	 * 
	 * @return
	 */
	private List<Object[]> getComunicazioniErrate() {
		Query q = entityManager.createQuery("select count(l.idClInvioComunicazione) as numCom, l.deStatoInvioCl.codStatoInvioCl as statoCom, l.mittente, l.destinatario "
				+ " from ClInvioComunicazione l "
				+ " where dtmIns >= current_date - 1 "
				+ " group by l.deStatoInvioCl.codStatoInvioCl, l.mittente, l.destinatario");
		List<Object[]> results = q.getResultList();		
		return results;
	}
	
	
}

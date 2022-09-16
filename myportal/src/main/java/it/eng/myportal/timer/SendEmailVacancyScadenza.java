package it.eng.myportal.timer;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.Schedule;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.jms.ConnectionFactory;
import javax.jms.Queue;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import it.eng.myportal.dtos.EmailDTO;
import it.eng.myportal.dtos.NotificaScadenzaVacancyDTO;
import it.eng.myportal.entity.home.VaDatiVacancyHome;
import it.eng.myportal.utils.ConstantsSingleton;
import it.eng.myportal.utils.Mailer;

@Startup
@Singleton
public class SendEmailVacancyScadenza {

	@EJB
	VaDatiVacancyHome vaDatiVacancyHome;

	@Resource(name = "RemoteConnectionFactory", mappedName = "java:/RemoteConnectionFactory")
	private ConnectionFactory connectionFactory;

	@Resource(name = "email_queue", mappedName = "java:/queue/email_queue")
	private Queue emailQueue;

	protected final Log log = LogFactory.getLog(SendEmailVacancyScadenza.class);

	@Schedule(dayOfMonth = "*", dayOfWeek = "*", hour = "6", minute = "30", persistent = false)
	public void sendEmailClicLavoro() {

		if (!ConstantsSingleton.COD_REGIONE.equals(ConstantsSingleton.COD_REGIONE_TRENTO)) {
			log.info("===BATCH INVIO EMAIL VACANCY SCADENZA DISATTIVATO: SOLO PAT===");
			return;
		}

		log.info("===BATCH INVIO EMAIL VACANCY SCADENZA START===");
		List<NotificaScadenzaVacancyDTO> vacancyList = vaDatiVacancyHome.findAllInScadenza();
		log.warn("Sono state selezionate num vacancy: " + vacancyList.size());
		// key: indirizzo email destinatario; value: emailDTO
		Map<String, List<NotificaScadenzaVacancyDTO>> emailVacanciesMap = new HashMap<String, List<NotificaScadenzaVacancyDTO>>();
		for (NotificaScadenzaVacancyDTO current : vacancyList) { 
			if(canAnotherProroga(current.getDtPubblicazione(),current.getDtScadenzaPubblicazione())){				
				if (emailVacanciesMap.containsKey(current.getEmail())) {
					List<NotificaScadenzaVacancyDTO> currentEmailDTOList = emailVacanciesMap.get(current.getEmail());
					currentEmailDTOList.add(current);
					emailVacanciesMap.put(current.getEmail(), currentEmailDTOList);
				} else {
					List<NotificaScadenzaVacancyDTO> currentEmailDTOList = new ArrayList<NotificaScadenzaVacancyDTO>();
					currentEmailDTOList.add(current);
					emailVacanciesMap.put(current.getEmail(), currentEmailDTOList);
				}
			}

		}
		List<EmailDTO> emailDTOList = new ArrayList<EmailDTO>();
		for (String current : emailVacanciesMap.keySet()) {
			EmailDTO emailDTO = EmailDTO.buildVacancyScadenzaEmail(emailVacanciesMap.get(current));
			emailDTOList.add(emailDTO);
		}
		for (EmailDTO current : emailDTOList) {
			Mailer.getInstance().putInQueue(connectionFactory, emailQueue, current);
		}
		log.info("===BATCH INVIO EMAIL VACANCY SCADENZA TERMINATO===");
	}
	
	public boolean canAnotherProroga(Date dtPubblicazione, Date dtScadenzaPubblicazione) {
		Calendar scadenza = Calendar.getInstance();
		long diffInMillies, diff = 0;
		// add 30 days to date of scadenza
		scadenza.setTime(dtScadenzaPubblicazione);
		scadenza.add(Calendar.DAY_OF_MONTH, 30);

		Date newScadezaDate = scadenza.getTime();
		diffInMillies = newScadezaDate.getTime() - dtPubblicazione.getTime();
		diff = TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS);
		if (diff < 60)
			return true;
		else
			return false;
	}
	
	

}

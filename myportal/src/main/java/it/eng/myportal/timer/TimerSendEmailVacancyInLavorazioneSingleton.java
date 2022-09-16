package it.eng.myportal.timer;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Schedule;
import javax.ejb.Singleton;
import javax.ejb.Timeout;
import javax.ejb.Timer;
import javax.ejb.TimerService;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.jms.ConnectionFactory;
import javax.jms.Queue;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import it.eng.myportal.dtos.EmailDTO;
import it.eng.myportal.dtos.NotificaInLavVacancyDTO;
import it.eng.myportal.entity.ejb.ts.TimerNotFoundException;
import it.eng.myportal.entity.ejb.ts.TsGetOpzioniEJB;
import it.eng.myportal.entity.ejb.ts.TsTimerEJB;
import it.eng.myportal.entity.home.VaDatiVacancyHome;
import it.eng.myportal.entity.ts.TsTimer;
import it.eng.myportal.utils.Mailer;

@Singleton
@LocalBean
public class TimerSendEmailVacancyInLavorazioneSingleton implements ITimerAmministrato{

	protected final Log log = LogFactory.getLog(TimerSendEmailVacancyInLavorazioneSingleton.class);
	private static final String TIMER_NAME = "sendEmailVacancyInLavTimer";
	private Date lastProgrammaticTimeout;
	private Date lastAutomaticTimeout;
	
	@Resource
	TimerService timerService;
	
	@EJB
	VaDatiVacancyHome vaDatiVacancyHome;

	@EJB
	TsTimerEJB tsTimerEJB;

	@EJB
	TsGetOpzioniEJB tsGetOpzioniEJB;

	@Resource(name = "RemoteConnectionFactory", mappedName = "java:/RemoteConnectionFactory")
	private ConnectionFactory connectionFactory;

	@Resource(name = "email_queue", mappedName = "java:/queue/email_queue")
	private Queue emailQueue;

	@Schedule(dayOfMonth = "*", dayOfWeek = "*", hour = "09", minute = "27", persistent = false)
	@TransactionAttribute(TransactionAttributeType.NEVER)
	public void sendEmailVacancyInLav() {

		if (tsTimerEJB.isTimerHostEnabled(TIMER_NAME)) {
			this.setLastAutomaticTimeout(new Date());
			log.info("===BATCH CREAZIONE E INVIO MAIL AD AZIENDA CON VACANCY IN LAV ===");
			this.sendEmailVacancyInLavBatchCore();
			log.info("===BATCH CREAZIONE E INVIO MAIL AD AZIENDA CON VACANCY IN LAV TERMINATO===");
		}
	}

	private void sendEmailVacancyInLavBatchCore() {
		int numGGVacInLav = 0;
		if (tsGetOpzioniEJB.getNumGGVacInLavTimer() != null) {
			numGGVacInLav = tsGetOpzioniEJB.getNumGGVacInLavTimer().intValue();
		}
		List<NotificaInLavVacancyDTO> vacancyList = vaDatiVacancyHome.findAllInLav(numGGVacInLav);
		log.warn("Sono state selezionate num vacancy: " + vacancyList.size());

		Map<String, List<NotificaInLavVacancyDTO>> emailVacanciesMap = new HashMap<String, List<NotificaInLavVacancyDTO>>();
		for (NotificaInLavVacancyDTO current : vacancyList) {

			if (emailVacanciesMap.containsKey(current.getEmail())) {
				List<NotificaInLavVacancyDTO> currentEmailDTOList = emailVacanciesMap.get(current.getEmail());
				currentEmailDTOList.add(current);
				emailVacanciesMap.put(current.getEmail(), currentEmailDTOList);
			} else {
				List<NotificaInLavVacancyDTO> currentEmailDTOList = new ArrayList<NotificaInLavVacancyDTO>();
				currentEmailDTOList.add(current);
				emailVacanciesMap.put(current.getEmail(), currentEmailDTOList);
			}
		}
		List<EmailDTO> emailDTOList = new ArrayList<EmailDTO>();
		for (String current : emailVacanciesMap.keySet()) {
			EmailDTO emailDTO = EmailDTO.buildVacancyInLavEmail(emailVacanciesMap.get(current), numGGVacInLav);
			emailDTOList.add(emailDTO);
		}
		for (EmailDTO current : emailDTOList) {
			Mailer.getInstance().putInQueue(connectionFactory, emailQueue, current);
		}

	}
	
	public boolean isLogicEnabled() {
		try {
			return tsTimerEJB.isTimerLogicEnabled(TIMER_NAME);
		} catch (TimerNotFoundException e) {
			log.info("Timer non trovato: torno FALSE isEnabledDaTsTimer() per " + TIMER_NAME + e.getMessage());
			return false;
		}
		
	}
	public boolean setLogicEnabled(boolean operativo) {
		try {
			TsTimer tt = tsTimerEJB.setTimerLogicEnabled(TIMER_NAME, operativo);
			return tt.getFlagAbilitato();
		} catch (TimerNotFoundException e) {
			log.error("GRAVE ERRORE set timer " + TIMER_NAME + e.getMessage());
			return false;
		}
		
	}
	
	public void setTimer(long intervalDuration) {
		log.debug("Setting a programmatic timeout for " + intervalDuration + " milliseconds from now.");
		Timer timer = timerService.createTimer(intervalDuration, "Created new programmatic timer");		
	}
	
	@Timeout
	public void programmaticTimeout() {
		log.info("===BATCH TimerSendEmailVacancyInLavorazioneSingleton: timeout MANUALE, NON controllo abilitazione TIMER ===");		
		this.setLastProgrammaticTimeout(new Date());
		this.sendEmailVacancyInLavBatchCore();
		log.info("===BATCH TimerSendEmailVacancyInLavorazioneSingleton: timeout MANUALE, NON controllo abilitazione TIMER ===");
	}
	
	public String getLastProgrammaticTimeout() {
		if (lastProgrammaticTimeout != null) {
			return lastProgrammaticTimeout.toString();
		} else {
			return "never";
		}
	}

	public void setLastProgrammaticTimeout(Date lastTimeout) {
		this.lastProgrammaticTimeout = lastTimeout;
	}

	public String getLastAutomaticTimeout() {
		if (lastAutomaticTimeout != null) {
			return lastAutomaticTimeout.toString();
		} else {
			return "never";
		}
	}

	public void setLastAutomaticTimeout(Date lastAutomaticTimeout) {
		this.lastAutomaticTimeout = lastAutomaticTimeout;
	}

}

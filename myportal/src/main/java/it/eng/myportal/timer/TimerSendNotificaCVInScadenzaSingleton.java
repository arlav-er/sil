package it.eng.myportal.timer;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.EJBException;
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
import it.eng.myportal.entity.AppNotifica;
import it.eng.myportal.entity.CvDatiPersonali;
import it.eng.myportal.entity.PfPrincipal;
import it.eng.myportal.entity.ejb.ts.TimerNotFoundException;
import it.eng.myportal.entity.ejb.ts.TsGetOpzioniEJB;
import it.eng.myportal.entity.ejb.ts.TsTimerEJB;
import it.eng.myportal.entity.enums.TipoNotificaEnum;
import it.eng.myportal.entity.home.AppNotificaHome;
import it.eng.myportal.entity.home.CvDatiPersonaliHome;
import it.eng.myportal.entity.home.PfPrincipalHome;
import it.eng.myportal.entity.home.VaDatiVacancyHome;
import it.eng.myportal.entity.ts.TsTimer;
import it.eng.myportal.rest.app.helper.AppUtils;
import it.eng.myportal.rest.app.helper.StatoNotifica;
import it.eng.myportal.utils.ConstantsSingleton;
import it.eng.myportal.utils.Mailer;
import it.eng.sil.base.utils.ConstantsBaseCommons;

@Singleton
@LocalBean
public class TimerSendNotificaCVInScadenzaSingleton implements ITimerAmministrato {

	protected final Log log = LogFactory.getLog(TimerSendNotificaCVInScadenzaSingleton.class);
	private static final String TIMER_NAME = "sendNotificaCVInScadenzaTimer";
	private Date lastProgrammaticTimeout;
	private Date lastAutomaticTimeout;

	@Resource
	TimerService timerService;
	
	@EJB
	AppNotificaHome appNotificaHome;
	@EJB
	CvDatiPersonaliHome cvDatiPersonaliHome;

	@EJB
	PfPrincipalHome pfPrincipalHome;
	
	@EJB
	TsTimerEJB tsTimerEJB;

	@EJB
	TsGetOpzioniEJB tsGetOpzioniEJB;

	@Resource(name = "RemoteConnectionFactory", mappedName = "java:/RemoteConnectionFactory")
	private ConnectionFactory connectionFactory;

	@Resource(name = "email_queue", mappedName = "java:/queue/email_queue")
	private Queue emailQueue;

	@Schedule(dayOfMonth = "*", dayOfWeek = "*", hour = "17", minute = "07", persistent = false)
	@TransactionAttribute(TransactionAttributeType.NEVER)
	public void sendNotificaCVInScadenza() {

		if (tsTimerEJB.isTimerHostEnabled(TIMER_NAME)) {
			this.setLastAutomaticTimeout(new Date());
			log.info("===BATCH CREAZIONE E INVIO NOTIFICA AD UTENTE CON CV IN SCADENZA===");
			this.sendNotificaCVInScadenzaBatchCore();
			log.info("===BATCH CREAZIONE E INVIO NOTIFICA AD UTENTE CON CV IN SCADENZA TERMINATO===");
		}
	}

	private void sendNotificaCVInScadenzaBatchCore() {
		
		int numGGCvInScad = 0;
		if (tsGetOpzioniEJB.getNumGGCVScadenzaTimer() != null) {
			numGGCvInScad = tsGetOpzioniEJB.getNumGGCVScadenzaTimer().intValue();
		}
		Integer idPfPrincipalAdmin = ConstantsBaseCommons.Users.ADMINISTRATOR;
		PfPrincipal pfPrincipalAdmin = pfPrincipalHome.findById(idPfPrincipalAdmin);

		List<AppNotifica> ret = new ArrayList<AppNotifica>();
		List<CvDatiPersonali> lstCv = cvDatiPersonaliHome.findAllCurriculaInScadenzaFlgIdo(numGGCvInScad);
		for (CvDatiPersonali cvDatiPersonali : lstCv) {
			ret.add(appNotificaHome.sendNotificationCVScadenzaBatch(cvDatiPersonali, pfPrincipalAdmin));
		}
		log.info("===lista cv notifiche inviate===" + ret.size());

	}
	
	@Timeout
	public void programmaticTimeout() {
		this.setLastProgrammaticTimeout(new Date());
		sendNotificaCVInScadenzaBatchCore();
		log.info("TimerSendNotificaCVInScadenzaSingleton: timeout MANUALE, NON controllo abilitazione TIMER");		
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
}

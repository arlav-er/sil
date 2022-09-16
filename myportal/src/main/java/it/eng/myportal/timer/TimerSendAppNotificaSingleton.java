package it.eng.myportal.timer;

import java.util.Date;
import java.util.List;
import java.util.concurrent.Future;

import javax.annotation.Resource;
import javax.ejb.AsyncResult;
import javax.ejb.Asynchronous;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Schedule;
import javax.ejb.Singleton;
import javax.ejb.Timeout;
import javax.ejb.Timer;
import javax.ejb.TimerService;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import it.eng.myportal.entity.AppNotifica;
import it.eng.myportal.entity.PfPrincipal;
import it.eng.myportal.entity.ejb.ts.TimerNotFoundException;
import it.eng.myportal.entity.ejb.ts.TsTimerEJB;
import it.eng.myportal.entity.home.AppNotificaHome;
import it.eng.myportal.entity.home.PfIdentityDeviceHome;
import it.eng.myportal.entity.home.PfPrincipalHome;
import it.eng.myportal.entity.ts.TsTimer;

@Singleton
@LocalBean
public class TimerSendAppNotificaSingleton implements ITimerAmministrato {
 
	private Date lastProgrammaticTimeout;
	private Date lastAutomaticTimeout;

	protected final Log log = LogFactory.getLog(TimerSendAppNotificaSingleton.class);
	private static final String TIMER_NAME = "sendAppNotificaTimer";

	@Resource
	TimerService timerService;
	
	@EJB
	AppNotificaHome appNotificaHome;

	@EJB
	PfIdentityDeviceHome pfIdentityDeviceHome;

	@EJB
	PfPrincipalHome pfPrincipalHome;

	@EJB
	TsTimerEJB tsTimerEJB;

	@Schedule(dayOfMonth = "*", dayOfWeek = "*", hour = "17", minute = "0", persistent = false)
	@TransactionAttribute(TransactionAttributeType.NEVER)
	public void sendAppNotifiche() {
		if (tsTimerEJB.isTimerHostEnabled(TIMER_NAME)) {
			log.info("===BATCH CREAZIONE E INVIO NOTIFICHE AD APP START===");
			this.sendNotificationBatchCore(null);
			log.info("===BATCH CREAZIONE E INVIO NOTIFICHE AD APP TERMINATO===");
			this.setLastAutomaticTimeout(new Date());
		}
	}
	

	public void setTimer(long intervalDuration) {
		log.debug("Setting a programmatic timeout for " + intervalDuration + " milliseconds from now.");
		Timer timer = timerService.createTimer(intervalDuration, "Created new programmatic timer");
	}
	
	@Timeout
	public void programmaticTimeout() {
		log.info("===BATCH CREAZIONE E INVIO NOTIFICHE AD APP CHIAMATA MANUALE ===");
		this.sendNotificationBatchCore(null);
		log.info("===BATCH CREAZIONE E INVIO NOTIFICHE AD APP TERMINATO CHIAMATA MANUALE ===");
		this.setLastProgrammaticTimeout(new Date());
	}

	@Override
	public boolean isLogicEnabled() {
		try {
			return tsTimerEJB.isTimerLogicEnabled(TIMER_NAME);
		} catch (TimerNotFoundException e) {
			log.info("Timer non trovato: torno FALSE isLogicEnabled() per " + TIMER_NAME);
			return false;
		}
	}
	
	@Override
	public boolean setLogicEnabled(boolean operativo) {
		try {
			TsTimer tt = tsTimerEJB.setTimerLogicEnabled(TIMER_NAME, operativo);
			return tt.getFlagAbilitato();
		} catch (TimerNotFoundException e) {
			log.error("GRAVE ERRORE setLogicEnabled() " + TIMER_NAME + e.getMessage());
			return false;
		}
		
	}

	@Asynchronous
	@TransactionAttribute(TransactionAttributeType.NEVER)
	public Future<Integer> sendAppNotificheProgrammato(List<Integer> listaIdUsersApp) {
		/*
		 * Da funzione amministrativa si è deciso di non controllare l'abilitazione del timer rispetto al nodo server,
		 * per questo motivo viene chiamato direttamente il metodo di business. La prima implementazione prevedeva,
		 * anche in questo caso, la verifica del timer, si è però poi deciso di estrapolare il controllo per il solo
		 * batch (dopo aver valutato le possibili conseguenze).
		 * 
		 */
		log.info("===BATCH CREAZIONE E INVIO NOTIFICHE AD APP START (PROGRAMMATO)===");
		
		Integer numNotificheTot = this.sendNotificationBatchCore(listaIdUsersApp);

		AsyncResult<Integer> ret = new AsyncResult<Integer>(numNotificheTot);

		log.info("===BATCH CREAZIONE E INVIO NOTIFICHE AD APP TERMINATO (PROGRAMMATO)===");

		return ret;
	}

	private int sendNotificationBatchCore(List<Integer> listaIdUsersApp) {

		int numNotificheTot = 0;

		// Gli invii batch verranno salvati in AppNotifica con utente amministratore (0)
		Integer idPfPrincipalAdmin = 0;
		PfPrincipal pfPrincipalAdmin = pfPrincipalHome.findById(idPfPrincipalAdmin);

		// Lista id di tutti gli utenti a cui inviare le notifiche rispetto alle ricerche salvate
		if (listaIdUsersApp == null)
			listaIdUsersApp = pfIdentityDeviceHome.findAllIdPfPrincipal();

		if (listaIdUsersApp != null && !listaIdUsersApp.isEmpty()) {
			for (Integer idUserApp : listaIdUsersApp) {
				try {
					List<AppNotifica> list = appNotificaHome.sendNotificationBatch(idUserApp, pfPrincipalAdmin);
					numNotificheTot += list.size();

				} catch (Exception e) {
					log.error("GRAVE: Errore durante l'invio della notifica batch per l'utente con id: " + idUserApp
							+ " " + e.getMessage());
				}
			}
		}
		return numNotificheTot;
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

	public String getLastProgrammaticTimeout() {
		if (lastProgrammaticTimeout != null) {
			return lastProgrammaticTimeout.toString();
		} else {
			return "never";
		}
	}

	public void setLastProgrammaticTimeout(Date lastProgrammaticTimeout) {
		this.lastProgrammaticTimeout = lastProgrammaticTimeout;
	}

	

}

package it.eng.sil.batch.timer.interval.configurable;

import javax.ejb.ScheduleExpression;
import javax.ejb.TimerConfig;
import javax.ejb.TimerService;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import it.eng.sil.batch.timer.EnabledTimerBatch;

/**
 * Classe per la gestione del timer configurabile
 * 
 * @author OMenghini
 *
 */
public abstract class ConfigurableTimerBatch extends EnabledTimerBatch {

	private static final String EVERY_TIME = "*";
	public final Log log = LogFactory.getLog(this.getClass().getName());

	private String timerName;
	private String classExecuteBatch;
	private TimerService timerService;

	// Separatore ora/minuti accettato: ":", ".", " "
	private static final String HOURS_MIN_REX_EXP = "[:\\. ]";
	// Separatore tra diversi orari
	private static final String TIME_REX_EXP = "\\|";

	public void init(String timerName, String classExecuteBatch, TimerService timerService) {
		this.timerName = timerName;
		this.classExecuteBatch = classExecuteBatch;
		this.timerService = timerService;
	}

	private String getBatchTime() {
		String batchProperty = "batch." + timerName + ".orario";

		return System.getProperty(batchProperty);
	}

	private String[] getBatchTimeArr() {
		if (this.getBatchTime() != null) {
			return this.getBatchTime().split(TIME_REX_EXP);

		}
		return null;
	}

	private String getOraInterruzioneBatch() {
		String batchProperty = "batch." + timerName + ".oraInterruzioneBatch";

		String oraInterruzione = System.getProperty(batchProperty);
		if (oraInterruzione == null)
			oraInterruzione = "";

		return oraInterruzione;
	}

	private String[] getOraInterruzioneBatchArr() {
		if (this.getOraInterruzioneBatch() != null) {
			return this.getOraInterruzioneBatch().split(TIME_REX_EXP);

		}
		return null;
	}

	private String getBatchHours(String time) {
		if (time != null) {
			String[] tmp = time.split(HOURS_MIN_REX_EXP);

			if (tmp != null && tmp.length > 0) {
				return tmp[0];
			}
		}
		return null;
	}

	private String getBatchMinutes(String time) {
		if (time != null) {
			String[] tmp = time.split(HOURS_MIN_REX_EXP);

			if (tmp != null && tmp.length > 1) {
				return tmp[1];
			}
		}
		return null;
	}

	/**
	 * Metodo di scheduling che legge la configurazione dell'orario dalle proprietà di sistema. La proprietà
	 * batch.enabled indica l'abilitazione o meno del timer su uno specifico nodo; la proprieta batch.nomeTimer.orario
	 * indica l'orario.
	 */
	public void scheduleTimer() {

		String timeArr[] = this.getBatchTimeArr();
		String oraInterruzioneBatchArr[] = this.getOraInterruzioneBatchArr();
		String batchUserId = System.getProperty("batch.user.id");
		String batchUserProfilo = System.getProperty("batch.user.profilo");
		String batchUserGruppo = System.getProperty("batch.user.gruppo");

		/*
		 * ------------------------ Controlli preliminari ------------------------
		 */

		if (!this.isEnabled()) {
			// Batch non abilitato su questo nodo
			log.warn(this.timerName + " non abilitato su questo nodo, probabilmente è abilitato su un altro nodo");
			return;
		}

		if (!(batchUserId != null && batchUserProfilo != null && batchUserGruppo != null)) {
			// Batch abilitato sul nodo ma non schedulato per mancanza di profilo utente
			log.warn(this.timerName
					+ " non schedulato (anche se abilitato) perché non è presente la configurazione relativa all'utente di esecuzione batch");
			return;
		}

		if (timeArr == null || timeArr.length == 0) {
			// Batch abilitato sul nodo ma non schedulato per mancanza di configurazione dell'orario
			log.warn(this.timerName
					+ " non schedulato (anche se abilitato) perché non è presente la configurazione relativa all'orario di avvio");
			return;
		}
		/*
		 * ---------------------------- Fine Controlli preliminari ---------------------------
		 */

		for (int i = 0; i < timeArr.length; i++) {
			String singleTime = timeArr[i];

			String hours = this.getBatchHours(singleTime);
			String minutes = this.getBatchMinutes(singleTime);

			// Recupero dell'eventuale corrispondente orario massimo di esecuzione
			String oraInterruzione = "";
			if (oraInterruzioneBatchArr != null && oraInterruzioneBatchArr.length > i) {
				oraInterruzione = oraInterruzioneBatchArr[i];
			}

			if (hours != null && !hours.isEmpty()) {
				ScheduleExpression scheduleExpression = new ScheduleExpression();
				scheduleExpression.dayOfMonth(EVERY_TIME).dayOfWeek(EVERY_TIME).hour(hours);

				if (minutes != null && !minutes.isEmpty())
					scheduleExpression.minute(minutes);

				TimerConfig config = new TimerConfig();
				config.setPersistent(false);

				InfoTimer info = new InfoTimer(this.timerName, this.classExecuteBatch, oraInterruzione, batchUserId,
						batchUserProfilo, batchUserGruppo);
				config.setInfo(info);

				this.timerService.createCalendarTimer(scheduleExpression, config);

				log.info(this.timerName + " schedulato alle " + singleTime + " di ogni giorno");
			} else {
				// Ora/minuti delle specifico elemento della lista non corretto
				log.warn(this.timerName + " all'orario " + singleTime
						+ " non schedulato (anche se abilitato) perché non è corretta la configurazione relativa all'orario di avvio");
			}
		}
	}
}

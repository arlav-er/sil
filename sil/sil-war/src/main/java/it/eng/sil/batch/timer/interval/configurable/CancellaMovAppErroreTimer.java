package it.eng.sil.batch.timer.interval.configurable;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.ejb.Timeout;
import javax.ejb.Timer;
import javax.ejb.TimerService;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Named;

import it.eng.sil.util.batch.mdb.BatchMDBHelper;
import it.eng.sil.util.batch.mdb.BatchObject;

/**
 * Timer per la gestione della cancellazione movimenti doppi da validare
 * 
 * @author OMenghini
 *
 */
@Singleton
@Startup
@Named(CancellaMovAppErroreTimer.TIMER_NAME)
public class CancellaMovAppErroreTimer extends ConfigurableTimerBatch {

	static final String TIMER_NAME = "cancellaMovAppErroreTimer";
	private static final String CLASS_EXECUTE_BATCH = "it.eng.sil.util.batch.BatchCancellaMovAppErrore";

	@Resource
	private TimerService timerService;

	@PostConstruct
	public void initScheduling() {
		this.init(TIMER_NAME, CLASS_EXECUTE_BATCH, timerService);
		this.scheduleTimer();
	}

	@Timeout
	@TransactionAttribute(TransactionAttributeType.NEVER)
	public void sendMessage(Timer timer) throws Exception {
		log.info(TIMER_NAME + ": inizio sendMessage()");

		// Recupero le informazioni del timer
		InfoTimer info = (InfoTimer) timer.getInfo();
		
		String comando = info.getClassExecuteBatch();
		comando += " 00/00/0000 00/00/0000 X X X X 50133 00/00/0000 00/00/0000 X " + info.getBatchUserInfo();

		BatchMDBHelper batchHelper = new BatchMDBHelper();
		batchHelper.enqueue(new BatchObject(comando, false));

		log.info(TIMER_NAME + ": fine sendMessage()");
	}

}

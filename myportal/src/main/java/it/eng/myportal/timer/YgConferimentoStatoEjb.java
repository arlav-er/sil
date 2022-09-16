package it.eng.myportal.timer;

import java.util.Calendar;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Schedule;
import javax.ejb.Singleton;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import it.eng.myportal.entity.YgConferimentoStato;
import it.eng.myportal.entity.ejb.ts.TsGetOpzioniEJB;
import it.eng.myportal.entity.ejb.ts.TsTimerEJB;
import it.eng.myportal.entity.home.YgConferimentoStatoHome;

@Singleton
public class YgConferimentoStatoEjb {
	protected final Log log = LogFactory.getLog(YgConferimentoStatoEjb.class);
	private static final String TIMER_NAME = "ygConferimentoStatoTimer";

	@PersistenceContext
	protected EntityManager entityManager;

	@EJB
	TsGetOpzioniEJB tsGetOpzioniEJB;
	
	@EJB
	private YgConferimentoStatoHome ygConferimentoStatoHome;

	@EJB
	TsTimerEJB tsTimerEJB;
	
	@Schedule(minute = "*/5", hour = "*", persistent = false)
	public void recuperoProvinciaRifAdesioni() {
		if (tsTimerEJB.isTimerHostEnabled(TIMER_NAME)) {
			log.info("===BATCH YG CONFERIMENTO INIZIALE START===");

			Calendar dtBatchConfMassivoYg = Calendar.getInstance();
			dtBatchConfMassivoYg.setTime(tsGetOpzioniEJB.getDtBatchConfMassivoYg());
			
			Calendar today = Calendar.getInstance();			
			
			if (dtBatchConfMassivoYg != null) {
				if (today.after(dtBatchConfMassivoYg)) {
					
					List<YgConferimentoStato> ygConferimentoStatoList = ygConferimentoStatoHome
							.getAdesioniYgConferimentoIniziale();
		
					for (YgConferimentoStato ygConferimentoStato : ygConferimentoStatoList) {
						log.info("start conferimento iniziale id_yg_conferimento_stat ="
								+ ygConferimentoStato.getIdYgConferimentoStato());
		
						ygConferimentoStatoHome.processBatchConferimentoIniziale(ygConferimentoStato);
		
						log.info("end conferimento iniziale id_yg_conferimento_stat ="
								+ ygConferimentoStato.getIdYgConferimentoStato());
					}
				}
				else {
					log.info("===BATCH YG CONFERIMENTO INIZIALE DA NON ESEGUIRE===");
				}
			}
			log.info("===BATCH YG CONFERIMENTO INIZIALE TERMINATO===");
		}
	}
}

package it.eng.myportal.timer;

import it.eng.myportal.entity.YgAdesione;
import it.eng.myportal.entity.ejb.ts.TsTimerEJB;
import it.eng.myportal.entity.home.YgAdesioneHome;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Schedule;
import javax.ejb.Singleton;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

@Singleton
public class YgRecuperoProvinciaRifNotificaEjb {

	protected final Log log = LogFactory.getLog(YgRecuperoProvinciaRifNotificaEjb.class);
	private static final String TIMER_NAME = "ygRecuperoProvinciaRifNotificaTimer";
	
	@PersistenceContext
	protected EntityManager entityManager;

	@EJB
	YgAdesioneHome ygAdesioneHome;
	
	@EJB
	TsTimerEJB tsTimerEJB;

	@Schedule(minute = "*/20", hour = "*", persistent = false)
	public void recuperoProvinciaRifAdesioni() {
		if (tsTimerEJB.isTimerHostEnabled(TIMER_NAME)) {
			log.info("===BATCH YG RECUPERO PROVINCIA RIF NOTIFICA START===");

			List<YgAdesione> ygAdesioneList = ygAdesioneHome.getAdesioniYgSenzaProvinciaRifNotifica();

			for (YgAdesione ygAdesione : ygAdesioneList) {

				log.info("start adesione id=" + ygAdesione.getIdYgAdesione());

				ygAdesioneHome.processBatch(ygAdesione);

				log.info("end adesione id=" + ygAdesione.getIdYgAdesione());

			}

			log.info("===BATCH YG RECUPERO PROVINCIA RIF NOTIFICA TERMINATO===");

			log.info("===BATCH YG RECUPERO PROVINCIA PER YG SAP NULLE START===");

			List<YgAdesione> ygAdesioneSapNulleList = ygAdesioneHome.getAdesioniYgSenzaSap();

			for (YgAdesione ygAdesioneSapNulla : ygAdesioneSapNulleList) {

				log.info("start adesione id=" + ygAdesioneSapNulla.getIdYgAdesione());

				ygAdesioneHome.processBatch(ygAdesioneSapNulla);

				log.info("end adesione id=" + ygAdesioneSapNulla.getIdYgAdesione());

			}

			log.info("===BATCH YG RECUPERO PROVINCIA PER YG SAP NULLE TERMINATO===");
		}
	}
}

package it.eng.myportal.timer;

import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.apache.commons.lang3.time.DateUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import it.eng.myportal.entity.PfAbilitazione;
import it.eng.myportal.entity.ejb.ts.TsGetOpzioniEJB;
import it.eng.myportal.entity.ejb.ts.TsTimerEJB;
import it.eng.myportal.entity.home.PfAbilitazioneHome;

@Singleton
public class YgTimerEjb {
	
	protected final Log log = LogFactory.getLog(YgTimerEjb.class);
	private static final String TIMER_NAME = "ygTimer";
	
	@PersistenceContext
	protected EntityManager entityManager;
	
	 
	@EJB
	PfAbilitazioneHome pfAbilitazioneHome;
	
	@EJB
	TsTimerEJB tsTimerEJB;
	
	@EJB
	TsGetOpzioniEJB tsGetOpzioniEJB;
	
	/*@Schedule(
			dayOfMonth="*", 
			dayOfWeek="*", 
			hour="00",
			minute="03",
			second="0",
			persistent = false)*/  	
	public void sendEmailClicLavoro() {
		if (tsTimerEJB.isTimerHostEnabled(TIMER_NAME)) {
			log.info("===BATCH YG START===");		
			
			//Date dtAvvioYg = stConfigurazioneHome.findById(new Integer("1")).getDtAvvioYg();
			Date dtAvvioYg = tsGetOpzioniEJB.getDtAttivazioneYg();
			Date today = new Date();
			
			if (dtAvvioYg != null) {
				if (DateUtils.isSameDay(dtAvvioYg, today)) {
					List<PfAbilitazione> abilitazioni = getAbilitazionePortletYg();
					for (PfAbilitazione abilitazione : abilitazioni) {
						// se ne aspetta solamente una
						abilitazione.setFlagVisibile(true);
						pfAbilitazioneHome.merge(abilitazione);
					}
				}
			}
			
			log.info("===BATCH YG TERMINATO===");
		}
	}
	
	private List<PfAbilitazione> getAbilitazionePortletYg() {
		
		Query q = entityManager.createQuery(
				" select a " +
				" from PfAbilitazione a " +
				" where a.deAttivitaPf.codAttivitaPf = '_portlet_yg' ");
		
		List<PfAbilitazione> results = q.getResultList();
		
		return results;
		
	}
	
}

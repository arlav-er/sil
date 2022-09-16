/**
 * 
 */
package it.eng.sil.batch.timer.interval.fixed.cliclavoro;

import javax.ejb.Singleton;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

import org.apache.log4j.Logger;

import it.eng.sil.batch.timer.interval.fixed.FixedTimerBatch;

/**
 * @author manuel
 *
 */
@Singleton
public class SpedisciVacancyCL extends FixedTimerBatch {

	private Logger logger = Logger.getLogger(SpedisciVacancyCL.class.getName());

	// Commentato perché nel vecchio scheduler jboss non è attivo
	// @Schedule(dayOfMonth = "*", dayOfWeek = "*", hour = "*", minute = "*/1", persistent = false)
	@TransactionAttribute(TransactionAttributeType.NEVER)
	public void perform() {
		if (this.isEnabled()) {
			if (logger.isDebugEnabled()) {
				logger.debug("perform() - start");
			}

			String[] str = new String[1];
			str[0] = "363";
			// BatchInvioVacancy.main(str);

			if (logger.isDebugEnabled()) {
				logger.debug("perform() - end");
			}
		} else {
			// Timer non abilitato su questo nodo
			logger.warn(
					"[SpedisciVacancyCL] ---> WARN: non abilitato su questo nodo, probabilmente è abilitato su un altro nodo");
		}
	}
}

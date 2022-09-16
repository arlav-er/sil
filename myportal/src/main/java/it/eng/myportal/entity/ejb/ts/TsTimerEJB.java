package it.eng.myportal.entity.ejb.ts;

import java.util.List;

import javax.ejb.Stateless;

import it.eng.myportal.entity.home.AbstractHibernateHome;
import it.eng.myportal.entity.ts.TsTimer;

@Stateless
public class TsTimerEJB extends AbstractHibernateHome<TsTimer, Integer> {

	// Nome del nodo definito da specifica proprietà
	private static final String MY_PORTAL_NODE_NAME = "myportal.node.name";
	// Nome del nodo jboss
	public static final String JBOSS_NODE_NAME = "jboss.node.name";

	public boolean isTimerHostEnabled(String timerName) {
		// Recupero del nome del nodo: se presente la property specifica, altrimenti quella di jboss
		String nodeName = System.getProperty(MY_PORTAL_NODE_NAME);
		if (nodeName == null)
			nodeName = System.getProperty(JBOSS_NODE_NAME);

		List<TsTimer> list = entityManager.createNamedQuery("tsTimer.getTimerEnabled", TsTimer.class)
				.setParameter("parTimerName", timerName).getResultList();

		if (list != null) {
			for (TsTimer tsTimer : list) {
				if (tsTimer.checkServerNode(nodeName))
					return true;
			}
		}

		return false;

	}

	@Override
	public TsTimer findById(Integer id) {
		return findById(TsTimer.class, id);
	}

	public TsTimer setTimerLogicEnabled(String timerName, boolean en) throws TimerNotFoundException {
		List<TsTimer> list = entityManager.createNamedQuery("tsTimer.getTimer", TsTimer.class)
				.setParameter("parTimerName", timerName).getResultList();

		if (list.isEmpty())
			throw new TimerNotFoundException(timerName + " NON TROVATO");

		if (list.size() > 1)
			throw new TimerNotFoundException("GRAVE setTimerLogicEnabled(): piu` di un timer per " + timerName);

		TsTimer toSet = list.get(0);
		toSet.setFlagAbilitato(en);
		return merge(toSet);

	}

	public boolean isTimerLogicEnabled(String timerName) throws TimerNotFoundException {
		List<TsTimer> list = entityManager.createNamedQuery("tsTimer.getTimer", TsTimer.class)
				.setParameter("parTimerName", timerName).getResultList();

		if (list.isEmpty())
			throw new TimerNotFoundException(timerName + " NON TROVATO");

		if (list.size() > 1)
			throw new TimerNotFoundException("GRAVE: piu` di un timer per " + timerName);

		TsTimer toSet = list.get(0);
		return toSet.getFlagAbilitato() != null &&  toSet.getFlagAbilitato();

	}

}
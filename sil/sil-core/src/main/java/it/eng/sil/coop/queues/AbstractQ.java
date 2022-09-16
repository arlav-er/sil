/*
 * Creato il 05-Apr-06
 * Author: rolfini
 * 
 */
package it.eng.sil.coop.queues;

import org.apache.log4j.Logger;

/**
 * 
 * @author rolfini
 *
 *
 *         To change the template for this generated type comment go to Window&gt;Preferences&gt;Java&gt;Code
 *         Generation&gt;Code and Comments
 */
public abstract class AbstractQ {

	protected QueueSingleton queueSingleton;

	public AbstractQ() {
	}

	public void init(String _dataSourceJndi) throws Exception {
		queueSingleton = QueueSingleton.getInstance(_dataSourceJndi);
	}

	// GETTER

	public abstract String getFactory();

	public abstract String getQueue();

}

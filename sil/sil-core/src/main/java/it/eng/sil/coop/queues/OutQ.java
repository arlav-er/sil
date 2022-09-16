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
public class OutQ extends AbstractQ implements IFaceQueue {

	private static final Logger log = Logger.getLogger(OutQ.class.getName());

	// GETTER

	public String getFactory() {
		return queueSingleton.getOutFactory();

	}

	public String getQueue() {
		return queueSingleton.getOutQueue();

	}

}

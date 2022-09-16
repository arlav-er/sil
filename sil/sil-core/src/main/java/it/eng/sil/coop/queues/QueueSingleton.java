/*
 * Creato il 05-Apr-06
 * Author: rolfini
 * 
 */
package it.eng.sil.coop.queues;

import org.apache.log4j.Logger;

/**
 * @author rolfini
 */
public class QueueSingleton {

	private static final Logger log = Logger.getLogger(QueueSingleton.class.getName());

	private static QueueSingleton _instance = null;

	private String in_factory = "java:/ConnectionFactory";
	private String in_queue = "java:/jms/queue/SilIn";
	
	private String out_factory = "java:/ConnectionFactory";
	private String out_queue = "java:/jms/queue/SilOut";

	protected static QueueSingleton getInstance(String dataSourceJndi) {
		if (_instance == null) {
			synchronized (QueueSingleton.class) {
				if (_instance == null) {
					try {
						_instance = new QueueSingleton();
					} // try
					catch (Exception ex) {
						// System.out.println("SILEndPointSingleton: Errore nelle procedure di reperimento degli
						// endpoint" + ex.getMessage());
						log.fatal("Errore nelle procedure di reperimento degli endpoint", ex);
						_instance = null;
					}
				}
			}
		}
		return _instance;
	}

	protected String getOutFactory() {
		return out_factory;
	}

	protected String getOutQueue() {
		return out_queue;
	}

	protected String getInFactory() {
		return in_factory;
	}

	protected String getInQueue() {
		return in_queue;
	}
	
	
}

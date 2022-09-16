package it.eng.myportal.utils;

import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Properties;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

@Singleton
@javax.ejb.Startup
public class Startup {

	private static final Log log = LogFactory.getLog(Startup.class);

	@PostConstruct
	private void postConstruct() {
		logSystemProperties();
	}

	private void logSystemProperties() {
		if (log.isInfoEnabled()) {
			log.info("---------------------------------------------");
			log.info("---------- Begin System properties ----------");
			log.info("---------------------------------------------");

			Properties prop = System.getProperties();

			if (prop != null) {
				Iterator<Entry<Object, Object>> it = prop.entrySet().iterator();

				while (it.hasNext()) {
					log.info(it.next());
				}
			}

			log.info("---------------------------------------------");
			log.info("---------- End System properties ------------");
			log.info("---------------------------------------------");
		}
	}

}

package it.eng.myportal.entity.ejb;

import it.eng.myportal.utils.ConstantsSingleton;

import java.net.URL;
import java.util.Properties;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Singleton contenente gli errori
 * del file errors.properties
 * 
 * @author Rodi A.
 *
 */
@Singleton
@Startup
public class ErrorsSingleton extends Properties{
		
	private static final long serialVersionUID = 6624047442299329210L;
	private Log log = LogFactory.getLog(this.getClass());
	
	@PostConstruct
	private void postConstruct() {
		String fileName = "errors.properties";
		if (ConstantsSingleton.COD_REGIONE == 22) {
			fileName = "errors_trento.properties";
		}
		URL url = Thread.currentThread().getContextClassLoader().getResource("messages/"+fileName);

		try {
			this.load(url.openStream());
		} catch (java.io.IOException e) {
			log.error(e.getMessage());
		}

	}

}

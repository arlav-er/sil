/*
 * Creato il 30-ago-06 da FV
 *
 */
package it.eng.afExt.utils;

import java.io.File;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.init.InitializerIFace;
import com.inet.report.RDC;

/**
 * @author vuoto
 * 
 */
public class InizializzaLicenzaCC implements InitializerIFace {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(InizializzaLicenzaCC.class.getName());

	private SourceBean config;

	public InizializzaLicenzaCC() {
	}

	public void init(SourceBean config) {

		File ccPropsFile = new File(
				System.getProperty("jboss.server.config.dir") + File.separatorChar + "crystalclear.properties");
		if (ccPropsFile.exists()) {
			System.setProperty("clearreports.configfile", ccPropsFile.getAbsolutePath());
		} else {
			_logger.error("Non è stato configurato il file " + ccPropsFile.getAbsolutePath());
		}

	}

	public SourceBean getConfig() {
		return config;
	}

}
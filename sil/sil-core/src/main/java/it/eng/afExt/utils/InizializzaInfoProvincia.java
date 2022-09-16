/*
 * Creato il 31-ago-05
 *
 */

package it.eng.afExt.utils;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.init.InitializerIFace;

import it.eng.sil.util.InfoProvinciaSingleton;

/**
 * @author Vuoto
 * 
 */
public class InizializzaInfoProvincia implements InitializerIFace {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger
			.getLogger(InizializzaInfoProvincia.class.getName());

	private SourceBean config;

	public InizializzaInfoProvincia() {
		super();
		config = null;
	}

	public void init(SourceBean config) {

		it.eng.sil.util.TraceWrapper.debug(_logger, "InizializzaInfoProvincia::init: config", config);

		this.config = config;
		InfoProvinciaSingleton prov = InfoProvinciaSingleton.getInstance();
	}

	public SourceBean getConfig() {
		return config;
	}

}
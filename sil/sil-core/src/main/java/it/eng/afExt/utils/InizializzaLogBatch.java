package it.eng.afExt.utils;

import java.io.File;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.configuration.ConfigSingleton;
import com.engiweb.framework.init.InitializerIFace;

public class InizializzaLogBatch implements InitializerIFace {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(InizializzaLogBatch.class.getName());

	private SourceBean config;

	public InizializzaLogBatch() {
		super();
		config = null;
	}

	public void init(SourceBean config) {

		it.eng.sil.util.TraceWrapper.debug(_logger, "InizializzaLogBatch::init: config", config);

		this.config = config;

		// Configurazione directory di log per i batch
		String logBatchPath = ConfigSingleton.getLogBatchPath();
		File f = new File(logBatchPath);
		if (!f.exists())
			f.mkdirs();

		it.eng.sil.util.TraceWrapper.debug(_logger,
				"InizializzaLogBatch::init: la directory per i log dei batch è stata configurata: " + logBatchPath,
				null);
	}

	public SourceBean getConfig() {
		return config;
	}
}

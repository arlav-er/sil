package com.engiweb.framework.dbaccess;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.error.EMFInternalError;
import com.engiweb.framework.init.InitializerIFace;

public class DataAccessInitializer implements InitializerIFace {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(DataAccessInitializer.class.getName());
	private SourceBean _config = null;

	public DataAccessInitializer() {
		super();
		_config = null;
	} // public DataAccessInitializer()

	public void init(SourceBean config) {
		it.eng.sil.util.TraceWrapper.debug(_logger, "DataAccessInitializer::init: config", config);

		_config = config;
		try {
			DataConnectionManager.getInstance();
		} // try
		catch (EMFInternalError eie) {
			it.eng.sil.util.TraceWrapper.debug(_logger, "DataAccessInitializer::init:", (Exception) eie);

		} // catch (EMFInternalError eie)
	} // public void init(SourceBean config)

	public SourceBean getConfig() {
		return _config;
	} // public SourceBean getConfig()
} // public class DataAccessInitializer implements InitializerIFace

package com.engiweb.framework.init;

import java.util.Vector;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.configuration.ConfigSingleton;

public class InitializerManager {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(InitializerManager.class.getName());

	private InitializerManager() {
		super();
	} 

	public static void init() {
		_logger.debug("InitializerManager::init: invocato");

		ConfigSingleton configure = ConfigSingleton.getInstance();
		Vector initializers = configure.getAttributeAsVector("INITIALIZERS.INITIALIZER");
		for (int i = 0; i < initializers.size(); i++) {
			try {
				SourceBean initializerDefinition = (SourceBean) (initializers.elementAt(i));
				String initializerClassName = (String) (initializerDefinition.getAttribute("CLASS"));
				String initializerConfigName = (String) (initializerDefinition.getAttribute("CONFIG"));
				_logger.debug("InitializerManager::init: " + initializerClassName + " [" + initializerConfigName + "]");

				SourceBean initializerConfig = (SourceBean) (configure.getAttribute(initializerConfigName));
				InitializerIFace initializer = (InitializerIFace) (Class.forName(initializerClassName).newInstance());
				initializer.init(initializerConfig);
			} 
			catch (Exception ex) {
				it.eng.sil.util.TraceWrapper.fatal(_logger, "InitializerManager::init:", ex);

			} 
		} 
	} 
} 

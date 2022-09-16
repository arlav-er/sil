package com.engiweb.framework.dispatching.coordinator;

import java.util.Vector;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.configuration.ConfigSingleton;
import com.engiweb.framework.init.InitializerIFace;

public class DispatcherInitializer implements InitializerIFace {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(DispatcherInitializer.class.getName());
	private SourceBean _config = null;

	public DispatcherInitializer() {
		super();
		_config = null;
	} // public DispatcherInitializer()

	public void init(SourceBean config) {
		it.eng.sil.util.TraceWrapper.debug(_logger, "DispatcherInitializer::init: config", config);

		_config = config;
		ConfigSingleton configure = ConfigSingleton.getInstance();
		Vector dispatchers = configure.getAttributeAsVector("DISPATCHERS.DISPATCHER");
		for (int i = 0; i < dispatchers.size(); i++) {
			try {
				SourceBean dispatcherBean = (SourceBean) dispatchers.elementAt(i);
				String dispatcherClass = (String) dispatcherBean.getAttribute("CLASS");
				DispatcherIFace dispatcher = (DispatcherIFace) Class.forName(dispatcherClass).newInstance();
				DispatcherManager.registerDispatcher(dispatcher);
			} // try
			catch (Exception ex) {
				System.out.println("DispatcherInitializer::init: eccezione");
				ex.printStackTrace();
			} // catch (Excpetion ex) try
		} // for (int i = 0; i < dispatchers.size(); i++)
	} // public void init(SourceBean config)

	public SourceBean getConfig() {
		return _config;
	} // public SourceBean getConfig()
} // public class DispatcherInitializer implements InitializerIFace

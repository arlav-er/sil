package com.engiweb.framework.util;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.configuration.ConfigSingleton;
import com.engiweb.framework.init.InitializerIFace;
import com.jamonapi.MonitorFactory;

public class MonitorInitializer implements InitializerIFace {
	private SourceBean _config = null;

	public MonitorInitializer() {
		super();
		_config = null;
	} // public MonitorInitializer()()

	public void init(SourceBean config) {
		ConfigSingleton configure = ConfigSingleton.getInstance();
		String enabled = (String) configure.getAttribute("COMMON.MONITOR_ENABLED");
		if ((enabled != null) && enabled.equalsIgnoreCase("TRUE"))
			MonitorFactory.setEnabled(true);
		else
			MonitorFactory.setEnabled(false);
	} // public void init(SourceBean config)

	public SourceBean getConfig() {
		return _config;
	} // public SourceBean getConfig()
} // public class MonitorInitializer implements InitializerIFace

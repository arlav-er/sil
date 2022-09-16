package it.eng.afExt.utils;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.init.InitializerIFace;
import com.jamonapi.MonitorFactory;

public class JamonDisabler implements InitializerIFace {

	public void init(SourceBean s) {

	}

	public SourceBean getConfig() {
		return null;
	}

	public JamonDisabler() {
		MonitorFactory.setEnabled(false);
	}
}
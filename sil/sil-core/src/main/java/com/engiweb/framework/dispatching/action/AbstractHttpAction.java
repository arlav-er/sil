package com.engiweb.framework.dispatching.action;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.dispatching.service.DefaultHttpRequestContext;
import com.engiweb.framework.init.InitializerIFace;

public abstract class AbstractHttpAction extends DefaultHttpRequestContext implements InitializerIFace, ActionIFace {
	private SourceBean _config = null;
	private String _action = null;

	public AbstractHttpAction() {
		super();
		_config = null;
		_action = null;
	} // public AbstractHttpAction()

	public void init(SourceBean config) {
		_config = config;
	} // public void init(SourceBean config)

	public SourceBean getConfig() {
		return _config;
	} // public SourceBean getConfig()

	public String getAction() {
		return _action;
	} // public String getAction()

	public void setAction(String action) {
		_action = action;
	} // public void setAction(String action)
} // public abstract class AbstractHttpAction extends
	// DefaultHttpRequestContext implements InitializerIFace, ActionIFace

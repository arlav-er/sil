package com.engiweb.framework.dispatching.module;

import java.io.Serializable;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.dispatching.service.DefaultRequestContext;
import com.engiweb.framework.init.InitializerIFace;

public abstract class AbstractPage extends DefaultRequestContext implements InitializerIFace, PageIFace, Serializable {
	private SourceBean _config = null;

	public AbstractPage() {
		super();
		_config = null;
	} // public AbstractPage()

	public void init(SourceBean config) {
		_config = config;
	} // public void init(SourceBean config)

	public SourceBean getConfig() {
		return _config;
	} // public SourceBean getConfig()
} // public abstract class AbstractPage extends DefaultRequestContext
	// implements InitializerIFace, PageIFace, Serializable

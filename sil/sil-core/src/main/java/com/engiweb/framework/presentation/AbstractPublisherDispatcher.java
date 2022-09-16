package com.engiweb.framework.presentation;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.init.InitializerIFace;

public abstract class AbstractPublisherDispatcher implements PublisherDispatcherIFace, InitializerIFace {
	private SourceBean _config = null;

	public AbstractPublisherDispatcher() {
		super();
		_config = null;
	} // public AbstractPublisherDispatcher()

	public void init(SourceBean config) {
		_config = config;
	} // public void init(SourceBean config)

	public SourceBean getConfig() {
		return _config;
	} // public SourceBean getConfig()
} // public abstract class AbstractPublisherDispatcher implements
	// PublisherDispatcherIFace, InitializerIFace

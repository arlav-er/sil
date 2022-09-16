package com.engiweb.framework.dispatching.distributor;

import java.io.Serializable;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.dispatching.service.DefaultRequestContext;
import com.engiweb.framework.init.InitializerIFace;

public abstract class AbstractDistributor extends DefaultRequestContext
		implements InitializerIFace, DistributorIFace, Serializable {
	private SourceBean _config = null;
	private String _distributor = null;

	public AbstractDistributor() {
		super();
		_config = null;
		_distributor = null;
	} // public AbstractDistributor()

	public void init(SourceBean config) {
		_config = config;
	} // public void init(SourceBean config)

	public SourceBean getConfig() {
		return _config;
	} // public SourceBean getConfig()

	public String getDistributor() {
		return _distributor;
	} // public String getDistributor()

	public void setDistributor(String distributor) {
		_distributor = distributor;
	} // public void setDistributor(String distributor)
} // public abstract class AbstractDistributor extends DefaultRequestContext
	// implements InitializerIFace, DistributorIFace, Serializable

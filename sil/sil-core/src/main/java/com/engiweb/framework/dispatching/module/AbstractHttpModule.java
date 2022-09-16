package com.engiweb.framework.dispatching.module;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.dispatching.service.DefaultHttpRequestContext;
import com.engiweb.framework.init.InitializerIFace;

public abstract class AbstractHttpModule extends DefaultHttpRequestContext implements InitializerIFace, ModuleIFace {
	private SourceBean _config = null;
	private String _module = null;
	private String _page = null;
	private SourceBean _sharedData = null;

	public AbstractHttpModule() {
		super();
		_config = null;
		_module = null;
		_page = null;
		_sharedData = null;
	} // public AbstractHttpModule()

	public void init(SourceBean config) {
		_config = config;
	} // public void init(SourceBean config)

	public SourceBean getConfig() {
		return _config;
	} // public SourceBean getConfig()

	public String getModule() {
		return _module;
	} // public String getModule()

	public void setModule(String module) {
		_module = module;
	} // public void setModule(String module)

	public String getPage() {
		return _page;
	} // public String getPage()

	public void setPage(String page) {
		_page = page;
	} // public void setMessage(String message)

	public SourceBean getSharedData() {
		return _sharedData;
	} // public SourceBean getSharedData()

	public void setSharedData(SourceBean sharedData) {
		_sharedData = sharedData;
	} // public SourceBean getSharedData(SourceBean sharedData)
} // public abstract class AbstractHttpModule extends
	// DefaultHttpRequestContext implements InitializerIFace, ModuleIFace

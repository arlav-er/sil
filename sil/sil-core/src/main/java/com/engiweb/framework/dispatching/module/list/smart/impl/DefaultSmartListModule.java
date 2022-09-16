package com.engiweb.framework.dispatching.module.list.smart.impl;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.dispatching.module.list.smart.AbstractSmartListModule;
import com.engiweb.framework.dispatching.service.list.smart.impl.DelegatedSmartListService;
import com.engiweb.framework.paginator.smart.IFaceListProvider;

public class DefaultSmartListModule extends AbstractSmartListModule {
	public DefaultSmartListModule() {
		super();
	} // public DefaultSmartListModule()

	public void init(SourceBean config) {
		super.init(config);
		DelegatedSmartListService.init(this, config);
	} // public void init(SourceBean config)

	public IFaceListProvider getList(SourceBean request, SourceBean response) throws Exception {
		return DelegatedSmartListService.getList(this, request, response);
	} // public IFaceListProvider getList(SourceBean request, SourceBean
		// response) throws Exception

	public boolean delete(SourceBean request, SourceBean response) {
		return DelegatedSmartListService.delete(this, request, response);
	} // public boolean delete(SourceBean request, SourceBean response)
} // public class DefaultSmartListModule extends AbstractSmartListModule

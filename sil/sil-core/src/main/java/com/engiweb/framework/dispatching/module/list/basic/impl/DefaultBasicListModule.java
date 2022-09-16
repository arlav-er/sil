package com.engiweb.framework.dispatching.module.list.basic.impl;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.dispatching.module.list.basic.AbstractBasicListModule;
import com.engiweb.framework.dispatching.service.list.basic.impl.DelegatedBasicListService;
import com.engiweb.framework.paginator.basic.ListIFace;

public class DefaultBasicListModule extends AbstractBasicListModule {
	public DefaultBasicListModule() {
		super();
	} // public DefaultBasicListModule()

	public ListIFace getList(SourceBean request, SourceBean response) throws Exception {
		return DelegatedBasicListService.getList(this, request, response);
	} // public ListIFace getList(SourceBean request, SourceBean response)
		// throws Exception

	public boolean delete(SourceBean request, SourceBean response) {
		return DelegatedBasicListService.delete(this, request, response);
	} // public boolean delete(SourceBean request, SourceBean response)
} // public class DefaultBasicListModule extends AbstractBasicListModule

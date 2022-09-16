package com.engiweb.framework.dispatching.module.detail.impl;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.dispatching.module.detail.AbstractDetailModule;
import com.engiweb.framework.dispatching.service.detail.impl.DelegatedDetailService;

public class DefaultDetailModule extends AbstractDetailModule {
	public DefaultDetailModule() {
		super();
	} // public DefaultDetailModule()

	public boolean insert(SourceBean request, SourceBean response) {
		return DelegatedDetailService.insert(this, request, response);
	} // public boolean insert(SourceBean request, SourceBean response)

	public boolean select(SourceBean request, SourceBean response) {
		return DelegatedDetailService.select(this, request, response);
	} // public boolean select(SourceBean request, SourceBean response)

	public boolean update(SourceBean request, SourceBean response) {
		return DelegatedDetailService.update(this, request, response);
	} // public boolean update(SourceBean request, SourceBean response)
} // public class DefaultDetailModule extends AbstractDetailModule

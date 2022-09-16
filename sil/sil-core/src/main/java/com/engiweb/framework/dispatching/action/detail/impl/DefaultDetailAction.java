package com.engiweb.framework.dispatching.action.detail.impl;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.dispatching.action.detail.AbstractDetailAction;
import com.engiweb.framework.dispatching.service.detail.impl.DelegatedDetailService;

public class DefaultDetailAction extends AbstractDetailAction {
	public DefaultDetailAction() {
		super();
	} // public DefaultDetailAction()

	public boolean insert(SourceBean request, SourceBean response) {
		return DelegatedDetailService.insert(this, request, response);
	} // public boolean insert(SourceBean request, SourceBean response)

	public boolean select(SourceBean request, SourceBean response) {
		return DelegatedDetailService.select(this, request, response);
	} // public boolean select(SourceBean request, SourceBean response)

	public boolean update(SourceBean request, SourceBean response) {
		return DelegatedDetailService.update(this, request, response);
	} // public boolean update(SourceBean request, SourceBean response)
} // public class DefaultDetailAction extends AbstractDetailAction

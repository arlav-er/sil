package com.engiweb.framework.dispatching.action.list.smart.impl;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.dispatching.action.list.smart.AbstractSmartListAction;
import com.engiweb.framework.dispatching.service.list.smart.impl.DelegatedSmartListService;
import com.engiweb.framework.paginator.smart.IFaceListProvider;

public class DefaultSmartListAction extends AbstractSmartListAction {
	public DefaultSmartListAction() {
		super();
	} // public DefaultSmartListAction()

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
} // public class DefaultSmartListAction extends AbstractSmartListAction

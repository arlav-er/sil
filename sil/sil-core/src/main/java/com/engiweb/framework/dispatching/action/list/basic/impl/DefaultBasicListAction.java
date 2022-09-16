package com.engiweb.framework.dispatching.action.list.basic.impl;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.dispatching.action.list.basic.AbstractBasicListAction;
import com.engiweb.framework.dispatching.service.list.basic.impl.DelegatedBasicListService;
import com.engiweb.framework.paginator.basic.ListIFace;

public class DefaultBasicListAction extends AbstractBasicListAction {
	public DefaultBasicListAction() {
		super();
	} // public DefaultBasicListAction()

	public ListIFace getList(SourceBean request, SourceBean response) throws Exception {
		return DelegatedBasicListService.getList(this, request, response);
	} // public ListIFace getList(SourceBean request, SourceBean response)
		// throws Exception

	public boolean delete(SourceBean request, SourceBean response) {
		return DelegatedBasicListService.delete(this, request, response);
	} // public boolean delete(SourceBean request, SourceBean response)
} // public class DefaultBasicListAction extends AbstractBasicListAction

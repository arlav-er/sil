package com.engiweb.framework.dispatching.module;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.dispatching.service.HttpServiceIFace;
import com.engiweb.framework.dispatching.service.list.basic.IFaceBasicListService;
import com.engiweb.framework.dispatching.service.list.basic.impl.DelegatedBasicListService;
import com.engiweb.framework.paginator.basic.ListIFace;

public abstract class AbstractHttpListModule extends AbstractHttpModule
		implements HttpServiceIFace, IFaceBasicListService {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(AbstractHttpListModule.class.getName());
	private ListIFace _list = null;

	public AbstractHttpListModule() {
		super();
	} // public AbstractHttpListModule()

	public void init(SourceBean config) {
		super.init(config);
		_list = null;
	} // public void init(SourceBean config)

	public ListIFace getList() {
		return _list;
	} // public ListIFace getList()

	public void setList(ListIFace list) {
		_list = list;
	} // public void setList(ListIFace list)

	public void service(SourceBean request, SourceBean response) throws Exception {
		DelegatedBasicListService.service(this, request, response);
	} // public void service(SourceBean request, SourceBean response) throws
		// Exception

	public abstract ListIFace getList(SourceBean request);

	public void callback(SourceBean request, ListIFace list, int page) {
		_logger.debug("AbstractHttpListModule::callback: non implementato");

	} // public void callback(SourceBean request, ListIFace list, int page)

	public void delete(SourceBean request) {
		_logger.debug("AbstractHttpListModule::delete: non implementato");

	} // public void delete(SourceBean request)
} // public abstract class AbstractHttpListModule extends AbstractHttpModule
	// implements HttpServiceIFace

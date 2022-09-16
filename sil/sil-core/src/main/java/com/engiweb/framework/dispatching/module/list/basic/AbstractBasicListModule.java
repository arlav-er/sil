package com.engiweb.framework.dispatching.module.list.basic;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.dispatching.module.AbstractModule;
import com.engiweb.framework.dispatching.service.list.basic.IFaceBasicListService;
import com.engiweb.framework.dispatching.service.list.basic.impl.DelegatedBasicListService;
import com.engiweb.framework.paginator.basic.ListIFace;

public abstract class AbstractBasicListModule extends AbstractModule implements IFaceBasicListService {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(AbstractBasicListModule.class.getName());
	private ListIFace _list = null;

	public AbstractBasicListModule() {
		super();
	} // public AbstractBasicListModule()

	/**
	 * @see com.engiweb.framework.dispatching.service.ServiceIFace#service(SourceBean, SourceBean)
	 */
	public void service(SourceBean serviceRequest, SourceBean serviceResponse) throws Exception {
		_logger.debug("AbstractBasicListModule::service: invocato");

		DelegatedBasicListService.service(this, serviceRequest, serviceResponse);
	} // public void service(SourceBean serviceRequest, SourceBean

	// serviceResponse) throws Exception

	/**
	 * @see com.engiweb.framework.dispatching.service.list.basic.IFaceBasicListService#getList()
	 */
	public ListIFace getList() {
		_logger.debug("AbstractBasicListModule::getList: invocato");

		return _list;
	} // public ListIFace getList()

	/**
	 * @see com.engiweb.framework.dispatching.service.list.basic.IFaceBasicListService#setList(ListIFace)
	 */
	public void setList(ListIFace list) {
		_logger.debug("AbstractBasicListModule::setList: invocato");

		_list = list;
	} // public void setList(ListIFace list)

	/**
	 * @see com.engiweb.framework.dispatching.service.list.basic.IFaceBasicListService#callback(SourceBean, ListIFace,
	 *      int)
	 */
	public void callback(SourceBean request, SourceBean response, ListIFace list, int page) throws Exception {
		_logger.debug("AbstractBasicListModule::callback: metodo non implementato !");

	} // public void callback(SourceBean request, SourceBean response,

	// ListIFace list, int page) throws Exception

	/**
	 * @see com.engiweb.framework.dispatching.service.list.basic.IFaceBasicListService#delete(SourceBean)
	 */
	public boolean delete(SourceBean request, SourceBean response) {
		_logger.debug("AbstractBasicListModule::delete: metodo non implementato !");

		return false;
	} // public boolean delete(SourceBean request, SourceBean response)
} // public abstract class AbstractBasicListModule extends AbstractModule
	// implements IFaceBasicListService

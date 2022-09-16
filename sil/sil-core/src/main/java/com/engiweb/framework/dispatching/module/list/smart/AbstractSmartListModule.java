package com.engiweb.framework.dispatching.module.list.smart;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.dispatching.module.AbstractModule;
import com.engiweb.framework.dispatching.service.list.smart.IFaceSmartListService;
import com.engiweb.framework.dispatching.service.list.smart.impl.DelegatedSmartListService;
import com.engiweb.framework.paginator.smart.IFaceListProvider;

public abstract class AbstractSmartListModule extends AbstractModule implements IFaceSmartListService {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(AbstractSmartListModule.class.getName());
	private IFaceListProvider _list = null;

	public AbstractSmartListModule() {
		super();
	} // public AbstractSmartListModule()

	/**
	 * @see com.engiweb.framework.dispatching.service.ServiceIFace#service(SourceBean, SourceBean)
	 */
	public void service(SourceBean serviceRequest, SourceBean serviceResponse) throws Exception {
		_logger.debug("AbstractSmartListModule::service: invocato");

		DelegatedSmartListService.service(this, serviceRequest, serviceResponse);
	} // public void service(SourceBean serviceRequest, SourceBean

	// serviceResponse) throws Exception

	/**
	 * @see com.engiweb.framework.dispatching.service.list.smart.IFaceSmartListService#getList()
	 */
	public IFaceListProvider getList() {
		_logger.debug("AbstractSmartListModule::getList: invocato");

		return _list;
	} // public IFaceListProvider getList()

	/**
	 * @see com.engiweb.framework.dispatching.service.list.smart.IFaceSmartListService#setList(IFaceListProvider)
	 */
	public void setList(IFaceListProvider list) {
		_logger.debug("AbstractSmartListModule::setList: invocato");

		_list = list;
	} // public void setList(IFaceListProvider list)

	/**
	 * @see com.engiweb.framework.dispatching.service.list.smart.IFaceSmartListService#callback(SourceBean,
	 *      IFaceListProvider, int)
	 */
	public void callback(SourceBean request, SourceBean response, IFaceListProvider list, int page) throws Exception {
		_logger.warn("AbstractSmartListModule::callback: metodo non implementato !");

	} // public void callback(SourceBean request,SourceBean response,

	// IFaceListProvider list, int page) throws Exception

	/**
	 * @see com.engiweb.framework.dispatching.service.list.smart.IFaceSmartListService#delete(SourceBean)
	 */
	public boolean delete(SourceBean request, SourceBean response) {
		_logger.warn("AbstractSmartListModule::delete: metodo non implementato !");

		return false;
	} // public boolean delete(SourceBean request, SourceBean response)
} // public abstract class AbstractSmartListModule extends AbstractModule
	// implements IFaceSmartListService

package com.engiweb.framework.dispatching.action.list.smart;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.dispatching.action.AbstractAction;
import com.engiweb.framework.dispatching.service.list.smart.IFaceSmartListService;
import com.engiweb.framework.dispatching.service.list.smart.impl.DelegatedSmartListService;
import com.engiweb.framework.paginator.smart.IFaceListProvider;

public abstract class AbstractSmartListAction extends AbstractAction implements IFaceSmartListService {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(AbstractSmartListAction.class.getName());
	private IFaceListProvider _list = null;

	public AbstractSmartListAction() {
		super();
		_list = null;
	} // public AbstractSmartListAction()

	/**
	 * @see com.engiweb.framework.dispatching.service.ServiceIFace#service(SourceBean, SourceBean)
	 */
	public void service(SourceBean serviceRequest, SourceBean serviceResponse) throws Exception {
		_logger.debug("AbstractSmartListAction::service: invocato");

		DelegatedSmartListService.service(this, serviceRequest, serviceResponse);
	} // public void service(SourceBean serviceRequest, SourceBean

	// serviceResponse) throws Exception

	/**
	 * @see com.engiweb.framework.dispatching.service.list.smart.IFaceSmartListService#getList()
	 */
	public IFaceListProvider getList() {
		_logger.debug("AbstractSmartListAction::getList: invocato");

		return _list;
	} // public IFaceListProvider getList()

	/**
	 * @see com.engiweb.framework.dispatching.service.list.smart.IFaceSmartListService#setList(IFaceListProvider)
	 */
	public void setList(IFaceListProvider list) {
		_logger.debug("AbstractSmartListAction::setList: invocato");

		_list = list;
	} // public void setList(IFaceListProvider list)

	/**
	 * @see com.engiweb.framework.dispatching.service.list.smart.IFaceSmartListService#callback(SourceBean,
	 *      IFaceListProvider, int)
	 */
	public void callback(SourceBean request, SourceBean response, IFaceListProvider list, int page) throws Exception {
		_logger.warn("AbstractSmartListAction::callback: metodo non implementato !");

	} // public void callback(SourceBean request, SourceBean response,

	// IFaceListProvider list, int page) throws Exception

	/**
	 * @see com.engiweb.framework.dispatching.service.list.smart.IFaceSmartListService#delete(SourceBean)
	 */
	public boolean delete(SourceBean request, SourceBean response) {
		_logger.warn("AbstractSmartListAction::delete: metodo non implementato !");

		return false;
	} // public boolean delete(SourceBean request, SourceBean response)
} // public abstract class AbstractSmartListAction extends AbstractAction
	// implements IFaceSmartListService

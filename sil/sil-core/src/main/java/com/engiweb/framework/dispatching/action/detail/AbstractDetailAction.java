package com.engiweb.framework.dispatching.action.detail;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.dispatching.action.AbstractAction;
import com.engiweb.framework.dispatching.service.detail.IFaceDetailService;
import com.engiweb.framework.dispatching.service.detail.impl.DelegatedDetailService;

public abstract class AbstractDetailAction extends AbstractAction implements IFaceDetailService {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(AbstractDetailAction.class.getName());

	public AbstractDetailAction() {
		super();
	} // public AbstractDetailAction()

	/**
	 * @see com.engiweb.framework.dispatching.service.ServiceIFace#service(SourceBean, SourceBean)
	 */
	public void service(SourceBean serviceRequest, SourceBean serviceResponse) throws Exception {
		_logger.debug("AbstractDetailAction::service: invocato");

		DelegatedDetailService.service(this, serviceRequest, serviceResponse);
	} // public void service(SourceBean serviceRequest, SourceBean

	// serviceResponse) throws Exception

	/**
	 * @see com.engiweb.framework.dispatching.service.detail.IFaceDetailService#insert(SourceBean, SourceBean)
	 */
	public boolean insert(SourceBean request, SourceBean response) {
		_logger.debug("AbstractDetailAction::insert: metodo non implementato !");

		return false;
	} // public boolean insert(SourceBean request, SourceBean response)

	/**
	 * @see com.engiweb.framework.dispatching.service.detail.IFaceDetailService#select(SourceBean, SourceBean)
	 */
	public boolean select(SourceBean request, SourceBean response) {
		_logger.debug("AbstractDetailAction::select: metodo non implementato !");

		return false;
	} // public boolean select(SourceBean request, SourceBean response)

	/**
	 * @see com.engiweb.framework.dispatching.service.detail.IFaceDetailService#update(SourceBean, SourceBean)
	 */
	public boolean update(SourceBean request, SourceBean response) {
		_logger.debug("AbstractDetailAction::update: metodo non implementato !");

		return false;
	} // public boolean update(SourceBean request, SourceBean response)
} // public abstract class AbstractDetailAction extends AbstractAction
	// implements IFaceDetailService

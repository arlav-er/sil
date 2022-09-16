package com.engiweb.framework.dispatching.module.detail;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.dispatching.module.AbstractModule;
import com.engiweb.framework.dispatching.service.detail.IFaceDetailService;
import com.engiweb.framework.dispatching.service.detail.impl.DelegatedDetailService;

/**
 * La classe <code>AbstractDetailModule</code> &egrave; la superclasse di un' implementazione di un dettaglio
 * generalizzato.
 * 
 * @author Luigi Bellio
 * @see com.engiweb.framework.base.SourceBean
 */
public abstract class AbstractDetailModule extends AbstractModule implements IFaceDetailService {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(AbstractDetailModule.class.getName());

	public AbstractDetailModule() {
		super();
	} // public AbstractDetailModule()

	/**
	 * @see com.engiweb.framework.dispatching.service.ServiceIFace#service(SourceBean, SourceBean)
	 */
	public void service(SourceBean serviceRequest, SourceBean serviceResponse) throws Exception {
		_logger.debug("AbstractDetailModule::service: invocato");

		DelegatedDetailService.service(this, serviceRequest, serviceResponse);
	} // public void service(SourceBean serviceRequest, SourceBean

	// serviceResponse) throws Exception

	/**
	 * @see com.engiweb.framework.dispatching.service.detail.IFaceDetailService#insert(SourceBean, SourceBean)
	 */
	public boolean insert(SourceBean request, SourceBean response) {
		_logger.debug("AbstractDetailModule::insert: metodo non implementato !");

		return false;
	} // public boolean insert(SourceBean request, SourceBean response)

	/**
	 * @see com.engiweb.framework.dispatching.service.detail.IFaceDetailService#select(SourceBean, SourceBean)
	 */
	public boolean select(SourceBean request, SourceBean response) {
		_logger.debug("AbstractDetailModule::select: metodo non implementato !");

		return false;
	} // public boolean select(SourceBean request, SourceBean response)

	/**
	 * @see com.engiweb.framework.dispatching.service.detail.IFaceDetailService#update(SourceBean, SourceBean)
	 */
	public boolean update(SourceBean request, SourceBean response) {
		_logger.debug("AbstractDetailModule::update: metodo non implementato !");

		return false;
	} // public boolean update(SourceBean request, SourceBean response)
} // public abstract class AbstractDetailModule extends AbstractModule
	// implements IFaceDetailService

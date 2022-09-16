package com.engiweb.framework.dispatching.module;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.dispatching.coordinator.AbstractCoordinator;
import com.engiweb.framework.dispatching.service.RequestContextIFace;
import com.jamonapi.Monitor;
import com.jamonapi.MonitorFactory;

public class ModuleCoordinator extends AbstractCoordinator {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(ModuleCoordinator.class.getName());

	public ModuleCoordinator(String businessType, String businessName) {
		super(businessType, businessName);
	} // public ModuleCoordinator(String businessType, String businessName)

	public void service(SourceBean serviceRequest, SourceBean serviceResponse) throws Exception {
		Monitor monitor = MonitorFactory.start("controller.coordinator.page");
		PageIFace page = PageFactory.getPage(getRequestContainer(), getBusinessName());
		synchronized (page) {
			Monitor pageMonitor = MonitorFactory.start("model.page." + getBusinessName().toLowerCase());
			try {
				it.eng.sil.util.TraceWrapper.debug(_logger, "ModuleCoordinator::service: serviceRequest",
						serviceRequest);

				((RequestContextIFace) page).setRequestContext(this);
				page.service(serviceRequest, serviceResponse);
			} // try
			catch (Exception ex) {
				_logger.fatal("ModuleCoordinator::service: eccezione nella page [" + getBusinessName() + "]");

				throw ex;
			} // catch (Exception ex)
			finally {
				((RequestContextIFace) page).setRequestContext(null);
				it.eng.sil.util.TraceWrapper.debug(_logger, "ModuleCoordinator::service: serviceResponse",
						serviceResponse);

				pageMonitor.stop();
				monitor.stop();
			} // finally
		} // synchronized (page)
	} // public void service(SourceBean request, SourceBean response)
} // public void service(SourceBean serviceRequest, SourceBean
	// serviceResponse) throws Exception

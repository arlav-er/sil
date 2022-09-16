package com.engiweb.framework.dispatching.module;

import com.engiweb.framework.base.Constants;
import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.dispatching.coordinator.CoordinatorIFace;
import com.engiweb.framework.dispatching.coordinator.DispatcherIFace;
import com.engiweb.framework.dispatching.service.RequestContextIFace;

public class ModuleDispatcher implements DispatcherIFace {
	public ModuleDispatcher() {
	} // public ModuleDispatcher()

	public boolean acceptsURL(RequestContextIFace requestContext) {
		SourceBean serviceRequest = requestContext.getRequestContainer().getServiceRequest();
		return serviceRequest.containsAttribute(Constants.PAGE);
	} // public boolean acceptsURL(RequestContextIFace requestContext)

	public String getBusinessType(RequestContextIFace requestContext) {
		return "PAGE";
	} // public String getBusinessType(RequestContextIFace requestContext)

	public String getBusinessName(RequestContextIFace requestContext) {
		SourceBean serviceRequest = requestContext.getRequestContainer().getServiceRequest();
		String pageName = (String) serviceRequest.getAttribute(Constants.PAGE);
		return pageName;
	} // public String getBusinessName(RequestContextIFace requestContext)

	public CoordinatorIFace getCoordinator(RequestContextIFace requestContext) {
		return new ModuleCoordinator(getBusinessType(requestContext), getBusinessName(requestContext));
	} // public CoordinatorIFace getCoordinator(RequestContextIFace
		// requestContext)
} // public class ModuleDispatcher implements DispatcherIFace

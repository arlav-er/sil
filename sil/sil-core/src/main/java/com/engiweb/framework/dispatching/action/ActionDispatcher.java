package com.engiweb.framework.dispatching.action;

import com.engiweb.framework.base.Constants;
import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.dispatching.coordinator.CoordinatorIFace;
import com.engiweb.framework.dispatching.coordinator.DispatcherIFace;
import com.engiweb.framework.dispatching.service.RequestContextIFace;

public class ActionDispatcher implements DispatcherIFace {
	public ActionDispatcher() {
	} // public ActionDispatcher()

	public boolean acceptsURL(RequestContextIFace requestContext) {
		SourceBean serviceRequest = requestContext.getRequestContainer().getServiceRequest();
		return serviceRequest.containsAttribute(Constants.ACTION_NAME);
	} // public boolean acceptsURL(RequestContextIFace requestContext)

	public String getBusinessType(RequestContextIFace requestContext) {
		return "ACTION";
	} // public String getBusinessType(RequestContextIFace requestContext)

	public String getBusinessName(RequestContextIFace requestContext) {
		SourceBean serviceRequest = requestContext.getRequestContainer().getServiceRequest();
		String actionName = (String) serviceRequest.getAttribute(Constants.ACTION_NAME);
		return actionName;
	} // public String getBusinessName(RequestContextIFace requestContext)

	public CoordinatorIFace getCoordinator(RequestContextIFace requestContext) {
		return new ActionCoordinator(getBusinessType(requestContext), getBusinessName(requestContext));
	} // public CoordinatorIFace getCoordinator(RequestContextIFace
		// requestContext)
} // public class ActionDispatcher implements DispatcherIFace

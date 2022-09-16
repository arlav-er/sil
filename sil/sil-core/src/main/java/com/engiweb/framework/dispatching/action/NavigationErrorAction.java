package com.engiweb.framework.dispatching.action;

import com.engiweb.framework.base.Constants;
import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.base.SourceBeanException;

public class NavigationErrorAction {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(NavigationErrorAction.class.getName());
	public static final String NAVIGATION_ERROR_ACTION = "NAVIGATION_ERROR_ACTION";

	private NavigationErrorAction() {
		super();
	} // private NavigationErrorAction()

	public static SourceBean getNavigationErrorServiceRequest() {
		SourceBean navigationErrorServiceRequest = null;
		try {
			navigationErrorServiceRequest = new SourceBean(Constants.SERVICE_REQUEST);
			navigationErrorServiceRequest.setAttribute(Constants.ACTION_NAME, NAVIGATION_ERROR_ACTION);
		} // try
		catch (SourceBeanException ex) {
			it.eng.sil.util.TraceWrapper.fatal(_logger, "Navigator::checkNavigation: ", ex);

		} // catch (SourceBeanException ex)
		return navigationErrorServiceRequest;
	} // public static SourceBean getNavigationErrorServiceRequest()
} // public class NavigationErrorAction

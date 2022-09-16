package com.engiweb.framework.dispatching.action;

import com.engiweb.framework.base.Constants;
import com.engiweb.framework.base.SourceBean;

public class SessionExpiredAction {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(SessionExpiredAction.class.getName());

	private SessionExpiredAction() {
		super();
	} // private SessionExpiredAction()

	public static void setSessionExpiredAction(SourceBean actionRequestBean) {
		try {
			actionRequestBean.delContainedAttributes();
			actionRequestBean.setAttribute(Constants.ACTION_NAME, "SESSION_EXPIRED_ACTION");
		} // try
		catch (Exception ex) {
			it.eng.sil.util.TraceWrapper.fatal(_logger, "SessionExpiredAction::setSessionExpiredAction: ", ex);

		} // catch (Excpetion ex) try
	} // public static void setSessionExpiredAction(SourceBean
		// actionRequestBean)
} // public class SessionExpiredAction

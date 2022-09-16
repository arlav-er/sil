package com.engiweb.framework.dispatching.action;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.dispatching.coordinator.AbstractCoordinator;
import com.engiweb.framework.dispatching.service.RequestContextIFace;
import com.jamonapi.Monitor;
import com.jamonapi.MonitorFactory;

public class ActionCoordinator extends AbstractCoordinator {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(ActionCoordinator.class.getName());

	public ActionCoordinator(String businessType, String businessName) {
		super(businessType, businessName);
	} // public ActionCoordinator(String businessType, String businessName)

	public void service(SourceBean serviceRequest, SourceBean serviceResponse) throws Exception {
		Monitor monitor = MonitorFactory.start("controller.coordinator.action");
		ActionIFace action = ActionFactory.getAction(getRequestContainer());
		synchronized (action) {
			// modifica A.B.
			// IEngUserProfile userProfile =
			// (IEngUserProfile) getRequestContainer()
			// .getSessionContainer()
			// .getPermanentContainer()
			// .getAttribute(IEngUserProfile.ENG_USER_PROFILE);
			// if (userProfile instanceof RequestContextIFace) {
			// ((RequestContextIFace)userProfile).setRequestContext(this);
			// }
			// ------
			boolean isActionAuthorized = true;
			// if (SecurityConfigurationSingleton
			// .getInstance()
			// .isActionChecked(getBusinessName())) {
			// if (userProfile == null) {
			// TracerSingleton.log(// Constants.NOME_MODULO,//
			// TracerSingleton.WARNING,// "ActionCoordinator::service:
			// userProfile nullo !");
			// isActionAuthorized = false;
			// } // if (userProfile == null)
			// else {
			// try {
			// isActionAuthorized =
			// userProfile.isAbleToExecuteAction(
			// getBusinessName());
			// } // try
			// catch (EMFInternalError ie) {
			// TracerSingleton.log(// Constants.NOME_MODULO,//
			// TracerSingleton.CRITICAL,// "ActionCoordinator::service:
			// userProfile.isAbleToExecuteAction("// + getBusinessName()// +
			// ")",// (Exception) ie);
			// isActionAuthorized = false;
			// } // catch (EMFInternalError ie)
			// } // if (userProfile == null) else
			// } // if
			// (SecurityConfigurationSingleton.getInstance().isActionChecked(_businessName))
			try {
				if (!isActionAuthorized) {
					_logger.warn("ActionCoordinator::service: autorizzazione esecuzione action [" + getBusinessName()
							+ "] negata !");

					throw new SecurityException();
				} // if (!isActionAuthorized)
				else {
					it.eng.sil.util.TraceWrapper.debug(_logger, "ActionCoordinator::service: serviceRequest",
							serviceRequest);

					((RequestContextIFace) action).setRequestContext(this);
					Monitor actionMonitor = MonitorFactory.start("model.action." + getBusinessName().toLowerCase());
					try {
						action.service(serviceRequest, serviceResponse);
					} // try
					catch (Exception ex) {
						throw ex;
					} // catch (Exception ex)
					finally {
						actionMonitor.stop();
					} // finally
				} // if (!isActionAuthorized) else
			} // try
			catch (Exception ex) {
				_logger.fatal("ActionCoordinator::service: eccezione nella action [" + getBusinessName() + "]");

				throw ex;
			} // catch (Exception ex)
			finally {
				((RequestContextIFace) action).setRequestContext(null);
				it.eng.sil.util.TraceWrapper.debug(_logger, "ActionCoordinator::service: serviceResponse",
						serviceResponse);

				monitor.stop();
			} // finally
		} // synchronized (action)

	} // public void service(SourceBean serviceRequest, SourceBean
		// serviceResponse) throws Exception
} // public class ActionCoordinator extends AbstractCoordinator
